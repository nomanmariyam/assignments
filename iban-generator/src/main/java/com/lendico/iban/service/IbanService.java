package com.lendico.iban.service;

import com.lendico.iban.exception.IbanServiceException;

/**
 * interface for IbanService
 * @author noman
 */
public interface IbanService {
	String generateIban(String countryCode, String bankCode, String accountNumber) throws IbanServiceException;
	
	String generateRandomIbanForCountry(String countryCode) throws IbanServiceException;
	
	void validateIban(String ibanString) throws IbanServiceException;
}
