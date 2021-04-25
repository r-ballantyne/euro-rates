# euro-rates

## Description

This is a Java Spring Boot application which loads historical Euro foreign exchange reference rates data from a .csv file that is packaged within the JAR on start-up. The file is sourced from https://www.ecb.europa.eu/stats/eurofxref/eurofxref-hist.zip. Future versions of this service would dynamically grab the latest data file from this URL at run-time, however in its current iteration the application is instantiated with data up to 2021-04-23.

## Endpoints

This service exposes 4 endpoints on port 8080. The request parameters listed for these requests are non-optional, and are validated in the following ways:

* `currency`: Currencies must be a 3-character non-numerical string in upper case (https://www.iso.org/iso-4217-currency-codes.html), e.g. "GBP", "USD", "JPY".
* `amount`: Amount must be a positive number with a maximum of 2 decimal places, e.g. 0.10, 577, 4.1
* `date`: Any date parameter should be in the format yyyy-MM-dd (https://www.iso.org/iso-8601-date-and-time-format.html) e.g. 2021-04-23, 2000-12-01, 1997-08-11
* `startDate` and `endDate`: In requests where two dates that form the boundary of a period of time are requested, the start date must be chronologically earlier than the end date, e.g. `startDate=2021-01-03&endDate=2021-04-23`

The endpoints are as follows:

### Retrieve Reference Data for a Given Date

`localhost:8080/api/referenceDataForDay?date=<date>`

Retrieve the reference rate data for a given date for all available currencies.

Example call and response:

```ssh
$ curl -x GET localhost:8080/api/referenceDataForDay?date=2021-04-23
```

```json
 "date": "2021-04-23",
    "exchangeRates": [
        {
            "currency": "USD",
            "rate": 1.2066
        },
        {
            "currency": "JPY",
            "rate": 129.98
        },
        {
            "currency": "BGN",
            "rate": 1.9558
        },
        {
            "currency": "CZK",
            "rate": 25.847
        },
        {
            "currency": "DKK",
            "rate": 7.4361
        },
        {
            "currency": "GBP",
            "rate": 0.86905
        }
    ]
}
```

### Convert an Amount Between Currencies on a Given Date

`localhost:8080/api/exchangeAmountOnDay?date=<date>&source=<sourceCurrency>&target=<targetCurrency>&amount=<amount>`

Given a date, source currency (eg. JPY), target currency (eg. GBP), and an amount, returns the amount given converted from the first to the second currency as it would have been on that date (assuming zero fees).

Example call and response:

```ssh
$ curl -x GET localhost:8080/api/exchangeAmountOnDay?date=2021-04-23&source=GBP&target=JPY&amount=10.00
```
```json
1496.07
```

### Get the Highest Exchange Rate of a Currency in a Given Period

`localhost:8080/api/highestExchangeRate?startDate=<startDate>&endDate=<endDate>&currency=<currency>`

Given a start date, an end date and a currency, return the highest reference exchange rate that the currency achieved for the period.

Example call and response:

```ssh
$ curl -x GET localhost:8080/api/highestExchangeRate?startDate=2000-04-23&endDate=2021-04-23&currency=GBP
```
```json
0.97855
```

### Get the Avergae Exchange Rate of a Currency in a Given Period

`localhost:8080/api/averageExchangeRate?startDate=<startDate>&endDate=<endDate>&currency=<currency>`

Given a start date, an end date and a currency, determine and return the average reference exchange rate of that currency for the period.

Example call and response:

```ssh
$ curl -x GET localhost:8080/api/averageExchangeRate?startDate=2020-04-23&endDate=2021-04-23&currency=DKK
```
```json
7.4442
```

