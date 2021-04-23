package com.rballantyne.eurorates.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.rballantyne.eurorates.model.ReferenceDay;

@Service
public class DataReaderService {

	@Value("classpath:eurofxref-hist.csv")
	private Resource historicalDataFile;

	public List<ReferenceDay> loadData() throws IOException {

		return readFromCsv();

	}

	private List<ReferenceDay> readFromCsv() throws IOException {

		List<ReferenceDay> data = new ArrayList<>();

		try (Scanner scanner = new Scanner((historicalDataFile.getInputStream()));) {

			List<String> currencies = getCurrencies(scanner.nextLine());

			while (scanner.hasNextLine()) {

			}

		}
		return data;

	}

	private List<String> getCurrencies(String headerRow) {

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
