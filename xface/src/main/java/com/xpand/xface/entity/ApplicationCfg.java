package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_app_cfg")
public class ApplicationCfg implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String KEY_API_CHANGE_PWD = "API_CHANGE_PWD";	
	public static final String KEY_API_ALARM_SUBSCRIPTION = "API_ALARM_SUBSCRIPTION";
	public static final String KEY_API_ALARM_UNSUBSCRIPTION = "API_ALARM_UNSUBSCRIPTION";		
	public static final String KEY_API_LOGIN = "API_LOGIN";	
	public static final String KEY_API_KEEP_ALIVE = "API_KEEP_ALIVE";
	public static final String KEY_API_LOGOUT = "API_LOGOUT";
	public static final String KEY_API_QUERY_CAMERA = "API_QUERY_CAMERA";
	public static final String KEY_API_ADD_FACE_LIST = "API_ADD_FACE_LIST";
	public static final String KEY_API_REMOVE_FACE_LIST = "API_REMOVE_FACE_LIST";
	public static final String KEY_API_QUERY_FACE_LIST = "API_QUERY_FACE_LIST";
	public static final String KEY_API_ADD_CAMERA_CHECK_POINT = "API_ADD_CAMERA_CHECK_POINT";
	public static final String KEY_API_QUERY_CAMERA_CHECK_POINT = "API_QUERY_CAMERA_CHECK_POINT";	
	public static final String KEY_API_REMOVE_CAMERA_CHECK_POINT = "API_REMOVE_CAMERA_CHECK_POINT";	
	public static final String KEY_API_ADD_CAMERA_TO_CHECK_POINT = "API_ADD_CAMERA_TO_CHECK_POINT";
	public static final String KEY_API_QUERY_CAMERA_FROM_CHECK_POINT = "API_QUERY_CAMERA_FROM_CHECK_POINT";
	public static final String KEY_API_REMOVE_CAMERA_FROM_CHECK_POINT = "API_REMOVE_CAMERA_FROM_CHECK_POINT";	
	public static final String KEY_API_ADD_SUSPECT_TASK = "API_ADD_SUSPECT_TASK";
	public static final String KEY_API_REMOVE_SUSPECT_TASK = "API_REMOVE_SUSPECT_TASK";
	public static final String KEY_API_QUERY_SUSPECT_TASK = "API_QUERY_SUSPECT_TASK";
	public static final String KEY_API_GET_UPLOAD_URL = "API_GET_UPLOAD_URL";
	public static final String KEY_API_PUBLISH_UPLOAD_FILE = "API_PUBLISH_UPLOAD_FILE";
	public static final String KEY_API_PUBLISH_UPLOAD_FILE_PARAM_SSID = "API_PUBLISH_UPLOAD_FILE_PARAM_SSID";
	public static final String KEY_API_ADD_FACE_TO_LIST = "API_ADD_FACE_TO_LIST";
	public static final String KEY_API_MODIFY_FACE_TO_LIST = "API_MODIFY_FACE_TO_LIST";
	public static final String KEY_API_REMOVE_FACE_FROM_LIST = "API_REMOVE_FACE_FROM_LIST";
	public static final String KEY_API_QUERY_FACE_FROM_LIST = "API_QUERY_FACE_FROM_LIST";
	public static final String KEY_API_QUERY_PERSON = "API_QUERY_PERSON";
	public static final String KEY_API_QUERY_PERSON_DB_PHOTO = "API_QUERY_PERSON_DB_PHOTO";
	public static final String KEY_API_NO_OF_RETRY = "KEY_API_NO_OF_RETRY";
	public static final String KEY_API_ADD_INTELLIGENT_ANALYSIS_TASKS_BATCHS  = "API_ADD_INTELLIGENT_ANALYSIS_TASKS_BATCHS";
	
	
	public static final String KEY_IMAGE_THUMBNAIL_SIZE = "KEY_IMAGE_THUMBNAIL_SIZE";
	public static final String KEY_IMAGE_UNKNOWN_PERSON = "KEY_IMAGE_UNKNOWN_PERSON";
	
	public static final String GUI_GRID_ROW_PER_PAGE = "GUI_GRID_ROW_PER_PAGE";
	public static final String GUI_GRID_PAGE_JUMP = "GUI_GRID_PAGE_JUMP";
	
	//ws error code
	public static final String KEY_WS_CODE_SUCCESS = "WS_CODE_SUCCESS";
	public static final String KEY_WS_CODE_FIRST_LOGIN = "WS_CODE_FIRST_LOGIN";
	public static final String KEY_WS_CODE_PLS_LOGIN_FIRST = "WS_CODE_PLS_LOGIN_FIRST";
	
	public static final String KEY_THREAD_NO_ALARM_TO_DB = "THREAD_NO_ALARM_TO_DB";
	public static final String KEY_THREAD_NO_ALARM_NOTIFICATION = "THREAD_NO_ALARM_NOTIFICATION";
	public static final String KEY_NOT_SEND_ALARM_IF_OLDER_THEN = "NOT_SEND_ALARM_IF_OLDER_THEN";
	
	public static final String KEY_ALARM_HIST_NOOFDAY_FILTER = "ALARM_HIST_NOOFDAY_FILTER";
	public static final String KEY_ALARM_ACTION_PLUGIN_DIRECTORY = "ALARM_ACTION_PLUGIN_DIRECTORY";
	public static final String KEY_ALARM_LIMIT_PRINT_LOG = "ALARM_LIMIT_PRINT_LOG";
	
	public static final String KEY_HTTP_CONNECTION_TIMEOUT = "HTTP_CONNECTION_TIMEOUT";
	public static final String KEY_GATE_IP_SERVICE = "GATE_IP_SERVICE";
	
	public static final String KEY_HW_KEEP_ALIVE_SERVICE = "HW_KEEP_ALIVE_SERVICE";
	
	
	
	//param
	public static final String API_PATH_PARAMETER = "{PATH_PARAM}";
	
	@Id
	@Column(name="appKey",length=50)	
	private String appKey;
	
	@Column(name="appValue1",length=200)
	private String appValue1;
	
	@Column(name="appValue2",length=200)
	private String appValue2;
	
	@Column(name="appValue3",length=200)
	private String appValue3;		
	
	@Lob
	@Column(name="appLobValue")
	private String appLobValue;
	
	@Column(name="appDesc",length=200)
	private String appDesc;
			
	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateCreated", updatable=false)	
	private Date dateCreated;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dateUpdated", nullable=false)	
	private Date dateUpdated;
	
	@Column(name="userCreated", updatable=false)
	String userCreated;
	@Column(name="userUpdated", nullable=false)
	String userUpdated;
	
	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppValue1() {
		return appValue1;
	}

	public void setAppValue1(String appValue1) {
		this.appValue1 = appValue1;
	}

	public String getAppValue2() {
		return appValue2;
	}

	public void setAppValue2(String appValue2) {
		this.appValue2 = appValue2;
	}

	public String getAppValue3() {
		return appValue3;
	}

	public void setAppValue3(String appValue3) {
		this.appValue3 = appValue3;
	}

	@PrePersist
	protected void onCreate() {
		this.dateUpdated = this.dateCreated = new Date();
	}

    @PreUpdate
    protected void onUpdate() {
   	 	this.dateUpdated = new Date();
    }

	public String getAppLobValue() {
		return appLobValue;
	}

	public void setAppLobValue(String appLobValue) {
		this.appLobValue = appLobValue;
	}

}
