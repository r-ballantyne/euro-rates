package com.rballantyne.eurorates.service;

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
	private void initialise() {

		exchangeRateData = dataReaderService.loadData();
	}

}
