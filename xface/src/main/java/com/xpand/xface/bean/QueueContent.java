package com.xpand.xface.bean;

public class QueueContent {
	public static final int CONTENTTYPE_HW_ALARM = 0;
	public static final int CONTENTTYPE_EMAIL_NOTITFICATION = 1;
	private int contentType;
	private String content;
	public QueueContent(int contentType, String content) {
		this.contentType = contentType;
		this.content = content;
	}
	public int getContentType() {
		return contentType;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
