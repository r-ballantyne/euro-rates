package com.rballantyne.eurorates.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.rballantyne.eurorates.model.ExchangeRate;
import com.rballantyne.eurorates.model.ReferenceDay;

@Service
public class DataReaderService {

	@Value("classpath:eurofxref-hist.csv")
	private Resource historicalDataFile;

	private static final Logger logger = LoggerFactory.getLogger(DataReaderService.class);

	public List<ReferenceDay> loadData() throws IOException {

		return readFromCsv();

	}

	private List<ReferenceDay> readFromCsv() throws IOException {

		List<ReferenceDay> data = new ArrayList<>();

		try (Scanner scanner = new Scanner((historicalDataFile.getInputStream()));) {

			List<String> currencies = readCurrencies(scanner.nextLine());

			while (scanner.hasNextLine()) {

				readDataForDay(scanner.nextLine(), currencies);

			}

		}

		return data;

	}

	private List<String> readCurrencies(String headerRow) {

		List<String> currencies = new ArrayList<>();

		try (Scanner rowScanner = new Scanner(headerRow)) {
			rowScanner.useDelimiter(",");

			// skip over "Date"
			rowScanner.next();

			while (rowScanner.hasNext()) {
				currencies.add(rowScanner.next());
			}
		}

		return currencies;
	}

	private ReferenceDay readDataForDay(String line, List<String> currencies) {

		List<ExchangeRate> exchangeRates = new ArrayList<>();
		LocalDate date;

		try (Scanner rowScanner = new Scanner(line)) {
			rowScanner.useDelimiter(",");

			date = LocalDate.parse(rowScanner.next(), DateTimeFormatter.ISO_LOCAL_DATE);
			int i = 0;

			while (rowScanner.hasNext()) {

				// should be able to use scanner.parseLong
				exchangeRates.add(new ExchangeRate(currencies.get(i), Float.parseFloat(rowScanner.next())));
				i++;
			}
		}

		return new ReferenceDay(date, exchangeRates);

	}

}
