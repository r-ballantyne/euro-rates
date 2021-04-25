package com.rballantyne.eurorates.controller;

import java.security.InvalidParameterException;
import java.text.DecimalFormat;
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
import com.rballantyne.eurorates.service.ValidatorService;

@RestController
@RequestMapping("/api")
public class EuroRatesController {

	@Autowired
	EuroExchangeRateService exchangeRateService;

	@Autowired
	ValidatorService validatorService;

	private static final Logger logger = LoggerFactory.getLogger(EuroRatesController.class);

	@GetMapping("/referenceDataForDay")
	@ResponseBody
	public ReferenceDay getReferenceDataForDay(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		logger.info("Received request at getReferenceDataForDay: date={}", date);

		try {
			return exchangeRateService.getReferenceDataForDay(date);

		} catch (NoSuchElementException e) {
			logger.error("No reference data found for {}", date, e);

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/exchangeAmountOnDay")
	public float getExchangedAmountForDay(
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			@RequestParam("source") String sourceCurrency, @RequestParam("target") String targetCurrency,
			@RequestParam("amount") float amount) {
		logger.info(
				"Received request at getExchangedAmountForDay: date={}, sourceCurrency={}, targetCurrency={}, amount={}",
				date, sourceCurrency, targetCurrency, amount);

		try {

			validatorService.validateCurrency(sourceCurrency);
			validatorService.validateCurrency(targetCurrency);
			validatorService.validateAmount(amount);

			return exchangeRateService.exchangeAmountOnDay(date, sourceCurrency, targetCurrency, amount);

		} catch (InvalidParameterException e) {
			logger.error("Input parameter(s) failed validation: {}", e.getMessage(), e);

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

		} catch (NoSuchElementException e) {
			logger.error("Missing data for {}", date, e);

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@GetMapping("/highestExchangeRate")
	public String getCurrencyHighestExchangeRateForPeriod(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("currency") String currency) {

		logger.info(
				"Received request at getCurrencyHighestExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);

		try {
			validatorService.validateCurrency(currency);

			return formatReturnRate(exchangeRateService.getHighestExchangeRateForPeriod(startDate, endDate, currency));

		} catch (InvalidParameterException e) {
			logger.error("Input parameter(s) failed validation: ", e);

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

		} catch (NoSuchElementException e) {
			logger.error("Missing data : ", e);

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}

	}

	@GetMapping("/averageExchangeRate")
	public String getCurrencyAverageExchangeRateForPeriod(
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("currency") String currency) {

		logger.info(
				"Received request at getCurrencyAverageExchangeRateForPeriod: startDate={}, endDate={}, currency={}",
				startDate, endDate, currency);
		try {
			validatorService.validateCurrency(currency);

			return formatReturnRate(exchangeRateService.getAverageExchangeRateForPeriod(startDate, endDate, currency));

		} catch (InvalidParameterException e) {
			logger.error("Input parameter(s) failed validation: ", e);

			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());

		} catch (NoSuchElementException e) {
			logger.error("Missing data : ", e);

			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	private String formatReturnRate(double rate) {
		return new DecimalFormat("0.0000").format(rate);
	}

}
