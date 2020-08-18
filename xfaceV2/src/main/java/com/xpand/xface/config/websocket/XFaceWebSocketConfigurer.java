package com.xpand.xface.config.websocket;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.xpand.xface.util.ConstUtil;

@Configuration
@EnableWebSocketMessageBroker
//public class XFaceWebSocketConfigurer extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession> {
//	@Override
//    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
//        stompEndpointRegistry.addEndpoint(ConstUtil.WEBSOCKET_ENDPOINT)
//                .withSockJS();
//        
//    }
public class XFaceWebSocketConfigurer extends AbstractWebSocketMessageBrokerConfigurer {
//	@Override
//	protected void configureStompEndpoints(StompEndpointRegistry registry) {
//		registry.addEndpoint(ConstUtil.WEBSOCKET_ENDPOINT).withSockJS();
//	}
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
		stompEndpointRegistry.addEndpoint(ConstUtil.WEBSOCKET_ENDPOINT)
              .withSockJS();
	}

    @Override	
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(ConstUtil.MQ_TOPIC_SERVER_TO_USER);
        registry.setApplicationDestinationPrefixes(ConstUtil.MQ_TOPIC_USER_TO_SERVER);        
    }
	
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
    	//1mb, default is 64k in byte
    	registration.setMessageSizeLimit(1024*1024);
    	//in millisec
    	registration.setSendTimeLimit(20 * 1000);// default : 10 * 10000
        registration.setSendBufferSizeLimit(3* 1024 * 1024); // default : 512 * 1024        
    }
}


//@Configuration
//@EnableWebSocket
//public class XFaceWebSocketConfigurer implements WebSocketConfigurer {
//
//	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//		registry.addHandler(this.getXFaceWebSocketHandler(), "/VCMAlarm");
//	}
//	@Bean
//	public WebSocketHandler getXFaceWebSocketHandler() {
//		return new XFaceWebSocketHandler(globalVarService());
//	}
//	@Bean
//	public GlobalVarService globalVarService() {
//		return new GlobalVarServiceImpl();
//	}
//
////	@Bean // bean for http session listener
////	public HttpSessionListener httpSessionListener() {
////		return new HttpSessionListener() {			
////			@Override
////			public void sessionDestroyed(HttpSessionEvent se) {
////				// TODO Auto-generated method stub
////				System.out.println("Session Destroyed, Session id:" + se.getSession().getId());
////			}			
////			@Override
////			public void sessionCreated(HttpSessionEvent se) {
////				// TODO Auto-generated method stub
////				System.out.println("Session Created with session id+" + se.getSession().getId());
////			}
////		};											
////	}
//}