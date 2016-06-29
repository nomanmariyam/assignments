package com.lendico.iban.exception;

public enum IbanExceptionType {
	emptyIban,
	invalidCountryCode,
	invalidCheckDigit,
	invalidIbanLength,
	invalidChracter
	;
}
