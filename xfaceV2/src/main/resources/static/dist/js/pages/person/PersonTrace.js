$(document).ready( function () {
	var dtpOptionFrom = $("#dtpOptionFrom"), dtpOptionTo = $("#dtpOptionTo");	
	var stompClient = null;
//	var noSleep = null;
	var timerCheckConnection = null;
	var formPersonTrace = $("#formPersonTrace");	
	globalImageLoader =  $("#imageLoader");
	dtpOptionFrom.datetimepicker({
		format: "dd/mm/yyyy hh:ii",
		todayBtn:  true,
		autoclose: true
	}).data("datetimepicker").setDate(globalStringYYYYMMDDToDate(new Date().YYYYMMDD()));
	
	dtpOptionTo.datetimepicker({
		format: "dd/mm/yyyy hh:ii",
		todayBtn:  true,
		autoclose: true
	}).data("datetimepicker").setDate(new Date());
	//check if running on mobile device then activate NoSleep to prevent screen shutdown
//	if(navigator.userAgent.indexOf("Mobile") > 0){				
//		noSleep = new NoSleep();
//		noSleep.enable();
//	}
	function reloadPageData(personTraceInfo){
		//reload history result page
		globalAddLoader();		
		$.ajax({
	        url: "/xFace/rest/person/personTrace",
	        type: "POST",
            contentType: false,            
            processData: false,            
	        data: personTraceInfo,
	        success: function(resultPersonRespList) {
	        	globalIsSessionExpire(resultPersonRespList);
	        	globalWriteConsoleLog(resultPersonRespList);
	        	showFaceToScreen(resultPersonRespList);	        	
	        	globalClearLoader();
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	
	///////////////////////////////////
    //validate search option, user have to select minimum 1 condition
    function validateInputData(){    	
    	var isCertNo = true;
    	var isFullName = true;
    	var isPhoto = true;
    	if ($("#txtOptionCertificateNo").val()===""){
    		isCertNo = false;
    	}
    	if ($("#txtOptionFullName").val()===""){
			isFullName = false;
    	}
		if ($("#btnOptionUploadFile").prop('files').length === 0){
			isPhoto = false;
		}
		return (isCertNo || isFullName || isPhoto);				
    }
    
	function createReloadParameter(){		
		var personTraceInfo = {};
		var isCondition = false;
		var myDate = dtpOptionFrom.data("datetimepicker").getDate(); 		
		personTraceInfo["startDate"] = myDate.YYYYMMDDHHMM();
		myDate = dtpOptionTo.data("datetimepicker").getDate();
		personTraceInfo["endDate"] = myDate.YYYYMMDDHHMM();
		if ($("#txtOptionCertificateNo").val()!==""){
			personTraceInfo["personCertificateNo"] = $("#txtOptionCertificateNo").val();			
			$("#txtOptionFullName").val("")
			$("#btnOptionUploadFile").val("");
			$("#imgOptionUploadPerview").attr("src","/xFace/dist/img/noimage.gif");
			isCondition = true;
		}else if ($("#txtOptionFullName").val()!==""){			
			personTraceInfo["fullName"] = $("#txtOptionFullName").val();
			$("#btnOptionUploadFile").val("");
			$("#imgOptionUploadPerview").attr("src","/xFace/dist/img/noimage.gif");
			isCondition = true;
		}		
		data = new FormData();
        data.append("webFEParam", JSON.stringify(personTraceInfo));
        if (!isCondition){        	
        	data.append("searchPhoto", $("#btnOptionUploadFile").prop("files")[0]);
        }     	       
		return data;		
	}	
	
	//event
	$("#btnOptionRefresh").on("click", function(event){
		if (validateInputData()){
			//disconnect web socket
			disconnectWebSocket();
			var personTraceInfo = createReloadParameter();
			reloadPageData(personTraceInfo);
		}else{
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorSelectConditionForSearch").text(), "danger");
		}
//		var myDate = dtpOptionTo.data("datetimepicker").getDate(); 
//		alert(myDate.YYYYMMDDHHMM());
	});
	$("#btnOptionUploadFile").bind("change", function() {
    	var imgFile = this.files[0]
    	//Check File size
    	if (imgFile.size > 5120000 || imgFile.fileSize > 5120000){
    		//show error file size
    		globalShowGrowlNotification($("#errorDialogTitle").text(), "Image file size should not more than 5Mb", "danger");
		    this.value = null;
		}else if (this.files && imgFile) {
			var reader = new FileReader();
		    reader.onload = function (e) {
		    	$("#imgOptionUploadPerview").attr("src", e.target.result);
		    }
		    reader.readAsDataURL(imgFile);
		}				  
    });
	$("#btnOptionDisconnect").on("click", function(event){
		disconnectWebSocket();
	});
	
	//show result to screen
	function showFaceToScreen(resultPersonRespList){
		var panelPersonList = $("#panelPersonList");
		var panelPerson = null;
		var panelPersonMap = null;
		panelPersonList.empty();
		$.each(resultPersonRespList.dataPersonList, function(i, dataPerson){
			panelPerson = $(".templatePanelPerson").clone();
			panelPerson.removeClass("templatePanelPerson");
			panelPerson.addClass("panelPerson");
			panelPerson.attr("id", "panelPerson_"+dataPerson.certificateNo);
			panelPerson.find("#linkPageOption").text(dataPerson.title+" "+dataPerson.fullName);
			panelPerson.find("#linkPageOption").attr("href","#panelPersonBody_"+dataPerson.certificateNo);
			panelPerson.find("#panelPersonBody").addClass("in");
			panelPerson.find("#panelPersonBody").attr("id", "panelPersonBody_"+dataPerson.certificateNo);			
			panelPerson.find("#txtResultTitle").val(dataPerson.title);
			panelPerson.find("#txtResultFullName").val(dataPerson.fullName);
			panelPerson.find("#txtResultCertificateType").val(dataPerson.certificateType);
			panelPerson.find("#txtResultCertificateNo").val(dataPerson.certificateNo);
			panelPerson.find("#imgResultDbPhoto").attr("src", dataPerson.dbPhoto)
			panelPerson.find("#chkResultMonitor").on("click",function(event){
				handleCheckResultMonitorClick();
			});
			$.each(dataPerson.dataMapList, function(j, dataMap){		
				//check if dataMap already then update
				panelPersonMap = generateMapFaceImage(dataMap);
				panelPerson.find("#panelPersonBody_"+dataPerson.certificateNo).find(".panel-body").find("#panelPersonMapList").append(panelPersonMap);
			});	
			panelPerson.show();
			panelPersonList.append(panelPerson);
		});		
	}
	function generateMapFaceImage(dataMap){		
		var panelPersonMap = $(".templatePanelPersonMap").clone();
		var faceImageString = "";
		panelPersonMap.removeClass("templatePanelPersonMap");
		panelPersonMap.attr("id","panelPersonMap_"+dataMap.mapId);
		panelPersonMap.find("#lblMapName").text(dataMap.mapName);
		//add map			
		panelPersonMap.find("#panelMapFaceImage").append('<img id="mapImage" src="'+dataMap.mapPhoto+'" class="mapImage"></img>');				
		//add face
		$.each(dataMap.dataIpcList, function(k, dataIpc){					
			$.each(dataIpc.dataFaceList, function(l, dataFace){
				faceImageString = faceImageString+generatePanelFaceImage(dataMap, dataIpc, dataFace);
			});					
		})
		panelPersonMap.find("#panelMapFaceImage").append(faceImageString)		
		panelPersonMap.find("#panelMapFaceImage").find(".container-imageFace").on("click", function(event){			
			handleFaceImageClick($(this));
		});
		panelPersonMap.show();
		return panelPersonMap;		
	}
	function generatePanelFaceImage(dataMap, dataIpc, dataFace){
		var faceImageString = '<div class="container container-imageFace" id="faceImage_'+dataMap.mapId+"_"+dataIpc.ipcCode+'">'
		faceImageString = faceImageString+generateFaceImage(dataIpc, dataFace, 0, "")+'</div>';
		return faceImageString;
	}
	function generateFaceImage(dataIpc, dataFace, prevNoOfFace, prevAlarmCodeList){
		var faceImageString = '<img src="'+dataFace.facePhoto+'" class="faceImage'+(dataIpc.latest?' ball"':'"');
		var isIncreaseFace = true;
		faceImageString = faceImageString+' style="top:'+dataIpc.locationY+'%;'
		faceImageString = faceImageString+'left:'+dataIpc.locationX+'%;">'							
		if (prevAlarmCodeList === ""){
			faceImageString = faceImageString+'</img><div data-alarmcodelist="'+(dataFace.alarmCodeList)+'"';
		}else if (globalCheckArrayContainValue(prevAlarmCodeList,dataFace.alarmCodeList)){
			faceImageString = faceImageString+'</img><div data-alarmcodelist="'+(prevAlarmCodeList)+'"';
			isIncreaseFace = false;
		}else{
			faceImageString = faceImageString+'</img><div data-alarmcodelist="'+(prevAlarmCodeList+","+dataFace.alarmCodeList)+'"';
		}
		if (isIncreaseFace){
			faceImageString = faceImageString+' data-noofface='+(dataFace.noOfFace+prevNoOfFace)
		}else{
			faceImageString = faceImageString+' data-noofface='+prevNoOfFace
		}
		faceImageString = faceImageString+' class="txtNoOfFace '+(dataIpc.latest?'txtCurrentLocation ball':'txtHistoryLocation')+'" ' 
		faceImageString = faceImageString+'style="top:'+dataIpc.locationY+'%;'
		faceImageString = faceImageString+'left:'+dataIpc.locationX+'%;">'
		if (isIncreaseFace){
			faceImageString = faceImageString+(dataFace.noOfFace+prevNoOfFace)+'</div>';
		}else{
			faceImageString = faceImageString+(prevNoOfFace)+'</div>';
		}
		return faceImageString;
	}
	
	//get certificate which user need to monitor
	function getMonitorCertificateNo(){
		var chkResultMonitor = null;
		var certificateNoList = "";
		var panelSpinner = null;
		//need all checkbox monitor
		$("#panelPersonList").find(".form-check-input").each(function(index){
			chkResultMonitor = $(this);
			panelSpinner = chkResultMonitor.parent().parent().find("#panelRadar");
			if (chkResultMonitor.is(":checked")){
				if (!panelSpinner.hasClass("lds-ripple")){
					panelSpinner.addClass("lds-ripple");
				}				
				certificateNoList =  certificateNoList+chkResultMonitor.parent().parent().find("#txtResultCertificateNo").val()+","; 
			}else{
				if (panelSpinner.hasClass("lds-ripple")){
					panelSpinner.removeClass("lds-ripple");
				}
			}				
		});
		if (certificateNoList.length > 0){
			certificateNoList = certificateNoList.substr(0, certificateNoList.length-1);
		}		
		return certificateNoList;
	}
	
	//handle checkbox monitor person check/uncheck
	function handleCheckResultMonitorClick(){
		var certificateNoList = getMonitorCertificateNo();
		if (certificateNoList.length > 0){						
			globalCheckSessionExpire(reconnectWebSocketConnection);
			//start timer 			
			timerCheckConnection = setInterval(checkWebSocketConnection, timerCheckConnectionMS); //5 sec
		}else{
			//cancel timer
			clearInterval(timerCheckConnection);
			disconnectWebSocket();
		}
	}
	
	function handleFaceImageClick(faceImage){		
		var alarmCodeList = faceImage.find(".txtNoOfFace").data("alarmcodelist").split(",");
		var dataCenter = formPersonTrace.find("#dataCenter");
		dataCenter.empty();		
		alarmCodeList.forEach(function(element) {
			dataCenter.append("<p>"+element+"</p>");
		});		
		formPersonTrace.modal("show");
	}
	///////websocket
	//web socker implement
	function checkWebSocketConnection(){		
		//0	CONNECTING	Socket has been created. The connection is not yet open.
		//1	OPEN	The connection is open and ready to communicate.
		//2	CLOSING	The connection is in the process of closing.
		//3	CLOSED		
		if (stompClient===null || stompClient.ws.readyState==3){
			//close then open new connection
			globalCheckSessionExpire(reconnectWebSocketConnection);					
		}	
	}
	
	//will call after check session is not expire
	function reconnectWebSocketConnection(){
		var certificateNoList = getMonitorCertificateNo();
		if (certificateNoList.length>0){
			connectWebSocket(certificateNoList);
		}
	}
	//handle message receive from server
	function handleMsgFromServer(content){		
		if (content.body === webSockGoodbye){
			globalWriteConsoleLog("server force disconnect");
			disconnectWebSocket();
			//redirect to invalidsession
			window.open("/xFace/error/invalidsession","_self");
		}else if (content.body !== webSockAccept){
			var dataPerson = JSON.parse(content.body);
			var panelPerson = $("#panelPerson_"+dataPerson.certificateNo);			
			var panelPersonMap = panelPerson.find("#panelPersonMap_"+dataPerson.dataMapList[0].mapId);
			//remove active class
			//panelPersonBody_certNo -> .panel-body -> panelPersonMapList -> .txtCurrentLocation
			var latestFace = panelPerson.find("#panelPersonBody_"+dataPerson.certificateNo).find(".panel-body").find("#panelPersonMapList").find(".txtCurrentLocation");
			if (latestFace.length > 0){
				latestFace.removeClass("txtCurrentLocation");
				latestFace.addClass("txtHistoryLocation");
			}
			if (panelPersonMap.length === 0){
				//not found map need to add new map and faceImage
				panelPersonMap = generateMapFaceImage(dataPerson.dataMapList[0]);
				panelPerson.find("#panelPersonBody_"+dataPerson.certificateNo).find(".panel-body").find("#panelPersonMapList").append(panelPersonMap);
			}else{		
				//existing map update person image only
				//panelPerson -> panelPersonBody -> panelPersonMapList -> panelMapFaceImage -> faceImage_mapId_IpcCode
				//var panelPersonMapList = panelPerson.find("#panelPersonBody_"+dataPerson.certificateNo).find(".panel-body").find("#panelPersonMapList");
				var faceImageId = "faceImage_"+dataPerson.dataMapList[0].mapId+"_"+dataPerson.dataMapList[0].dataIpcList[0].ipcCode;
				var panelFaceImage = panelPersonMap.find("#"+faceImageId);							
				var faceImageString = "";
				if(panelFaceImage.length === 0){
					//not found faceImage on map then insert
					faceImageString = generatePanelFaceImage(dataPerson.dataMapList[0], dataPerson.dataMapList[0].dataIpcList[0]
									  , dataPerson.dataMapList[0].dataIpcList[0].dataFaceList[0]);					
					panelPersonMap.find("#panelMapFaceImage").append(faceImageString);
					panelPersonMap.find("#panelMapFaceImage").find("#"+faceImageId).on("click", function(event){						
						handleFaceImageClick($(this));
					});
				}else{
					//found image on map then update
					var prevNoOfFace = Number(panelFaceImage.find(".txtNoOfFace").data("noofface"));
					var prevAlarmCodeList = panelFaceImage.find(".txtNoOfFace").data("alarmcodelist")+"";
					panelFaceImage.empty();					
					panelFaceImage.append(generateFaceImage(dataPerson.dataMapList[0].dataIpcList[0]
									, dataPerson.dataMapList[0].dataIpcList[0].dataFaceList[0], prevNoOfFace, prevAlarmCodeList));					
				}
			}
		}		
	}
	
	function connectWebSocket(certificateNoList) {
		webFEParam = JSON.stringify({"webSocketModule": webSocketModule, "personCertificateNo":certificateNoList});					
		try{				
			if (stompClient === null ||stompClient.ws.readyState === 3){
				//not null and not open
				var webSocket = new SockJS(webSockEndPoint);
			    stompClient = Stomp.over(webSocket);			    
			    stompClient.connect({}, function (frame) {
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