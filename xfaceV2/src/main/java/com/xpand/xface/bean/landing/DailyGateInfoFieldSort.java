package com.xpand.xface.bean.landing;

import java.util.Comparator;

public class DailyGateInfoFieldSort implements Comparator<DailyGateInfoField>{

	@Override
	public int compare(DailyGateInfoField o1, DailyGateInfoField o2) {		
		return o1.getGateCode().compareTo(o2.getGateCode());
	}

}
