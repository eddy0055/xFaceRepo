package com.xpand.xface.gate;

import java.util.Queue;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;

import com.xpand.xface.util.OtherUtil;

public class WGUdpCommShort4Cloud { 

	public static final int WGPacketSize = 64; 
	public static final byte Type = 0x17; 
	public static final int ControllerPort = 60000; 
	public static final long SpecialFlag = 0x55AAAA55;
	
	private static long _Global_xid = 0;
	private static long m_send_xid = 0x70000000l;

	public byte functionID; 
	public long iDevSn; 
	public byte[] data = new byte[56];
//	private IoConnector connector;
	protected long _xid = 0;
	private HWGateWatchingHandler gateWatchingHandler;

	public WGUdpCommShort4Cloud(HWGateWatchingHandler gateWatchingHandler) {
		this.gateWatchingHandler = gateWatchingHandler;
		resetData();
	}
	
	private void getNewXid() {
		WGUdpCommShort4Cloud._Global_xid++;
		if ((WGUdpCommShort4Cloud._Global_xid >= 0x4fffffff)|| (WGUdpCommShort4Cloud._Global_xid < 0x40000001)) {
			WGUdpCommShort4Cloud._Global_xid = 0x40000001;
		}
		this._xid = WGUdpCommShort4Cloud._Global_xid;
	}
	
	public void resetData() {
		for (int i = 0; i < this.data.length; i++) {
			this.data[i] = 0;
		}
	}
		
	public byte[] toByte(){
		byte[] buff = new byte[WGUdpCommShort4Cloud.WGPacketSize];
		for (int i = 0; i < this.data.length; i++) {
			buff[i] = 0;
		}
		buff[0] = WGUdpCommShort4Cloud.Type;
		buff[1] = functionID;
		System.arraycopy(longToByte(this.iDevSn), 0, buff, 4, 4);
		System.arraycopy(data, 0, buff, 8, this.data.length);
		this.getNewXid();
		System.arraycopy(WGUdpCommShort4Cloud.longToByte(this._xid), 0, buff, 40, 4);
		return buff;
	}
	
	public byte[] run() {
		return this.getInfo(this.iDevSn, toByte());
	}
	public byte[] run(byte[] command1024) {
		return this.getInfo(this.iDevSn, command1024);
	}
	public byte[] getInfo(long sn, byte[] command) {
		byte[] bytCommand = command;
		IoBuffer b;

		int iget = this.gateWatchingHandler.getArrSNReceived().indexOf((int) sn);
		if (iget < 0) {
			return null;
		}
		IoSession session = this.gateWatchingHandler.getArrControllerInfo().get(iget).getIoSession(); 
		Queue<byte[]> queueApplication = this.gateWatchingHandler.getArrControllerInfo().get(iget).getQueueOfReply();

		if (queueApplication != null) {
			synchronized (queueApplication) {
				queueApplication.clear();
			}
		}

		boolean bSent = false;
		if (session != null) {
			if (session.isConnected()) {
				b = IoBuffer.allocate(bytCommand.length);
				b.put(bytCommand);
				b.flip();
				session.write(b);
				bSent = true;
			}
		}

		int bSuccess = 0;
		int tries = 3;
		long xid = WGUdpCommShort4Cloud.getXidOfCommand(bytCommand);
		byte[] bytget = null;
		long startTicks = 0;
		long CommTimeoutMsMin = 0;
		long endTicks = 0;
		long startIndex = 0;
		while ((tries--) > 0) {
			startTicks = java.util.Calendar.getInstance().getTimeInMillis();
			CommTimeoutMsMin = 300;
			endTicks = startTicks + CommTimeoutMsMin;
			if (startTicks > endTicks) {			
				OtherUtil.waitMilliSecond(30);
				continue;
			}
			startIndex = 0;
			while (endTicks > java.util.Calendar.getInstance().getTimeInMillis()) {
				if (!bSent){
					if (session != null) {
						if (session.isConnected()) {
							b = IoBuffer.allocate(bytCommand.length);
							b.put(bytCommand);
							b.flip();
							session.write(b);
							bSent = true;
						}
					}
				}
				if (!queueApplication.isEmpty()) {
					synchronized (queueApplication) {
						bytget = queueApplication.poll();
					}
					if ((bytget[0] == bytCommand[0]) && (bytget[1] == bytCommand[1])
							&& (xid == getXidOfCommand(bytget))){
						bSuccess = 1;
						break; 
					} else {
						
					}
				}else{
					if ((startTicks + 1) < java.util.Calendar.getInstance().getTimeInMillis()) {
						//nothing
					} else if (startIndex > 10) {
						OtherUtil.waitMilliSecond(30);
					} else {
						startIndex++;
						OtherUtil.waitMilliSecond(1);
					}
				}
			}
			if (bSuccess > 0) {
				break;
			} else if (session != null) {
				if (session.isConnected()) {
					b = IoBuffer.allocate(bytCommand.length);
					b.put(bytCommand);
					b.flip();
					session.write(b);
				}
			}
		}		
		if (bSuccess > 0) {
			return bytget;
		}else{
			//nothing
		}
		return null;
	}
	public static byte[] longToByte(long number) {
		byte[] b = new byte[8];
		for (int i = 0; i < 8; i++) {
			b[i] = (byte) (number % 256);
			number >>= 8;
		}
		return b;
	}

	public static int getIntByByte(byte bt) {
		if (bt < 0) {
			return (bt + 256);
		} else {
			return bt;
		}
	}

	public static long getLongByByte(byte[] data, int startIndex, int bytlen) {
		long ret = -1;
		if ((bytlen >= 1) && (bytlen <= 8)) {
			ret = getIntByByte(data[startIndex + bytlen - 1]);
			for (int i = 1; i < bytlen; i++) {
				ret <<= 8;
				ret += getIntByByte(data[startIndex + bytlen - 1 - i]);
			}
		}
		return ret;
	}
	
	public static boolean isValidCommandReply(byte[] cmd) {
		long xd = getXidOfCommand(cmd);
		if ((xd >= 0x4fffffff)|| (xd < 0x40000001)) {
			return false;
		}
		return true;
	}

	public static long getXidOfCommand(byte[] cmd){
		long ret = -1;
		if (cmd.length >= WGUdpCommShort4Cloud.WGPacketSize) {
			ret = getLongByByte(cmd, 40, 4);
		}
		return ret;
	}	
			
    public static int getControllerType(long controllerSN){
    	int result = 0;
        if (controllerSN > 100000000l){
            if (controllerSN <= 199999999l){
                result = 1;
            }else if (controllerSN <= 299999999l){
            	result = 2;
            }else if (controllerSN <= 399999999l){
            	result = 0;
            }else if (controllerSN <= 499999999l){
            	result = 4;
            }
        }
        return result;
    }
    
    public static boolean isElevator(long controllerSN){
        return (controllerSN >= 170000000l && controllerSN <= 179999999l);
    }
    
    public static long getXid(){
        WGUdpCommShort4Cloud.m_send_xid++;
        if (WGUdpCommShort4Cloud.m_send_xid >= 0x7FFFFFFFl){
        	WGUdpCommShort4Cloud.m_send_xid = 0x70000000l;
        }
        return WGUdpCommShort4Cloud.m_send_xid;
    }
}
