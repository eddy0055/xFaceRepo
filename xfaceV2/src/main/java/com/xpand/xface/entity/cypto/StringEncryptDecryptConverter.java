package com.xpand.xface.entity.cypto;

import javax.persistence.Converter;

import com.xpand.xface.util.StringUtil;

/**
 * Concrete implementation of the {@link AbstractEncryptDecryptConverter}
 * abstract class to encrypt/decrypt an entity attribute of type
 * {@link java.lang.String} <br/>
 * Note: This is the class where the {@literal @}Converter annotation is applied
 * 
 * @author Sunit Katkar, sunitkatkar@gmail.com
 * @since ver 1.0 (Apr 2018)
 * @version 1.0 *
 */
@Converter(autoApply = false)
public class StringEncryptDecryptConverter extends AbstractEncryptDecryptConverter<String> {

	/**
	 * Default constructor initializes with an instance of the {@link CipherMaker}
	 * crypto class to get a {@link javax.crypto.Cipher} instance
	 */
	public StringEncryptDecryptConverter() {
		this(new CipherMaker());
	}

	/**
	 * Constructor
	 * 
	 * @param cipherMaker
	 */
	public StringEncryptDecryptConverter(CipherMaker cipherMaker) {
		super(cipherMaker);
	}

	@Override
	boolean isNotNullOrEmpty(String attribute) {
		return !StringUtil.checkNull(attribute);
	}

	@Override
	String convertStringToEntityAttribute(String dbData) {
		// the input is a string and output is a string
		return dbData;
	}

	@Override
	String convertEntityAttributeToString(String attribute) {
		// Here too the input is a string and output is a string
		return attribute;
	}
}