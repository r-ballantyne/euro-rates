package com.rballantyne.eurorates.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class ValidatorServiceTest {

	@MockBean
	DataReaderService dataReaderService;

	@Autowired
	ValidatorService validatorService;

	@Test
	void testValidateCurrency_DoesNotThrow_When3UpperCaseLetters() {
		assertDoesNotThrow(() -> validatorService.validateCurrency("USD"));
		assertDoesNotThrow(() -> validatorService.validateCurrency("HEY"));
		assertDoesNotThrow(() -> validatorService.validateCurrency("ROL"));
	}

	@Test
	void testValidateCurrency_DoesThrow_WhenNot3Letters() {
		assertThrows(InvalidParameterException.class, () -> validatorService.validateCurrency("USDT"));
		assertThrows(InvalidParameterException.class, () -> validatorService.validateCurrency("GB"));
		assertThrows(InvalidParameterException.class, () -> validatorService.validateCurrency(""));
	}

	@Test
	void testValidateCurrency_DoesThrow_WhenNotUpperCase() {
		assertThrows(InvalidParameterException.class, () -> validatorService.validateCurrency("usd"));
	}

	@Test
	void testValidateCurrency_DoesThrow_WhenNumerical() {
		assertThrows(InvalidParameterException.class, () -> validatorService.validateCurrency("123"));
	}

	@Test
	void testValidateAmount_DoesNotThrow_WhenGreaterThanZeroAndTwoOrLessDecimalPlaces() {
		assertDoesNotThrow(() -> validatorService.validateAmount(new BigDecimal("10")));
		assertDoesNotThrow(() -> validatorService.validateAmount(new BigDecimal("99.5")));
		assertDoesNotThrow(() -> validatorService.validateAmount(new BigDecimal("01")));
		assertDoesNotThrow(() -> validatorService.validateAmount(new BigDecimal("0.01")));
		assertDoesNotThrow(() -> validatorService.validateAmount(new BigDecimal("10101022.7")));
	}

	@Test
	void testValidateAmount_DoesThrow_WhenZeroOrLess() {

		BigDecimal bdTest1 = new BigDecimal("0");
		BigDecimal bdTest2 = new BigDecimal("0.00");
		BigDecimal bdTest3 = new BigDecimal("-5");

		assertThrows(InvalidParameterException.class, () -> validatorService.validateAmount(bdTest1));
		assertThrows(InvalidParameterException.class, () -> validatorService.validateAmount(bdTest2));
		assertThrows(InvalidParameterException.class, () -> validatorService.validateAmount(bdTest3));
	}

	@Test
	void testValidateAmount_DoesThrow_WhenMoreThanTwoDecimalPlaces() {

		BigDecimal bdTest1 = new BigDecimal("1.233");
		BigDecimal bdTest2 = new BigDecimal("590247493.0008");
		BigDecimal bdTest3 = new BigDecimal("0.000000");

		assertThrows(InvalidParameterException.class, () -> validatorService.validateAmount(bdTest1));
		assertThrows(InvalidParameterException.class, () -> validatorService.validateAmount(bdTest2));
		assertThrows(InvalidParameterException.class, () -> validatorService.validateAmount(bdTest3));
	}

	@Test
	void testValidateDates_DoesNotThrow_WhenDatesChronological() {

		LocalDate startDate = LocalDate.of(2021, 1, 1);
		LocalDate endDate = LocalDate.of(2021, 2, 1);

		assertDoesNotThrow(() -> validatorService.validateDates(startDate, endDate));
	}

	@Test
	void testValidateDates_DoesNotThrow_WhenDatesSame() {

		LocalDate startDate = LocalDate.of(2021, 1, 1);
		LocalDate endDate = LocalDate.of(2021, 1, 1);

		assertDoesNotThrow(() -> validatorService.validateDates(startDate, endDate));
	}

	@Test
	void testValidateDates_DoesThrow_WhenDatesInWrongOrder() {

		LocalDate startDate = LocalDate.of(2021, 2, 1);
		LocalDate endDate = LocalDate.of(2021, 1, 1);

		assertThrows(InvalidParameterException.class, () -> validatorService.validateDates(startDate, endDate));
	}

}
