package com.rballantyne.eurorates.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

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

	Comparator<ExchangeRate> byRate = (ExchangeRate er1, ExchangeRate er2) -> Float.compare(er1.getRate(),
			er2.getRate());

	public ReferenceDay getReferenceDataForDay(LocalDate date) {

		return exchangeRateData.stream().filter(day -> day.getDate().equals(date)).findFirst()
				.orElseThrow(() -> new NoSuchElementException("No reference data found for " + date));
	}

	private BiFunction<String, ReferenceDay, ExchangeRate> exchangeRateForCurrency = (currency, refDay) -> refDay
			.getExchangeRates().stream().filter(er -> er.getCurrency().equals(currency)).findFirst()
			.orElseThrow(() -> new NoSuchElementException("No exchange rate data found for currency " + currency));

	public float exchangeAmountOnDay(LocalDate date, String sourceCurrency, String targetCurrency, float amount) {

		ReferenceDay refData = getReferenceDataForDay(date);

		ExchangeRate sourceRate = exchangeRateForCurrency.apply(sourceCurrency, refData);
		ExchangeRate targetRate = exchangeRateForCurrency.apply(targetCurrency, refData);

		float exchangedAmount = (amount / sourceRate.getRate()) * targetRate.getRate();

		logger.info("Exchange on date {}: {} {} converts to {} {} (RATES: {}, {})", date, amount, sourceCurrency,
				exchangedAmount, targetCurrency, sourceRate, targetRate);

		return exchangedAmount;
	}

	public float getHighestExchangeRateForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		logger.info("Starting search for highest exhange rate");
		
		ExchangeRate highestEr = exchangeRateData.parallelStream()
				.filter(refDay -> refDay.getDate().isAfter(startDate.minusDays(1))
						&& refDay.getDate().isBefore(endDate.plusDays(1)))
				.map(refDay -> exchangeRateForCurrency.apply(currency, refDay)).max(byRate)
				.orElseThrow(() -> new NoSuchElementException("No exchange rate data found for currency " + currency
						+ " for period " + startDate + " -> " + endDate));

		logger.info("Highest exchange rate found for period {} -> {} for currency {}: {}", startDate, endDate, currency,
				highestEr);

		return highestEr.getRate();

	}

	public float getAverageExchangeRateForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		return 0f;

	}

}
