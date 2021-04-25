package com.rballantyne.eurorates.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.rballantyne.eurorates.model.ExchangeRate;
import com.rballantyne.eurorates.model.ReferenceDay;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class EuroExchangeRateServiceTest {

	@Autowired
	EuroExchangeRateService exchangeRateService;

	ReferenceDay refDay;

	@BeforeAll
	void setUp() {

		LocalDate date = LocalDate.of(2021, 4, 23);

		// Date,USD,JPY,BGN,CYP,CZK,DKK,EEK,GBP
		// 2021-04-23,1.2066,129.98,1.9558,N/A,25.847,7.4361,N/A,0.86905

		ExchangeRate usd = new ExchangeRate("USD", new BigDecimal("1.2066"));
		ExchangeRate jpy = new ExchangeRate("JPY", new BigDecimal("129.98"));
		ExchangeRate bgn = new ExchangeRate("BGN", new BigDecimal("1.9558"));
		ExchangeRate czk = new ExchangeRate("CZK", new BigDecimal("25.847"));
		ExchangeRate dkk = new ExchangeRate("DKK", new BigDecimal("7.4361"));
		ExchangeRate gbp = new ExchangeRate("GBP", new BigDecimal("0.86905"));

		List<ExchangeRate> rates = new ArrayList<>(List.of(usd, jpy, bgn, czk, dkk, gbp));
		refDay = new ReferenceDay(date, rates);
	}

	@Test
	void testGetReferenceDataForDay_RetrievesDataForDate_IgnoringNAData() {

		LocalDate date = LocalDate.of(2021, 4, 23);

		assertEquals(refDay, exchangeRateService.getReferenceDataForDay(date));

	}

	@Test
	void testGetReferenceDataForDay_DoesThrow_WhenNoData() {

		LocalDate date = LocalDate.of(1500, 1, 1);

		assertThrows(NoSuchElementException.class, () -> exchangeRateService.getReferenceDataForDay(date));
	}

	@Test
	void testExchangeAmountOnDay_ReturnsCorrectAmount() {

		LocalDate date = LocalDate.of(2021, 4, 23);
		BigDecimal expected = new BigDecimal("277.69");

		assertEquals(expected, exchangeRateService.exchangeAmountOnDay(date, "GBP", "USD", new BigDecimal("200.00")));
	}

	@Test
	void testExchangeAmountOnDay_DoesThrow_WhenMissingDataForCurrency() {

		LocalDate date = LocalDate.of(2021, 4, 23);
		BigDecimal amount = new BigDecimal("5.00");

		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.exchangeAmountOnDay(date, "CYP", "USD", amount));

		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.exchangeAmountOnDay(date, "JPY", "BOG", amount));
	}

	@Test
	void testExchangeAmountOnDay_DoesThrow_WhenMissingDataForDate() {

		LocalDate date = LocalDate.of(1500, 1, 1);
		BigDecimal amount = new BigDecimal("5.00");

		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.exchangeAmountOnDay(date, "JPY", "DKK", amount));

	}

	
	@Test
	void testGetHighestExchangeRateForPeriod_ReturnsHighestRate() {

		BigDecimal expected1 = new BigDecimal("130.64");
		BigDecimal expected2 = new BigDecimal("130.37");

		LocalDate startDate = LocalDate.of(2021, 4, 15);
		LocalDate endDate1 = LocalDate.of(2021, 4, 23);
		LocalDate endDate2 = LocalDate.of(2021, 4, 19);

		assertEquals(expected1, exchangeRateService.getHighestExchangeRateForPeriod(startDate, endDate1, "JPY"));
		assertEquals(expected2, exchangeRateService.getHighestExchangeRateForPeriod(startDate, endDate2, "JPY"));
	}

	@Test
	void testGetHighestExchangeRateForPeriod_DoesThrow_WhenMissingDataForCurrency() {

		LocalDate startDate = LocalDate.of(2021, 4, 15);
		LocalDate endDate = LocalDate.of(2021, 4, 23);

		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getHighestExchangeRateForPeriod(startDate, endDate, "EEK"));
		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getHighestExchangeRateForPeriod(startDate, endDate, "BOG"));
	}

	@Test
	void testGetHighestExchangeRateForPeriod_DoesThrow_WhenMissingDataForPeriod() {

		LocalDate startDate1 = LocalDate.of(2020, 1, 1);
		LocalDate startDate2 = LocalDate.of(2021, 4, 24);

		LocalDate endDate1 = LocalDate.of(2020, 12, 1);
		LocalDate endDate2 = LocalDate.of(2021, 4, 14);
		LocalDate endDate3 = LocalDate.of(2021, 4, 30);

		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getHighestExchangeRateForPeriod(startDate1, endDate1, "USD"));
		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getHighestExchangeRateForPeriod(startDate1, endDate2, "USD"));
		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getHighestExchangeRateForPeriod(startDate2, endDate3, "USD"));
	}
	
	@Test
	void testGetAverageExchangeRateForPeriod_ReturnsAverageRate() {

		BigDecimal expected1 = new BigDecimal("0.8660");
		BigDecimal expected2 = new BigDecimal("0.8663");

		LocalDate startDate = LocalDate.of(2021, 4, 15);
		LocalDate endDate1 = LocalDate.of(2021, 4, 23);
		LocalDate endDate2 = LocalDate.of(2021, 4, 19);

		assertEquals(expected1, exchangeRateService.getAverageExchangeRateForPeriod(startDate, endDate1, "GBP"));
		assertEquals(expected2, exchangeRateService.getAverageExchangeRateForPeriod(startDate, endDate2, "GBP"));
	}

	@Test
	void testGetAverageExchangeRateForPeriod_DoesThrow_WhenMissingDataForCurrency() {

		LocalDate startDate = LocalDate.of(2021, 4, 15);
		LocalDate endDate = LocalDate.of(2021, 4, 23);

		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getAverageExchangeRateForPeriod(startDate, endDate, "EEK"));
		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getAverageExchangeRateForPeriod(startDate, endDate, "BOG"));
	}

	@Test
	void testGetAverageExchangeRateForPeriod_DoesThrow_WhenMissingDataForPeriod() {

		LocalDate startDate1 = LocalDate.of(2020, 1, 1);
		LocalDate startDate2 = LocalDate.of(2021, 4, 24);

		LocalDate endDate1 = LocalDate.of(2020, 12, 1);
		LocalDate endDate2 = LocalDate.of(2021, 4, 14);
		LocalDate endDate3 = LocalDate.of(2021, 4, 30);

		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getAverageExchangeRateForPeriod(startDate1, endDate1, "USD"));
		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getAverageExchangeRateForPeriod(startDate1, endDate2, "USD"));
		assertThrows(NoSuchElementException.class,
				() -> exchangeRateService.getAverageExchangeRateForPeriod(startDate2, endDate3, "USD"));
	}
	


}
