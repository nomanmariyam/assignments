package com.lendico.iban.structure;

import static org.junit.Assert.*;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

public class IbanAttributeTest {

	@Test
	public void testGetRandomNumeric() {
		IbanAttribute ibanAttribute = new IbanAttribute(AttributeType.bankCode, AttributeChracterType.numeric, 8);
		
		String randomBankCode = ibanAttribute.getRandom();
		assertEquals(8, randomBankCode.length());
		assertEquals(true, StringUtils.isNumeric(randomBankCode));
	}
	
	@Test
	public void testGetRandomUpperAlpha() {
		IbanAttribute ibanAttribute = new IbanAttribute(AttributeType.bankCode, AttributeChracterType.upperAlpha, 4);
		
		String randomBankCode = ibanAttribute.getRandom();
		assertEquals(4, randomBankCode.length());
		assertEquals(true, StringUtils.isAllUpperCase(randomBankCode));
	}
	
}
