package com.xpand.xface.util;

import org.w3c.dom.Node;

import com.jcabi.log.Logger;

public class BooleanUtil {
	public static int TRUE_VALUE = 1;
	public static int FALSE_VALUE = 0;
	public static boolean stringToBoolean(String value, boolean defaultValue) {
		try {
			return Boolean.valueOf(value);
		}catch(Exception ex) {
			return defaultValue;
		}
	}
	public static boolean stringToBoolean(Node node, boolean defaultValue) {
		try {			
			if (node!=null) {
				String attributeValue = node.getNodeValue();
				return BooleanUtil.stringToBoolean(attributeValue, defaultValue);
			}else {
				return defaultValue;
			}
		}catch(Exception ex) {
			return defaultValue;
		}
	}
}
