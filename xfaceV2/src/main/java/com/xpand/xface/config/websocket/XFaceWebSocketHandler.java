package com.xpand.xface.config.websocket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.util.LogUtil;

@Component
public class XFaceWebSocketHandler{	

//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;
    
    @Autowired
    GlobalVarService globalVarService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//    	StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
//    	String userName = event.getUser().getName();
//    	String transactionId = null;
//    	if (headers==null) {
//    		transactionId = UUID.randomUUID().toString();
//    		Logger.info(this, LogUtil.getLogInfo(transactionId, "[UUID]start establish websocket connection with user "+userName));
//    	}else {
//    		transactionId = headers.getSessionId();    		
//    		Logger.info(this, LogUtil.getLogInfo(transactionId, "[sessionId]start establish websocket connection with user "+userName));
//    		 Iterator it = headers.getSessionAttributes().entrySet().iterator();
//    		 while(it.hasNext()) {
//    			 Map.Entry pair = (Map.Entry)it.next();    		     
//    		     Logger.info(this, LogUtil.getLogInfo(transactionId, "header:"+pair.getKey() + " = " + pair.getValue()));
//    		 }
//    	}    	 		
//		try {			
//			this.messagingTemplate.convertAndSend(ConstUtil.MQ_TOPIC_USER_TO_SERVER+userName, ConstUtil.MQ_WELCOME_MSG_FROM_SERVER);
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "sent message to user "+userName+" success"));
//			WebSocketHolder prevSession = this.globalVarService.getWebSocketHolder(userName); 
//			if (prevSession==null){			
//				this.globalVarService.addWebSocketHolder(new WebSocketHolder(transactionId, userName));		
//				Logger.info(this, LogUtil.getLogInfo(transactionId, "establish websocket connection with user "+userName));
//			}else {
//				Logger.info(this, LogUtil.getLogInfo(prevSession.getTransactionId(), "prev session of user is "+userName+" is still alive ["+transactionId+"]"));							
//				this.globalVarService.removeWebSocketHolder(userName);				
//			}			
//		}catch (Exception ex) {			
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while handle web socket connection", ex));
//		}
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {    	
        String userNameNSessionId = event.getUser().getName()+event.getSessionId();		
		WebSocketHolder wsHolder = this.globalVarService.getWebSocketHolder(userNameNSessionId);		
		if (wsHolder==null) {
			Logger.info(this, LogUtil.getLogInfo(event.getSessionId(), "cannot find socket holder for user:"+userNameNSessionId+" when disconnect from web socket"));
		}else {
			Logger.info(this, LogUtil.getLogInfo(wsHolder.getSessionId(), "session "+userNameNSessionId+" disconnect"));
			this.globalVarService.removeWebSocketHolder(userNameNSessionId);
		}
    }
	
	
//	private final GlobalVarService globalVarService;	
//			
//	
//	@Autowired	
//	public XFaceWebSocketHandler(@Value("#{globalVarService}") GlobalVarService globalVar) {
//		this.globalVarService = globalVar;		
//	}	
//	@Override
//	public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
//		Logger.debug(this, LogUtil.getLogDebug(session.getPrincipal().getName()+"_"+session.getId(), "recv msg "+message.getPayload()));
//	}
//
//	@Override
//	public void afterConnectionEstablished(WebSocketSession session) {		
//		String transactionId = UUID.randomUUID().toString();		
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "start establish websocket connection with user "+session.getPrincipal().getName()));
//		try {
//			session.sendMessage(new TextMessage("hello from server"));
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "sent message to user "+session.getPrincipal().getName()+" success"));
//			WebSocketHolder prevSession = this.globalVarService.getWebSocketHolder(session.getPrincipal().getName()); 
//			if (prevSession!=null){			
//				Logger.info(this, LogUtil.getLogInfo(prevSession.getTransactionId(), "prev session of user is "+session.getPrincipal().getName()+" is still alive ["+transactionId+"]"));			
//				prevSession.closeSocket();							
//				this.globalVarService.removeWebSocketHolder(session.getPrincipal().getName());				
//				Logger.info(this, LogUtil.getLogInfo(prevSession.getTransactionId(), "remove socket of previouse logon user["+session.getPrincipal().getName()+","+transactionId+"]"));
//			}		
//			this.globalVarService.addWebSocketHolder(new WebSocketHolder(UUID.randomUUID().toString(), session, session.getPrincipal().getName()));		
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "establish websocket connection with user "+session.getPrincipal().getName()));
//		}catch (Exception ex) {
//			ex.printStackTrace();
//		}		
//	}
//
//	@Override
//	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {		
//		Logger.info(this, LogUtil.getLogInfo("xxxxxxxxx", "connection close for user "+session.getPrincipal().getName()));
//		WebSocketHolder wsHolder = this.globalVarService.getWebSocketHolder(session.getPrincipal().getName());
//		if (wsHolder!=null) {
//			Logger.info(this, LogUtil.getLogInfo(wsHolder.getTransactionId(), "session "+wsHolder.getWebSocket().getPrincipal().getName()+" disconnect"));
//			this.globalVarService.removeWebSocketHolder(session.getPrincipal().getName());
//		}		
//	}
}
