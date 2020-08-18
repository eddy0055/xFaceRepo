package com.xpand.xface.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
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
	public static Date getTimeRoundDownToPortion(int minutePortion, Date date) {
		LocalDateTime now =  date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	    int currentMinutes = now.get(ChronoField.MINUTE_OF_HOUR);
	    int correctionToLowestQuarterHour = currentMinutes % minutePortion;
	    LocalDateTime quantizedDate = now.withMinute(currentMinutes - correctionToLowestQuarterHour).withSecond(0).withNano(0);
	    ZonedDateTime zdt = quantizedDate.atZone(ZoneId.systemDefault());	    
	    return (Date.from(zdt.toInstant()));
	}
	
	public static Date addDate(long date, int noOfAddDate) {
		//86400000 = 24*60*60*1000
		date = date + (noOfAddDate * 86400000);
		return new Date(date);
	}
}
