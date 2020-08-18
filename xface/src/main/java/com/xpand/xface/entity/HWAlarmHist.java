package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="tbl_alarm_hist")
public class HWAlarmHist implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="alarmhId", nullable=false)	
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer alarmhId;
	
	@Column(name="alarmCode",length=100)
	private String alarmCode;
	@Column(name="taskId",length=50)
	private String taskId;
	@Column(name="cameraId",length=100)
	private String cameraId;
	@Column(name="cameraName",length=100)
	private String cameraName;
	@Column(name="cameraIndex")
	private Integer cameraIndex;
	@Column(name="vstationSn",length=50)
	private String vstationSn;
	@Column(name="vstationName",length=50)
	private String vstationName;
	@Column(name="vstationIndex",length=50)
	private String vstationIndex;
	@Column(name="caseId",length=50)
	private String caseId;
	@Column(name="caseFileId",length=50)
	private String caseFileId;
	@Column(name="sliceNum",length=50)
	private String sliceNum;
	@Column(name="source",length=50)
	private String source;
	@Column(name="sourceSystemId",length=50)
	private String sourceSystemId;
	@Column(name="resolution",length=50)
	private String resolution;
	@Column(name="alarmPicName",length=50)
	private String alarmPicName;
	@Column(name="cTime",length=50)
	private String cTime;
	@Column(name="ruleType",length=50)
	private String ruleType;
	@Column(name="confirm",length=50)
	private String confirm;
	@Column(name="closed",length=50)
	private String closed;
	@Column(name="objectId",length=50)
	private String objectId;
	@Column(name="videoType",length=50)
	private String videoType;
	@Column(name="objectType",length=50)
	private String objectType;
	@Column(name="suspectId",length=100)
	private String suspectId;
	@Column(name="blkgrpId",length=50)
	private String blkgrpId;
	@Column(name="blackListId",length=50)
	private String blackListId;
	@Column(name="domainCode",length=50)
	private String domainCode;
	//meta-data db_name
	@Column(name="metaDBName",length=50)
	private String metaDBName;
	@Column(name="metaColor",length=10)
	private String metaColor;
	@Column(name="metaVehicleBrand",length=10)
	private String metaVehicleBrand;
	@Column(name="metaVehicleSub",length=10)
	private String metaVehicleSub;
	@Column(name="metaYear",length=10)
	private String metaYear;
	@Column(name="metaPType",length=10)
	private String metaPType;
	@Column(name="metaPColor",length=10)
	private String metaPColor;
	@Column(name="metaPnr",length=10)
	private String metaPnr;
	@Column(name="metaCarType",length=10)
	private String metaCarType;
	@Column(name="metaDirec",length=10)
	private String metaDirec;
	
	@Column(name="metaScr")
	private int metaScr;
	@Column(name="metaAlgorithmCode",length=10)
	private String metaAlgorithmCode;
	@Column(name="metaAlgorithmName",length=30)
	private String metaAlgorithmName;
	@Column(name="metaAlarmMold")
	private int metaAlarmMold;
	@Column(name="metaAlarmMatch")
	private int metaAlarmMatch;
	
	@Column(name="metaPosLeft")
	private int metaPosLeft;
	@Column(name="metaPosTop")
	private int metaPosTop;
	@Column(name="metaPosRight")
	private int metaPosRight;
	@Column(name="metaPosBottom")
	private int metaPosBottom;
	//////////////////////////
	//pic////
	@Column(name="picCaseFileId",length=50)
	private String picCaseFileId;
	@Column(name="picFileId",length=200)
	private String picFileId;
	@Column(name="picStartPos",length=10)
	private String picStartPos;
	@Column(name="picThumbLen",length=10)
	private String picThumbLen;
	@Column(name="picLen",length=10)
	private String picLen;
	@Column(name="picMId",length=10)
	private String picMId;
	@Column(name="picSInx",length=10)
	private String picSInx;
	@Column(name="picImageUrl",length=500)
	private String picImageUrl;
	@Column(name="picThumImageUrl",length=500)
	private String picThumImageUrl;
	@Column(name="picFeatureValue",length=500)
	private String picFeatureValue;
	@Column(name="picFeatureLength")
	private int picFeatureLength;
	@Column(name="picFeatureId",length=30)
	private String picFeatureId;
	@Column(name="picFeatureIndex",length=30)
	private String picFeatureIndex;
	@Column(name="picIsURL",length=5)
	private String picIsURL;
	@Column(name="livePhoto")
	private String livePhoto;
	
	///////////////////
	@Column(name="alarmLevel",length=5)
	private String alarmLevel;
	@Column(name="alarmTime")
	private Date alarmTime;
	@Column(name="alarmType",length=5)
	private String alarmType;
	@Column(name="resultStatus")
	private String resultStatus;
	
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="ipcId")
	private HWIPC hwIPC;

	@ManyToOne
	@JoinColumn(name="personId")
	private PersonInfo personInfo;
				
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;	

	public HWAlarmHist() {
		
	}	
	public Integer getAlarmhId() {
		return alarmhId;
	}
	public void setAlarmhId(Integer alarmhId) {
		this.alarmhId = alarmhId;
	}
	public String getAlarmCode() {
		return alarmCode;
	}
	public void setAlarmCode(String alarmCode) {
		this.alarmCode = alarmCode;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getCameraId() {
		return cameraId;
	}
	public void setCameraId(String cameraId) {
		this.cameraId = cameraId;
	}
	public String getCameraName() {
		return cameraName;
	}
	public void setCameraName(String cameraName) {
		this.cameraName = cameraName;
	}
	public Integer getCameraIndex() {
		return cameraIndex;
	}
	public void setCameraIndex(Integer cameraIndex) {
		this.cameraIndex = cameraIndex;
	}
	public String getVstationSn() {
		return vstationSn;
	}
	public void setVstationSn(String vstationSn) {
		this.vstationSn = vstationSn;
	}
	public String getVstationName() {
		return vstationName;
	}
	public void setVstationName(String vstationName) {
		this.vstationName = vstationName;
	}
	public String getVstationIndex() {
		return vstationIndex;
	}
	public void setVstationIndex(String vstationIndex) {
		this.vstationIndex = vstationIndex;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	public String getCaseFileId() {
		return caseFileId;
	}
	public void setCaseFileId(String caseFileId) {
		this.caseFileId = caseFileId;
	}
	public String getSliceNum() {
		return sliceNum;
	}
	public void setSliceNum(String sliceNum) {
		this.sliceNum = sliceNum;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSourceSystemId() {
		return sourceSystemId;
	}
	public void setSourceSystemId(String sourceSystemId) {
		this.sourceSystemId = sourceSystemId;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	public String getAlarmPicName() {
		return alarmPicName;
	}
	public void setAlarmPicName(String alarmPicName) {
		this.alarmPicName = alarmPicName;
	}
	public String getcTime() {
		return cTime;
	}
	public void setcTime(String cTime) {
		this.cTime = cTime;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public String getConfirm() {
		return confirm;
	}
	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}
	public String getClosed() {
		return closed;
	}
	public void setClosed(String closed) {
		this.closed = closed;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getVideoType() {
		return videoType;
	}
	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getSuspectId() {
		return suspectId;
	}
	public void setSuspectId(String suspectId) {
		this.suspectId = suspectId;
	}
	public String getBlkgrpId() {
		return blkgrpId;
	}
	public void setBlkgrpId(String blkgrpId) {
		this.blkgrpId = blkgrpId;
	}
	public String getBlackListId() {
		return blackListId;
	}
	public void setBlackListId(String blackListId) {
		this.blackListId = blackListId;
	}
	public String getDomainCode() {
		return domainCode;
	}
	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}
	public String getMetaDBName() {
		return metaDBName;
	}
	public void setMetaDBName(String metaDBName) {
		this.metaDBName = metaDBName;
	}
	public String getMetaColor() {
		return metaColor;
	}
	public void setMetaColor(String metaColor) {
		this.metaColor = metaColor;
	}
	public String getMetaVehicleBrand() {
		return metaVehicleBrand;
	}
	public void setMetaVehicleBrand(String metaVehicleBrand) {
		this.metaVehicleBrand = metaVehicleBrand;
	}
	public String getMetaVehicleSub() {
		return metaVehicleSub;
	}
	public void setMetaVehicleSub(String metaVehicleSub) {
		this.metaVehicleSub = metaVehicleSub;
	}
	public String getMetaYear() {
		return metaYear;
	}
	public void setMetaYear(String metaYear) {
		this.metaYear = metaYear;
	}
	public String getMetaPType() {
		return metaPType;
	}
	public void setMetaPType(String metaPType) {
		this.metaPType = metaPType;
	}
	public String getMetaPColor() {
		return metaPColor;
	}
	public void setMetaPColor(String metaPColor) {
		this.metaPColor = metaPColor;
	}
	public String getMetaPnr() {
		return metaPnr;
	}
	public void setMetaPnr(String metaPnr) {
		this.metaPnr = metaPnr;
	}
	public String getMetaCarType() {
		return metaCarType;
	}
	public void setMetaCarType(String metaCarType) {
		this.metaCarType = metaCarType;
	}
	public String getMetaDirec() {
		return metaDirec;
	}
	public void setMetaDirec(String metaDirec) {
		this.metaDirec = metaDirec;
	}
	
	public String getPicCaseFileId() {
		return picCaseFileId;
	}
	public void setPicCaseFileId(String picCaseFileId) {
		this.picCaseFileId = picCaseFileId;
	}
	public String getPicFileId() {
		return picFileId;
	}
	public void setPicFileId(String picFileId) {
		this.picFileId = picFileId;
	}
	public String getPicStartPos() {
		return picStartPos;
	}
	public void setPicStartPos(String picStartPos) {
		this.picStartPos = picStartPos;
	}
	public String getPicThumbLen() {
		return picThumbLen;
	}
	public void setPicThumbLen(String picThumbLen) {
		this.picThumbLen = picThumbLen;
	}
	public String getPicLen() {
		return picLen;
	}
	public void setPicLen(String picLen) {
		this.picLen = picLen;
	}
	public String getPicMId() {
		return picMId;
	}
	public void setPicMId(String picMId) {
		this.picMId = picMId;
	}
	public String getPicSInx() {
		return picSInx;
	}
	public void setPicSInx(String picSInx) {
		this.picSInx = picSInx;
	}
	public String getPicImageUrl() {
		return picImageUrl;
	}
	public void setPicImageUrl(String picImageUrl) {
		this.picImageUrl = picImageUrl;
	}
	public String getPicThumImageUrl() {
		return picThumImageUrl;
	}
	public void setPicThumImageUrl(String picThumImageUrl) {
		this.picThumImageUrl = picThumImageUrl;
	}
	public String getAlarmLevel() {
		return alarmLevel;
	}
	public void setAlarmLevel(String alarmLevel) {
		this.alarmLevel = alarmLevel;
	}
	public Date getAlarmTime() {
		return alarmTime;
	}
	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}
	public String getAlarmType() {
		return alarmType;
	}
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}
	public HWIPC getHwIPC() {
		return hwIPC;
	}
	public void setHwIPC(HWIPC hwIPC) {
		this.hwIPC = hwIPC;
	}
	public PersonInfo getPersonInfo() {
		return personInfo;
	}
	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}	
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public int getMetaScr() {
		return metaScr;
	}

	public void setMetaScr(int metaScr) {
		this.metaScr = metaScr;
	}

	public String getMetaAlgorithmCode() {
		return metaAlgorithmCode;
	}

	public void setMetaAlgorithmCode(String metaAlgorithmCode) {
		this.metaAlgorithmCode = metaAlgorithmCode;
	}

	public String getMetaAlgorithmName() {
		return metaAlgorithmName;
	}

	public void setMetaAlgorithmName(String metaAlgorithmName) {
		this.metaAlgorithmName = metaAlgorithmName;
	}

	public int getMetaAlarmMold() {
		return metaAlarmMold;
	}

	public void setMetaAlarmMold(int metaAlarmMold) {
		this.metaAlarmMold = metaAlarmMold;
	}

	public int getMetaAlarmMatch() {
		return metaAlarmMatch;
	}

	public void setMetaAlarmMatch(int metaAlarmMatch) {
		this.metaAlarmMatch = metaAlarmMatch;
	}

	public int getMetaPosLeft() {
		return metaPosLeft;
	}

	public void setMetaPosLeft(int metaPosLeft) {
		this.metaPosLeft = metaPosLeft;
	}

	public int getMetaPosTop() {
		return metaPosTop;
	}

	public void setMetaPosTop(int metaPosTop) {
		this.metaPosTop = metaPosTop;
	}

	public int getMetaPosRight() {
		return metaPosRight;
	}

	public void setMetaPosRight(int metaPosRight) {
		this.metaPosRight = metaPosRight;
	}

	public int getMetaPosBottom() {
		return metaPosBottom;
	}

	public void setMetaPosBottom(int metaPosBottom) {
		this.metaPosBottom = metaPosBottom;
	}

	public String getPicFeatureValue() {
		return picFeatureValue;
	}

	public void setPicFeatureValue(String picFeatureValue) {
		this.picFeatureValue = picFeatureValue;
	}

	public int getPicFeatureLength() {
		return picFeatureLength;
	}

	public void setPicFeatureLength(int picFeatureLength) {
		this.picFeatureLength = picFeatureLength;
	}

	public String getPicFeatureId() {
		return picFeatureId;
	}

	public void setPicFeatureId(String picFeatureId) {
		this.picFeatureId = picFeatureId;
	}

	public String getPicFeatureIndex() {
		return picFeatureIndex;
	}

	public void setPicFeatureIndex(String picFeatureIndex) {
		this.picFeatureIndex = picFeatureIndex;
	}

	public String getPicIsURL() {
		return picIsURL;
	}

	public void setPicIsURL(String picIsURL) {
		this.picIsURL = picIsURL;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getLivePhoto() {
		return livePhoto;
	}
	public void setLivePhoto(String livePhoto) {
		this.livePhoto = livePhoto;
	}

}
