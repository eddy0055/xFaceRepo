var loadRegisterComplete=false, loadIPCComplete=false, loadGateInfoComplete=false, isFirstLoad = true;

function hideLoader(){
	if (loadRegisterComplete && loadIPCComplete && loadGateInfoComplete && isFirstLoad){
		isFirstLoad = !isFirstLoad;
		globalClearLoader();				
	}	
}
$(document).ready(function(){
	var chartRegisterResultList = null;
	var chartRegister = null; 	
	var chartIPCResultList = null;
	var chartIPC = null;
	var chartGateInfoResultList = null;
	var chartGateInfo = null;
	var timerReloadRegisterChart = null, timerReloadIPCChart = null, timerReloadGateInfoChart = null;
	var dateRegisterChart = null, dateIPCChart = null, dateGateInfoChart = null;
	globalImageLoader =  $("#imageLoader");	
	dateRegisterChart = new Date();
	dateIPCChart = new Date();
	dateGateInfoChart = new Date();
	$("#btnRegisterNextPage").hide();
	$("#btnIPCNextPage").hide();
	$("#btnGateInfoNextPage").hide();
	google.load("visualization", "1", {packages:["corechart"]});
    google.setOnLoadCallback(forceReloadChart);
    timerReloadRegisterChart = setInterval(doTimerReloadRegisterChart, timerReloadChartMS); //5 sec
    timerReloadIPCChart = setInterval(doTimerReloadIPCChart, timerReloadChartMS); //5 sec
    timerReloadGateInfoChart = setInterval(doTimerReloadGateInfoChart, timerReloadChartMS); //5 sec
	
	function showChartResult(chartResultList, chartElementName, chartName, panelChartWaitId, reloadChart, dateParam) {
    	globalWriteConsoleLog("show "+chartElementName+" result");
  		var data = google.visualization.arrayToDataTable(chartResultList["resultList"]);
  		var options = {
    			title: chartResultList["title"],
		        hAxis: {title: chartResultList["hAxisTitle"], titleTextStyle: {color: "red"}},
  		 		vAxis: {title: chartResultList["vAxisTitle"], titleTextStyle: {color: "red"}}
		    };		
  		var chartObject = new google.visualization.ColumnChart($("#"+chartElementName)[0]);
  		chartObject.draw(data, options);
  		
  		google.visualization.events.addListener(chartObject, "click", chartClickHandler);  		
  		function chartClickHandler(event) {  			  		
  			var parts = event.targetID.split("#");
  			var idxLabel = parts.indexOf("label");
  			var idxBar = parts.indexOf("bar");
  			var idxLegendentry = parts.indexOf("legendentry");
  			var redirectURL = null;
  			if (idxLabel>=0){
  				//["hAxis", "0", "label", "2"]  				
//  				alert("lable click "+data.getValue(Number(parts[idxLabel]), 0));
  				idxLabel++;
  				if (chartObject===chartIPC){  					
  					redirectURL = "/xFace/person/alarmHistory?ipc="+data.getValue(Number(parts[idxLabel]), 0);						
				}else if (chartObject===chartGateInfo){
					redirectURL = "/xFace/person/alarmHistory?gate="+data.getValue(Number(parts[idxLabel]), 0);
				}else if (data.getValue(Number(parts[idxLabel]), 0)!=="Unknown"){
					redirectURL = "/xFace/person/personRegister?nation="+data.getValue(Number(parts[idxLabel]), 0);
				}else{
					globalShowGrowlNotification($("#errorDialogTitle").text(),"Nationality "+data.getValue(Number(parts[idxLabel]), 0)+" not allow to drill down", "danger");
				}
  			}else if (idxBar>=0){  				
  				//["bar", "1", "2"] 1=chart no.2, 2=column 3
  				idxBar++;
  				var chartBar = data.getColumnLabel(Number(parts[idxBar])+1);
  				idxBar++;
  				var label = data.getValue(Number(parts[idxBar]), 0);
//  				alert("bar click "+chartBar+" label "+label);
  				if (chartObject===chartIPC){
  					redirectURL = "/xFace/person/alarmHistory?ipc="+label+"&cond="+chartBar;
  				}else if (chartObject===chartGateInfo){
  					redirectURL = "/xFace/person/alarmHistory?gate="+label+"&direct="+chartBar;
  				}else if (chartBar==="register"){
  					redirectURL = "/xFace/person/personRegister?nation="+label;
				}else {
					//visit
					redirectURL = "/xFace/person/alarmHistory?direct="+chartBar;
				}
  			}else if (idxLegendentry>=0){
  				idxLegendentry++;
//  				alert("Legendentry click "+data.getColumnLabel(Number(parts[idxLegendentry])+1));
  				if (chartObject===chartIPC){
  					redirectURL = "/xFace/person/alarmHistory?cond="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
  				}else if (chartObject===chartGateInfo){
  					redirectURL = "/xFace/person/alarmHistory?direct="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
  				}else if (data.getColumnLabel(Number(parts[idxLegendentry])+1)==="register"){
  					redirectURL = "/xFace/person/personRegister?direct="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
				}else{
					//visit
					redirectURL = "/xFace/person/alarmHistory?direct="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
				}
  			}
  			if (redirectURL!==null){
  				disableAllTimer();
  				window.open(redirectURL+"&date="+dateParam.YYYYMMDD(),"_self");
  			}
  		}  		  			       
  		if (!isFirstLoad){  		
  			$("#"+panelChartWaitId).removeClass("lds-spinner");
  			$("#"+panelChartWaitId).hide();
  		} 
  		return chartObject;
	}
	
	function disableAllTimer(){
		//disable all timer
		clearInterval(timerReloadRegisterChart);
		clearInterval(timerReloadIPCChart);
		clearInterval(timerReloadGateInfoChart);
		timerReloadRegisterChart = null;
		timerReloadIPCChart = null;
		timerReloadGateInfoChart = null;
		//$("#"+panelChartWaitId).addClass("lds-spinner");
		//$("#"+panelChartWaitId).show();
		globalAddLoader();
	}
    function forceReloadChart(){
    	reloadRegisterChart(true, dateRegisterChart);
    	reloadIPCChart(true, dateIPCChart);
    	reloadGateInfoChart(true, dateGateInfoChart);
    }    
    function doTimerReloadRegisterChart(){
    	if (!isFirstLoad){
    		globalWriteConsoleLog("auto reload register chart");
    		reloadRegisterChart(true, dateRegisterChart);
    	}
    }
    function doTimerReloadIPCChart(){
    	if (!isFirstLoad){
    		globalWriteConsoleLog("auto reload IPC chart");
    		reloadIPCChart(true, dateIPCChart);
    	}
    }
    function doTimerReloadGateInfoChart(){
    	if (!isFirstLoad){
    		globalWriteConsoleLog("auto reload gateInfo chart");
    		reloadGateInfoChart(true, dateGateInfoChart);
    	}
    }
    function reloadRegisterChart(forceReload, dateParam){    	
    	globalWriteConsoleLog("reload passenger register with force param:"+forceReload);
    	if (forceReload || chartRegisterResultList===null){
    		if (!isFirstLoad){
    			$("#panelRegisterChartWait").addClass("lds-spinner");
    			$("#panelRegisterChartWait").show();
    		}    		
    		globalWriteConsoleLog("force call api");
    		var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = dateParam.YYYYMMDD();
        	reportFEParam["endDate"] = reportFEParam["startDate"]+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/getPassengerRegisterList",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartRegisterResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartRegister = showChartResult(chartRegisterResultList, "chartRegister", "register", "panelRegisterChartWait", reloadRegisterChart, dateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Register]", "danger", error);
    	        	if (!isFirstLoad){
    	        		$("#panelRegisterChartWait").removeClass("lds-spinner");
    	        		$("#panelRegisterChartWait").hide();
    	        	}    	        	
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartRegister = showChartResult(chartRegisterResultList, "chartRegister", "register", "panelRegisterChartWait", reloadRegisterChart, dateParam);
    	}   	
    }
    ///end of register chart ///
    ///start IPC chart//
    function reloadIPCChart(forceReload, dateParam){    	
    	globalWriteConsoleLog("reload passenger IPC with force param:"+forceReload);
    	if (forceReload || chartIPCResultList===null){
    		if (!isFirstLoad){
    			$("#panelIPCChartWait").addClass("lds-spinner");
    			$("#panelIPCChartWait").show();
    		}    		
    		globalWriteConsoleLog("force call api");
    		var reportFEParam = {};
//    		var now = new Date(); 
        	reportFEParam["startDate"] = dateParam.YYYYMMDD();
        	reportFEParam["endDate"] = reportFEParam["startDate"]+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/getPassengerIPCList",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartIPCResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartIPC = showChartResult(chartIPCResultList, "chartIPC", "IPC", "panelIPCChartWait", reloadIPCChart, dateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[IPC]", "danger", error);
    	        	if (!isFirstLoad){
    	        		$("#panelIPCChartWait").removeClass("lds-spinner");
    	        		$("#panelIPCChartWait").hide();
    	        	}    	        	
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartIPC = showChartResult(chartIPCResultList, "chartIPC", "IPC", "panelIPCChartWait", reloadIPCChart, dateParam);
    	}   	
    }
    ///end of IPC chart//
    ///start gateInfo chart//
    function reloadGateInfoChart(forceReload, dateParam){    	
    	globalWriteConsoleLog("reload passenger gateInfo with force param:"+forceReload);
    	if (forceReload || chartIPCResultList===null){
    		if (!isFirstLoad){
    			$("#panelGateInfoChartWait").addClass("lds-spinner");
    			$("#panelGateInfoChartWait").show();
    		}    		
    		globalWriteConsoleLog("force call api");
    		var reportFEParam = {};
//    		var now = new Date(); 
        	reportFEParam["startDate"] = dateParam.YYYYMMDD();
        	reportFEParam["endDate"] = reportFEParam["startDate"]+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/getPassengerGateInfoList",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartGateInfoResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartGateInfo = showChartResult(chartGateInfoResultList, "chartGateInfo", "GateInfo", "panelGateInfoChartWait", reloadGateInfoChart, dateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[GateInfo]", "danger", error);
    	        	if (!isFirstLoad){
    	        		$("#panelGateInfoChartWait").removeClass("lds-spinner");
    	        		$("#panelGateInfoChartWait").hide();
    	        	}    	        	
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartGateInfo = showChartResult(chartGateInfoResultList, "chartGateInfo", "GateInfo", "panelGateInfoChartWait", reloadGateInfoChart, dateParam);
    	}   	
    }
    ///end of gateInfo chart//        
    //////////
	$(window).resize(function(){
		reloadRegisterChart(false, dateRegisterChart);
    	reloadIPCChart(false, dateIPCChart);
    	reloadGateInfoChart(false, dateGateInfoChart);
	});
	
	//btnevent
	$("#btnRegisterPrevPage").on("click", function(event){
		event.preventDefault();
		dateRegisterChart.setDate(dateRegisterChart.getDate()-1);
		if (timerReloadRegisterChart!==null){
			clearInterval(timerReloadRegisterChart);
			timerReloadRegisterChart = null;
		}		
		reloadRegisterChart(true, dateRegisterChart);
		$("#btnRegisterNextPage").show();
	});
	$("#btnIPCPrevPage").on("click", function(event){
		event.preventDefault();
		dateIPCChart.setDate(dateIPCChart.getDate()-1);
		if (timerReloadIPCChart!==null){
			clearInterval(timerReloadIPCChart);
			timerReloadIPCChart = null;
		}
		reloadIPCChart(true, dateIPCChart);
		$("#btnIPCNextPage").show();
	});
	$("#btnGateInfoPrevPage").on("click", function(event){
		event.preventDefault();
		dateGateInfoChart.setDate(dateGateInfoChart.getDate()-1);
		if (timerReloadGateInfoChart!==null){
			clearInterval(timerReloadGateInfoChart);
			timerReloadGateInfoChart = null;
		}
		reloadGateInfoChart(true, dateGateInfoChart);
		$("#btnGateInfoNextPage").show();
	});
	$("#btnRegisterNextPage").on("click", function(event){
		event.preventDefault();
		dateRegisterChart.setDate(dateRegisterChart.getDate()+1);		
		var nowDate = new Date();
		if (dateRegisterChart.YYYYMMDD()===nowDate.YYYYMMDD()){
			$("#btnRegisterNextPage").hide();						
			if (timerReloadRegisterChart===null){
				timerReloadRegisterChart = setInterval(doTimerReloadRegisterChart, timerReloadChartMS); //5 sec
			}
		}else{
			if (timerReloadRegisterChart!==null){
				clearInterval(timerReloadRegisterChart);
				timerReloadRegisterChart = null;
			}	
			$("#btnRegisterNextPage").show();
		}
		reloadRegisterChart(true, dateRegisterChart);
	});
	$("#btnIPCNextPage").on("click", function(event){
		event.preventDefault();
		dateIPCChart.setDate(dateIPCChart.getDate()+1);		
		var nowDate = new Date();
		if (dateIPCChart.YYYYMMDD()===nowDate.YYYYMMDD()){
			$("#btnIPCNextPage").hide();
			if (timerReloadIPCChart===null){
				timerReloadIPCChart = setInterval(doTimerReloadIPCChart, timerReloadChartMS); //5 sec
			}
		}else{
			if (timerReloadIPCChart!==null){
				clearInterval(timerReloadIPCChart);
				timerReloadIPCChart = null;
			}	
			$("#btnIPCNextPage").show();
		}
		reloadIPCChart(true, dateIPCChart);
	});
	$("#btnGateInfoNextPage").on("click", function(event){
		event.preventDefault();
		dateGateInfoChart.setDate(dateGateInfoChart.getDate()+1);		
		var nowDate = new Date();
		if (dateGateInfoChart.YYYYMMDD()===nowDate.YYYYMMDD()){
			$("#btnGateInfoNextPage").hide();
			if (timerReloadGateInfoChart===null){
				timerReloadGateInfoChart = setInterval(doTimerReloadGateInfoChart, timerReloadChartMS); //5 sec
			}
		}else{
			if (timerReloadGateInfoChart!==null){
				clearInterval(timerReloadGateInfoChart);
				timerReloadGateInfoChart = null;
			}	
			$("#btnGateInfoNextPage").show();
		}
		reloadGateInfoChart(true, dateGateInfoChart);
	});
});	

$(document).ajaxComplete(function( event, request, settings ) {
	if (settings.url==="/xFace/rest/rep/getPassengerRegisterList"){
		loadRegisterComplete = true;		
	}
	if (settings.url==="/xFace/rest/rep/getPassengerIPCList"){
		loadIPCComplete = true;		
	}
	if (settings.url==="/xFace/rest/rep/getPassengerGateInfoList"){
		loadGateInfoComplete = true;		
	}
	hideLoader();
});
$(document).ajaxError(function( event, request, settings ) {
	if (settings.url==="/xFace/rest/rep/getPassengerRegisterList"){
		loadRegisterComplete = true;
	}
	if (settings.url==="/xFace/rest/rep/getPassengerIPCList"){
		loadIPCComplete = true;		
	}
	if (settings.url==="/xFace/rest/rep/getPassengerGateInfoList"){
		loadGateInfoComplete = true;
	}
	hideLoader();
});


///backup code session //
//google.visualization.events.addListener(chartObject, "select", selectHandler);
//function selectHandler() {
//	handlerChartClick(chartObject, chartName, data, reloadChart, dateParam);
//}
//objectChart is element chart
//chartName is register, ipc and gateinfo
//function handlerChartClick(objectChart, chartName, arrayData, reloadChart, dateParam){
//	var selection = objectChart.getSelection();
//	if (selection.length>0){
//		var item = selection[0];
//		if (item.row !== null && item.column !== null) {
////			message += chartName+" {row:" + item.row + ",column:" + item.column+ "} = " + arrayData.wg[item.row].c[item.column].v
////					+ "  The Category is:" + arrayData.wg[item.row].c[0].v
////			+ " it belongs to : " + arrayData.vg[item.column].label;
//			if (objectChart===chartIPC){
//				var url = "/xFace/person/alarmHistory?ipc="+arrayData.wg[item.row].c[0].v
//				url += "&cond="+arrayData.vg[item.column].label+"&date="+dateParam.YYYYMMDD();
//				window.open(url,"_self");
//			}
//		}else if(item.column !== null){
//			reloadChart(true, dateParam);
//		}else if(item.row !== null){		
//			reloadChart(true, dateParam); 
//		}
//	}
//}
//google.visualization.events.addListener(chartObject, 'onmouseover', chartMouseOverHandler);
//google.visualization.events.addListener(chartObject, 'onmouseout', chartMouseOutHandler);
//function chartMouseOverHandler() {  			 
//	if (chartObject===chartRegister){
//		$(chartRegister).css("cursor","not-allowed");
//	}else if (chartObject===chartIPC){
//		$(chartIPC).css("cursor","pointer");
//	}else if (chartObject===chartGateInfo){
//		$(chartGateInfo).css("cursor","not-allowed");
//	}
//}  
//function chartMouseOutHandler() {  			  			
//	if (chartObject===chartRegister){
//		$(chartRegister).css("cursor","default");
//	}else if (chartObject===chartIPC){
//		$(chartIPC).css("cursor","default");
//	}else if (chartObject===chartGateInfo){
//		$(chartGateInfo).css("cursor","default");
//	}
//}