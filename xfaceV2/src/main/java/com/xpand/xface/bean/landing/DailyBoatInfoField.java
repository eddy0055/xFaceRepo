package com.xpand.xface.bean.landing;

import com.xpand.xface.util.StringUtil;

public class DailyBoatInfoField {

	private String boatCode;
	private String boatName;
	private String arrivalTime;	
	private String departureTime;
	private Integer noOfIN;
	private Integer noOfOUT;
	private Integer noOfPassenger;
	private Integer direction;
	private Integer gateId;
	
	private String mapCode;
	private String boatLocationX;
	private String boatLocationY;
	private String boatNameColor;
	private String boatIconWidth;
	private String boatIconHeight;
	private String boatNameLocationX;
	private String boatNameLocationY;
	private String boatIconTransformX;
	private String boatIconTransformY;
	private String boatNameSize;	
	
	public DailyBoatInfoField(Object[] columns) {
		this.boatCode = ""+columns[0];
		this.boatName = ""+columns[1];		
		this.arrivalTime = ""+columns[2];
		this.departureTime = ""+columns[3];		
		this.direction = StringUtil.stringToInteger(""+columns[4], 0);
		this.gateId = StringUtil.stringToInteger(""+columns[5], 0);
		this.noOfPassenger = StringUtil.stringToInteger(""+columns[6], 0);
	}
	public String getBoatCode() {
		return boatCode;
	}
	public void setBoatCode(String boatCode) {
		this.boatCode = boatCode;
	}
	public String getBoatName() {
		return boatName;
	}
	public void setBoatName(String boatName) {
		this.boatName = boatName;
	}		
	public String getArrivalTime() {
		return arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	public String getDepartureTime() {
		return departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}	
	public Integer getDirection() {
		return direction;
	}
	public void setDirection(Integer direction) {
		this.direction = direction;
	}
	public Integer getNoOfIN() {
		return noOfIN;
	}
	public void setNoOfIN(Integer noOfIN) {
		this.noOfIN = noOfIN;
	}
	public Integer getNoOfOUT() {
		return noOfOUT;
	}
	public void setNoOfOUT(Integer noOfOUT) {
		this.noOfOUT = noOfOUT;
	}
	public Integer getNoOfPassenger() {
		return noOfPassenger;
	}
	public void setNoOfPassenger(Integer noOfPassenger) {
		this.noOfPassenger = noOfPassenger;
	}
	public Integer getGateId() {
		return gateId;
	}
	public void setGateId(Integer gateId) {
		this.gateId = gateId;
	}
	public String getBoatLocationX() {
		return boatLocationX;
	}
	public void setBoatLocationX(String boatLocationX) {
		this.boatLocationX = boatLocationX;
	}
	public String getBoatLocationY() {
		return boatLocationY;
	}
	public void setBoatLocationY(String boatLocationY) {
		this.boatLocationY = boatLocationY;
	}
	public String getBoatNameColor() {
		return boatNameColor;
	}
	public void setBoatNameColor(String boatNameColor) {
		this.boatNameColor = boatNameColor;
	}
	public String getBoatIconWidth() {
		return boatIconWidth;
	}
	public void setBoatIconWidth(String boatIconWidth) {
		this.boatIconWidth = boatIconWidth;
	}
	public String getBoatIconHeight() {
		return boatIconHeight;
	}
	public void setBoatIconHeight(String boatIconHeight) {
		this.boatIconHeight = boatIconHeight;
	}
	public String getBoatNameLocationX() {
		return boatNameLocationX;
	}
	public void setBoatNameLocationX(String boatNameLocationX) {
		this.boatNameLocationX = boatNameLocationX;
	}
	public String getBoatNameLocationY() {
		return boatNameLocationY;
	}
	public void setBoatNameLocationY(String boatNameLocationY) {
		this.boatNameLocationY = boatNameLocationY;
	}
	public String getBoatIconTransformX() {
		return boatIconTransformX;
	}
	public void setBoatIconTransformX(String boatIconTransformX) {
		this.boatIconTransformX = boatIconTransformX;
	}
	public String getBoatIconTransformY() {
		return boatIconTransformY;
	}
	public void setBoatIconTransformY(String boatIconTransformY) {
		this.boatIconTransformY = boatIconTransformY;
	}
	public String getBoatNameSize() {
		return boatNameSize;
	}
	public void setBoatNameSize(String boatNameSize) {
		this.boatNameSize = boatNameSize;
	}
	public String getMapCode() {
		return mapCode;
	}
	public void setMapCode(String mapCode) {
		this.mapCode = mapCode;
	}
}
