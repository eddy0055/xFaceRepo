package com.xpand.xface.service.impl;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import com.xpand.xface.dao.BoatScheduleDAO;
import com.xpand.xface.entity.Boat;
import com.xpand.xface.entity.BoatSchedule;
import com.xpand.xface.entity.HWGateInfo;
import com.xpand.xface.service.BoatScheduleService;




@SessionScope
@Component
public class BoatScheduleServiceImpl implements BoatScheduleService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
	BoatScheduleDAO boatScheduleDAO;
	@Override
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void purgeCache() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public BoatSchedule getPendingDeparture(String transactionId, Boat boat, HWGateInfo hwGateInfo) {		
		return this.boatScheduleDAO.findPendingDeparture(boat, hwGateInfo);
	} 	
	
	
	
}
