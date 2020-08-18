package com.xpand.xface.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.service.XFaceServerService;
import com.xpand.xface.util.LogUtil;

@Component
public class XFaceApplicationStop implements ApplicationListener<ContextClosedEvent>{

	@Autowired
	XFaceServerService xFaceServerService;	
	
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		Logger.info(this, LogUtil.getLogInfo(this.xFaceServerService.getTransactionId(), "get event stop web server"));
		this.xFaceServerService.stop();    	    
    	Logger.info(this, LogUtil.getLogInfo(this.xFaceServerService.getTransactionId(), "web server stop successful"));
	}
}
