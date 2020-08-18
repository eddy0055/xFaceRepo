package com.xpand.xface.config;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.WebSocketHolder;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.util.LogUtil;

@Component
public class XFaceWebSocketHandler extends TextWebSocketHandler implements WebSocketHandler {		
	private final GlobalVarService globalVarService;	
			
	
	@Autowired	
	public XFaceWebSocketHandler(@Value("#{globalVarService}") GlobalVarService globalVar) {
		this.globalVarService = globalVar;		
	}	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
		Logger.debug(this, LogUtil.getLogDebug(session.getPrincipal().getName()+"_"+session.getId(), "recv msg "+message.getPayload()));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {		
		String transactionId = UUID.randomUUID().toString();		
		Logger.info(this, LogUtil.getLogInfo(transactionId, "start establish websocket connection with user "+session.getPrincipal().getName()));
		try {
			session.sendMessage(new TextMessage("hello from server"));
			Logger.info(this, LogUtil.getLogInfo(transactionId, "sent message to user "+session.getPrincipal().getName()+" success"));
			WebSocketHolder prevSession = this.globalVarService.getWebSocketHolder(session.getPrincipal().getName()); 
			if (prevSession!=null){			
				Logger.info(this, LogUtil.getLogInfo(prevSession.getTransactionId(), "prev session of user is "+session.getPrincipal().getName()+" is still alive ["+transactionId+"]"));			
				prevSession.closeSocket();							
				this.globalVarService.removeWebSocketHolder(session.getPrincipal().getName());				
				Logger.info(this, LogUtil.getLogInfo(prevSession.getTransactionId(), "remove socket of previouse logon user["+session.getPrincipal().getName()+","+transactionId+"]"));
			}		
			this.globalVarService.addWebSocketHolder(new WebSocketHolder(UUID.randomUUID().toString(), session, session.getPrincipal().getName()));		
			Logger.info(this, LogUtil.getLogInfo(transactionId, "establish websocket connection with user "+session.getPrincipal().getName()));
		}catch (Exception ex) {
			ex.printStackTrace();
		}		
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {		
		Logger.info(this, LogUtil.getLogInfo("xxxxxxxxx", "connection close for user "+session.getPrincipal().getName()));
		WebSocketHolder wsHolder = this.globalVarService.getWebSocketHolder(session.getPrincipal().getName());
		if (wsHolder!=null) {
			Logger.info(this, LogUtil.getLogInfo(wsHolder.getTransactionId(), "session "+wsHolder.getWebSocket().getPrincipal().getName()+" disconnect"));
			this.globalVarService.removeWebSocketHolder(session.getPrincipal().getName());
		}		
	}
}
