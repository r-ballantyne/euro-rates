package com.rballantyne.eurorates.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

		List<ReferenceDay> data = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(historicalDataFile.getInputStream()));) {

			List<String> currencies = readCurrencies(br.readLine());

			logger.info("Started data read...");

			data = br.lines().parallel().map(line -> mapToReferenceDay.apply(line, currencies))
					.collect(Collectors.toList());

			logger.info("Read {} lines of data", data.size());
		}
		return data;
	}

	private BiFunction<String, List<String>, ReferenceDay> mapToReferenceDay = (line, currencies) -> {

		LocalDate date;
		List<ExchangeRate> exchangeRates = new ArrayList<>();

		try (Scanner rowScanner = new Scanner(line)) {
			rowScanner.useDelimiter(",");
			date = LocalDate.parse(rowScanner.next(), DateTimeFormatter.ISO_LOCAL_DATE);
			int i = 0;

			while (rowScanner.hasNext()) {
				try {
					exchangeRates.add(new ExchangeRate(currencies.get(i), new BigDecimal(rowScanner.next())));

				} catch (NumberFormatException e) {
					logger.debug("No valid currency value found for currency {}", currencies.get(i));

				}
				i++;
			}
		}
		return new ReferenceDay(date, exchangeRates);
	};

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
}
