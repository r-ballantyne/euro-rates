package com.rballantyne.eurorates.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rballantyne.eurorates.model.ReferenceDay;

@Service
public class EuroExchangeRateService {

	@Autowired
	private DataReaderService dataReaderService;

	List<ReferenceDay> exchangeRateData;

	@PostConstruct
	private void initialise() throws IOException {
		exchangeRateData = dataReaderService.loadData();
	}

	public ReferenceDay getReferenceDataForDay(LocalDate date) {

		return null;

	}

	public float exchangeAmountOnDay(LocalDate date, String sourceCurrency, String targetCurrency) {

		return 0f;

	}

	public float getHighestExchangeRateForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		return 0f;

	}

	public float getAverageExchangeRateForPeriod(LocalDate startDate, LocalDate endDate, String currency) {

		return 0f;

	}

}
