var ajaxComplete = 0;
$(document).ready( function () {	
	var isCompleteLoadToolTip = false;
	var notificationList = []; 
	var timerShowToolTip = null;
	var firstSetInterval = true;
	var stompClient = null;
	var timerCheckConnection = null;	
	globalImageLoader =  $("#imageLoader");
	//reload location map
	function reloadLocationMap(){
		globalAddLoader();		
		$.ajax({
	        url: "/xFace/rest/cfg/getAllMap",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 	        
	        success: function(locationMapList) {
	        	globalIsSessionExpire(locationMapList);
	        	globalWriteConsoleLog(locationMapList);
	        	showMapToScreen(locationMapList);	        	
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [MAP]", "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	//reload gate
	function reloadHwGateInfo(){			
		$.ajax({
	        url: "/xFace/rest/cfg/getAllHWGate",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 	        
	        success: function(hwGateInfoList) {
	        	globalIsSessionExpire(hwGateInfoList);
	        	globalWriteConsoleLog(hwGateInfoList);
	        	showHWGateInfoToScreen(hwGateInfoList);	        	
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [Gate]", "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	//reload IPC
	function reloadHWIPC(){
		globalAddLoader();		
		$.ajax({
	        url: "/xFace/rest/cfg/getHWIPCWOGate",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 	        
	        success: function(hwIPCList) {
	        	globalIsSessionExpire(hwIPCList);
	        	globalWriteConsoleLog(hwIPCList);
	        	showHWIPCToScreen(hwIPCList);
	        	globalClearLoader();
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [IPC]", "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	/////////////////////////
	//show map to screen, boat, gate and IP
	function showMapToScreen(locationMapList){
		var mapContainer = $("#mapContainer");
		var mapContent = null;
		var screenHeight = $(window).height();
		var screenWidth = $(window).width();
		
		mapContainer.find(".mapContainer").remove();
		$.each(locationMapList, function(i, locationMap){
			//mapContent = '<div id="mapContainer'+locationMap.mapCode+'" class="mapContainerList" style="border: double;position: relative;width:'+screenWidth+'px;height:'+screenHeight+'px;">' +						 
			//			 '<svg id="iconMap'+locationMap.mapCode+'" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" width="100%" height="100%" style="position: relative;">'+
			//			 '<g transform="translate(0,0)">'+
			//			 '<image xlink:href="'+locationMap.mapPhoto+'" width="100%" height="100%"></image>'+
			//			 '</g></svg></div>';
			mapContent = '<div id="mapContainer'+locationMap.mapCode+'" style="position: relative;" class="mapContainer">' +
						 '<img id="iconMap"'+locationMap.mapCode+'" src="'+locationMap.mapPhoto+'" alt="Smiley face" style="height: 100%; width: 100%;position: relative;">'
						 '</div>';			
			mapContainer.append(mapContent);
		});		
		//after reload map complete then reload the rest of object		
		//gate
		reloadHwGateInfo();
		//ipc
		reloadHWIPC();
		//activate timer	
		
		//timerShowToolTip = setInterval(showToolTipSterV2, 1000); //default 5 minute
		
		//open web socket connection
		connectWebSocket();
		timerCheckConnection = setInterval(checkWebSocketConnection, timerCheckConnectionMS); //5 sec
	}	
	function showHWGateInfoToScreen(hwGateInfoList){
		var mapContainer = $("#mapContainer");
		var mapContent = null;
		var componentTmp = null;
		$.each(hwGateInfoList, function(i, gate){
			var xFull=gate.gateLocationX , yFull=gate.gateLocationY;
			var r = 1 , e = 3.5;
			var x = xFull.substring(0,2);
			var y = yFull.substring(0,2);
			var x1 = x - r;
			var y1 = y;
			var x2 = x - r + e;
			var y2 = y;
			var positionX1 = x1 + "%";
			var positionY1 = y1 + "%"; 
			var positionX2 = x2 + "%";
			var positionY2 = y2 + "%";
			var gateIn,gateOut = null;
			var gateIn = gate.noOfIN;
			var gateOut = gate.noOfOut;
			mapContent = mapContainer.find("#mapContainer"+gate.locationMap.mapCode); 
			componentTmp = '<svg id="iconGate'+gate.gateCode+'" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" width="'+gate.gateIconWidth+'" height="'+gate.gateIconHeight+'" style="top: '+gate.gateLocationY+';left: '+gate.gateLocationX+';position: absolute;" class="tooltip svg_gate">'+
			 			   '<g transform="translate('+gate.gateIconTransformX+','+(navigator.userAgent.indexOf("Mobile") > 0 ? 0 :gate.gateIconTransformY)+')">'+						  				
						   '<image xlink:href="'+iconGate+'" width="100%" height="100%"></image>'+ 	
						   '</g>';
			if(navigator.userAgent.indexOf("Mobile") < 0){	
				componentTmp =  componentTmp+'<text y="'+gate.gateNameLocationY+'" x="'+gate.gateNameLocationX+'" font-weight="bold" font-size="'+gate.gateNameSize+'" fill="'+gate.gateNameColor+'" dominant-baseline="middle" text-anchor="middle">'+gate.gateShortName+'</text>';
			}						   
			componentTmp =  componentTmp+
			 '<span id="gateIn'+gate.gateCode+'" class="badge" style="top: '+ positionY1 +';left:'+ positionX1 +'" >' + 0  + '</span>' + 
			 '<span id="gateOut'+gate.gateCode+'" class="badgeOut" style="top: '+ positionY2 +';left:'+ positionX2 +'" >' + 0 + '</span>' + 
			'</svg>';	 
			mapContent.append(componentTmp);			
			contentHtml = 'IN:<a href="https://www.google.co.th" target="_blank">'+ gate.noOfIN  +'</a> passenger, OUT:<a href="https://www.facebook.com" target="_blank">'+gate.noOfOut+'</a> passenger';						
			initToolTipSter("iconGate"+gate.gateCode, iconGate, "Gate:"+gate.gateShortName, contentHtml, gate.locationMap.mapCode);
			$("#iconGate" + gate.gateCode).mouseover(function (){
				$("#iconGate"+gate.gateCode).tooltipster('show');
			});
			$("#iconGate" + gate.gateCode).mouseout(function () {
				$("#iconGate"+gate.gateCode).tooltipster('hide');
			});
		});	
	}
	
	function showHWIPCToScreen(hwIPCList){
		var mapContainer = $("#mapContainer");
		var mapContent = null;
		var componentTmp = null;
		var contentHtml = null;
		var iconColor = null;
		$.each(hwIPCList, function(i, hwIPC){
			mapContent = mapContainer.find("#mapContainer"+hwIPC.locationMap.mapCode); 
			//IPC_STATUS_ONLINE = 1 Color Green
			//IPC_STATUS_OFFLINE = 0 Color Gray
			//IPC_STATUS_ALARM = 2 Color Red
			if(hwIPC.ipcStatus===0){
				iconColor = iconCameraGray;
			}else if(hwIPC.ipcStatus===1){
				iconColor = iconCameraGreen;
			}else if(hwIPC.ipcStatus===2){
				iconColor = iconCameraRed;
			}
			var xFull=hwIPC.mapLocationX,yFull=hwIPC.mapLocationY;
			var r=1 , e=3.5;
			var x = xFull.substring(0,2);
			var y = yFull.substring(0,2);
			var x1 = x - r;
			var y1 = y;
			var x2 = x - r + e;
			var y2 = y;
			var positionX1 = x1 + "%";
			var positionY1 = y1 + "%"; 
			var positionX2 = x2 + "%";
			var positionY2 = y2 + "%";
			componentTmp = '<svg id="iconCamera'+hwIPC.ipcCode+'" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" width="'+hwIPC.iconWidth+'" height="'+hwIPC.iconHeight+'" style="top: '+hwIPC.mapLocationY+';left: '+hwIPC.mapLocationX+';" class="tooltip svg_camera">' +
			        ' <g transform="translate('+hwIPC.iconTransformX+','+(navigator.userAgent.indexOf("Mobile") > 0 ? 0 :hwIPC.iconTransformY)+')">'+						  				
					'<image xlink:href="'+iconColor+'" data-boat="boat_in" width="100%" height="100%"> </image> '+
					'</g>';
			if(navigator.userAgent.indexOf("Mobile") < 0){
				componentTmp = componentTmp+ '<text y="'+hwIPC.nameLocationY+'" x="'+hwIPC.nameLocationX+'" font-weight="bold" font-size="'+hwIPC.nameSize+'" fill="'+hwIPC.nameColor+'" dominant-baseline="middle" text-anchor="middle">'+hwIPC.ipcName+'</text>';
			}
			componentTmp = componentTmp+
			'<span id="cameraIn'+hwIPC.ipcCode+'" class="badge" style="top: '+ positionY1 +';left:'+ positionX1 +'" >'+ 0 +'</span>' + 
			'<span id="cameraOut'+hwIPC.ipcCode+'" class="badgeOut" style="top: '+ positionY2 +';left:'+ positionX2 +'" >'+ 0 +'</span>'+
			'</svg>';	
			mapContent.append(componentTmp);
			contentHtml = 'Match:<a href="https://www.google.co.th" target="_blank">0</a> passenger, UnMatch:<a href="https://www.facebook.com" target="_blank">0</a> passenger';						
			initToolTipSter("iconCamera"+hwIPC.ipcCode, iconColor, "Camera:"+hwIPC.ipcName, contentHtml, hwIPC.locationMap.mapCode);	
			$("#iconCamera" +hwIPC.ipcCode).mouseover(function () {
				$("#iconCamera"+hwIPC.ipcCode).tooltipster('show');
			});
			$("#iconCamera" + hwIPC.ipcCode).mouseout(function () {
				$("#iconCamera"+hwIPC.ipcCode).tooltipster('hide');
			});
		});
	}
	$(window).resize(function(){		
		$(".tooltip").tooltipster("hide");		
	});
	
	function initToolTipSter(objectId, icon, title, content, mapCode){
		var objectShowToolTip = $("#"+objectId);		
		var objectPos = objectShowToolTip.position(); 
		var contentHtml = '<div id="content'+objectId+'">'+
			'<div class="col-xs-2" style="width:20%;">'+
			'<img src="'+icon+'" width="50px" height="50px" style="float: left;margin-top: 5px;"/>'+
			'</div>'+
			'<div class="col-xs-10" style="width:80%;padding: 2px;">'+
			'<p>'+
			'<strong>'+title+'</strong><br/>'+content+					
			'</p>'+
			'</div>'+
			'</div>';		
		var notification = {};
		notification["objectId"] = objectId;			
		notification["show"] = false; 
		notification["top"] = objectPos.top;
		notification["left"] = objectPos.left;
		notification["width"] = objectShowToolTip.width();
		notification["height"] = objectShowToolTip.height();
		notification["mapCode"] = mapCode;
		notificationList.push(notification);
		objectShowToolTip.tooltipster({
			contentAsHTML: true,
			content: contentHtml,
			interactive: true,
			theme: 'tooltipster-shadow',
		    trigger: 'custom',	
		    viewportAware: false,
		    triggerClose: {
		        click: false,
		        scroll: false,
		        mouseleave: true,
		        originClick: false,
		        tap: false,
		        touchleave: false
		    }
		});		
		$('.objectShowToolTip').tooltipster('hide');
	}
	function calculatePosition(notification, containerPos){		
		var AxisXCheck = 250;
		var AxisYCheck = 250;
		var objectPos = {};
		//top left
		objectPos["topLeftX"] = ((notification.left-(AxisXCheck/2)) - containerPos.left) + notification.width/2;
		objectPos["topLeftX"] = objectPos["topLeftX"] < 0? 0: objectPos["topLeftX"];
		objectPos["topLeftY"] = ((notification.top-(AxisYCheck/2)) - containerPos.top) + notification.height/2;
		objectPos["topLeftY"] = objectPos["topLeftY"] < 0? 0: objectPos["topLeftY"];					 
		//top right
		objectPos["topRightX"] = ((notification.left+(AxisXCheck/2)) - containerPos.left) + notification.width/2;
		objectPos["topRightY"] = ((notification.top-(AxisYCheck/2)) - containerPos.top) + notification.height/2;
		objectPos["topRightY"] = objectPos["topRightY"] < 0? 0: objectPos["topRightY"];
		
		//bottom left
		objectPos["bottomLeftX"] = ((notification.left-(AxisXCheck/2)) - containerPos.left) + notification.width/2;
		objectPos["bottomLeftX"] = objectPos["bottomLeftX"] < 0? 0: objectPos["bottomLeftX"];
		objectPos["bottomLeftY"] = ((notification.top+(AxisYCheck/2)) - containerPos.top) + notification.height/2;
		//bottom right
		objectPos["bottomRightX"] = ((notification.left+(AxisXCheck/2)) - containerPos.left) + notification.width/2;
		objectPos["bottomRightY"] = ((notification.top+(AxisYCheck/2)) - containerPos.top) + notification.height/2;
		return objectPos;
		
	}
	function showToolTipSterV2(){		
		if (ajaxComplete>=3){
			//complete all api
			var showList = [];
			var thisPosition, otherPosition;
			var shouldShow = false;
			var containerPos = $("#mapContainer").position();
			$.each(notificationList, function(indexLoop1, notification){
				//1. never show
				if (!notification.show){
					//2.not in the range of other icon
					thisPosition = calculatePosition(notification, containerPos);					
					shouldShow = true;
					$.each(showList, function(indexLoop2, show){
						if (notification.objectId!==notificationList[show].objectId){
							otherPosition = calculatePosition(notificationList[show], containerPos);
							if (thisPosition.topLeftY > otherPosition.bottomLeftY){
								shouldShow = true;
							}else if (thisPosition.bottomLeftY < otherPosition.topLeftY){
								shouldShow = true;
							}else if (thisPosition.topLeftX > otherPosition.topRightX){
								shouldShow = true;
							}else if (thisPosition.topRightX < otherPosition.topLeftX){
								shouldShow = true;
							}else{	
								shouldShow = false;
								return false;
							}
						}						
					});// end of $.each(showList, function(indexLoop2, show){
					if (shouldShow || showList.lenght===0){
						showList.push(indexLoop1);
					}
				}//end of if (!notification.show){
			});//$.each(notificationList, function(indexLoop1, notification){
			$('.tooltip').tooltipster("hide");			
			
			if (showList.length===0){
				//clear show flag
				$.each(notificationList, function(indexLoop1, notification){
					notification.show = false;
				});
				showToolTipSterV2();
			}else{
				$.each(showList, function(indexLoop2, show){
					$("#"+notificationList[show].objectId).tooltipster("show");
					notificationList[show].show = true;														
				});
			}
			if (firstSetInterval){
				clearInterval(timerShowToolTip);		
				timerShowToolTip = setInterval(showToolTipSterV2, 5000); //default 5 minute
				firstSetInterval = false;
			}
		}//end of if (ajaxComplete>=5){
	}//end of function showToolTipSter(){	
	/////show landing page info ////
	function showLandingPageInfo(landingPageInfo){
		$("#txtGateNoOfIN").text(landingPageInfo.noOfPassengerIN);
		$("#txtGateNoOfOUT").text(landingPageInfo.noOfPassengerOUT);
		$("#txtIPCNoOfMatch").text(landingPageInfo.noOfFaceMatch);
		$("#txtIPCNoOfUnMatch").text(landingPageInfo.noOfFaceUnMatch);
		//alarm online & offLine 
		$("#txtAlarmOnLine").text(landingPageInfo.noOfAlarmOnLine);
		$("#txtAlarmOffLine").text(landingPageInfo.noOfAlarmOffLine);
		var boatHistory = $("#listBoatHistory");
		var alarmHistory = $("#listAlarmHistory");
		var dataHistory = "";
		var contentHtml = "";
		var iconColor = null;
		boatHistory.empty();
		alarmHistory.empty();
		
		//change Icon IPC Camera
		if (landingPageInfo.ipcInfoList.result.statusCode==="0"){
			$.each(landingPageInfo.ipcStatusInfoList.ipcInfoList, function(i, ipcInfoList){	
				if(ipcInfoList.ipcStatus==="0"){
					globalWriteConsoleLog("ipcStatus in case 0 color Gray::" + ipcInfoList.ipcStatus);
					iconColor = iconCameraGray;
				}else if(ipcInfoList.ipcStatus==="1"){
					globalWriteConsoleLog("ipcStatus in case 1 color Green::" + ipcInfoList.ipcStatus);
					iconColor = iconCameraGreen;
				}else if(ipcInfoList.ipcStatus==="2"){
					globalWriteConsoleLog("ipcStatus in case 2 color Red::" + ipcInfoList.ipcStatus);
					iconColor = iconCameraRed;
				}
				var changeIconCamera = $("#iconCamera" + ipcInfoList.ipcCode);
				changeIconCamera.find("image").attr("xlink:href",iconColor);
				
				$.each(landingPageInfo.ipcInfoList.ipcinfoList, function(i, ipcInfoList) {
					//show match , un-match
					$("#" + "cameraIn" + ipcInfoList.fieldGroup).text(ipcInfoList.noOfMatch);
					$("#" + "cameraOut" + ipcInfoList.fieldGroup).text(ipcInfoList.noOfUnMatch);
					contentHtml =
						'<div id="content'+ipcInfoList.fieldGroup+'">'+
						'<div class="col-xs-2" style="width:20%;">'+
						'<img src="'+iconColor+'" width="50px" height="50px" style="float: left;margin-top: 5px;"></image>'+
						'</div>'+
						'<div class="col-xs-10" style="width:80%;padding: 2px;">'+
						'<p>'+
						'<strong>Camera:'+ipcInfoList.fieldGroup+'</strong><br/>'+'Match:<a href="https://www.google.co.th" target="_blank">'+ipcInfoList.noOfMatch+'</a> passenger, UnMatch:<a href="https://www.facebook.com" target="_blank">'+ipcInfoList.noOfUnMatch+'</a> passenger'+
						'</p>'+
						'</div>'+
						'</div>';
					$("#iconCamera"+ipcInfoList.fieldGroup).tooltipster("content", contentHtml);
				});
				//check mouse over , mouse out 
				$("#iconCamera" + ipcInfoList.ipcCode).mouseover(function (){
					$("#iconCamera"+ipcInfoList.ipcCode).tooltipster('show');
				});
				$("#iconCamera" +ipcInfoList.ipcCode).mouseout(function () {
					$("#iconCamera"+ipcInfoList.ipcCode).tooltipster('hide');
				});
			});			
		}else{
			//display error
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [ipcInfoList]", "danger", 0);
		}	
		
		
		if (landingPageInfo.alarmInfoList.result.statusCode==="0"){
			$.each(landingPageInfo.alarmInfoList.alarmInfoList, function(i, alarmInfo){
				dataHistory = dataHistory+'<li><a href="#">Alarm '+alarmInfo.ipcName+' on '+alarmInfo.alarmTime+'</a></li>';
			});
			alarmHistory.html(dataHistory)
		}else{
			//display error
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [Boat History]", "danger", 0);
		}		
		//eddy
		dataHistory = "";		
		if (landingPageInfo.boatInfoList.result.statusCode==="0"){
			var existingBoat = null;
			var mapContainer = $("#mapContainer");
			var mapContent = null;
			var componentTmp = null;	
			var listOfBoatCode = "";									
			$.each(landingPageInfo.boatInfoList.boatInfoList, function(i, boatInfo){				
				dataHistory = dataHistory+'<li><a href="#">'+boatInfo.boatName+' In:'+boatInfo.arrivalTime+'['+boatInfo.noOfIN+'] Out:'+boatInfo.departureTime+'['+boatInfo.noOfOUT+']</a></li>';
				if (boatInfo.departureTime===""){	
					listOfBoatCode = listOfBoatCode +boatInfo.boatCode + ","; 
					existingBoat = $("#iconBoat"+boatInfo.boatCode);
					if (existingBoat.length===1){
						//update content
						contentHtml = '<div id="content'+boatInfo.boatCode+'">'+
						'<div class="col-xs-2" style="width:20%;">'+
						'<img src="'+iconBoat+'" width="80px" height="80px" style="float: left;margin-top: 5px;"/>'+
						'</div>'+
						'<div class="col-xs-10" style="width:80%;padding: 2px;">'+
						'<p>'+
						'<strong>Boat:'+boatInfo.boatName+'</strong><br/>'+'IN:<a href="https://www.google.co.th" target="_blank">'+boatInfo.noOfIN+'</a> passenger, Out:<a href="https://www.facebook.com" target="_blank">'+boatInfo.noOfOUT+'</a> passenger'+
						'</p>'+
						'</div>'+
						'</div>';
						$("#iconBoat"+boatInfo.boatCode).tooltipster("content", contentHtml);
					}else{
						//boat on the gate then show icon
						var xFull= boatInfo.boatLocationX;
						var yFull= boatInfo.boatLocationY;
						var r = 1 , e = 3.5;
						var x = xFull.substring(0,2);
						var y = yFull.substring(0,2);
						var x1 = x - r;
						var y1 = y;
						var x2 = x - r + e;
						var y2 = y;
						var positionX1 = x1 + "%";
						var positionY1 = y1 + "%"; 
						var positionX2 = x2 + "%";
						var positionY2 = y2 + "%";
						mapContent = mapContainer.find("#mapContainer"+boatInfo.mapCode); 
						componentTmp = '<svg id="iconBoat'+boatInfo.boatCode+'" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" width="'+boatInfo.boatIconWidth+'" height="'+boatInfo.boatIconHeight+'" style="top: '+boatInfo.boatLocationY+';left: '+boatInfo.boatLocationX+';position: absolute;" class="tooltip svg_boat">'+
									   '<g transform="translate('+boatInfo.boatIconTransformX+','+(navigator.userAgent.indexOf("Mobile") > 0 ? 0 :boatInfo.boatIconTransformY)+')">'+						  				
									   '<image xlink:href="'+iconBoat+'" width="100%" height="100%"></image>'+ 	
									   '</g>';
						if(navigator.userAgent.indexOf("Mobile") < 0){
							componentTmp = componentTmp+'<text y="'+boatInfo.boatNameLocationY+'" x="'+boatInfo.boatNameLocationX+'" font-weight="bold" font-size="'+boatInfo.boatNameSize+'" fill="'+boatInfo.boatNameColor+'" dominant-baseline="middle" text-anchor="middle">'+boatInfo.boatName+'</text>';
						}
						componentTmp = componentTmp+
						 '<span id="boatIn'+boatInfo.boatCode+'" class="badge" style="top: '+ positionY2 +';left:'+ positionX1 +'" >' + boatInfo.noOfIN  + '</span>' + 
							'<span id="boatOut'+boatInfo.boatCode+'" class="badgeOut" style="top: '+positionY1 +';left:'+ positionX2 +'" >' + boatInfo.noOfOUT + '</span>' + 
						'</svg>';	 
						mapContent.append(componentTmp);
						
						//show content of tooltip
						contentHtml = 'IN:<a href="https://www.google.co.th" target="_blank">'+boatInfo.noOfIN+'</a> passenger, Out:<a href="https://www.facebook.com" target="_blank">'+boatInfo.noOfOUT+'</a> passenger';						
						initToolTipSter("iconBoat"+boatInfo.boatCode, iconBoat, "Boat:"+boatInfo.boatName, contentHtml, boatInfo.mapCode);
					
						//check mouse over , mouse out 
						$("#iconBoat" + boatInfo.boatCode).mouseover(function (){
							$("#iconBoat"+boatInfo.boatCode).tooltipster('show');
						});
						$("#iconBoat" +boatInfo.boatCode).mouseout(function () {
							$("#iconBoat"+boatInfo.boatCode).tooltipster('hide');
						});
						
					}
					
				}//end of if (boatInfo.date_departure===""){			
			});//end of loop boatInfo			
			boatHistory.html(dataHistory)
			//remove existing boat icon if not found in listOfBoatCode
			var elements = $(".svg_boat");
			var boatCode = null;
			var jqElement = null;
			elements.each(function (index, element) {
				jqElement = $(element);
				boatCode = jqElement.attr("id").replace("iconBoat","");
				if (!globalCheckArrayContainValue(listOfBoatCode, boatCode)){
					//not found then remove element
					jqElement.tooltipster("destroy");
					jqElement.remove();
				}
			});
		}else{
			//display error
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [VCN Alarm History]", "danger", 0);
		}
		
		if (landingPageInfo.gateInfoList.result.statusCode==="0"){
			$.each(landingPageInfo.gateInfoList.gateInfoList, function(i, gateInfo){	
				$("#" + "gateIn" + gateInfo.gateCode ).text(gateInfo.noOfIN);
				$("#" + "gateOut" + gateInfo.gateCode).text(gateInfo.noOfOut);
				contentHtml = '<div id="content'+gateInfo.gateCode+'">'+
				'<div class="col-xs-2" style="width:20%;">'+
				'<img src="'+iconGate+'" width="50px" height="50px" style="float: left;margin-top: 5px;"/>'+
				'</div>'+
				'<div class="col-xs-10" style="width:80%;padding: 2px;">'+
				'<p>'+
				'<strong>Gate:'+gateInfo.gateName+'</strong><br/>'+'IN:<a href="https://www.google.co.th" target="_blank">'+gateInfo.noOfIN+'</a> passenger, Out:<a href="https://www.facebook.com" target="_blank">'+gateInfo.noOfOut+'</a> passenger'+
				'</p>'+
				'</div>'+
				'</div>';
				$("#iconGate"+gateInfo.gateCode).tooltipster("content", contentHtml);
				$("#iconGate" + gateInfo.gateCode).mouseover(function (){
					$("#iconGate"+gateInfo.gateCode).tooltipster('show');
				});
				
				$("#iconGate" + gateInfo.gateCode).mouseout(function () {
					$("#iconGate" + gateInfo.gateCode).tooltipster('hide');
				});
			});			
		}else{
			//display error
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [GateInfo]", "danger", 0);
		}	
		
		
		
		if (landingPageInfo.ipcInfoList.result.statusCode==="0"){
			$.each(landingPageInfo.ipcInfoList.ipcInfoList, function(i, ipcInfo){
				$("#" + "cameraIn" + ipcInfo.fieldLabel).text(ipcInfo.noOfMatch);
				$("#" + "cameraOut" + ipcInfo.fieldLabel).text(ipcInfo.noOfUnMatch);
				contentHtml = '<div id="content'+ipcInfo.fieldGroup+'">'+
				'<div class="col-xs-2" style="width:20%;">'+
				'<img src="'+iconCamera+'" width="50px" height="50px" style="float: left;margin-top: 5px;"/>'+
				'</div>'+
				'<div class="col-xs-10" style="width:80%;padding: 2px;">'+
				'<p>'+
				'<strong>Camera:'+ipcInfo.fieldLabel+'</strong><br/>'+'Match:<a href="https://www.google.co.th" target="_blank">'+ipcInfo.noOfMatch+'</a> passenger, UnMatch:<a href="https://www.facebook.com" target="_blank">'+ipcInfo.noOfUnMatch+'</a> passenger'+
				'</p>'+
				'</div>'+
				'</div>';
				$("#iconCamera" + ipcInfo.fieldGroup).mouseover(function (){
					$("#iconCamera"+ipcInfo.fieldGroup).tooltipster('show');
				});
				$("#iconCamera" + ipcInfo.fieldGroup).mouseout(function () {
					$("#iconCamera"+ipcInfo.fieldGroup).tooltipster('hide');
				});
				$("#iconCamera"+ipcInfo.fieldGroup).tooltipster("content", contentHtml);
			});			
		}else{
			//display error
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [GateInfo]", "danger", 0);
		}
		
		
		
	}
	
	
	
	
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
			var landingPageInfo = JSON.parse(content.body);
			showLandingPageInfo(landingPageInfo);
		}		
	}	
	//connect web socket
	//1. refresh parameter
	//2. create parameter
	//3. open connection
	function connectWebSocket() {		
		webFEParam = JSON.stringify({"webSocketModule": webSocketModule});
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
	///init command call reload map //
	reloadLocationMap();
	/////////////////////////////
});

$(document).ajaxComplete(function( event, request, settings ) {
	ajaxComplete = ajaxComplete+1; 
});

//function showToolTipSter(){
//var widthX = 300;
//var heightX = 300;
//var colorX = "red";
//var cntX = -1;
//var posX, posY;
//var containerPos = $("#mapContainer").position();
//if (ajaxComplete >= 4){
//	$.each(notificationList, function(indexLoop1, notification){
//		cntX = cntX + 1;
//		if (cntX % 2 === 0){
//			colorX = "red";
//		}else{
//			colorX = "green";
//		}
//		posX = (notification.left - (widthX/2))-containerPos.left;
//		posY = (notification.top - (heightX/2))-containerPos.top;
//		posX = Math.ceil(posX + notification.width/2);
//		posY = Math.ceil(posY + notification.height/2);
//		$("#map1").append('<svg width="'+widthX+'" height="'+heightX+'" style="top: '+posY+'px;left: '+posX+'px;position: absolute;"><rect width="'+widthX+'" height="'+heightX+'" style="fill:'+colorX+';stroke-width:3;stroke:rgb(0,0,0);"></rect></svg>');
//	});
//	clearInterval(timerShowToolTip);	
	
	
//	var showList = [];
//	var showGroup = null;
//	$.each(notificationList, function(indexLoop1, notification){
//		if (!notification.show){
//			if (showGroup===null){
//				showGroup = notification.toolTipGroup;
//				showList.push(indexLoop1);
//			}else if (showGroup===notification.toolTipGroup){
//				showList.push(indexLoop1);
//			}
//		}				
//	});
//	//show tooltip
//	$('.tooltip').tooltipster("hide");
//	if (showList.length===0){
//		$.each(notificationList, function(indexLoop1, notification){
//			notification.show = false;
//		});
//		showToolTipSter();
//	}else{
//		$.each(showList, function(indexLoop2, show){
//			$("#"+notificationList[show].objectId).tooltipster("show");
//			notificationList[show].show = true;					
//		});
//	}			
//	if (firstSetInterval){
//		clearInterval(timerShowToolTip);		
//		timerShowToolTip = setInterval(showToolTipSter, 5000); //default 5 minute
//		firstSetInterval = false;
//	}
//}						
//}		

//function reloadBoat(){		
//$.ajax({
//    url: "/xFace/rest/cfg/getAllBoat",
//    type: "POST",
//    contentType: "application/json; charset=utf-8",
//    dataType: "json", 	        
//    success: function(boatList) {
//    	globalIsSessionExpire(boatList);
//    	globalWriteConsoleLog(boatList);
//    	showBoatToScreen(boatList);	
//    	globalClearLoader();
//    },
//    error: function(error){            	
//    	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+" [Boat]", "danger", error);
//    	globalClearLoader();
//    }
//});
//}

//function showBoatToScreen(boatList){		
//	var mapContainer = $("#mapContainer");
//	var mapContent = null;
//	var componentTmp = null;		
//	$.each(boatList, function(i, boat){
//		mapContent = mapContainer.find("#mapContainer"+boat.locationMap.mapCode); 
//		componentTmp = '<svg id="iconBoat'+boat.boatCode+'" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" width="'+boat.iconWidth+'" height="'+boat.iconHeight+'" style="top: '+boat.mapLocationY+';left: '+boat.mapLocationX+';position: absolute;" class="tooltip svg_boat">'+
//					   '<g transform="translate('+boat.iconTransformX+','+(navigator.userAgent.indexOf("Mobile") > 0 ? 0 :boat.iconTransformY)+')">'+						  				
//					   '<image xlink:href="'+iconBoat+'" data-boat="boat_in" width="100%" height="100%"></image>'+ 	
//					   '</g>';
//		if(navigator.userAgent.indexOf("Mobile") < 0){
//			componentTmp = componentTmp+'<text y="'+boat.nameLocationY+'" x="'+boat.nameLocationX+'" font-weight="bold" font-size="'+boat.nameSize+'" fill="'+boat.nameColor+'" dominant-baseline="middle" text-anchor="middle">'+boat.boatShortName+'</text>';
//		}
//		componentTmp = componentTmp+'</svg>';	 
//		mapContent.append(componentTmp);			
//		contentHtml = 'IN:<a href="https://www.google.co.th" target="_blank">0</a> passenger, Out:<a href="https://www.facebook.com" target="_blank">0</a> passenger';						
//		initToolTipSter("iconBoat"+boat.boatCode, iconBoat, "Boat:"+boat.boatShortName, contentHtml, boat.locationMap.mapCode);			
//	});		
//	//hide boat first
//	$.each(boatList, function(i, boat){
//		$("#iconBoat"+boat.boatCode).find(".tooltip").tooltipster("hide");			
//		$("#iconBoat"+boat.boatCode).hide();
//	});
//}