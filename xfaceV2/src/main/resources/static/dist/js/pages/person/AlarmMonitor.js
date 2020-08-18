$(document).ready( function () {
	var divShowMonitor = $("#divShowMonitor");
	var noOfTimePortion = Number($("noOfTimePortion").text());
	var monitorOptions = {};
	var formAlarmInfo = $("#formAlarmInfo");
	var stompClient = null;
	var timerCheckConnection = null;
	var isGateInfoChange = false;
//	var noSleep = null;
	globalImageLoader =  $("#imageLoader");	
	reloadGateInfo();			
	connectWebSocket();
	timerCheckConnection = setInterval(checkWebSocketConnection, timerCheckConnectionMS); //5 sec
	//check if running on mobile device then activate NoSleep to prevent screen shutdown
//	if(navigator.userAgent.indexOf("Mobile") > 0){				
//		noSleep = new NoSleep();
//		noSleep.enable();
//	}
	/////////////////////////
	function reloadAlarmInfo(alarmCode){
		//show alarm data		
		globalAddLoader();
		$.ajax({
	        url: "/xFace/rest/rep/getAlarmByCode",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 
	        data: JSON.stringify(createReloadAlarmInfoParameter(alarmCode)),
	        success: function(queryAlarmResult) {
	        	globalIsSessionExpire(queryAlarmResult);
	        	globalWriteConsoleLog(queryAlarmResult);
	        	showAlarmInfo(queryAlarmResult);	        		        	
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	
	//reload IPC by create webFEParam and call API to get IPC
	//parameter is 1. array of gate [1,2,3], 2. array of select IPC 
	function reloadIPCFromGateInfoList(gateInfoList, ipcSelectedList){		
		var paramGateInfoList = "";			
	    $.each(gateInfoList, function(i, item){			
	    	paramGateInfoList = paramGateInfoList+ item + ",";
		});
		if (paramGateInfoList.length > 0){
			paramGateInfoList = paramGateInfoList.substr(0, paramGateInfoList.length-1);
			var webFEParam = {};
			webFEParam["gateInfoCodeList"] = paramGateInfoList; 
			reloadIPC(webFEParam, ipcSelectedList);
		}else{
			var cmbOptionIPC = $("#cmbOptionIPC");		
			cmbOptionIPC.find("option").remove().end().append("<option value=>Please select gate first</option>");
			cmbOptionIPC.selectpicker("refresh");
		}	
	}
	
	//call api for get ipc by list of gate "1,2,3"
	//param 1.webFEParam contain list of gate "1,2,3", 2> ipcSelectList = array of selected IPC [1,2,3]	
	function reloadIPC(webFEParam, ipcSelectedList){	
		//show alarm data				
		$.ajax({
	        url: "/xFace/rest/cfg/getHWIPCByGate",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 	 
	        data: JSON.stringify(webFEParam),
	        success: function(ipcList) {
	        	globalIsSessionExpire(ipcList);
	        	globalWriteConsoleLog(ipcList);
	        	showIpcList(ipcList, ipcSelectedList);	        	
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	globalClearLoader();	        	
	        }
	    });
	}	
	
	function reloadGateInfo(){
		//show alarm data				
		$.ajax({
	        url: "/xFace/rest/cfg/getAllHWGate",
	        type: "GET",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 	        
	        success: function(gateInfoList) {
	        	globalIsSessionExpire(gateInfoList);
	        	globalWriteConsoleLog(gateInfoList);
	        	showGateInfoList(gateInfoList);	        		        	
	        },
	        error: function(error){	        	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	
	function createReloadAlarmInfoParameter(alarmCode){
		var param = {};
		param["alarmCode"] = alarmCode;
		return param;
	}
	//////////////////////////
	//show face data to screen
	function showFaceToScreen(personNotification){
		var faceImage = $("#faceArea");													
		faceImage.find("div > div > ul").prepend(generateFaceImageHtml(personNotification));
		faceImage.find("div > div > ul").find("#linkFaceImage"+personNotification.alarmCode).on("click", function(event){
			event.preventDefault();
			faceImageClick(this);
		});						
		if (faceImage.find(".faceImage").length > noOfFace){
			faceImage.find(".faceImage").last().remove();
		}
	}
	
	function generateFaceImageHtml(personNotification){
		var alarmDate = globalStringYYYYMMDDHHMMSSToDate(personNotification.alarmDate).DDMMYYYY_HHMMSS();
		var uiClassBtn = null;
		var uiAside = null;		
		if (personNotification.personId===-1){
			//not match
			uiClassBtn = "ui-btn ui-li-unmatch";
			uiAside = '<p class="ui-li-aside ui-li-aside-unmatch">'+personNotification.category+'</p></a></li>';			
		}else{
			//match
			uiClassBtn = "ui-btn ui-btn-icon-right ui-icon-carat-r";
			uiAside = '<p class="ui-li-aside ui-li-aside-match">'+personNotification.category+'</p></a></li>';			
		}
		return '<li id="linkFaceImage'+personNotification.alarmCode+'" class="ui-li-has-thumb faceImage ball"><a href="#" class="'+uiClassBtn+'">' +
				'<img src="'+personNotification.livePhoto+'" class="ui-li-thumb">' +				
				'<h2 class="personName"><u>Name:</u>'+personNotification.fullName+'</h2>' +
				'<p class="alarmDate"><u>Date:</u>'+alarmDate+'</p>' +
				'<p class="gateInfoName"><u>Gate:</u>'+personNotification.gateInfoName+'</p>' +
				'<p class="ipcName"><u>CAM:</u>'+personNotification.ipcName+'</p>' + uiAside;
				
	}
	function setupMonitorOptions(){		
		monitorOptions["certificateNo"] = $("#txtOptionCertificateNo").val();
		if (monitorOptions["certificateNo"]!==""){
			$("#txtOptionFullName").val("");
		}	
		monitorOptions["fullName"] = $("#txtOptionFullName").val();
		//get gate info list
		var gateInfoList = []
		var paramGateInfoList = "";
		gateInfoList = $("#cmbOptionGateInfo").val()
	    $.each(gateInfoList, function(i, item){			
	    	paramGateInfoList = paramGateInfoList+ item + ",";
		});
		if (paramGateInfoList.length > 0){
			paramGateInfoList = paramGateInfoList.substr(0, paramGateInfoList.length-1);
		}
		monitorOptions["gateInfoCodeList"] = paramGateInfoList;
		
		//get ipc list
		var ipcList = []
		var paramIPCList = "";
		ipcList = $("#cmbOptionIPC").val()
	    $.each(ipcList, function(i, item){			
	    	paramIPCList = paramIPCList+ item + ",";
		});
		if (paramIPCList.length > 0){
			paramIPCList = paramIPCList.substr(0, paramIPCList.length-1);
		}
		monitorOptions["ipcCodeList"] = paramIPCList;
		/////////////////////////
	}			
	function showMonitorOptions(){
		setupMonitorOptions();
		var optionText = "";
		if (monitorOptions["certificateNo"]!==""){			
			optionText = "certifiate:"+monitorOptions["certificateNo"];
		}else if (monitorOptions["fullName"]!==""){
			optionText = "full Name:"+monitorOptions["fullName"];
		}
		if (monitorOptions["gateInfoCodeList"]!==""){
			if (optionText===""){
				optionText = "gate:"+monitorOptions["gateInfoCodeList"];
			}else{
				optionText += ",gate:"+monitorOptions["gateInfoCodeList"];
			}			
		}
		if (monitorOptions["ipcCodeList"]!==""){
			if (optionText===""){
				optionText = "camera:"+monitorOptions["ipcCodeList"];
			}else{
				optionText += ",camera:"+monitorOptions["ipcCodeList"];
			}			
		}		
		if (optionText===""){
			$("#linkPageOption").html("Monitor Option");
		}else{
			$("#linkPageOption").html("Monitor Option <i>("+optionText+")</i>");
		}		
	}
	function showAlarmInfo(queryAlarmResult){
		formAlarmInfo.find("#imgDBPhoto").attr("src", queryAlarmResult.dbPhoto);
		formAlarmInfo.find("#imgLivePhoto").attr("src", queryAlarmResult.livePhoto);
		formAlarmInfo.find(".numberCircle").text(queryAlarmResult.percentMatch+"%");
		formAlarmInfo.find("#txtCertificateType").val(queryAlarmResult.certificateType);
		formAlarmInfo.find("#txtCertificateNo").val(queryAlarmResult.certificateNo);		
		formAlarmInfo.find("#txtAlarmDate").val(globalStringYYYYMMDDHHMMSSToDate(queryAlarmResult.alarmDate).DDMMYYYY_HHMMSS());		
		formAlarmInfo.find("#txtTitle").val(queryAlarmResult.title);
		formAlarmInfo.find("#txtFullName").val(queryAlarmResult.fullName);
		formAlarmInfo.find("#txtCategory").val(queryAlarmResult.category);
		formAlarmInfo.find("#txtGateInfo").val(queryAlarmResult.gateInfoName);
		formAlarmInfo.find("#txtIPC").val(queryAlarmResult.ipcName);
		globalClearLoader();
		formAlarmInfo.modal("show");
	}
	
	//bind data to cmb IPC
	//param 1 is json ipc list which return from backend
	//2. is ipcSelectedList which user select before call API to get IPC list
	function showIpcList(ipcList, ipcSelectedList){
		var cmbOptionIPC = $("#cmbOptionIPC");		
		var options = [];
		$.each(ipcList, function(i, item){			
			options.push('<option value="'+item.ipcCode+'">'+item.ipcName+'</option>');			
		});
		//show dialog		
		cmbOptionIPC.html(options);
		if (ipcSelectedList!==null){
			cmbOptionIPC.selectpicker("val", ipcSelectedList);
		}		
		cmbOptionIPC.selectpicker("refresh");
	}
	
	//bind data to cmb gateinfo 
	function showGateInfoList(gateInfoList){
		var cmbOptionGateInfo = $("#cmbOptionGateInfo");		
		var options = [];
		$.each(gateInfoList, function(i, item){			
			options.push('<option value="'+item.gateCode+'">'+item.gateName+'</option>');			
		});
		//show dialog
		cmbOptionGateInfo.html(options);
		cmbOptionGateInfo.selectpicker("refresh");
	}
	
	//event of object	
	function faceImageClick(clickLI){
		if ($(clickLI).find(".ui-li-unmatch").length === 0){
			//match
			reloadAlarmInfo($(clickLI).attr("id").replace("linkFaceImage",""));
		}		
		//unmatch no show detail screen
	}

	$("#monitorOption").on("hidden.bs.collapse", function () {
		$("#txtOptionCertificateNo").val(monitorOptions["certificateNo"]);
		$("#txtOptionFullName").val(monitorOptions["fullName"]);
		if (monitorOptions["gateInfoCodeList"]===""){
			$("#cmbOptionGateInfo").selectpicker("val", "");			
			$("#cmbOptionIPC").find("option").remove().end().append("<option value=''>Please select gate first</option>");
		}else{			
			gateInfoList = monitorOptions["gateInfoCodeList"].split(",");
			$("#cmbOptionGateInfo").selectpicker("val", gateInfoList);			
		}		
		$("#cmbOptionGateInfo").selectpicker("refresh");
		
		if (monitorOptions["gateInfoCodeList"]!==""){
			gateInfoList = monitorOptions["gateInfoCodeList"].split(",");
			if (monitorOptions["ipcCodeList"]===""){
				reloadIPCFromGateInfoList(gateInfoList, null);
			}else{
				reloadIPCFromGateInfoList(gateInfoList, monitorOptions["ipcCodeList"].split(","));
			}
		}
		//reset flag in case we programming to change value of cmbGateInfo
		//system may fight event change on gate info
		isGateInfoChange = false;		
	});	
	$("#btnAlarmInfoClose").on("click", function(event){
		formAlarmInfo.modal("hide");
	});
	$("#btnOptionApply").on("click", function(event){
		connectWebSocket();
//		disconnectWebSocket();
	});
	
	$("#cmbOptionGateInfo").on("change", function(event){		
		isGateInfoChange = true;
	});
	
	//gate info hide selection
	$("#cmbOptionGateInfo").on("hidden.bs.select", function(event){
		if (!isGateInfoChange){
			return;
		}
		isGateInfoChange = false;		
		reloadIPCFromGateInfoList($("#cmbOptionGateInfo").val(), null);
	});
	
	//////////////////////////////////////////////////
	
	function checkWebSocketConnection(){		
			//0	CONNECTING	Socket has been created. The connection is not yet open.
			//1	OPEN	The connection is open and ready to communicate.
			//2	CLOSING	The connection is in the process of closing.
			//3	CLOSED
		if (stompClient===null || stompClient.ws.readyState==3){
			//this case for web socket session expire
			globalCheckSessionExpire(reconnectWebSocketConnection);			
		}	
	}
	
	//callback function after check session is not expire
	function reconnectWebSocketConnection(){
		connectWebSocket();
	}
	///////websocket
	//web socker implement
	function handleMsgFromServer(content){		
		if (content.body === webSockGoodbye){
			globalWriteConsoleLog("server force disconnect");
			disconnectWebSocket();
			//redirect to invalidsession
			window.open("/xFace/error/invalidsession","_self");
		}else if (content.body !== webSockAccept){
			var personNotification = JSON.parse(content.body);
			showFaceToScreen(personNotification);
		}		
	}
	
	//1. refresh parameter
	//2. create parameter
	//3. open connection
	function connectWebSocket() {
		showMonitorOptions();
		webFEParam = JSON.stringify({"webSocketModule": webSocketModule,"personCertificateNo": monitorOptions["certificateNo"]
						, "fullName": monitorOptions["fullName"]
						, "gateInfoCodeList":monitorOptions["gateInfoCodeList"]
						, "ipcCodeList":monitorOptions["ipcCodeList"]});
		try{									
			if (stompClient === null ||stompClient.ws.readyState === 3){
				//not null and not open				
				var webSocket = new SockJS(webSockEndPoint);				
			    stompClient = Stomp.over(webSocket);			    
			    stompClient.connect({}, function (frame) {
			    	//check for session id
			    	var sessionId = globalGetWebSocketSessionId(webSocket._transport.url);
			    	stompClient.subscribe(webSockSTU+sessionId+"/"+webSocketModule, function (msgFromServer) {
			        	//var content = JSON.parse(msgFromServer.body).content;
			        	handleMsgFromServer(msgFromServer);
			        });
			        stompClient.send(webSockUTS, {}, webFEParam);
			    });			  
			}else{
				/////////send msg ////
				stompClient.send(webSockUTS, {}, webFEParam);
			}					
		}catch(error) {		
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorConnectWebSocket").text(), "danger", error);
			globalWriteConsoleLog(error);			
		}				
	}
	function disconnectWebSocket() {
		try{
			if (stompClient !== null && stompClient.ws.readyState == 1) {
				//open state
		        stompClient.disconnect();
		        stompClient = null;
		    }
		}catch(err){
			globalWriteToConsole("error while close websocket");
			globalWriteToConsole(err);
		}		
	}		
	/////////////////////////
});

