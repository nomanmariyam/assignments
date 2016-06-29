package com.lendico.iban.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.lendico.iban.exception.IbanExceptionType;
import com.lendico.iban.exception.IbanServiceException;
import com.neovisionaries.i18n.CountryCode;

public class IbanUtilTest {

	@Test
	public void testValidateIbanExceptionEmptyIban() {
		try {
			IbanUtil.validate("");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.emptyIban, ex.getIbanExceptionType());
		}
	}
	
	@Test
	public void testValidateIbanExceptionInvalidCountry() {
		try {
			IbanUtil.validate("D");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidCountryCode, ex.getIbanExceptionType());
			assertEquals("Iban must contain 2 char country code.", ex.getMessage());
		}
		
		try {
			IbanUtil.validate("De");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidCountryCode, ex.getIbanExceptionType());
			assertEquals("Iban country code must contain upper case letters.", ex.getMessage());
		}
		
		try {
			IbanUtil.validate(CountryCode.AC.name());
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidCountryCode, ex.getIbanExceptionType());
			assertEquals("Country code is not supported.", ex.getMessage());
		}
	}
	
	
	@Test
	public void testValidateIbanExceptionInvalidCheckDigitPresence() {
		try {
			IbanUtil.validate("DE0");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidCheckDigit, ex.getIbanExceptionType());
			assertEquals("Iban must contain 2 digit check digit.", ex.getMessage());
		}
		
		try {
			IbanUtil.validate("DE0A");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidCheckDigit, ex.getIbanExceptionType());
			assertEquals("Iban's check digit should contain only digits.", ex.getMessage());
		}
	}
	
	@Test
	public void testValidateIbanExceptionInvalidLength() {
		try {
			IbanUtil.validate("DE007006002403948400");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidIbanLength, ex.getIbanExceptionType());
			assertEquals("[7006002403948400] length is 16, expected IBAN length is: 18", ex.getMessage());
		}
	}
	
	
	@Test
	public void testValidateIbanExceptionInvalidChracterForDe() {
		try {
			IbanUtil.validate("DE007A0600240390048400");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidChracter, ex.getIbanExceptionType());
			assertEquals("[7A060024] must contain only digits.", ex.getMessage());
		}
		
		try {
			IbanUtil.validate("DE00700600240390B48400");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidChracter, ex.getIbanExceptionType());
			assertEquals("[0390B48400] must contain only digits.", ex.getMessage());
		}
	}
	
	
	@Test
	public void testValidateIbanExceptionInvalidChracterForAt() {
		try {
			IbanUtil.validate("AT007A06012345678912");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidChracter, ex.getIbanExceptionType());
			assertEquals("[7A060] must contain only digits.", ex.getMessage());
		}
		
		try {
			IbanUtil.validate("AT0070060123456789B2");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidChracter, ex.getIbanExceptionType());
			assertEquals("[123456789B2] must contain only digits.", ex.getMessage());
		}
	}
	
	@Test
	public void testValidateIbanExceptionInvalidChracterForNl() {
		try {
			IbanUtil.validate("NL00ABC41234567892");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidChracter, ex.getIbanExceptionType());
			assertEquals("[ABC4] must contain only upper case letters.", ex.getMessage());
		}
		
		try {
			IbanUtil.validate("NL00ABCD123456789M");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidChracter, ex.getIbanExceptionType());
			assertEquals("[123456789M] must contain only digits.", ex.getMessage());
		}
	}


	@Test
	public void testValidateIbanExceptionInvalidCheckDigit() {
		try {
			IbanUtil.validate("DE00700600240390048400");
		} catch (IbanServiceException ex) {
			assertEquals(IbanExceptionType.invalidCheckDigit, ex.getIbanExceptionType());
			assertEquals("[DE00700600240390048400] has invalid check digit: 00, expected check digit is: 27", ex.getMessage());
		}
	}
	
	@Test
	public void testCalculateCheckDigit() throws IbanServiceException {
		assertEquals("27", IbanUtil.calculateCheckDigit("DE00700600240390048400"));
		assertEquals("49", IbanUtil.calculateCheckDigit("DE00701500240390048400"));
		assertEquals("73", IbanUtil.calculateCheckDigit("DE00701600240390048400"));
	}
	
	@Test(expected=IbanServiceException.class)
	public void testCalculateCheckDigitException() throws IbanServiceException {
		IbanUtil.calculateCheckDigit("DE00;700600240390048400");
	}

	@Test
	public void testReplaceCheckDigit() throws IbanServiceException {
		assertEquals("DE05700600240390048400", IbanUtil.replaceCheckDigit("DE00700600240390048400", "05"));
		assertEquals("DE09700600240390048400", IbanUtil.replaceCheckDigit("DE00700600240390048400", "09"));
	}

	@Test
	public void testGetCountryCodeAndCheckDigit() {
		assertEquals("DE05", IbanUtil.getCountryCodeAndCheckDigit("DE05700600240390048400"));
	}

	@Test
	public void testGetCheckDigit() {
		assertEquals("05", IbanUtil.getCheckDigit("DE05700600240390048400"));
		assertEquals("09", IbanUtil.getCheckDigit("DE09700600240390048400"));
	}

	@Test
	public void testGetCountryCode() {
		assertEquals("DE", IbanUtil.getCountryCode("DE05700600240390048400"));
		assertEquals("NL", IbanUtil.getCountryCode("NL05700600240390048400"));
	}

	@Test
	public void testGetDefaultIbanLengthForCountry() {
		assertEquals(22, IbanUtil.getDefaultIbanLengthForCountry(CountryCode.DE));
		assertEquals(20, IbanUtil.getDefaultIbanLengthForCountry(CountryCode.AT));
		assertEquals(18, IbanUtil.getDefaultIbanLengthForCountry(CountryCode.NL));
	}

	@Test
	public void testIsSupportedCountry() {
		assertEquals(true, IbanUtil.isSupportedCountry(CountryCode.DE));
		assertEquals(true, IbanUtil.isSupportedCountry(CountryCode.NL));
		assertEquals(true, IbanUtil.isSupportedCountry(CountryCode.AT));
		
		assertEquals(false, IbanUtil.isSupportedCountry(CountryCode.AD));
	}

	@Test
	public void testGetIbanWithoutCountryAndCheckDigit() {
		assertEquals("700600240390048400", IbanUtil.getIbanWithoutCountryAndCheckDigit("NL05700600240390048400"));
	}
}
