package com.xpand.xface.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.WebFEParam;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.util.LogUtil;

@Controller
public class MQWebSocketController {

	@Autowired
  	GlobalVarService globalVarService;
	@MessageMapping("/user/{userName}")
	//no need to response
    //@SendTo(ConstUtil.MQ_TOPIC_SERVER_TO_USER+"user/{userName}")
    public void webSocketConnect(@Header("simpSessionId") String sessionId, @DestinationVariable String userName, WebFEParam webParam) {				    	    	    	
		try {									
			WebSocketHolder prevSession = this.globalVarService.getWebSocketHolder(userName+sessionId); 
			if (prevSession==null){
				Logger.info(this, LogUtil.getLogInfo(sessionId, "[UUID]start establish websocket connection with user "+userName+sessionId));
				this.globalVarService.addWebSocketHolder(new WebSocketHolder(sessionId, userName, webParam));		
				Logger.info(this, LogUtil.getLogInfo(sessionId, "establish websocket connection with user "+userName+sessionId+" WebSocketModule:"+webParam.getWebSocketModule()));
			}else {
				Logger.info(this, LogUtil.getLogInfo(sessionId, "prev session of user is "+userName+sessionId+" is still alive WebSocketModule:"+webParam.getWebSocketModule()));
				prevSession.updateFilter(webParam);
				this.globalVarService.addWebSocketHolder(prevSession);				
			}			
		}catch (Exception ex) {			
			Logger.error(this, LogUtil.getLogError(sessionId, "error while handle web socket connection", ex));
		}        
		//return ConstUtil.MQ_WELCOME_MSG_FROM_SERVER;
    }
}
