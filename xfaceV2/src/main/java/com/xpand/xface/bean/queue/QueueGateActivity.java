package com.xpand.xface.bean.queue;

import com.xpand.xface.bean.PassengerBoatActivity;

public class QueueGateActivity {
	private PassengerBoatActivity passengerBoatActivity;
	public QueueGateActivity(PassengerBoatActivity passengerBoatActivity) {		
		this.passengerBoatActivity = passengerBoatActivity;
	}
	public PassengerBoatActivity getPassengerBoatActivity() {
		return passengerBoatActivity;
	}
	public void setPassengerBoatActivity(PassengerBoatActivity passengerBoatActivity) {
		this.passengerBoatActivity = passengerBoatActivity;
	}	
}
