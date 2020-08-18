package com.xpand.xface.bean;

public class ResultStatus {
	public static final String SUCCESS_CODE = "0";
	public static final String SUCCESS_DESC = "Success";

	public static final String HW_WS_SUCCESS_CODE = "0";
	public static final String HW_WS_SUCCESS_DESC = "Success";
	
	public static final String HW_WS_SDK_SUCCESS_CODE = "0";
	public static final String HW_WS_SDK_SUCCESS_DESC = "Success";
	
	public static final String SUCCESS_UPDATE_CUSTOMER_CODE = "-99";
	public static final String SUCCESS_UPDATE_CUSTOMER_DESC = "Update customer success";
	public static final String SUCCESS_ADDNEW_CUSTOMER_CODE = "0";
	public static final String SUCCESS_ADDNEW_CUSTOMER_DESC = "AddNew customer success";

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
	public static final String ENTITY_PERSON_CERTIFICATE_NOT_FOUND_ERROR_CODE = "-21";
	public static final String ENTITY_PERSON_CERTIFICATE_NOT_FOUND_ERROR_DESC = "Person Certificate not found";
	public static final String START_XFACE_PARTIAL_FAIL_ERROR_CODE = "-22";
	public static final String START_XFACE_PARTIAL_FAIL_ERROR_DESC = "Partial fail to start xface server. Some suspectId could not subscribe";
	public static final String START_XFACE_TOTALLY_FAIL_ERROR_CODE = "-23";
	public static final String START_XFACE_TOTALLY_FAIL_ERROR_DESC = "Totally fail to start xface server.";		
	public static final String START_GATE_ADDRESS_FAIL_ERROR_CODE = "-24";
	public static final String START_GATE_ADDRESS_FAIL_ERROR_DESC = "Start gate service fail bind address.";
	public static final String RECORD_ALREADY_EXIST_ERROR_CODE = "-25";
	public static final String RECORD_ALREADY_EXIST_ERROR_DESC = "Record already exist.";
	public static final String HW_CFG_NOT_READY_ERROR_CODE = "-26";
	public static final String HW_CFG_NOT_READY_ERROR_DESC = "HW configuration in xface is not ready.";
	
	public static final String DB_READ_FAIL_ERROR_CODE = "-27";
	public static final String DB_READ_FAIL_ERROR_DESC = "Fail to read data from table.";
	public static final String NO_WSH_NEED_TO_SEND_ALARM_CODE = "-28";
	public static final String NO_WSH_NEED_TO_SEND_ALARM_DESC = "No web socket need to send alarm";
	public static final String FUNCTION_IS_NOT_SUPPORT_CODE = "-29";
	public static final String FUNCTION_IS_NOT_SUPPORT_DESC = "function is not support";
	public static final String CHANGE_PWD_OLD_PASSWORD_NOT_MATCH_CODE = "-30";
	public static final String CHANGE_PWD_OLD_PASSWORD_NOT_MATCH_DESC = "old password not match";
	public static final String FORGET_PWD_INVALID_USERNAME_CODE = "-31";
	public static final String FORGET_PWD_INVALID_USERNAME_DESC = "invalid username";
	
	public static final String PWD_CHECK_SIZE_CODE = "-32";
	public static final String PWD_CHECK_SIZE_DESC = "Password should be less than 15 and more than 8 characters in length";
	public static final String PWD_CHECK_USER_PWD_CODE = "-33";
	public static final String PWD_CHECK_USER_PWD_DESC = "Password Should not be same as user name";
	public static final String PWD_CHECK_UPPER_CASE_CODE = "-34";
	public static final String PWD_CHECK_UPPER_CASE_DESC = "Password should contain atleast one upper case alphabet";
	public static final String PWD_CHECK_LOWER_CASE_CODE = "-35";
	public static final String PWD_CHECK_LOWER_CASE_DESC = "Password should contain atleast one lower case alphabet";
	public static final String PWD_CHECK_NUMBER_CODE = "-36";
	public static final String PWD_CHECK_NUMBER_DESC = "Password should contain atleast one number";
	public static final String PWD_CHECK_SPECIAL_CHAR_CODE = "-37";
	public static final String PWD_CHECK_SPECIAL_CHAR_DESC = "Password should contain atleast one special character";	
	public static final String PROCESS_STILL_RUNNING_CODE = "-38";
	public static final String PROCESS_STILL_RUNNING_DESC = "Process still running";
	public static final String INVALID_DASHBOARD_PARAMETER_CODE = "-39";
	public static final String INVALID_DASHBOARD_PARAMETER_DESC = "Invalid dashboard parameter";
	
	public static final String INVALID_SYSTEM_ID_CODE = "-40";
	public static final String INVALID_SYSTEM_ID_DESC = "Invalid system Id";
	public static final String INVALID_ALLOW_IP_ADDRESS_CODE = "-41";
	public static final String INVALID_ALLOW_IP_ADDRESS_DESC = "Invalid allow IP address";	
	
	public static final String HW_WS_SDK_LOGIN_REQUIRE_ERROR_CODE = "-42";
	public static final String HW_WS_SDK_LOGIN_REQUIRE_ERROR_DESC = "please login first";
	public static final String HW_SDK_CHANGE_PWD_ERROR_CODE = "-43";
	public static final String HW_SDK_CHANGE_PWD_ERROR_DESC = "VCN SDK server request to change password";
	public static final String NOT_SUPPORT_VCN_ALARM_ERROR_CODE = "-44";
	public static final String NOT_SUPPORT_VCN_ALARM_ERROR_DESC = "VCN alarm type is not support";
	public static final String FAIL_TO_REMOVE_PERSON_FROM_VCM_ZK_CODE = "-45";
	public static final String FAIL_TO_REMOVE_PERSON_FROM_VCM_ZK_DESC = "Fail to remove person from VCM and ZK system";
	public static final String FAIL_TO_REMOVE_PERSON_FROM_VCM_CODE = "-46";
	public static final String FAIL_TO_REMOVE_PERSON_FROM_VCM_DESC = "Fail to remove person from VCM system";
	public static final String FAIL_TO_REMOVE_PERSON_FROM_ZK_CODE = "-47";
	public static final String FAIL_TO_REMOVE_PERSON_FROM_ZK_DESC = "Fail to remove person from ZK system";	
	public static final String FAIL_TO_ADD_PERSON_TO_VCM_ZK_CODE = "-48";
	public static final String FAIL_TO_ADD_PERSON_TO_VCM_ZK_DESC = "Fail to add person to VCM and ZK system";
	public static final String FAIL_TO_ADD_PERSON_TO_VCM_CODE = "-49";
	public static final String FAIL_TO_ADD_PERSON_TO_VCM_DESC = "Fail to add person to VCM system";
	public static final String FAIL_TO_ADD_PERSON_TO_ZK_CODE = "-50";
	public static final String FAIL_TO_ADD_PERSON_TO_ZK_DESC = "Fail to add person to ZK system";
	public static final String BOAT_DRIVER_CARD_NO_NOT_LINK_BOAT_CODE = "-51";
	public static final String BOAT_DRIVER_CARD_NO_NOT_LINK_BOAT_DESC = "Boat driver card no associate to boat";
	public static final String HW_GATE_CODE_NOT_FOUND_CODE = "-52";
	public static final String HW_GATE_CODE_NOT_FOUND_DESC = "Gate code not found";
	public static final String BOAT_CODE_NOT_FOUND_CODE = "-53";
	public static final String BOAT_CODE_NOT_FOUND_DESC = "Boat not found";
	
	public static final String FAIL_TO_ADD_BOAT_TO_ZK_CODE ="-54";
	public static final String FAIL_TO_ADD_BOAT_TO_ZK_DESC = "Fail to connect ZK system";
	
		
	public static final String UNEXPECTED_ERROR_CODE = "-999";
	public static final String UNEXPECTED_ERROR_DESC = "Unexpected error";
	
	
	private String statusCode=SUCCESS_CODE;
	private String statusDesc=SUCCESS_DESC;
	private String statusParam = "";
	public ResultStatus() {
		
	}
	public ResultStatus(String statusCode, String moreInfo) {
		this.setStatusCode(statusCode, moreInfo);
	}
	public void setStatusCode(String statusCode, String statusDesc, String moreInfo) {
		this.statusCode = statusCode;
		this.statusDesc = statusDesc + (moreInfo==null ? "" : "["+moreInfo+"]");
	}
	public String getStatusCode() {
		return statusCode;
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
		}else if (this.statusCode.equals(ResultStatus.ENTITY_PERSON_CERTIFICATE_NOT_FOUND_ERROR_CODE)) {
			this.statusDesc = ResultStatus.ENTITY_PERSON_CERTIFICATE_NOT_FOUND_ERROR_DESC;						
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
		}else if (this.statusCode.equals(ResultStatus.HW_CFG_NOT_READY_ERROR_CODE)) {
			this.statusDesc = ResultStatus.HW_CFG_NOT_READY_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.DB_READ_FAIL_ERROR_CODE)) {
			this.statusDesc = ResultStatus.DB_READ_FAIL_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.NO_WSH_NEED_TO_SEND_ALARM_CODE)) {
			this.statusDesc = ResultStatus.NO_DATA_FOUND_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.FUNCTION_IS_NOT_SUPPORT_CODE)) {
			this.statusDesc = ResultStatus.FUNCTION_IS_NOT_SUPPORT_DESC;
		}else if (this.statusCode.equals(ResultStatus.CHANGE_PWD_OLD_PASSWORD_NOT_MATCH_CODE)) {
			this.statusDesc = ResultStatus.CHANGE_PWD_OLD_PASSWORD_NOT_MATCH_DESC;
		}else if (this.statusCode.equals(ResultStatus.FORGET_PWD_INVALID_USERNAME_CODE)) {
			this.statusDesc = ResultStatus.FORGET_PWD_INVALID_USERNAME_DESC;
		}else if (this.statusCode.equals(ResultStatus.PWD_CHECK_LOWER_CASE_CODE)) {
			this.statusDesc = ResultStatus.PWD_CHECK_LOWER_CASE_DESC;
		}else if (this.statusCode.equals(ResultStatus.PWD_CHECK_NUMBER_CODE)) {
			this.statusDesc = ResultStatus.PWD_CHECK_NUMBER_DESC;
		}else if (this.statusCode.equals(ResultStatus.PWD_CHECK_SIZE_CODE)) {
			this.statusDesc = ResultStatus.PWD_CHECK_SIZE_DESC;
		}else if (this.statusCode.equals(ResultStatus.PWD_CHECK_SPECIAL_CHAR_CODE)) {
			this.statusDesc = ResultStatus.PWD_CHECK_SPECIAL_CHAR_DESC;
		}else if (this.statusCode.equals(ResultStatus.PWD_CHECK_UPPER_CASE_CODE)) {
			this.statusDesc = ResultStatus.PWD_CHECK_UPPER_CASE_DESC;
		}else if (this.statusCode.equals(ResultStatus.PWD_CHECK_USER_PWD_CODE)) {
			this.statusDesc = ResultStatus.PWD_CHECK_USER_PWD_DESC;
		}else if (this.statusCode.equals(ResultStatus.PROCESS_STILL_RUNNING_CODE)) {
			this.statusDesc = ResultStatus.PROCESS_STILL_RUNNING_DESC;
		}else if (this.statusCode.equals(ResultStatus.INVALID_DASHBOARD_PARAMETER_CODE)) {
			this.statusDesc = ResultStatus.INVALID_DASHBOARD_PARAMETER_DESC;			
		}else if (this.statusCode.equals(ResultStatus.INVALID_SYSTEM_ID_CODE)) {
			this.statusDesc = ResultStatus.INVALID_SYSTEM_ID_DESC;
		}else if (this.statusCode.equals(ResultStatus.INVALID_ALLOW_IP_ADDRESS_CODE)) {
			this.statusDesc = ResultStatus.INVALID_ALLOW_IP_ADDRESS_DESC;
		}else if (this.statusCode.equals(ResultStatus.HW_WS_SDK_LOGIN_REQUIRE_ERROR_CODE)) {
			this.statusDesc = ResultStatus.HW_WS_SDK_LOGIN_REQUIRE_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.HW_SDK_CHANGE_PWD_ERROR_CODE)) {
			this.statusDesc = ResultStatus.HW_SDK_CHANGE_PWD_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.NOT_SUPPORT_VCN_ALARM_ERROR_CODE)) {
			this.statusDesc = ResultStatus.NOT_SUPPORT_VCN_ALARM_ERROR_DESC;
		}else if (this.statusCode.equals(ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_VCM_ZK_CODE)) {
			this.statusDesc = ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_VCM_ZK_DESC;			
		}else if (this.statusCode.equals(ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_VCM_CODE)) {
			this.statusDesc = ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_VCM_DESC;
		}else if (this.statusCode.equals(ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_ZK_CODE)) {
			this.statusDesc = ResultStatus.FAIL_TO_REMOVE_PERSON_FROM_ZK_DESC;
		}else if (this.statusCode.equals(ResultStatus.FAIL_TO_ADD_PERSON_TO_VCM_ZK_CODE)) {
			this.statusDesc = ResultStatus.FAIL_TO_ADD_PERSON_TO_VCM_ZK_DESC;
		}else if (this.statusCode.equals(ResultStatus.FAIL_TO_ADD_PERSON_TO_VCM_CODE)) {
			this.statusDesc = ResultStatus.FAIL_TO_ADD_PERSON_TO_VCM_DESC;
		}else if (this.statusCode.equals(ResultStatus.FAIL_TO_ADD_PERSON_TO_ZK_CODE)) {
			this.statusDesc = ResultStatus.FAIL_TO_ADD_PERSON_TO_ZK_DESC;		
		}else if (this.statusCode.equals(ResultStatus.BOAT_DRIVER_CARD_NO_NOT_LINK_BOAT_CODE)) {
			this.statusDesc = ResultStatus.BOAT_DRIVER_CARD_NO_NOT_LINK_BOAT_DESC;
		}else if (this.statusCode.equals(ResultStatus.HW_GATE_CODE_NOT_FOUND_CODE)) {
			this.statusDesc = ResultStatus.HW_GATE_CODE_NOT_FOUND_DESC;
		}else if (this.statusCode.equals(ResultStatus.FAIL_TO_ADD_BOAT_TO_ZK_CODE)) {
			this.statusDesc = ResultStatus.FAIL_TO_ADD_BOAT_TO_ZK_DESC;
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
	public String getStatusParam() {
		return statusParam;
	}
	public void setStatusParam(String statusParam) {
		this.statusParam = statusParam;
	}

}
