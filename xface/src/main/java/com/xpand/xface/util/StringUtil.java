package com.xpand.xface.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.codec.Hex;
import org.w3c.dom.Node;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtil {
	public static final String DATE_FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	
	public static boolean checkNull(String value) {
		if (value==null || "".equals(value)||"null".equals(value)) {
			return true;
		}else {
			return false;
		}
	}
	public static Integer stringToInteger(String value, Integer defaultValue) {
		try {
			return Integer.parseInt(value);
		}catch (Exception ex) {
			return defaultValue;
		}
	}
	public static Long stringToLong(String value, long defaultValue) {
		try {
			return Long.parseLong(value);
		}catch (Exception ex) {
			return defaultValue;
		}
	}
	public static Integer stringToInteger(Node node, Integer defaultValue) {
		try {			
			if (node!=null) {
				String attributeValue = node.getNodeValue();
				return StringUtil.stringToInteger(attributeValue, defaultValue);
			}else {
				return defaultValue;
			}
		}catch(Exception ex) {
			return defaultValue;
		}
	}	
	
	public static Date stringToDate(String value, String valueDateFormat) {		
		DateFormat format = new SimpleDateFormat(valueDateFormat, Locale.ENGLISH);
		try {
			return format.parse(value);
		}catch (Exception ex) {
			return new Date();
		}			
	}
	public static String dateToString(Date date, String outputFormat){
    	SimpleDateFormat sdfDate = new SimpleDateFormat(outputFormat);
    	if (date==null) {
    		date = new Date();
    	}	    
	    String strDate = sdfDate.format(date);
	    return strDate;
    }
	public static String dateToString(long date, String outputFormat){    	
	    return StringUtil.dateToString(new Date(date), outputFormat);
    }
	public static String dateToString(Long date, String outputFormat){
		if (date==null) {
			return "date is null";
		}else {
			return StringUtil.dateToString(new Date(date), outputFormat);
		}	    
    }
	public static String getSha256(String value) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(value.toString().getBytes(StandardCharsets.UTF_8));
			String sha256hex = new String(Hex.encode(hash));
	        return sha256hex;
		}catch (Exception ex) {
			return value;
		}		
	}
	public static String generateRandomPassword(int noOfUpper, int noOfLower, int noOfSpecialChar, int noOfDigit, int pwdLength) {
		List<CharacterRule> rules = Arrays.asList(new CharacterRule(EnglishCharacterData.UpperCase, noOfUpper)
				,new CharacterRule(EnglishCharacterData.LowerCase, noOfLower)
				,new CharacterRule(EnglishCharacterData.Digit, noOfDigit)
				,new CharacterRule(EnglishCharacterData.Special, noOfSpecialChar));
		PasswordGenerator generator = new PasswordGenerator();		
		String password = generator.generatePassword(pwdLength, rules);
		return password;
	}
	
	public static String getJson(Object object, Class<?> classType) {
		ObjectMapper mapper = new ObjectMapper();
		try {						
			return mapper.writeValueAsString(object);
		}catch(Exception ex) {
			return "convert object to json fail";
		}
	}
	public static String arrayToString(Set<?> objectList) {
		if (objectList==null) {
			return "";
		}else {
//			StringBuilder sb = new StringBuilder();
//			sb.append(objectList.toString());			
			return objectList.toString();
		}		
	}
	public static String arrayToString(ArrayList<?> objectList) {
		if (objectList==null) {
			return "";
		}else {			
			return objectList.toString();
		}		
	}
	
	
}
