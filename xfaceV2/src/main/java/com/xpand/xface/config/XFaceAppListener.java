package com.xpand.xface.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class XFaceAppListener implements ServletContextListener{	
//	XFaceServerServiceImpl xFaceServer;	
//	String transactionId = null;
	@Override
    public void contextInitialized(ServletContextEvent sce) {
//		this.transactionId = UUID.randomUUID().toString();		
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "starting XFace server"));
//		this.xFaceServer = new XFaceServer(this.transactionId);
//		this.xFaceServer.start();
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "XFace server started"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {    	
//    	Logger.info(this, LogUtil.getLogInfo(transactionId, "stoping XFace server"));
//    	this.xFaceServer.stopService();
//    	while (!this.xFaceServer.isTerminate()) {    		
//    		Logger.debug(this, LogUtil.getLogDebug(transactionId, "wait for stop"));
//    		OtherUtil.waitMilliSecond(1000);
//    	}    	
//    	Logger.info(this, LogUtil.getLogInfo(transactionId, "XFace success stop"));
   }
}
