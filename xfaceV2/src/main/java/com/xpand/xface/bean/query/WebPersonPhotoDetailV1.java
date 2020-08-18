package com.xpand.xface.bean.query;

import java.util.Date;

import com.xpand.xface.util.StringUtil;

public class WebPersonPhotoDetailV1 {
	private Date alarmDateTime;
	private String alarmDateTimeDSP;
	private String livePhoto;
	public WebPersonPhotoDetailV1(Date alarmDateTime, String livePhoto) {
		this.alarmDateTime = alarmDateTime;
		this.livePhoto = livePhoto;
		this.alarmDateTimeDSP = StringUtil.dateToString(this.alarmDateTime, StringUtil.DATE_FORMAT_DD_MM_YYYY_HH_MM_SS);
	}
	public Date getAlarmDateTime() {
		return alarmDateTime;
	}
	public void setAlarmDateTime(Date alarmDateTime) {
		this.alarmDateTime = alarmDateTime;
	}
	public String getLivePhoto() {
		return livePhoto;
	}
	public void setLivePhoto(String livePhoto) {
		this.livePhoto = livePhoto;
	}
	public String getAlarmDateTimeDSP() {
		return alarmDateTimeDSP;
	}
	public void setAlarmDateTimeDSP(String alarmDateTimeDSP) {
		this.alarmDateTimeDSP = alarmDateTimeDSP;
	}

}
