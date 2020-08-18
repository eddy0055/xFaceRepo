package com.xpand.xface.util;

import java.net.InetAddress;

import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;

public class NetworkUtil {
	public static boolean checkVIP() {
		return true;
	}
	public static String getLocalIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		}catch (Exception ex) {
			return "Get IP Fail";
		}
		
	}
	public static void closeNioDatagramAcceptorSession(NioDatagramAcceptor acceptor) {
		try {
			if (acceptor!=null) {
				acceptor.unbind();
			}
		}catch(Exception ex) {}
	}
}
