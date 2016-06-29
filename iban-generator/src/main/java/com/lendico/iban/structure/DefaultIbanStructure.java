package com.lendico.iban.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neovisionaries.i18n.CountryCode;

public class DefaultIbanStructure {

    private static final Map<CountryCode, List<IbanAttribute>> defaultIbanStructureForCountry;

    static {
        defaultIbanStructureForCountry = new HashMap<CountryCode, List<IbanAttribute>>();

        List<IbanAttribute> ibanAttributeForDe = new ArrayList<IbanAttribute>();
        ibanAttributeForDe.add(new IbanAttribute(AttributeType.bankCode, AttributeChracterType.numeric, 8));
        ibanAttributeForDe.add(new IbanAttribute(AttributeType.accountNumber, AttributeChracterType.numeric, 10));
        defaultIbanStructureForCountry.put(CountryCode.DE, ibanAttributeForDe);
        
        List<IbanAttribute> ibanAttributeForAt = new ArrayList<IbanAttribute>();
        ibanAttributeForAt.add(new IbanAttribute(AttributeType.bankCode, AttributeChracterType.numeric, 5));
        ibanAttributeForAt.add(new IbanAttribute(AttributeType.accountNumber, AttributeChracterType.numeric, 11));
        defaultIbanStructureForCountry.put(CountryCode.AT, ibanAttributeForAt);

        List<IbanAttribute> ibanAttributeForNl = new ArrayList<IbanAttribute>();
        ibanAttributeForNl.add(new IbanAttribute(AttributeType.bankCode, AttributeChracterType.upperAlpha, 4));
        ibanAttributeForNl.add(new IbanAttribute(AttributeType.accountNumber, AttributeChracterType.numeric, 10));
        defaultIbanStructureForCountry.put(CountryCode.NL, ibanAttributeForNl);
    }

    public static List<IbanAttribute> getIbanAttributeForCountry(final CountryCode countryCode) {
    	List<IbanAttribute> ibanAttribute =  defaultIbanStructureForCountry.get(countryCode);
    	if(ibanAttribute != null) {
    		return ibanAttribute;
    	}
    	return null;
    }

    public static List<CountryCode> supportedCountries() {
        final List<CountryCode> countryCodes = new ArrayList<CountryCode>(defaultIbanStructureForCountry.size());
        countryCodes.addAll(defaultIbanStructureForCountry.keySet());
        return Collections.unmodifiableList(countryCodes);
    }

    public static int getIbanLengthWithoutCountryAndCheckDigit(CountryCode countryCode) {
        int length = 0;
        for (IbanAttribute entry : defaultIbanStructureForCountry.get(countryCode)) {
            length += entry.getAttributeLength();
        }
        return length;
    }
}