package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.EquipmentDirection;

@Repository
public interface EquipmentDirectionDAO extends JpaRepository<EquipmentDirection, Integer>{
	public EquipmentDirection findByDirectionCode(Integer directionCode);
	public EquipmentDirection findByDirectionDesc(String directionDesc);
}
