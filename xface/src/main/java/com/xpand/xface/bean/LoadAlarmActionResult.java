package com.xpand.xface.bean;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

public class LoadAlarmActionResult {
	private ResultStatus result;
	private URLClassLoader urlClassLoader;
//	private AlarmActionBase alarmAction;
	private Method alarmActionMethod;
	private Object alarmActionInstance;
	private int timeNotSendActionAfterSendPerviouseAction;
	private String threadName;
	private String className;
	private String actionName;
	public LoadAlarmActionResult() {
		this.result = new ResultStatus();
	}
	public LoadAlarmActionResult(URLClassLoader urlClassLoader, Method alarmActionMethod, Object alarmActionInstance) {
		this.urlClassLoader = urlClassLoader;
		this.alarmActionMethod = alarmActionMethod;
		this.alarmActionInstance = alarmActionInstance;
		this.result = new ResultStatus();
	}
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public URLClassLoader getUrlClassLoader() {
		return urlClassLoader;
	}
	public void setUrlClassLoader(URLClassLoader urlClassLoader) {
		this.urlClassLoader = urlClassLoader;
	}	
	public int getTimeNotSendActionAfterSendPerviouseAction() {
		return timeNotSendActionAfterSendPerviouseAction;
	}
	public void setTimeNotSendActionAfterSendPerviouseAction(int timeNotSendActionAfterSendPerviouseAction) {
		this.timeNotSendActionAfterSendPerviouseAction = timeNotSendActionAfterSendPerviouseAction;
	}
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public Method getAlarmActionMethod() {
		return alarmActionMethod;
	}
	public void setAlarmActionMethod(Method alarmActionMethod) {
		this.alarmActionMethod = alarmActionMethod;
	}
	public Object getAlarmActionInstance() {
		return alarmActionInstance;
	}
	public void setAlarmActionInstance(Object alarmActionInstance) {
		this.alarmActionInstance = alarmActionInstance;
	}
}
