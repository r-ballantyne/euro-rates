package com.rballantyne.eurorates.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rballantyne.eurorates.model.ExchangeRate;
import com.rballantyne.eurorates.model.ReferenceDay;

@RestController
@RequestMapping("/api")
public class EuroRatesController {

	private static final Logger logger = LoggerFactory.getLogger(EuroRatesController.class);

	@GetMapping()
	public ReferenceDay getReferenceDataForDay(LocalDate date) {

		logger.info("Received request at getReferenceDataForDay: date={}");

		return null;
	}

	@GetMapping()
	public long getExchangedAmountForDay(LocalDate date, String sourceCurrency, String targetCurrency) {

		logger.info("Received request at getExchangedAmountForDay: date={}, sourceCurrency={}, targetCurrency={}", date,
				sourceCurrency, targetCurrency);

		return 0l;
	}

	@GetMapping()
	public ExchangeRate getCurrencyHighestExchangeRateForPeriod(LocalDate startDate, LocalDate endDate,
			String currency) {

		logger.info(
				"Received request at getCurrencyHighestExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);

		return null;
	}

	@GetMapping()
	public long getCurrencyAverageExchangeRateForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		logger.info(
				"Received request at getCurrencyAverageExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);

		return 0l;
	}
}
