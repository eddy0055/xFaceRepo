package com.xpand.xface.service;

import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.BoatSchedule;
import com.xpand.xface.entity.HWGateInfo;

public interface BoatScheduleService extends CacheManageService{
	public BoatSchedule getPendingDeparture(String transactionId, Boat boat, HWGateInfo hwGateInfo);
	
}
