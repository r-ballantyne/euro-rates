package com.rballantyne.eurorates.service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
public class ValidatorService {

	public void validateCurrency(String currency) {

		Matcher m = Pattern.compile("^[A-Z]{3}$").matcher(currency);
		if (!m.matches()) {
			throw new InvalidParameterException(
					"Provided currency " + currency + " does not match currency standard ISO 4217");
		}

	}

	public void validateAmount(BigDecimal amount) {
		
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidParameterException("Provided amount " + amount + " cannot be less than 0");
		}
		if (amount.scale() > 2) {
			throw new InvalidParameterException(
					"Provided amount " + amount + " cannot have more than 2 demical places");
		}

	}
}
