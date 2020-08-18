package com.xpand.xface.gate;

import org.apache.log4j.Logger;

import com.xpand.xface.entity.HWGateAccessInfo;
import com.xpand.xface.util.StringUtil;

public class HWGateDataField {
	private int recordType = -1;
	private int recordValid = -1;
	private int recordDoorNO = -1;
	private int recordInOrOut = -1;
	private long recordCardNo = -1;
	private long recordIndex = -1;
	private String recordTime = "";
	private int reasonCode = -1;
	private String reasonDesc = "";
	private long doorSN = -1;
	private static Logger log = Logger.getLogger(HWGateDataField.class);
	private static final String RESPONSE_DETAILS[] = {
		"1","SwipePass","Swipe","刷卡开门",
		"2","SwipePass","Swipe Close","刷卡关",
		"3","SwipePass","Swipe Open","刷卡开",
		"4","SwipePass","Swipe Limited Times","刷卡开门(带限次)",
		"5","SwipeNOPass","Denied Access: PC Control","刷卡禁止通过: 电脑控制",
		"6","SwipeNOPass","Denied Access: No PRIVILEGE","刷卡禁止通过: 没有权限",
		"7","SwipeNOPass","Denied Access: Wrong PASSWORD","刷卡禁止通过: 密码不对",
		"8","SwipeNOPass","Denied Access: AntiBack","刷卡禁止通过: 反潜回",
		"9","SwipeNOPass","Denied Access: More Cards","刷卡禁止通过: 多卡",
		"10","SwipeNOPass","Denied Access: First Card Open","刷卡禁止通过: 首卡",
		"11","SwipeNOPass","Denied Access: Door Set NC","刷卡禁止通过: 门为常闭",
		"12","SwipeNOPass","Denied Access: InterLock","刷卡禁止通过: 互锁",
		"13","SwipeNOPass","Denied Access: Limited Times","刷卡禁止通过: 受刷卡次数限制",
		"14","SwipeNOPass","Denied Access: Limited Person Indoor","刷卡禁止通过: 门内人数限制",
		"15","SwipeNOPass","Denied Access: Invalid Timezone","刷卡禁止通过: 卡过期或不在有效时段",
		"16","SwipeNOPass","Denied Access: In Order","刷卡禁止通过: 按顺序进出限制",
		"17","SwipeNOPass","Denied Access: SWIPE GAP LIMIT","刷卡禁止通过: 刷卡间隔约束",
		"18","SwipeNOPass","Denied Access","刷卡禁止通过: 原因不明",
		"19","SwipeNOPass","Denied Access: Limited Times","刷卡禁止通过: 刷卡次数限制",
		"20","ValidEvent","Push Button","按钮开门",
		"21","ValidEvent","Push Button Open","按钮开",
		"22","ValidEvent","Push Button Close","按钮关",
		"23","ValidEvent","Door Open","门打开[门磁信号]",
		"24","ValidEvent","Door Closed","门关闭[门磁信号]",
		"25","ValidEvent","Super Password Open Door","超级密码开门",
		"26","ValidEvent","Super Password Open","超级密码开",
		"27","ValidEvent","Super Password Close","超级密码关",
		"28","Warn","Controller Power On","控制器上电",
		"29","Warn","Controller Reset","控制器复位",
		"30","Warn","Push Button Invalid: Disable","按钮不开门: 按钮禁用",
		"31","Warn","Push Button Invalid: Forced Lock","按钮不开门: 强制关门",
		"32","Warn","Push Button Invalid: Not On Line","按钮不开门: 门不在线",
		"33","Warn","Push Button Invalid: InterLock","按钮不开门: 互锁",
		"34","Warn","Threat","胁迫报警",
		"35","Warn","Threat Open","胁迫报警开",
		"36","Warn","Threat Close","胁迫报警关",
		"37","Warn","Open too long","门长时间未关报警[合法开门后]",
		"38","Warn","Forced Open","强行闯入报警",
		"39","Warn","Fire","火警",
		"40","Warn","Forced Close","强制关门",
		"41","Warn","Guard Against Theft","防盗报警",
		"42","Warn","7*24Hour Zone","烟雾煤气温度报警",
		"43","Warn","Emergency Call","紧急呼救报警",
		"44","RemoteOpen","Remote Open Door","操作员远程开门",
		"45","RemoteOpen","Remote Open Door By USB Reader","发卡器确定发出的远程开门"
	};
	
	public HWGateDataField(byte[] recvBuff) {
		this.getDataInfo(recvBuff);		
	}
	private void getDataInfo(byte[] recvBuff) {
		HWGateDataField.log.info("in getDataInfo");
		if (recvBuff==null || recvBuff.length < 28) {
			HWGateDataField.log.info("out getDataInfo coz size of buffer is less than 28 member");
			return;
		}
		this.doorSN = WGUdpCommShort4Cloud.getLongByByte(recvBuff, 4, 4);
		this.recordIndex = WGUdpCommShort4Cloud.getLongByByte(recvBuff, 8, 4);
		//record type is
        //0=No data
        //1=save data
        //2=push button, open door
        //3=warning level 1
        //0xFF = don't known
		this.recordType =WGUdpCommShort4Cloud.getIntByByte(recvBuff[12]);
		this.recordValid = WGUdpCommShort4Cloud.getIntByByte(recvBuff[13]);
		this.recordDoorNO = WGUdpCommShort4Cloud.getIntByByte(recvBuff[14]);
		this.recordInOrOut = WGUdpCommShort4Cloud.getIntByByte(recvBuff[15]);
		this.recordCardNo =WGUdpCommShort4Cloud.getLongByByte(recvBuff, 16, 4);
		this.recordTime =  String.format("%02X%02X-%02X-%02X %02X:%02X:%02X", 
				WGUdpCommShort4Cloud.getIntByByte(recvBuff[20]),
				WGUdpCommShort4Cloud.getIntByByte(recvBuff[21]),
				WGUdpCommShort4Cloud.getIntByByte(recvBuff[22]),
				WGUdpCommShort4Cloud.getIntByByte(recvBuff[23]),
				WGUdpCommShort4Cloud.getIntByByte(recvBuff[24]),
				WGUdpCommShort4Cloud.getIntByByte(recvBuff[25]),
				WGUdpCommShort4Cloud.getIntByByte(recvBuff[26]));
		this.reasonCode = WGUdpCommShort4Cloud.getIntByByte(recvBuff[27]);
		this.reasonDesc = this.getReasonDetailEnglish(this.reasonCode);		
		HWGateDataField.log.info("out getDataInfo");
	}
	private String getReasonDetailEnglish(int reasonCode){
        if (reasonCode > 45){
            return "";
        }else if (reasonCode <= 0){
            return "";
        }
        return HWGateDataField.RESPONSE_DETAILS[(reasonCode - 1) * 4 + 2];
    }
	@Override
	public String toString() {
		return "DataInfoField [doorSN="+this.doorSN+", recordType=" + recordType + ", recordValid=" + recordValid + ", recordDoorNO=" + recordDoorNO + ", recordInOrOut=" + recordInOrOut + ", recordCardNo=" + recordCardNo + ", recordIndex=" + recordIndex + ", recordTime=" + recordTime + ", reasonCode=" + reasonCode
				+ ", reasonDesc=" + reasonDesc + "]";
	}
	public long getDoorSN() {
		return doorSN;
	}
	public void setDoorSN(long doorSN) {
		this.doorSN = doorSN;
	}
	public HWGateAccessInfo getHWGateAccessInfo() {
		HWGateAccessInfo gateAccess = new HWGateAccessInfo();
		gateAccess.setDoorSN(this.doorSN);
		gateAccess.setReasonCode(this.reasonCode);
		gateAccess.setReasonDesc(this.reasonDesc);
		gateAccess.setRecordCardNo(this.recordCardNo);
		gateAccess.setRecordDoorNo(this.recordDoorNO);
		gateAccess.setRecordId(this.recordIndex);
		gateAccess.setRecordInOrOut(this.recordInOrOut);
		gateAccess.setRecordTime(StringUtil.stringToDate(this.recordTime, StringUtil.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS));
		gateAccess.setRecordType(this.recordType);
		gateAccess.setRecordValid(this.recordValid);
		return gateAccess;
		
	}
}
