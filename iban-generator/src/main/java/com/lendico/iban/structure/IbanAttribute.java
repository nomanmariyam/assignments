package com.lendico.iban.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IbanAttribute {
	private final AttributeType attributeType;
    private final AttributeChracterType attributeCharacterType;
    private final int attributeLength;

    private static final Map<AttributeChracterType, char[]> charByCharacterType;
    private final Random random = new Random();

    static {
        charByCharacterType = new HashMap<AttributeChracterType, char[]>();
        StringBuilder numericChracter = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            numericChracter.append(ch);
        }
        
        charByCharacterType.put(AttributeChracterType.numeric, numericChracter.toString().toCharArray());

        StringBuilder upperAlphaChracter = new StringBuilder();
        for (char ch = 'A'; ch <= 'Z'; ++ch) {
            upperAlphaChracter.append(ch);
        }
        charByCharacterType.put(AttributeChracterType.upperAlpha, upperAlphaChracter.toString().toCharArray());
    }

    public IbanAttribute(final AttributeType attributeType, 
    		final AttributeChracterType attributeCharacterType, final int length) {
        this.attributeType = attributeType;
        this.attributeCharacterType = attributeCharacterType;
        this.attributeLength = length;
    }

    public static IbanAttribute bankCode(final int length, final AttributeChracterType characterType) {
        return new IbanAttribute(AttributeType.bankCode, characterType, length);
    }

    public static IbanAttribute accountNumber(final int length, final AttributeChracterType characterType) {
        return new IbanAttribute(AttributeType.accountNumber, characterType, length);
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public AttributeChracterType getAttributeCharacterType() {
        return attributeCharacterType;
    }

    public int getAttributeLength() {
        return attributeLength;
    }

    public String getRandom() {
        StringBuilder s = new StringBuilder("");
        char[] charChoices = charByCharacterType.get(this.attributeCharacterType);
        if (charChoices == null) {
            throw new RuntimeException(String.format("Unsupported ChracterType %s",
                    attributeCharacterType.name()));
        }
        for (int i = 0; i < getAttributeLength(); i++) {
            s.append(charChoices[random.nextInt(charChoices.length)]);
        }
        return s.toString();
    }
}
