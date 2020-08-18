package com.xpand.xface.util;

import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
	public static int getDayFromDate(Date date, int defaultValue) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);		
	}
	public static int getDayFromDate(long date, int defaultValue) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(date));
		return c.get(Calendar.DAY_OF_MONTH);		
	}
	public static Date epochToDate(long epochTime) {
		return new Date(epochTime);
	}
	public static Date epochToDate(String epochTime, long defaultValue) {
		return DateTimeUtil.epochToDate(StringUtil.stringToLong(epochTime, defaultValue));
	}
}
