package com.xpand.xface.bean;

import org.springframework.web.socket.WebSocketSession;

public class WebSocketHolder {
	private String transactionId = null;
	private WebSocketSession webSocket;
	private String userName;
	public WebSocketHolder(String transactionId, WebSocketSession webSocket, String userName) {
		this.transactionId = transactionId;
		this.webSocket = webSocket;
		this.userName = userName;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public WebSocketSession getWebSocket() {
		return webSocket;
	}
	public void setWebSocket(WebSocketSession webSocket) {
		this.webSocket = webSocket;
	}
	public void closeSocket() {
		try {
			if (this.webSocket!=null && this.webSocket.isOpen()) {
				this.webSocket.close();
			}
		}catch (Exception ex) {}		
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
