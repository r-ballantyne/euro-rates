package com.rballantyne.eurorates.controller;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rballantyne.eurorates.model.ExchangeRate;
import com.rballantyne.eurorates.model.ReferenceDay;

@RestController
@RequestMapping("/api")
public class EuroRatesController {

	private static final Logger logger = LoggerFactory.getLogger(EuroRatesController.class);

	@GetMapping("/referenceDataForDay")
	public ReferenceDay getReferenceDataForDay(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

		logger.info("Received request at getReferenceDataForDay: date={}", date);

		return null;
	}

	@GetMapping("/exchangeAmountOnDay")
	public long getExchangedAmountForDay(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam("source") String sourceCurrency, @RequestParam("target") String targetCurrency) {

		logger.info("Received request at getExchangedAmountForDay: date={}, sourceCurrency={}, targetCurrency={}", date,
				sourceCurrency, targetCurrency);

		return 0l;
	}

	@GetMapping("/highestExchangeRate")
	public ExchangeRate getCurrencyHighestExchangeRateForPeriod(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("currency") String currency) {

		logger.info(
				"Received request at getCurrencyHighestExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);

		return null;
	}

	@GetMapping("/averageExchangeRate")
	public long getCurrencyAverageExchangeRateForPeriod(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("currency") String currency) {

		logger.info(
				"Received request at getCurrencyAverageExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);

		return 0l;
	}
}
