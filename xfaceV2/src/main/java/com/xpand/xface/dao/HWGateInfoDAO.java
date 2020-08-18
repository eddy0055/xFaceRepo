package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.HWGateInfo;

@Repository
public interface HWGateInfoDAO extends JpaRepository<HWGateInfo, Integer>{
	public HWGateInfo findByGateCode(String gateCode);
	public HWGateInfo findByGateName(String gateName);
	public HWGateInfo findByGateShortName(String gateShortName);
	public List<HWGateInfo> findByGateCodeIn(List<String> gateCodeList);
}
