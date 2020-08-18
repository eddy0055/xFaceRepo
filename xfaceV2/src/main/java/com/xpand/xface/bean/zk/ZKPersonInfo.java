package com.xpand.xface.bean.zk;

public class ZKPersonInfo {
	private int code;
	private String message;
	private ZKPerson data;
	public ZKPersonInfo() {
		
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ZKPerson getData() {
		return data;
	}
	public void setData(ZKPerson data) {
		this.data = data;
	}

}
