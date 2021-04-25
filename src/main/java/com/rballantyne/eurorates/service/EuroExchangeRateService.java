package com.rballantyne.eurorates.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.DoubleStream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rballantyne.eurorates.model.ExchangeRate;
import com.rballantyne.eurorates.model.ReferenceDay;

@Service
public class EuroExchangeRateService {

	private static final Logger logger = LoggerFactory.getLogger(EuroExchangeRateService.class);

	@Autowired
	private DataReaderService dataReaderService;

	List<ReferenceDay> exchangeRateData;

	@PostConstruct
	private void initialise() throws IOException {

		exchangeRateData = dataReaderService.loadData();
	}

	public ReferenceDay getReferenceDataForDay(LocalDate date) {

		ReferenceDay referenceDay = exchangeRateData.stream().filter(day -> day.getDate().equals(date)).findFirst()
				.orElseThrow(() -> new NoSuchElementException("No reference data found for " + date));

		logger.info("Found reference exchange rate data for {} currencies for {} ",
				referenceDay.getExchangeRates().size(), date);

		return referenceDay;

	}

	public BigDecimal exchangeAmountOnDay(LocalDate date, String sourceCurrency, String targetCurrency,
			BigDecimal amount) {

		ReferenceDay refData = getReferenceDataForDay(date);

		ExchangeRate sourceRate = getExchangeRateForCurrencyFromDay(sourceCurrency, refData)
				.orElseThrow(() -> new NoSuchElementException(
						"No exchange rate data found for currency " + sourceCurrency + " on " + date));
		ExchangeRate targetRate = getExchangeRateForCurrencyFromDay(targetCurrency, refData)
				.orElseThrow(() -> new NoSuchElementException(
						"No exchange rate data found for currency " + targetCurrency + " on " + date));

		BigDecimal exchangedAmount = (amount.divide(sourceRate.getRate(), 2, RoundingMode.HALF_UP)
				.multiply(targetRate.getRate())).setScale(2, RoundingMode.HALF_UP);

		logger.info("Exchange on date {}: {} {} converts to {} {} (RATES: {}, {})", date, amount, sourceCurrency,
				exchangedAmount, targetCurrency, sourceRate, targetRate);

		return exchangedAmount;
	}

	public BigDecimal getHighestExchangeRateForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		BigDecimal highestRate = BigDecimal
				.valueOf(getStreamOfRatesForCurrencyForPeriod(startDate, endDate, currency).max().orElseThrow(
						() -> new NoSuchElementException("Could not find max exchange rate (no data for currency "
								+ currency + " for period " + startDate + " -> " + endDate + ")")));

		logger.info("Highest exchange rate found for period {} -> {} for currency {}: {}", startDate, endDate, currency,
				highestRate);

		return highestRate;
	}

	public BigDecimal getAverageExchangeRateForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		BigDecimal avgRate = BigDecimal
				.valueOf(getStreamOfRatesForCurrencyForPeriod(startDate, endDate, currency).average().orElseThrow(
						() -> new NoSuchElementException("Could not find average exchange rate (no data for currency "
								+ currency + " for period " + startDate + " -> " + endDate + ")")))
				.setScale(4, RoundingMode.HALF_UP);

		logger.info("Average exchange rate calculated for period {} -> {} for currency {}: {}", startDate, endDate,
				currency, avgRate);

		return avgRate;
	}

	public DoubleStream getStreamOfRatesForCurrencyForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		return exchangeRateData.parallelStream()
				.filter(refDay -> refDay.getDate().isAfter(startDate.minusDays(1))
						&& refDay.getDate().isBefore(endDate.plusDays(1)))
				.map(refDay -> getExchangeRateForCurrencyFromDay(currency, refDay)).flatMap(Optional::stream)
				.mapToDouble(er -> er.getRate().doubleValue());
	}

	public Optional<ExchangeRate> getExchangeRateForCurrencyFromDay(String currency, ReferenceDay referenceDay) {

		return referenceDay.getExchangeRates().stream().filter(er -> er.getCurrency().equals(currency)).findFirst();
	}

}
