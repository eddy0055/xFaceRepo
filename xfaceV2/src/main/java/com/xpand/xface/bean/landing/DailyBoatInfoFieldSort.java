package com.xpand.xface.bean.landing;

import java.util.Comparator;

public class DailyBoatInfoFieldSort implements Comparator<DailyBoatInfoField>{

	@Override
	public int compare(DailyBoatInfoField o1, DailyBoatInfoField o2) {
		//step to compare
		//1. arrival time
		//2. departure time
		//3. boat code
		int compareResult = o1.getArrivalTime().compareTo(o2.getArrivalTime());
		if (compareResult==0) {
			compareResult = o1.getDepartureTime().compareTo(o2.getDepartureTime());
		}
		if (compareResult==0) {
			compareResult = o1.getBoatCode().compareTo(o2.getBoatCode());
		}
		return compareResult;
	}

	 

}
