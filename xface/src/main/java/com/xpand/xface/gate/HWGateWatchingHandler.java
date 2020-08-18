package com.xpand.xface.gate;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

    
/**
 * Class the extends IoHandlerAdapter in order to properly handle
 * connections and the data the connections send
 *
 * @author <a href="http://mina.apache.org" mce_href="http://mina.apache.org">Apache MINA Project</a>
 */
public class HWGateWatchingHandler extends IoHandlerAdapter {
	private Queue<byte[]> dataQueue = null;
	private Queue<byte[]> udpQueue4Mobile = null;
	private Queue<IoSession> remoteEndPointQueue4Mobile = null;
	private Queue<byte[]> udpQueue4RemoteOpenDoor = null;  
	private Queue<IoSession> remoteEndPointQueue4RemoteOpenDoor = null;
	public ArrayList<Integer> arrSNReceived = null;
    public ArrayList<Integer> arrRecordIndex = null;
    public ArrayList<WGControllerInfo> arrControllerInfo = null; 
    public Queue<byte[]> queueApp = null;
	
	public HWGateWatchingHandler() {
		super();    	
		this.dataQueue = new LinkedList<byte[]>();
		this.udpQueue4Mobile = new LinkedList<byte[]>();
		this.remoteEndPointQueue4Mobile = new LinkedList<IoSession>();
		this.udpQueue4RemoteOpenDoor=new LinkedList<byte[]>();  
		this.remoteEndPointQueue4RemoteOpenDoor= new LinkedList<IoSession>();
		
		this.arrSNReceived = new ArrayList<Integer>();
		this.arrRecordIndex = new ArrayList<Integer>();
		this.arrControllerInfo = new ArrayList<WGControllerInfo>();  
		this.queueApp= new LinkedList<byte[]>(); 
  	}
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        session.closeOnFlush();
    }      
    public boolean isConnected(long controllerSN){
    	int iget = this.arrSNReceived.indexOf((int) controllerSN);
	   	if (iget >= 0) {
	   		if (this.arrControllerInfo.size()>=iget){
	   			WGControllerInfo info = this.arrControllerInfo.get(iget);
	   			if (info != null){
	   				if ((info.getUpdateDateTime() + 5*60*1000) > System.currentTimeMillis()){
	   				   return true;
	   				}
	   			}
	   		}
	   	}
    	return false;
    }           
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
		IoBuffer io = (IoBuffer) message;
		if (io.hasRemaining()){
			byte[] validBytes = new byte[io.remaining()];
			io.get(validBytes,0,io.remaining());
        	if (((validBytes.length == WGUdpCommShort4Cloud.WGPacketSize) || ((validBytes.length % WGUdpCommShort4Cloud.WGPacketSize) ==0))
        			&&(validBytes.length>0) && ((validBytes[0] == WGUdpCommShort4Cloud.Type)||(validBytes[0] == 0x37))){
        		if ((validBytes.length == 64) && ((validBytes[0] == 0x37)) && ((validBytes[1]) == 0x40)){
        			//UDPQueue4Mobile
     				synchronized (this.udpQueue4Mobile){
     					this.udpQueue4Mobile.offer(validBytes);
                        this.remoteEndPointQueue4Mobile.offer(session);
                    }
     				return;
        		}else if ((validBytes.length == 64) && ((validBytes[0] == 0x17)) && ((validBytes[1]) == 0x40) 
        				&& ((validBytes[43] & 0xf0)==0x70)){
        			synchronized (this.udpQueue4RemoteOpenDoor){
        				this.udpQueue4RemoteOpenDoor.offer(validBytes);
                        this.remoteEndPointQueue4RemoteOpenDoor.offer(session);
        			}
     			    return;
        		}
   				synchronized (this.dataQueue){
   					this.dataQueue.offer(validBytes);
   				}
    			long sn = WGUdpCommShort4Cloud.getLongByByte(validBytes, 4, 4);
    			int iget = this.arrSNReceived.indexOf((int)sn);
		        if (iget < 0){
		        	if (WGUdpCommShort4Cloud.getControllerType(sn) > 0){
		        		this.arrSNReceived.add((int) sn);
		                long recordIndexGet = WGUdpCommShort4Cloud.getLongByByte(validBytes, 8, 4);
						this.arrRecordIndex.add((int) recordIndexGet);                         				   	
		                WGControllerInfo con = new WGControllerInfo();	
		                con.update((int)sn, session,  validBytes);
		                this.arrControllerInfo.add(con);
		            }
		        }else{
	    		    this.arrControllerInfo.get(iget).update((int)sn, session, validBytes);
		        }
        	}else{
    		}    		
		}
    }
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

	public Queue<byte[]> getDataQueue() {
		return dataQueue;
	}

	public Queue<byte[]> getUdpQueue4Mobile() {
		return udpQueue4Mobile;
	}

	public Queue<IoSession> getRemoteEndPointQueue4Mobile() {
		return remoteEndPointQueue4Mobile;
	}

	public Queue<byte[]> getUdpQueue4RemoteOpenDoor() {
		return udpQueue4RemoteOpenDoor;
	}

	public Queue<IoSession> getRemoteEndPointQueue4RemoteOpenDoor() {
		return remoteEndPointQueue4RemoteOpenDoor;
	}

	public ArrayList<Integer> getArrSNReceived() {
		return arrSNReceived;
	}

	public ArrayList<Integer> getArrRecordIndex() {
		return arrRecordIndex;
	}

	public ArrayList<WGControllerInfo> getArrControllerInfo() {
		return arrControllerInfo;
	}

	public Queue<byte[]> getQueueApp() {
		return queueApp;
	}
}
