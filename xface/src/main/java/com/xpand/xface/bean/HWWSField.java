package com.xpand.xface.bean;

import java.util.ArrayList;
import java.util.List;

import com.xpand.xface.entity.HWAlarmHist;

public class HWWSField {
	private String jSessionId;
	private ResultStatus result;
	private List<QueryCameraResp> queryCameraRespList;
	private List<AddIntelligentAnalsisBatchesResp> addIntelligentAnalsisBatchesRespList; 
	private QueryFaceListResp queryFaceListResp;
	private QueryCheckPointResp queryCheckPointResp;
	private QuerySuspectTaskResp querySuspectTaskResp;
	private String addFaceListId; 
	private String addFaceToListId;	
	private String cameraCheckPointSN;
	private String addSuspectTaskId;
	private String uploadFileURL;
	private String uploadFileFileId;
	private String uploadCaseFileId;
	private String hwPersonId; //use in query person api	
	private HWAlarmHist hwAlarmHist;	
	public HWWSField() {
		this.result = new ResultStatus();
	}
	public String getjSessionId() {
		return jSessionId;
	}
	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
	}
	public ResultStatus getResult() {
		return result;
	}
	public void setResult(ResultStatus result) {
		this.result = result;
	}
	public List<QueryCameraResp> getQueryCameraRespList() {
		if (this.queryCameraRespList==null) {
			this.queryCameraRespList = new ArrayList<QueryCameraResp>();
		}
		return queryCameraRespList;
	}
	public void setQueryCameraRespList(List<QueryCameraResp> queryCameraRespList) {
		this.queryCameraRespList = queryCameraRespList;
	}
	public void addQueryCameraResp(QueryCameraResp queryCameraResp) {
		if (this.queryCameraRespList==null) {
			this.queryCameraRespList = new ArrayList<QueryCameraResp>();
		}
		this.queryCameraRespList.add(queryCameraResp);
	}
	public String getAddFaceListId() {
		return addFaceListId;
	}
	public void setAddFaceListId(String addFaceToListId) {
		this.addFaceListId = addFaceToListId;
	}
	public String getCameraCheckPointSN() {
		return cameraCheckPointSN;
	}
	public void setCameraCheckPointSN(String cameraCheckPointSN) {
		this.cameraCheckPointSN = cameraCheckPointSN;
	}
	public String getAddSuspectTaskId() {
		return addSuspectTaskId;
	}
	public void setAddSuspectTaskId(String addSuspectTaskId) {
		this.addSuspectTaskId = addSuspectTaskId;
	}
	public String getUploadFileURL() {
		return uploadFileURL;
	}
	public void setUploadFileURL(String uploadFileURL) {
		this.uploadFileURL = uploadFileURL;
	}
	public String getUploadFileFileId() {
		return uploadFileFileId;
	}
	public void setUploadFileFileId(String uploadFileFileId) {
		this.uploadFileFileId = uploadFileFileId;
	}
	public String getUploadCaseFileId() {
		return uploadCaseFileId;
	}
	public void setUploadCaseFileId(String uploadCaseFileId) {
		this.uploadCaseFileId = uploadCaseFileId;
	}
	public String getAddFaceToListId() {
		return addFaceToListId;
	}
	public void setAddFaceToListId(String addFaceToListId) {
		this.addFaceToListId = addFaceToListId;
	}
	public QueryFaceListResp getQueryFaceListResp() {
		return queryFaceListResp;
	}
	public void setQueryFaceListResp(QueryFaceListResp queryFaceListResp) {
		this.queryFaceListResp = queryFaceListResp;
	}
	public QueryCheckPointResp getQueryCheckPointResp() {
		return queryCheckPointResp;
	}
	public void setQueryCheckPointResp(QueryCheckPointResp queryCheckPointResp) {
		this.queryCheckPointResp = queryCheckPointResp;
	}
	public QuerySuspectTaskResp getQuerySuspectTaskResp() {
		return querySuspectTaskResp;
	}
	public void setQuerySuspectTaskResp(QuerySuspectTaskResp querySuspectTaskResp) {
		this.querySuspectTaskResp = querySuspectTaskResp;
	}
	public HWAlarmHist getHwAlarmHist() {
		return hwAlarmHist;
	}
	public void setHwAlarmHist(HWAlarmHist hwAlarmHist) {
		this.hwAlarmHist = hwAlarmHist;
	}
	public String getHwPersonId() {
		return hwPersonId;
	}
	public void setHwPersonId(String hwPersonId) {
		this.hwPersonId = hwPersonId;
	}
	public List<AddIntelligentAnalsisBatchesResp> getAddIntelligentAnalsisBatchesRespList() {
		if (this.addIntelligentAnalsisBatchesRespList==null) {
			this.addIntelligentAnalsisBatchesRespList = new ArrayList<AddIntelligentAnalsisBatchesResp>();
		}
		return addIntelligentAnalsisBatchesRespList;
	}
	public void setAddIntelligentAnalsisBatchesRespList(List<AddIntelligentAnalsisBatchesResp> addIntelligentAnalsisBatchesRespList) {
		this.addIntelligentAnalsisBatchesRespList = addIntelligentAnalsisBatchesRespList;
	}
}
