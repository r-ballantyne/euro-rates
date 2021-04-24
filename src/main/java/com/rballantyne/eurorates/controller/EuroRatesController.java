package com.rballantyne.eurorates.controller;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rballantyne.eurorates.model.ReferenceDay;
import com.rballantyne.eurorates.service.EuroExchangeRateService;

@RestController
@RequestMapping("/api")
public class EuroRatesController {

	@Autowired
	EuroExchangeRateService exchangeRateService;

	private static final Logger logger = LoggerFactory.getLogger(EuroRatesController.class);

	@GetMapping("/referenceDataForDay")
	@ResponseBody
	public ReferenceDay getReferenceDataForDay(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		logger.info("Received request at getReferenceDataForDay: date={}", date);
		
		try {
			return exchangeRateService.getReferenceDataForDay(date);
			
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No reference data found for " + date);
			
		}
	}

	@GetMapping("/exchangeAmountOnDay")
	public float getExchangedAmountForDay(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam("source") String sourceCurrency, @RequestParam("target") String targetCurrency) {

		logger.info("Received request at getExchangedAmountForDay: date={}, sourceCurrency={}, targetCurrency={}", date,
				sourceCurrency, targetCurrency);

		return exchangeRateService.exchangeAmountOnDay(date, sourceCurrency, targetCurrency);
	}

	@GetMapping("/highestExchangeRate")
	public float getCurrencyHighestExchangeRateForPeriod(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("currency") String currency) {

		logger.info(
				"Received request at getCurrencyHighestExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);

		return exchangeRateService.getHighestExchangeRateForPeriod(startDate, endDate, currency);
	}

	@GetMapping("/averageExchangeRate")
	public float getCurrencyAverageExchangeRateForPeriod(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("currency") String currency) {

		logger.info(
				"Received request at getCurrencyAverageExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);

		return exchangeRateService.getAverageExchangeRateForPeriod(startDate, endDate, currency);
	}
}
