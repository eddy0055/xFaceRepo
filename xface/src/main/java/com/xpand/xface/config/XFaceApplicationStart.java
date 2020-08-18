package com.xpand.xface.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.service.XFaceServerService;
import com.xpand.xface.util.LogUtil;

@Component
public class XFaceApplicationStart implements ApplicationListener<ContextRefreshedEvent>{

	@Autowired
	XFaceServerService xFaceServerService;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {	
		Logger.info(this, LogUtil.getLogInfo(this.xFaceServerService.getTransactionId(), "web server start"));
		this.xFaceServerService.start();
		Logger.info(this, LogUtil.getLogInfo(this.xFaceServerService.getTransactionId(), "web server start successful"));					
	}

}
