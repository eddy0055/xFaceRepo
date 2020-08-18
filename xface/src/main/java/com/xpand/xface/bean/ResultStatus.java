package com.xpand.xface.bean;

public class ResultStatus {
	public static final String SUCCESS_CODE = "0";
	public static final String SUCCESS_DESC = "Success";
	public static final String SUCCESS_UPDATE_CUSTOMER_CODE = "-99";
	public static final String SUCCESS_UPDATE_CUSTOMER_DESC = "Update customer success";
	public static final String SUCCESS_ADDNEW_CUSTOMER_CODE = "0";
	public static final String SUCCESS_ADDNEW_CUSTOMER_DESC = "AddNew customer success";
	

	public static final String HW_WS_SUCCESS_CODE = "0";
	public static final String HW_WS_SUCCESS_DESC = "Success";		

	public static final String XFACE_SERVER_RUNNING_CODE = "1";
	public static final String XFACE_SERVER_RUNNING_DESC = "XFace server still running";
	public static final String XFACE_SERVER_STOP_CODE = "2";
	public static final String XFACE_SERVER_STOP_DESC = "XFace server stop";
	
	public static final String DB_UPDATE_ERROR_CODE = "-1";
	public static final String DB_UPDATE_ERROR_DESC = "fail to update infomation to database";
	
	public static final String MANDATORY_REQUIRE_ERROR_CODE = "-2";
	public static final String MANDATORY_REQUIRE_ERROR_DESC = "Mandatory field is require";
	public static final String NO_DATA_FOUND_ERROR_CODE = "-3";
	public static final String NO_DATA_FOUND_ERROR_DESC = "No data found";
	public static final String CMD_NOT_SUPPORT_ERROR_CODE = "-4";
	public static final String CMD_NOT_SUPPORT_ERROR_DESC = "Command is not support";
	
	public static final String HW_CHANGE_PWD_ERROR_CODE = "-5";
	public static final String HW_CHANGE_PWD_ERROR_DESC = "VCM server request to change password";
	
	public static final String HW_CONSUME_WS_ERROR_CODE = "-6";
	public static final String HW_CONSUME_WS_ERROR_DESC = "Fail to consume web service";
	
	public static final String HW_WS_LOGIN_REQUIRE_ERROR_CODE = "-7";
	public static final String HW_WS_LOGIN_REQUIRE_ERROR_DESC = "please login first";
	public static final String FILE_NOT_FOUND_ERROR_CODE = "-8";
	public static final String FILE_NOT_FOUND_ERROR_DESC = "file not found";
	public static final String ALARM_PARSER_ERROR_CODE = "-9";
	public static final String ALARM_PARSER_ERROR_DESC = "alarm content parser error";
	public static final String ALARM_ACTION_JAR_NOTFOUND_ERROR_CODE = "-10";
	public static final String ALARM_ACTION_JAR_NOTFOUND_ERROR_DESC = "jar file for alarm action not found";
	public static final String ALARM_ACTION_CLASS_NOTFOUND_ERROR_CODE = "-11";
	public static final String ALARM_ACTION_CLASS_NOTFOUND_ERROR_DESC = "class for alarm action not found";
	public static final String JSON_PARSER_ERROR_CODE = "-12";
	public static final String JSON_PARSER_ERROR_DESC = "json parser error";
	public static final String HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE = "-13";
	public static final String HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_DESC = "Reach maximum retry logon limit";
	public static final String ALARM_NOT_SEND_NO_IPC_CFG_ERROR_CODE = "-14";
	public static final String ALARM_NOT_SEND_NO_IPC_CFG_ERROR_DESC = "Ignore alarm coz IPC not configure";
	public static final String ALARM_NOT_SEND_UNKNOWN_PERSON_CFG_ERROR_CODE = "-15";
	public static final String ALARM_NOT_SEND_UNKNOWN_PERSON_CFG_ERROR_DESC = "Ignore alarm coz IPC unknown person configure";
	public static final String ALARM_NOT_SEND_TIME_CFG_ERROR_CODE = "-16";
	public static final String ALARM_NOT_SEND_TIME_CFG_ERROR_DESC = "Ignore alarm coz time configure";
	public static final String ALARM_NOT_SEND_AGE_CFG_ERROR_CODE = "-17";
	public static final String ALARM_NOT_SEND_AGE_CFG_ERROR_DESC = "Ignore alarm coz alarm age configure";
	public static final String ALARM_NOT_SEND_NO_IPCG_ERROR_CODE = "-18";
	public static final String ALARM_NOT_SEND_NO_IPCG_ERROR_DESC = "Ignore alarm coz no IPCG for IPC";
	public static final String ALARM_NOT_SEND_NO_MATCH_USER_ERROR_CODE = "-19";
	public static final String ALARM_NOT_SEND_NO_MATCH_USER_ERROR_DESC = "Ignore alarm coz no user configure to receive alarm";
	public static final String ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_CODE = "-20";
	public static final String ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_DESC = "Person Category not found";
	public static final String ENTITY_PERSON_CERTIFICATION_NOT_FOUND_ERROR_CODE = "-21";
	public static final String ENTITY_PERSON_CERTIFICATION_NOT_FOUND_ERROR_DESC = "Person Certification not found";
	public static final String START_XFACE_PARTIAL_FAIL_ERROR_CODE = "-22";
	public static final String START_XFACE_PARTIAL_FAIL_ERROR_DESC = "Partial fail to start xface server. Some suspectId could not subscribe";
	public static final String START_XFACE_TOTALLY_FAIL_ERROR_CODE = "-23";
	public static final String START_XFACE_TOTALLY_FAIL_ERROR_DESC = "Totally fail to start xface server.";
	public static final String START_GATE_ADDRESS_FAIL_ERROR_CODE = "-24";
	public static final String START_GATE_ADDRESS_FAIL_ERROR_DESC = "Start gate service fail bind address.";
	public static final String RECORD_ALREADY_EXIST_ERROR_CODE = "-25";
	public static final String RECORD_ALREADY_EXIST_ERROR_DESC = "Record already exist.";
	
	public static final String UNEXPECTED_ERROR_CODE = "-999";
	public static final String UNEXPECTED_ERROR_DESC = "Unexpected error";
	
	
	
	private String statusCode=SUCCESS_CODE;
	private String statusDesc=SUCCESS_DESC;
	public ResultStatus() {
		
	}
	public ResultStatus(String statusCode, String moreInfo) {
		this.setStatusCode(statusCode, moreInfo);
	}
	public String getStatusCode() {
		return statusCode;
	}	
	public void setStatusCode(String statusCode, String statusDesc, String moreInfo) {
		this.statusCode = statusCode;
		this.statusDesc = statusDesc + (moreInfo==null ? "" : "["+moreInfo+"]");
	}
	public void setStatusCode(String statusCode, String moreInfo) {
		this.statusCode = statusCode;
		if (this.statusCode.equals(ResultStatus.SUCCESS_CODE)) {
			this.statusDesc = ResultStatus.SUCCESS_DESC;
		}else if (this.statusCode.equals(ResultStatus.DB_UPDATE_ERROR_CODE)) {
			this.statusDesc = ResultStatus.DB_UPDATE_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.MANDATORY_REQUIRE_ERROR_CODE)) {
			this.statusDesc = ResultStatus.MANDATORY_REQUIRE_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.NO_DATA_FOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.NO_DATA_FOUND_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.CMD_NOT_SUPPORT_ERROR_CODE)) {
			this.statusDesc = ResultStatus.CMD_NOT_SUPPORT_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.XFACE_SERVER_RUNNING_CODE)) {
			this.statusDesc = ResultStatus.XFACE_SERVER_RUNNING_CODE;
		}else if (this.statusCode.equals(ResultStatus.XFACE_SERVER_STOP_CODE)) {
			this.statusDesc = ResultStatus.XFACE_SERVER_STOP_DESC;
		}else if (this.statusCode.equals(ResultStatus.HW_CHANGE_PWD_ERROR_CODE)) {
			this.statusDesc = ResultStatus.HW_CHANGE_PWD_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.HW_WS_SUCCESS_CODE)) {
			this.statusDesc = ResultStatus.HW_WS_SUCCESS_DESC;
		}else if (this.statusCode.equals(ResultStatus.HW_CONSUME_WS_ERROR_CODE)) {
			this.statusDesc = ResultStatus.HW_CONSUME_WS_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_CODE)) {
			this.statusDesc = ResultStatus.HW_WS_LOGIN_REQUIRE_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.FILE_NOT_FOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.FILE_NOT_FOUND_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.UNEXPECTED_ERROR_CODE)) {
			this.statusDesc = ResultStatus.UNEXPECTED_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_PARSER_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_PARSER_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_ACTION_CLASS_NOTFOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_ACTION_CLASS_NOTFOUND_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_ACTION_JAR_NOTFOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_ACTION_JAR_NOTFOUND_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_ACTION_CLASS_NOTFOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_ACTION_CLASS_NOTFOUND_ERROR_CODE;
		}else if (this.statusCode.equals(ResultStatus.JSON_PARSER_ERROR_CODE)) {
			this.statusDesc = ResultStatus.JSON_PARSER_ERROR_CODE;
		}else if (this.statusCode.equals(ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_CODE)) {
			this.statusDesc = ResultStatus.HW_MAXIMUM_RETRY_LOGON_LIMIT_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_NOT_SEND_NO_IPC_CFG_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_NOT_SEND_NO_IPC_CFG_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_NOT_SEND_UNKNOWN_PERSON_CFG_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_NOT_SEND_UNKNOWN_PERSON_CFG_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_NOT_SEND_TIME_CFG_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_NOT_SEND_TIME_CFG_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_NOT_SEND_AGE_CFG_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_NOT_SEND_AGE_CFG_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_NOT_SEND_NO_IPCG_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_NOT_SEND_NO_IPCG_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ALARM_NOT_SEND_NO_MATCH_USER_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ALARM_NOT_SEND_NO_MATCH_USER_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ENTITY_PERSON_CATEGORY_NOT_FOUND_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.ENTITY_PERSON_CERTIFICATION_NOT_FOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ENTITY_PERSON_CERTIFICATION_NOT_FOUND_ERROR_DESC;						
		}else if (this.statusCode.equals(ResultStatus.START_XFACE_PARTIAL_FAIL_ERROR_CODE)) {
			this.statusDesc = ResultStatus.START_XFACE_PARTIAL_FAIL_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.START_XFACE_TOTALLY_FAIL_ERROR_CODE)) {
			this.statusDesc = ResultStatus.START_XFACE_TOTALLY_FAIL_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.SUCCESS_UPDATE_CUSTOMER_CODE)) {
			this.statusDesc = ResultStatus.SUCCESS_UPDATE_CUSTOMER_DESC;
		}else if (this.statusCode.equals(ResultStatus.START_GATE_ADDRESS_FAIL_ERROR_CODE)) {
			this.statusDesc = ResultStatus.START_GATE_ADDRESS_FAIL_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.RECORD_ALREADY_EXIST_ERROR_CODE)) {
			this.statusDesc = ResultStatus.RECORD_ALREADY_EXIST_ERROR_DESC;
		}
		this.statusDesc = this.statusDesc + (moreInfo==null ? "" : "["+moreInfo+"]");		
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	@Override
	public String toString() {
		return "ResultStatus [statusCode=" + statusCode + ", statusDesc=" + statusDesc + "]";
	}

}
