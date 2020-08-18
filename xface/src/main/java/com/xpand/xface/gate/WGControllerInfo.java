package com.xpand.xface.gate;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.core.session.IoSession;

public class WGControllerInfo {
	private int controllerSN;
	private String ipAddress ="";
	private int portNo = 60000;
	private long updateDateTime = System.currentTimeMillis() ;
	private byte[] receivedBytes = null;
	private  Queue<byte[]> queueOfReply= new LinkedList<byte[]>();
	private IoSession ioSession = null;
	private long recordIndex4WatchingRemoteOpen = 0;
    
    public void update(int sn, String ip, int port,  byte[] recv)
    {
    	this.controllerSN =(int) sn;
        this.ipAddress = ip;
        this.portNo = port;
        this.updateDateTime = System.currentTimeMillis() ; 
        this.receivedBytes = recv;
    }
    
    public void update(int sn, IoSession sess,  byte[] recv)
    {
        this.controllerSN =(int) sn;
        this.ioSession = sess;
        this.updateDateTime = System.currentTimeMillis() ; 
        this.receivedBytes = recv; 
        if (WGUdpCommShort4Cloud.isValidCommandReply(recv)){
        	if (this.queueOfReply == null){
        		this.queueOfReply = new LinkedList<byte[]>();
        	}
	        if (this.queueOfReply.size() >10000){
	        	synchronized(this.queueOfReply){
	        		this.queueOfReply.clear();
	    		}
	        }      
	        synchronized (this.queueOfReply){
	        	this.queueOfReply.offer(recv); 
			}
        }
    }

	public int getControllerSN() {
		return controllerSN;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getPortNo() {
		return portNo;
	}

	public long getUpdateDateTime() {
		return updateDateTime;
	}

	public byte[] getReceivedBytes() {
		return receivedBytes;
	}

	public Queue<byte[]> getQueueOfReply() {
		return queueOfReply;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public long getRecordIndex4WatchingRemoteOpen() {
		return recordIndex4WatchingRemoteOpen;
	}                
	public void setRecordIndex4WatchingRemoteOpen(long value) {
		this.recordIndex4WatchingRemoteOpen = value;
	}
}
