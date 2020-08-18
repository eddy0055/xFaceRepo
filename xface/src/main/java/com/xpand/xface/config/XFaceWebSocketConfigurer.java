package com.xpand.xface.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.impl.GlobalVarServiceImpl;

@Configuration
@EnableWebSocket
public class XFaceWebSocketConfigurer implements WebSocketConfigurer {

	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(this.getXFaceWebSocketHandler(), "/VCMAlarm");
	}
	@Bean
	public WebSocketHandler getXFaceWebSocketHandler() {
		return new XFaceWebSocketHandler(globalVarService());
	}
	@Bean
	public GlobalVarService globalVarService() {
		return new GlobalVarServiceImpl();
	}

//	@Bean // bean for http session listener
//	public HttpSessionListener httpSessionListener() {
//		return new HttpSessionListener() {			
//			@Override
//			public void sessionDestroyed(HttpSessionEvent se) {
//				// TODO Auto-generated method stub
//				System.out.println("Session Destroyed, Session id:" + se.getSession().getId());
//			}			
//			@Override
//			public void sessionCreated(HttpSessionEvent se) {
//				// TODO Auto-generated method stub
//				System.out.println("Session Created with session id+" + se.getSession().getId());
//			}
//		};											
//	}
}