package com.xpand.xface.service.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.springframework.beans.factory.annotation.Autowired;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.gate.HWGateDataField;
import com.xpand.xface.gate.HWGateWatchingHandler;
import com.xpand.xface.gate.WGControllerInfo;
import com.xpand.xface.gate.WGUdpCommShort4Cloud;
import com.xpand.xface.service.BaseXFaceThreadService;
import com.xpand.xface.service.HWGateAccessInfoService;
import com.xpand.xface.util.LogUtil;
import com.xpand.xface.util.NetworkUtil;
import com.xpand.xface.util.OtherUtil;

public class HWGateServiceImpl extends Thread implements BaseXFaceThreadService{
	@Autowired
	private HWGateAccessInfoService hwGateAccessInfoService;
	
	private String serverIP = "";
	private int serverPort = 61005;	
	private ArrayList<Long> arrXIDSend = new ArrayList<Long>();
	private ArrayList<Long> arrXIDReceivedMobile = new ArrayList<Long>();
	private ArrayList<IoSession>  arrXIDEndPointMobile = new ArrayList<IoSession>();
	private long dtXIDSendLast = System.currentTimeMillis() ; // DateTime.Now.Ticks;
	private String transactionId;
	private HWGateWatchingHandler gateWatchingHandler;
	private NioDatagramAcceptor acceptor;
	private boolean isTerminate = false;
	private boolean isLoop = true;
	
	public HWGateServiceImpl() {
		super("HWGateServiceImpl");
	}
	
	public void initClass(String transactionId, String serverIP, int serverPort) {		
		this.transactionId = transactionId;
		this.serverIP = serverIP;
		this.serverPort = serverPort;		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "start server:"+this.serverIP+" port:"+this.serverPort));
	}
	
	public ResultStatus performTask() {		
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "in perform task"));
		ResultStatus result = new ResultStatus();
        this.acceptor = new NioDatagramAcceptor();        
        this.gateWatchingHandler = new HWGateWatchingHandler(); 
        this.acceptor.setHandler(this.gateWatchingHandler);
        DatagramSessionConfig dcfg = this.acceptor.getSessionConfig();
        dcfg.setReuseAddress(true);
        try {        	
        	Logger.info(this, LogUtil.getLogInfo(this.transactionId, "bind server and port to recv data"));
			acceptor.bind(new InetSocketAddress(this.serverIP, this.serverPort));
			Logger.info(this, LogUtil.getLogInfo(this.transactionId, "bind server and port to recv data success"));
		} catch (Exception e) {
			Logger.error(this, LogUtil.getLogError(this.transactionId, "Bind address of gate server failed", e));
			result.setStatusCode(ResultStatus.START_GATE_ADDRESS_FAIL_ERROR_CODE, "Bind address of gate server failed");			
		}						
        if (result.getStatusCode()==ResultStatus.SUCCESS_CODE) {
        	Logger.info(this, LogUtil.getLogInfo(this.transactionId, "Watching server started"));
        }		
		return result;
	}
	
	@Override
	public void run() {
		long recordIndex = 0;
		boolean bDealt = false;
		byte[] recvBuff;
		long doorSN = 0;
		long recordIndexGet = 0;
		HWGateDataField data = null;
		int iget = 0;
		ResultStatus result = null;
		this.isLoop = true;
		this.isTerminate = false;
		while(this.isLoop){
			bDealt = false;
			if (!this.gateWatchingHandler.getDataQueue().isEmpty()){
				bDealt= true;				
				synchronized (this.gateWatchingHandler.getDataQueue()){
					recvBuff= this.gateWatchingHandler.getDataQueue().poll();
				}				
				if ((recvBuff[1]== 0x20)){
					doorSN = WGUdpCommShort4Cloud.getLongByByte(recvBuff, 4, 4);
					if (WGUdpCommShort4Cloud.getControllerType(doorSN) > 0){
						recordIndexGet = WGUdpCommShort4Cloud.getLongByByte(recvBuff, 8, 4);																	                          
						iget = this.gateWatchingHandler.getArrSNReceived().indexOf((int) doorSN);
						if (iget >= 0) {
							if (this.gateWatchingHandler.getArrControllerInfo().size()>=iget){
								recordIndex = (long) this.gateWatchingHandler.getArrRecordIndex().get(iget);
								this.gateWatchingHandler.getArrRecordIndex().set(iget,(int) recordIndexGet);
								WGControllerInfo info = this.gateWatchingHandler.getArrControllerInfo().get(iget);
								if (info != null){
									recordIndex = info.getRecordIndex4WatchingRemoteOpen();
									info.setRecordIndex4WatchingRemoteOpen(recordIndexGet);
									if ((recordIndex < recordIndexGet)||((recordIndexGet - recordIndex) < -5)){
										data = new HWGateDataField(recvBuff);
										if (this.hwGateAccessInfoService==null) {
											Logger.info(this, LogUtil.getLogInfo(this.transactionId, data.toString()));
										}else {
											result = this.hwGateAccessInfoService.update(this.transactionId,data.getHWGateAccessInfo());
											Logger.info(this, LogUtil.getLogInfo(this.transactionId, "insert data:"+data.toString()+", rseult is:"+result.toString()));
										}										
									} //if ((recordIndex < recordIndexGet)||((recordIndexGet - recordIndex) < -5)){
								} //if (info != null){
							} //if (this.gateWatchingHandler.getArrControllerInfo().size()>=iget){
						} //if (iget >= 0) {
					} //if (WGUdpCommShort4Cloud.getControllerType(doorSN) > 0){		
				} //if ((recvBuff[1]== 0x20)){
			}
			if (DealRuninfoPacketProc4Mobile()){
				bDealt = true;  
			}
			if (DealRuninfoPacketProc4RemoteOpenDoor()){
				bDealt = true;  
			}
			if(!bDealt){
				OtherUtil.waitMilliSecond(100);
			}	     
		} //while(this.isLoop){
		this.isTerminate = true;
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "gate service stop success"));
	}	
	private boolean DealRuninfoPacketProc4Mobile(){
		byte[] recv;
	    boolean ret = false;
	    try {
			if (!this.gateWatchingHandler.getUdpQueue4Mobile().isEmpty()){
				IoSession senderRemote;
				synchronized (this.gateWatchingHandler.getUdpQueue4Mobile()){
					recv = (byte[])this.gateWatchingHandler.getUdpQueue4Mobile().poll();
					senderRemote = (IoSession)this.gateWatchingHandler.getRemoteEndPointQueue4Mobile().poll();
				}
				if ((recv.length == 64) && ((recv[0] == 0x37))){
					long m_ControllerSN = WGUdpCommShort4Cloud.getLongByByte(recv, 4,4);
					int iloc = -1;
					if ((iloc = this.gateWatchingHandler.getArrSNReceived().indexOf((int)m_ControllerSN)) >= 0){
						recv[0] = 0x17; //2018-02-12 10:22:05 修改
						long oldxid = WGUdpCommShort4Cloud.getXidOfCommand(recv);
						long newxid = WGUdpCommShort4Cloud.getXid();	                         
						System.arraycopy(WGUdpCommShort4Cloud.longToByte(newxid) , 0, recv, 40, 4);
						synchronized (this.arrXIDSend){
							this.arrXIDSend.add(newxid);
							this.arrXIDReceivedMobile.add(oldxid);
							this.arrXIDEndPointMobile.add(senderRemote);
						}
						this.dtXIDSendLast = System.currentTimeMillis() + 2 * 60 * 1000 ; // DateTime.Now.Ticks + 2 * 60 * 1000 * 1000 * 10; //2015-06-22 13:16:32 2分钟 Date.Now.Ticks  'us
						this.udpOnlySend(recv, this.gateWatchingHandler.getArrControllerInfo().get(iloc).getIoSession()); //2018-02-12 17:02:35
					}
				}
				ret = true;
			}else if (!this.arrXIDSend.isEmpty()){
				if (this.dtXIDSendLast < System.currentTimeMillis() ){
					synchronized (this.arrXIDSend){
						this.arrXIDSend.clear();
						this.arrXIDReceivedMobile.clear();
						this.arrXIDEndPointMobile.clear();
					}
					ret = true;
				}
			}	                 			
	    }catch (Exception e){
	    	e.printStackTrace();
	    }
	    return ret;
	}
	private boolean DealRuninfoPacketProc4RemoteOpenDoor(){
		byte[] recv;
	    boolean ret = false;
	    try{
    		if (!this.gateWatchingHandler.getUdpQueue4RemoteOpenDoor().isEmpty()){
    			synchronized (this.gateWatchingHandler.getUdpQueue4RemoteOpenDoor()){
    				recv = (byte[])this.gateWatchingHandler.getUdpQueue4RemoteOpenDoor().poll();
    			}
                if ((recv.length == 64) && ((recv[1] == 0x40))){
                	long oldxid = WGUdpCommShort4Cloud.getXidOfCommand(recv);
                    int iloc = -1;
                    if ((iloc = this.arrXIDSend.indexOf(oldxid)) >= 0){
                    	recv[0] = 0x37;
                    	long newxid = (long)this.arrXIDReceivedMobile.get(iloc);
                        IoSession senderRemote = (IoSession)this.arrXIDEndPointMobile.get(iloc);
                        System.arraycopy(WGUdpCommShort4Cloud.longToByte(newxid) , 0, recv, 40, 4);
                        synchronized (this.arrXIDSend){
                        	this.arrXIDEndPointMobile.remove(iloc);
                        	this.arrXIDReceivedMobile.remove(iloc);
                        	this.arrXIDSend.remove(iloc);
                        }
                        this.udpOnlySend(recv, senderRemote); //2018-02-12 17:02:35
                    }
                }
                ret = true;	    		
    		}
	    }catch (Exception e){
	    	//  Debug.WriteLine(e.ToString());
	    }
	    return ret;
	}
	public long udpOnlySend(byte[] bytCommand, IoSession session){
		IoBuffer b;
		if (session != null) {
			if (session.isConnected()) {
				b = IoBuffer.allocate(bytCommand.length);
				b.put(bytCommand);
				b.flip();
				session.write(b);
			}
		}       
		return 0;
	}
	@Override
	public boolean isServiceRunning() {
		return this.isLoop;
	}
	@Override
	public void stopService() {
		Logger.info(this, LogUtil.getLogInfo(this.transactionId, "receive request to stop [ThreadName:"+Thread.currentThread().getName())+"]");
		NetworkUtil.closeNioDatagramAcceptorSession(this.acceptor);
		this.isLoop = false;
		this.isTerminate = false;				
	}
	@Override
	public boolean isTerminate() {
		return this.isTerminate;
	}
}
