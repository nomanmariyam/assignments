package com.lendico.iban.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lendico.iban.exception.IbanExceptionType;
import com.lendico.iban.exception.IbanServiceException;
import com.lendico.iban.structure.DefaultIbanStructure;
import com.lendico.iban.structure.IbanAttribute;
import com.lendico.iban.util.IbanUtil;
import com.neovisionaries.i18n.CountryCode;

public class IbanServiceImpl implements IbanService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateIban(String countryCode, String bankCode,
			String accountNumber) throws IbanServiceException {
		if(StringUtils.isBlank(countryCode) || 
		   StringUtils.isBlank(bankCode) || 
		   StringUtils.isBlank(accountNumber)) {
			
			logger.error("countryCode, bankCode and accountNumber must be filled");
			throw new IbanServiceException("countryCode, bankCode and accountNumber must be filled");
		}
		
		final String formattedIban = formatIban(countryCode, bankCode, accountNumber);
		
		final String checkDigit = IbanUtil.calculateCheckDigit(formattedIban);
		
		final String ibanValue = IbanUtil.replaceCheckDigit(formattedIban, checkDigit);
		
		validateIban(ibanValue);
		
		return ibanValue;
	}
	
	@Override
	public String generateRandomIbanForCountry(String countryCode)
			throws IbanServiceException {
		if(StringUtils.isBlank(countryCode)) {
			logger.error("Iban can not be created as countryCode is null");
			throw new IbanServiceException("Iban can not be created as countryCode is null");
		}
		
		final String formattedIban = formatRandomIban(countryCode);
		
		final String checkDigit = IbanUtil.calculateCheckDigit(formattedIban);
		
		final String ibanValue = IbanUtil.replaceCheckDigit(formattedIban, checkDigit);
		
		validateIban(ibanValue);

		return ibanValue;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void validateIban(String ibanString) throws IbanServiceException {
		IbanUtil.validate(ibanString);
	}
	
	
	private String formatIban(String countryCode, String bankCode, String accountNumber) throws IbanServiceException {
        final StringBuilder sb = new StringBuilder();

        CountryCode countryCodeEnum = CountryCode.getByCode(countryCode);
        final List<IbanAttribute> ibanAttributeForCountry = DefaultIbanStructure.getIbanAttributeForCountry(countryCodeEnum);
        if (ibanAttributeForCountry == null || ibanAttributeForCountry.isEmpty()) {
        	logger.error("CountryCode is not supported");
            throw new IbanServiceException("CountryCode is not supported.");
        }
        sb.append(countryCodeEnum.getAlpha2());
        sb.append(IbanUtil.DEFAULT_CHECK_DIGIT);

        for(IbanAttribute ibanAttribute : ibanAttributeForCountry) {
            switch (ibanAttribute.getAttributeType()) {
                case bankCode:
                    sb.append(bankCode);
                    break;
                case accountNumber:
                    sb.append(accountNumber);
                    break;
            }
        }
        return sb.toString();
    }
	
	
	private String formatRandomIban(String countryCode) throws IbanServiceException {
        final StringBuilder sb = new StringBuilder();

        CountryCode countryCodeEnum = CountryCode.getByCode(countryCode);
        final List<IbanAttribute> ibanAttributeForCountry = DefaultIbanStructure.getIbanAttributeForCountry(countryCodeEnum);
        
        if (ibanAttributeForCountry == null || ibanAttributeForCountry.isEmpty()) {
        	logger.error("CountryCode is not supported");
            throw new IbanServiceException(IbanExceptionType.invalidCountryCode, "CountryCode is not supported.");
        }
        sb.append(countryCodeEnum.getAlpha2());
        sb.append(IbanUtil.DEFAULT_CHECK_DIGIT);

        for(IbanAttribute ibanAttribute : ibanAttributeForCountry) {
            switch (ibanAttribute.getAttributeType()) {
                case bankCode:
                    sb.append(ibanAttribute.getRandom());
                    break;
                case accountNumber:
                	sb.append(ibanAttribute.getRandom());
                    break;
            }
        }
        return sb.toString();
    }
}
