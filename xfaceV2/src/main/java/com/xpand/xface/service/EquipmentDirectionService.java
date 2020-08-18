package com.xpand.xface.service;

import com.xpand.xface.entity.EquipmentDirection;

public interface EquipmentDirectionService extends CacheManageService{
	public EquipmentDirection findById(String transactionId, Integer gdirectionId);	
	public EquipmentDirection findByDirectionCode(String transactionId, Integer directionCode);
	public EquipmentDirection findByDirectionDesc(String transactionId, String directionDesc);
}
