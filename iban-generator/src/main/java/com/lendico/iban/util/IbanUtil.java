package com.lendico.iban.util;

import java.util.List;

import com.lendico.iban.exception.IbanExceptionType;
import com.lendico.iban.exception.IbanServiceException;
import com.lendico.iban.structure.DefaultIbanStructure;
import com.lendico.iban.structure.IbanAttribute;
import com.neovisionaries.i18n.CountryCode;

public class IbanUtil {
	private static final int MOD = 97;
    private static final long MAX = 999999999;
	
    public static final String DEFAULT_CHECK_DIGIT = "00";
    
	private static final int COUNTRY_CODE_INDEX = 0;
	
	private static final int COUNTRY_CODE_LENGTH = 2;
	private static final int CHECK_DIGIT_INDEX = COUNTRY_CODE_LENGTH;
	
    private static final int CHECK_DIGIT_LENGTH = 2;
    
    private static final int IBAN_INDEX = CHECK_DIGIT_INDEX + CHECK_DIGIT_LENGTH;
	
    public static void validate(final String iban) throws IbanServiceException {
        try {
            validateEmpty(iban);
            
            validateCountryCode(iban);
            
            validateCheckDigitPresence(iban);

            CountryCode countryCode = CountryCode.getByCode(getCountryCode(iban));

            validateIbanLength(iban, countryCode);
            
            validateIbanEntries(iban, countryCode);

            validateCheckDigit(iban);
            
        }  catch (RuntimeException e) {
            throw new IbanServiceException(e.getMessage());
        }
    }
    
    private static void validateEmpty(final String iban) throws IbanServiceException {
        if(iban == null || iban.length() == 0) {
            throw new IbanServiceException(IbanExceptionType.emptyIban, "Not Valid because, Iban is null or empty");
        }
    }
    
    private static void validateCountryCode(final String iban) throws IbanServiceException {
        if(iban.length() < COUNTRY_CODE_LENGTH) {
            throw new IbanServiceException(IbanExceptionType.invalidCountryCode, 
            		"Iban must contain 2 char country code.");
        }

        final String countryCode = getCountryCode(iban);

        if(!countryCode.equals(countryCode.toUpperCase()) ||
            !Character.isLetter(countryCode.charAt(0)) ||
            !Character.isLetter(countryCode.charAt(1))) {
            throw new IbanServiceException(IbanExceptionType.invalidCountryCode, 
            		"Iban country code must contain upper case letters.");
        }

        List<IbanAttribute> ibanAttributes = DefaultIbanStructure.getIbanAttributeForCountry(
        		CountryCode.getByCode(countryCode));
        if (ibanAttributes == null || ibanAttributes.isEmpty()) {
            throw new IbanServiceException(IbanExceptionType.invalidCountryCode, 
            		"Country code is not supported.");
        }
    }
    
    private static void validateCheckDigitPresence(final String iban) throws IbanServiceException {
        if(iban.length() < COUNTRY_CODE_LENGTH + CHECK_DIGIT_LENGTH) {
            throw new IbanServiceException(IbanExceptionType.invalidCheckDigit, 
            		"Iban must contain 2 digit check digit.");
        }

        final String checkDigit = getCheckDigit(iban);

        if(!Character.isDigit(checkDigit.charAt(0)) ||
           !Character.isDigit(checkDigit.charAt(1))) {
            throw new IbanServiceException(IbanExceptionType.invalidCheckDigit,
            		"Iban's check digit should contain only digits.");
        }
    }
    
    private static void validateIbanLength(final String iban, 
    		final CountryCode countryCode) throws IbanServiceException {
    	
		final int expectedIbanLength = DefaultIbanStructure.getIbanLengthWithoutCountryAndCheckDigit(countryCode);
		final String ibanWithoutCountryAndCheckDigit = getIbanWithoutCountryAndCheckDigit(iban);
		
		final int ibanLength = ibanWithoutCountryAndCheckDigit.length();
		
		if (expectedIbanLength != ibanLength) {
			throw new IbanServiceException(IbanExceptionType.invalidIbanLength,
			String.format("[%s] length is %d, expected IBAN length is: %d",
			ibanWithoutCountryAndCheckDigit, ibanLength, expectedIbanLength));
		}
    }
    
    
    private static void validateIbanEntries(final String iban,
            final CountryCode countryCode) throws IbanServiceException {
    	
    	List<IbanAttribute> ibanAttributeListForCountry = getIbanAttribute(countryCode);
    	
		final String ibanWithoutCountryAndCheckDigit = getIbanWithoutCountryAndCheckDigit(iban);
		
		int ibanAttributeIndex = 0;
		
		for(final IbanAttribute ibanAttribute : ibanAttributeListForCountry) {
			final int entryLength = ibanAttribute.getAttributeLength();
			
			final String entryValue = ibanWithoutCountryAndCheckDigit.substring(ibanAttributeIndex,
					ibanAttributeIndex + entryLength);
		
			ibanAttributeIndex = ibanAttributeIndex + entryLength;
		
			// validate character type
			validateIbanEntryCharacterType(ibanAttribute, entryValue);
		}
    }
    
    private static void validateIbanEntryCharacterType(final IbanAttribute entry,
            final String entryValue) throws IbanServiceException {
		switch (entry.getAttributeCharacterType()) {
		case upperAlpha:
			for(char ch: entryValue.toCharArray()) {
				if(!Character.isUpperCase(ch)) {
					throw new IbanServiceException(IbanExceptionType.invalidChracter,
							String.format("[%s] must contain only upper case letters.", entryValue));
				}
			}
			break;
			
		case numeric:
			for(char ch: entryValue.toCharArray()) {
				if(!Character.isDigit(ch)) {
					throw new IbanServiceException(IbanExceptionType.invalidChracter,
							String.format("[%s] must contain only digits.", entryValue));
				}
			}
			break;
		}
	 }
    
    private static void validateCheckDigit(final String iban) throws IbanServiceException {
        if (calculateMod(iban) != 1) {
            final String checkDigit = getCheckDigit(iban);
            final String expectedCheckDigit = calculateCheckDigit(iban);
            
            throw new IbanServiceException(IbanExceptionType.invalidCheckDigit,
                    String.format("[%s] has invalid check digit: %s, " +
                                    "expected check digit is: %s",
                            iban, checkDigit, expectedCheckDigit));
        }
    }
    
    public static String calculateCheckDigit(final String iban) throws IbanServiceException {
        final String reformattedIban = replaceCheckDigit(iban, DEFAULT_CHECK_DIGIT);
        final int modResult = calculateMod(reformattedIban);
        final int checkDigitIntValue = (98 - modResult);
        final String checkDigit = Integer.toString(checkDigitIntValue);
        return checkDigitIntValue > 9 ? checkDigit : "0" + checkDigit;
    }
    
    public static String replaceCheckDigit(final String iban, final String checkDigit) {
        return getCountryCode(iban) + checkDigit + getIbanWithoutCountryAndCheckDigit(iban);
    }
    
    private static int calculateMod(final String iban) throws IbanServiceException {
        final String reformattedIban = getIbanWithoutCountryAndCheckDigit(iban) + getCountryCodeAndCheckDigit(iban);
        long total = 0;
        for (int i = 0; i < reformattedIban.length(); i++) {
            final int numericValue = Character.getNumericValue(reformattedIban.charAt(i));
            if (numericValue < 0 || numericValue > 35) {
                throw new IbanServiceException(
                        String.format("Invalid Character[%d] = '%d'", i, numericValue));
            }
            total = (numericValue > 9 ? total * 100 : total * 10) + numericValue;

            if (total > MAX) {
                total = (total % MOD);
            }

        }
        return (int) (total % MOD);
    }
    
    public static String getCountryCodeAndCheckDigit(final String iban) {
        return iban.substring(COUNTRY_CODE_INDEX,
                COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH + CHECK_DIGIT_LENGTH);
    }
    
    public static String getCheckDigit(final String iban) {
        return iban.substring(CHECK_DIGIT_INDEX,
                CHECK_DIGIT_INDEX + CHECK_DIGIT_LENGTH);
    }

    public static String getCountryCode(final String iban) {
        return iban.substring(COUNTRY_CODE_INDEX,
                COUNTRY_CODE_INDEX + COUNTRY_CODE_LENGTH);
    }
    
    
    public static int getDefaultIbanLengthForCountry(final CountryCode countryCode) {
        return COUNTRY_CODE_LENGTH + CHECK_DIGIT_LENGTH + 
        		DefaultIbanStructure.getIbanLengthWithoutCountryAndCheckDigit(countryCode);
    }
    
    public static boolean isSupportedCountry(final CountryCode countryCode) {
        return DefaultIbanStructure.getIbanAttributeForCountry(countryCode) != null;
    }
    
    private static List<IbanAttribute> getIbanAttribute(final CountryCode countryCode) {
        return DefaultIbanStructure.getIbanAttributeForCountry(countryCode);
    }
    
    public static String getIbanWithoutCountryAndCheckDigit(final String iban) {
        return iban.substring(IBAN_INDEX);
    }
}
