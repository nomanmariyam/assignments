package com.lendico.iban.structure;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.neovisionaries.i18n.CountryCode;

public class DefaultIbanStructureTest {
	
	@Test
	public void testGetIbanAttributeForDe() {
		List<IbanAttribute> ibanAttribute = 
				DefaultIbanStructure.getIbanAttributeForCountry(CountryCode.DE);
		
		assertEquals(2, ibanAttribute.size());
		assertEquals(AttributeType.bankCode, ibanAttribute.get(0).getAttributeType());
		assertEquals(AttributeChracterType.numeric, ibanAttribute.get(0).getAttributeCharacterType());
		assertEquals(8, ibanAttribute.get(0).getAttributeLength());
		
		assertEquals(AttributeType.accountNumber, ibanAttribute.get(1).getAttributeType());
		assertEquals(AttributeChracterType.numeric, ibanAttribute.get(1).getAttributeCharacterType());
		assertEquals(10, ibanAttribute.get(1).getAttributeLength());
	}
	
	@Test
	public void testGetIbanAttributeForAt() {
		List<IbanAttribute> ibanAttribute = 
				DefaultIbanStructure.getIbanAttributeForCountry(CountryCode.AT);
		
		assertEquals(2, ibanAttribute.size());
		assertEquals(AttributeType.bankCode, ibanAttribute.get(0).getAttributeType());
		assertEquals(AttributeChracterType.numeric, ibanAttribute.get(0).getAttributeCharacterType());
		assertEquals(5, ibanAttribute.get(0).getAttributeLength());
		
		assertEquals(AttributeType.accountNumber, ibanAttribute.get(1).getAttributeType());
		assertEquals(AttributeChracterType.numeric, ibanAttribute.get(1).getAttributeCharacterType());
		assertEquals(11, ibanAttribute.get(1).getAttributeLength());
	}
	
	@Test
	public void testGetIbanAttributeForNl() {
		List<IbanAttribute> ibanAttribute = 
				DefaultIbanStructure.getIbanAttributeForCountry(CountryCode.NL);
		
		assertEquals(2, ibanAttribute.size());
		assertEquals(AttributeType.bankCode, ibanAttribute.get(0).getAttributeType());
		assertEquals(AttributeChracterType.upperAlpha, ibanAttribute.get(0).getAttributeCharacterType());
		assertEquals(4, ibanAttribute.get(0).getAttributeLength());
		
		assertEquals(AttributeType.accountNumber, ibanAttribute.get(1).getAttributeType());
		assertEquals(AttributeChracterType.numeric, ibanAttribute.get(1).getAttributeCharacterType());
		assertEquals(10, ibanAttribute.get(1).getAttributeLength());
	}

	@Test
	public void testSupportedCountries() {
		List<CountryCode> supportedCountries = DefaultIbanStructure.supportedCountries();
		assertEquals(3, supportedCountries.size());
	}

	@Test
	public void testGetIbanLengthWithoutCountryAndCheckDigit() {
		assertEquals(18, 
				DefaultIbanStructure.getIbanLengthWithoutCountryAndCheckDigit(CountryCode.DE));
		
		assertEquals(16, 
				DefaultIbanStructure.getIbanLengthWithoutCountryAndCheckDigit(CountryCode.AT));
		
		assertEquals(14, 
				DefaultIbanStructure.getIbanLengthWithoutCountryAndCheckDigit(CountryCode.NL));
	}

}
