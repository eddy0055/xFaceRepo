package com.xpand.xface.bean;

import java.util.ArrayList;

import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.util.StringUtil;

public class WebSocketHolder {
	private String sessionId = null;
	private String userName;
	private int webSocketModule;	
	private ArrayList<String> certificateNoList;
	private String fullName;
	private ArrayList<String> gateInfoCodeList;
	private ArrayList<String> ipcCodeList;
	private boolean markDelete = false;
	public WebSocketHolder(String sessionId, String userName, WebFEParam param) {
		this.sessionId = sessionId;	
		this.userName = userName;
		this.webSocketModule = param.getWebSocketModule();
		this.updateFilter(param);
	}
	public void updateFilter(WebFEParam param) {
		this.updateCertificateNoList(param.getPersonCertificateNo());
		this.updateGateInfoCodeList(param.getGateInfoCodeList());
		this.updateIpcCodeList(param.getIpcCodeList());
	}
	//search for certificate no in array list
	public int findCertificateNo(String certificateNo) {
		if (this.certificateNoList==null) {
			return -1;
		}else {
			int cntCertificateNo = -1;
			for (String certNo: this.certificateNoList) {
				cntCertificateNo++;
				if (certNo.equals(certificateNo)) {
					return cntCertificateNo;
				}
			}
			return -1;
		}
	}
	//search for ipc in array list
	public int findIpcCode(String ipcCode) {
		if (this.ipcCodeList==null) {
			return -1;
		}else {
			int cntIpcCode = -1;
			for (String code: this.ipcCodeList) {
				cntIpcCode++;
				if (code.equals(ipcCode)) {
					return cntIpcCode;
				}
			}
			return -1;
		}
	}
	
	//search for gate info in array list
	public int findGateInfoCode(String gateInfoCode) {
		if (this.gateInfoCodeList==null) {
			return -1;
		}else {
			int cntGateInfoCode = -1;
			for (String code: this.gateInfoCodeList) {
				cntGateInfoCode++;
				if (code.equals(gateInfoCode)) {
					return cntGateInfoCode;
				}
			}
			return -1;
		}
	}
		
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
//	private WebSocketSession webSocket;
//	public WebSocketHolder(String transactionId, WebSocketSession webSocket, String userName) {
//		this.transactionId = transactionId;
//		this.webSocket = webSocket;
//		this.userName = userName;
//	}
//	public WebSocketSession getWebSocket() {
//	return webSocket;
//}
//public void setWebSocket(WebSocketSession webSocket) {
//	this.webSocket = webSocket;
//}
//public void closeSocket() {
//	try {
//		if (this.webSocket!=null && this.webSocket.isOpen()) {
//			this.webSocket.close();
//		}
//	}catch (Exception ex) {}		
//}
	public int getWebSocketModule() {
		return webSocketModule;
	}
	public void setWebSocketModule(int webSocketModule) {
		this.webSocketModule = webSocketModule;
	}
	public ArrayList<String> getCertificateNoList() {
		return certificateNoList;
	}
	public void setCertificateNoList(ArrayList<String> certificateNoList) {
		this.certificateNoList = certificateNoList;
	}
	public void updateCertificateNoList(String certificateNoList) {
		if (this.certificateNoList==null) {
			this.certificateNoList = new ArrayList<>();
		}		
		if (!StringUtil.checkNull(certificateNoList)) {
			String arrayCertNo[] = certificateNoList.split(ConstUtil.STRING_ID_DELIMINATOR);
			for (String certNo: arrayCertNo) {
				this.certificateNoList.add(certNo);
			}
		}			
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public ArrayList<String> getIpcCodeList() {
		return ipcCodeList;
	}
	public void setIpcCodeList(ArrayList<String> ipcCodeList) {
		this.ipcCodeList = ipcCodeList;
	}
	public void updateIpcCodeList(String ipcCodeList) {
		if (this.ipcCodeList==null) {
			this.ipcCodeList = new ArrayList<>();
		}		
		if (!StringUtil.checkNull(ipcCodeList)) {
			String arrayIpcCode[] = ipcCodeList.split(ConstUtil.STRING_ID_DELIMINATOR);
			for (String ipcCode: arrayIpcCode) {
				this.ipcCodeList.add(ipcCode);
			}
		}			
	}
	public ArrayList<String> getGateInfoCodeList() {
		return gateInfoCodeList;
	}
	public void setGateInfoCodeList(ArrayList<String> gateInfoCodeList) {
		this.gateInfoCodeList = gateInfoCodeList;
	}
	public void updateGateInfoCodeList(String gateInfoCodeList) {
		if (this.gateInfoCodeList==null) {
			this.gateInfoCodeList = new ArrayList<>();
		}		
		if (!StringUtil.checkNull(gateInfoCodeList)) {
			String arrayGateInfoCode[] = gateInfoCodeList.split(ConstUtil.STRING_ID_DELIMINATOR);
			for (String gateInfoCode: arrayGateInfoCode) {
				this.gateInfoCodeList.add(gateInfoCode);
			}
		}			
	}
	public boolean isMarkDelete() {
		return markDelete;
	}
	public void setMarkDelete(boolean markDelete) {
		this.markDelete = markDelete;
	}
}
