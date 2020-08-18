package com.xpand.xface.util;

import java.math.BigInteger;

public class NumberUtil {

	public static long bigIntegerToLong(BigInteger value, long defaultValue) {
		try {
			return value.longValue();
		}catch(Exception ex) {
			return defaultValue;
		}
	}
	
	
}
