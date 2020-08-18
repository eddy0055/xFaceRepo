package com.xpand.xface.util;

import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

import com.xpand.xface.entity.UserInfo;

public class LogUtil {
	public static String getLogInfo(String transactionId, String message) {
		return transactionId +"|"+message;
	}
	public static String getLogInfo(String transactionId, String module ,String message) {
		return transactionId +"|"+module+"|"+message;
	}
	public static String getLogStart(String transactionId, String module ,String message) {
		return transactionId +"|start|"+module+"|"+message;
	}
	public static String getLogStop(String transactionId, String module ,String message, Date startDate) {			
		return transactionId +"|stop|"+module+"|"+message+"|"+(((new Date()).getTime()-startDate.getTime())/1000)+"s";
	}
	public static String getLogDebug(String transactionId, String message) {
		return transactionId +"|"+message;
	}
	public static String getLogError(String transactionId, String message, Throwable error) {
		if (error==null) {
			return transactionId +"|"+message;
		}else {
			return transactionId +"|"+message+"["+error.toString()+"]"+"\n"+LogUtil.stackTraceToString(transactionId, error);
		}		
	}
	public static String getLogError(String transactionId, String module, String message, Throwable error) {
		if (error==null) {
			return transactionId +"|"+module +"|"+message;
		}else {
			return transactionId +"|"+module+"|"+message+"["+error.toString()+"]"+"\n"+LogUtil.stackTraceToString(transactionId, error);
		}		
	}
	
	public static String stackTraceToString(String transactionId, Throwable e) {
	    StringBuilder sb = new StringBuilder();
	    String oneline = null;
	    for (StackTraceElement element : e.getStackTrace()) {
	    	oneline = element.toString();
	    	//if (oneline.contains("com.xpand")){
	    		sb.append(transactionId + "|" + oneline);
		        sb.append("\n");
	    	//}	        
	    }
	    return sb.toString();
	}
	public static String getWebSessionId() {
		return RequestContextHolder.currentRequestAttributes().getSessionId();
	}
	public static String getCurrentLogOnUserName() {		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getName()==null||"anonymousUser".equals(auth.getName())) {
			return UserInfo.DEFAULT_LOGON_USERNAME;
		}else {
			return auth.getName();
		}		
	}
}
