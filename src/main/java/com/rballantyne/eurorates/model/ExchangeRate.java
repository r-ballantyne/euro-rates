package com.rballantyne.eurorates.model;

import java.math.BigDecimal;
import java.util.Objects;

public class ExchangeRate {

	private final String currency;
	private final BigDecimal rate;

	public ExchangeRate(String currency, BigDecimal rate) {
		this.currency = currency;
		this.rate = rate;
	}

	public String getCurrency() {
		return currency;
	}

	public BigDecimal getRate() {
		return rate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(currency, rate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExchangeRate other = (ExchangeRate) obj;
		return Objects.equals(currency, other.currency) && Objects.equals(rate, other.rate);
	}

	@Override
	public String toString() {
		return "ExchangeRate [currency=" + currency + ", rate=" + rate + "]";
	}

}
