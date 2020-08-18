package com.xpand.xface.bean.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.util.LogUtil;

public class WebFEPersonByPhotoRespV1 {
	private List<WebFEPersonPhotoCameraV1> webFEPersonPhotoCameraList;
	private ResultStatus resultStatus;
	public WebFEPersonByPhotoRespV1(String transactionId, HashMap<String, QueryPersonByPhotoListRespV1> searchResultList) {
		this.extractResult(transactionId, searchResultList);
	}
	private void extractResult(String transactionId, HashMap<String, QueryPersonByPhotoListRespV1> searchResultList) {
		Logger.info(this, LogUtil.getLogInfo(transactionId, "in extract result"));
		this.resultStatus = new ResultStatus();
		if (searchResultList.size() == 0) {
			this.resultStatus.setStatusCode(ResultStatus.NO_DATA_FOUND_ERROR_CODE, "");
			Logger.info(this, LogUtil.getLogInfo(transactionId, "out extract result with result:"+this.resultStatus.toString()));
			return;
		}
		Iterator<String>  keyList = searchResultList.keySet().iterator();
		QueryPersonByPhotoListRespV1 queryPersonByPhotoListResp = null;
		WebFEPersonPhotoCameraV1  personPhotoCamera = null;
		String algorithmCode = null;
		int cntFoundCamera = 0;
		int cntFoundRecord = 0;
		while (keyList.hasNext()) {
			cntFoundCamera = 0;
			cntFoundRecord = 0;
			//key is algorigthm code
			algorithmCode = keyList.next();
			Logger.info(this, LogUtil.getLogInfo(transactionId, "found alrogithm code:"+algorithmCode));
			queryPersonByPhotoListResp = searchResultList.get(algorithmCode);
			for (QueryPersonByPhotoRespV1 queryPersonByPhotoResp: queryPersonByPhotoListResp.getResultList()) {
				cntFoundRecord++;
				personPhotoCamera = this.findCameraByCode(queryPersonByPhotoResp.getCameraSn());
				if (personPhotoCamera==null) {
					personPhotoCamera = new WebFEPersonPhotoCameraV1();
					personPhotoCamera.setIpcCode(queryPersonByPhotoResp.getCameraSn());
					personPhotoCamera.setIpcName(queryPersonByPhotoResp.getCameraName());					
					personPhotoCamera.addPersonPhotoDetailList(new WebPersonPhotoDetailV1(queryPersonByPhotoResp.getRecordTime(), "photo"));
					this.addWebFEPersonPhotoCameraList(personPhotoCamera);
					cntFoundCamera++;
				}else {
					personPhotoCamera.addPersonPhotoDetailList(new WebPersonPhotoDetailV1(queryPersonByPhotoResp.getRecordTime(), "photo"));
				}
			}
			Logger.info(this, LogUtil.getLogInfo(transactionId, "algorithm code:"+algorithmCode+" no of camera:"+cntFoundCamera+" no record:"+cntFoundRecord));
		}
		Logger.info(this, LogUtil.getLogInfo(transactionId, "out extract result with result:"+this.resultStatus.toString()));
	}
	private WebFEPersonPhotoCameraV1 findCameraByCode(String ipcCode) {
		for (WebFEPersonPhotoCameraV1 camera: this.webFEPersonPhotoCameraList) {
			if (camera.getIpcCode().equals(ipcCode)) {
				return camera;
			}
		}
		return null;
	}
	public List<WebFEPersonPhotoCameraV1> getPersonPhotoCameraList() {
		if (this.webFEPersonPhotoCameraList==null) {
			this.webFEPersonPhotoCameraList = new ArrayList<>();
		}
		return webFEPersonPhotoCameraList;
	}
	public void setWebFEPersonPhotoCameraList(List<WebFEPersonPhotoCameraV1> webFEPersonPhotoCameraList) {
		this.webFEPersonPhotoCameraList = webFEPersonPhotoCameraList;
	}
	public void addWebFEPersonPhotoCameraList(WebFEPersonPhotoCameraV1 webFEPersonPhotoCamera) {
		this.getPersonPhotoCameraList();		
		//order by camera
		int index = 0;
		for (WebFEPersonPhotoCameraV1 camera: this.webFEPersonPhotoCameraList) {
			//a > b 
			if (camera.getIpcName().compareTo(webFEPersonPhotoCamera.getIpcName()) > 0) {
				//sort by character
				this.webFEPersonPhotoCameraList.add(index, webFEPersonPhotoCamera);
				return;
			}
			index++;				
		}
	}
	public ResultStatus getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}
}
