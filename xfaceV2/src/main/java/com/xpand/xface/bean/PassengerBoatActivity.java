package com.xpand.xface.bean;

public class PassengerBoatActivity {
	//direction (in, out), timestamp (yyyymmddhh24miss), gate id (pad id), boat driver card id, system id (fixed to ZKBioSecurity)
	//,passenger live photo (base64), passenger certificate id (thaiid, passort id)
	public static final byte ACTIVITY_BOAT_CHECK_IN_OUT = -99;
	public static final byte ACTIVITY_PASSENGER_PASS_GATE = -98;
	public static final String ACTIVITY_BOAT_DIRECTION_IN = "A";
	public static final String ACTIVITY_BOAT_DIRECTION_OUT = "D";
	private String transactionId;
	private String direction;
	private String eventDT;
	private String gateId;
	private String boatDriverCardId;
	private String systemId;
	private String passengerLivePhoto;
	private String passengerCertId;
	private byte activityType;
	public PassengerBoatActivity() {				
		
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getEventDT() {
		return eventDT;
	}
	public void setEventDT(String eventDT) {
		this.eventDT = eventDT;
	}
	public String getGateId() {
		return gateId;
	}
	public void setGateId(String gateId) {
		this.gateId = gateId;
	}
	public String getBoatDriverCardId() {
		return boatDriverCardId;
	}
	public void setBoatDriverCardId(String boatDriverCardId) {
		this.boatDriverCardId = boatDriverCardId;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getPassengerLivePhoto() {
		return passengerLivePhoto;
	}
	public void setPassengerLivePhoto(String passengerLivePhoto) {
		this.passengerLivePhoto = passengerLivePhoto;
	}
	public String getPassengerCertId() {
		return passengerCertId;
	}
	public void setPassengerCertId(String passengerCertId) {
		this.passengerCertId = passengerCertId;
	}
	public byte getActivityType() {
		return activityType;
	}
	public void setActivityType(byte activityType) {
		this.activityType = activityType;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	@Override
	public String toString() {
		return "PassengerBoatActivity [transactionId=" + transactionId + ", direction=" + direction + ", eventDT="
				+ eventDT + ", gateId=" + gateId + ", boatDriverCardId=" + boatDriverCardId + ", systemId=" + systemId
				+ ", passengerLivePhoto=" + passengerLivePhoto + ", passengerCertId=" + passengerCertId
				+ ", activityType=" + activityType + "]";
	}

}
