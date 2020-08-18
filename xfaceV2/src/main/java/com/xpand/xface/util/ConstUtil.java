package com.xpand.xface.util;

public class ConstUtil {
	public static final byte BYTE_ZERO_VALUE = 0;
	public static final byte BYTE_ONE_VALUE = 1;	
	
	public static final byte INTEGER_FALSE_VALUE = 0;
	public static final byte INTEGER_TRUE_VALUE = 1;
	
	public static final String NO_IMAGE_DISPLAY = "/xFace/dist/img/noimage.gif";
	
	public static final String MQ_TOPIC_SERVER_TO_USER = "/topic/stu/";
	public static final String MQ_TOPIC_USER_TO_SERVER = "/topic/uts/";
	public static final String MQ_WELCOME_MSG_FROM_SERVER = "HELO_FROM_SERVER";
	public static final String MQ_GOODBYE_MSG_FROM_SERVER = "GOODBYE_FROM_SERVER";
	public static final String WEBSOCKET_ENDPOINT = "/websock";
	
	public static final int WEBSOCKET_MODULE_PERSON_TRACE = 0;
	public static final int WEBSOCKET_MODULE_ALARM_MONITOR = 1;
	public static final int WEBSOCKET_MODULE_LANDING_PAGE = 2;
	
//	public static final String UNKNOW_CERTIFICATE_NO = "____XXXX____YYYY";
	
	public static final String UNKNOWN_PERSON_FULLNAME = "Unknown";
	public static final String UNKNOWN_PERSON_CATEGORY = "Unknown";
	public static final String UNKNOWN_PERSON_TITLE = "Unknown";
	public static final String UNKNOWN_PERSON_CERTIFICATE_NO = "Unknown";	
	public static final String UNKNOWN_PERSON_CERTIFICATE_TYPE = "Unknown";
	public static final String UNKNOWN_IPC_CODE = "Unknown";
	public static final String UNKNOWN_GATEINFO_CODE = "Unknown";
	public static final String UNKNOWN_PERSON_NATIONALITY = "Unknown";
	public static final String UNKNOW_PERSON_CONTACTNO = "Unknow";
	public static final String UNKNOWN_PERSON_DBPHOTO = "Unknown";
	public static final String UNKNOWN_BOAT_CODE = "Unknown";
	public static final String UNKNOWN_VISIT_DATE_CODE = "Unknown";	
	
	public static final int GET_ALARM_BY_ID = 0;
	public static final int GET_ALARM_BY_CODE = 1;
	
	public static final String STRING_ID_DELIMINATOR = ",";
	
	public static final long SCHEDULE_CHECK_WEBSOCK_DISCONNECT = 5000;
	
	//match unmatch alarm history condition
	public static final String ALARM_HISTORY_MATCH_CONDITION = "match";
	public static final String ALARM_HISTORY_UNMATCH_CONDITION = "unmatch";		
	public static final String ALARM_HISTORY_PERSON_REGISTER = "register";
	public static final String ALARM_HISTORY_PERSON_VISIT = "visit";
	
	
	//manu and sub menu
	public static final String MAIN_MENU_DASHBOARD = "mnuDB";
	public static final String MAIN_MENU_DASHBOARD_LANDING_PAGE = "mnuDBLP";
	public static final String MAIN_MENU_DASHBOARD_DAILY_STATISTICS = "mnuDBDS";    
    public static final String MAIN_MENU_DASHBOARD_DAILY_STATISTICS_BY_TIME = "mnuDBDSBT";
	public static final String MAIN_MENU_DASHBOARD_VISITOR_TIME = "mnuDBDVT";
	public static final String MAIN_MENU_DASHBOARD_VISITOR_GATE_TIME = "mnuDBDVGT";
	public static final String MAIN_MENU_DASHBOARD_FACE_TIME = "mnuDBDFT";
	public static final String MAIN_MENU_DASHBOARD_FACE_GATE_TIME = "mnuDBDFGT";
	public static final String MAIN_MENU_DASHBOARD_VISITOR_BOAT_TIME = "mnuDBDVBT";
	
    
    public static final String MAIN_MENU_DASHBOARD_GATE_STATISTICS = "mnuDBGS";
    public static final String MAIN_MENU_PERSON = "mnuPS";
    public static final String MAIN_MENU_PERSON_REGISTER = "mnuPSRG";
	public static final String MAIN_MENU_PERSON_ALARM_HISTORY = "mnuPSAH";
    public static final String MAIN_MENU_PERSON_ALARM_MONITOR = "mnuPSAM";
    public static final String MAIN_MENU_PERSON_ALARM_MONITOR_IDV ="mnuPSAMIDV";
    public static final String MAIN_MENU_PERSON_PERSON_TRACE = "mnuPSPT";
    public static final String MAIN_MENU_USER_MNG = "mnuUM";
    public static final String MAIN_MENU_USER_MNG_USER = "mnuUMUS";
    public static final String MAIN_MENU_USER_MNG_ROLE = "mnuUMRL";
    public static final String MAIN_MENU_CFG = "mnuCFG";
    public static final String MAIN_MENU_CFG_APP = "mnuCFGAPP";
    public static final String MAIN_MENU_CFG_GATE_INFO = "mnuCFGGI";
    public static final String MAIN_MENU_CFG_BOAT_INFO = "mnuCFGBOAT";
    public static final String MAIN_MENU_CFG_IPC = "mnuCFGIC";

    public static final String MAIN_MENU_HTML_PAGE = "mnuHTML";
    public static final String MAIN_MENU_PERSON_TITLE = "mnuPERSONTIT";
    public static final String MAIN_MENU_PERSON_CER ="mnuPERSONCER";
    public static final String MAIN_MENU_PERSON_CAT ="mnuPERSONCAT";
    public static final String MAIN_MENU_PERSON_NAT ="mnuPERSONNAT";
    public static final String MAIN_MENU_LOCATION_MAP ="mnuLOCATION";

    public static final String USER_ROLE_FORCE_CHANGE_PWD_CONFIG = "FORCE_CHANGE_PWD";
    public static final String USER_ROLE_FORCE_CHANGE_PWD_USAGE = "ROLE_FORCE_CHANGE_PWD";
    
    //dashboard by time
    public static final String REPORT_NO_PASSENGER_BY_TIME_CODE = "RNPBT";
    public static final String REPORT_NO_PASSENGER_BY_TIME_DESC = "Number of passenger";
    public static final String REPORT_NO_PASSENGER_GATE_BY_TIME_CODE = "RNPGBT";
    public static final String REPORT_NO_PASSENGER_GATE_BY_TIME_DESC = "Number of passenger by gate";
    public static final String REPORT_NO_FACE_BY_TIME_CODE = "RNFBT";
    public static final String REPORT_NO_FACE_BY_TIME_DESC = "Number of face";
    public static final String REPORT_NO_FACE_GATE_BY_TIME_CODE = "RNFGBT";
    public static final String REPORT_NO_FACE_GATE_BY_TIME_DESC = "Number of face by gate";
    public static final String REPORT_NO_PASSENGER_BOAT_BY_TIME_CODE = "RNPBBT";
    public static final String REPORT_NO_PASSENGER_BOAT_BY_TIME_DESC = "Number of passenger by boat";
    
    
    //direction for add or remove person on VCM
    public static final byte VCM_DIRECTION_ADD = 0;
    public static final byte VCM_DIRECTION_REMOVE = 1;
    
    public static final String ACTION_COMMAND_ADD = "ADD";
	public static final String ACTION_COMMAND_EDIT = "EDIT";
	
	public static final String MY_VALUE_TEST = "h@LlOM@t0";
		
}
