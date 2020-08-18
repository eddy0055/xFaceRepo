package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xpand.xface.dao.HWVCMDAO;
import com.xpand.xface.entity.HWVCM;
import com.xpand.xface.service.HWVCMService;

@Component
public class HWVCMServiceImpl implements HWVCMService {

	@Autowired
	HWVCMDAO hwVCMDAO;
	 
	@Override
	public HWVCM findByVcmId(Integer vcmId) {
		return this.hwVCMDAO.findOne(vcmId);
	}

	@Override
	public List<HWVCM> getAll() {
		return this.hwVCMDAO.findAll();
	}

}
