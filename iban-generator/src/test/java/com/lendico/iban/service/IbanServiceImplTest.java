package com.lendico.iban.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.lendico.iban.exception.IbanExceptionType;
import com.lendico.iban.exception.IbanServiceException;
import com.lendico.iban.util.IbanUtil;
import com.neovisionaries.i18n.CountryCode;

public class IbanServiceImplTest {
	IbanService ibanService;
	
	@Before
	public void setUp() throws Exception {
		ibanService = new IbanServiceImpl();
	}

	@Test
	public void testGenerateIban() throws IbanServiceException {
		assertEquals("DE"+IbanUtil.calculateCheckDigit("DE00700600240390048400")+"700600240390048400", 
				ibanService.generateIban("DE", "70060024", "0390048400"));
	}
	
	@Test
	public void testGenerateIbanException() throws IbanServiceException {
		try {
			ibanService.generateIban("DE", "7006002A", "0390048400");
		} catch(IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidChracter, ex.getIbanExceptionType());
			assertEquals("[7006002A] must contain only digits.", ex.getMessage());
		}
	}
	
	@Test
	public void testGenerateRandomIbanForCountry() throws IbanServiceException {
		assertEquals(22, ibanService.generateRandomIbanForCountry("DE").length());
		assertEquals(20, ibanService.generateRandomIbanForCountry("AT").length());
		assertEquals(18, ibanService.generateRandomIbanForCountry("NL").length());
	}
	
	@Test
	public void testGenerateRandomIbanForCountryException() throws IbanServiceException {
		try {
			ibanService.generateRandomIbanForCountry(CountryCode.AD.name()).length();
		} catch(IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidCountryCode, ex.getIbanExceptionType());
			assertEquals("CountryCode is not supported.", ex.getMessage());
		}
	}
	
	@Test
	public void testValidateIbanSuccess() throws IbanServiceException {
		ibanService.validateIban("DE89370400440532013000");
	}
}
