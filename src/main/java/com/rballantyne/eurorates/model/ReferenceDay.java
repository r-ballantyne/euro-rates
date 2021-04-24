package com.rballantyne.eurorates.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class ReferenceDay {

	private final LocalDate date;
	private final List<ExchangeRate> exchangeRates;

	public ReferenceDay(LocalDate date, List<ExchangeRate> exchangeRates) {
		this.date = date;
		this.exchangeRates = exchangeRates;
	}

	public LocalDate getDate() {
		return date;
	}

	public List<ExchangeRate> getExchangeRates() {
		return exchangeRates;
	}

	@Override	
	public int hashCode() {
		return Objects.hash(date, exchangeRates);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferenceDay other = (ReferenceDay) obj;
		return Objects.equals(date, other.date) && Objects.equals(exchangeRates, other.exchangeRates);
	}

	@Override
	public String toString() {
		return "ReferenceDay [date=" + date + ", exchangeRates=" + exchangeRates + "]";
	}
}
