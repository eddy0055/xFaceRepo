$(document).ready( function () {
	var dtpOptionFrom = $("#dtpOptionFrom"), dtpOptionTo = $("#dtpOptionTo");
	var chartPassengerResultList = null, chartPassengerByGateResultList = null, chartFaceResultList = null, chartFaceByGateResultList = null, chartPassengerByBoatResultList = null;
	var chartStartDate = new Date();
	var chartEndDate = new Date();
	var chartPassenger = null, chartPassengerByGate = null, chartFace = null, chartFaceByGate = null, chartPassengerByBoat = null;
	//start date is 7 day behide 
	var dateOffset = (24*60*60*1000) * 7;
	chartStartDate = new Date(chartStartDate.getTime()-dateOffset);	
	//var dtpOptionFrom = $("#dtpOptionFrom"), dtpOptionTo = $("#dtpOptionTo");	
	//var chartStartDate = new Date(), chartEndDate = new Date();	
	//start date is 7 day behide 
	//var dateOffset = (24*60*60*1000) * 7;
	var chartResultList = null;
	chartStartDate = new Date(chartStartDate.getTime()-dateOffset);
		
	//////////////////
	dtpOptionFrom.datetimepicker({
		format: "dd/mm/yyyy",
		todayBtn:  true,
		autoclose: true,
		clearBtn: true,
		startView: 2,
        minView: 2
	}).data("datetimepicker").setDate(globalStringYYYYMMDDToDate((paramStartDate===null?chartStartDate.YYYYMMDD():paramStartDate)));	
	dtpOptionTo.datetimepicker({
		format: "dd/mm/yyyy",
		todayBtn:  true,
		autoclose: true,
		clearBtn: true,
		startView: 2,
        minView: 2	
	}).data("datetimepicker").setDate(globalStringYYYYMMDDToDate((paramEndDate===null?chartEndDate.YYYYMMDD():paramEndDate)));	
	
	google.load("visualization", "1", {packages:["corechart"]});
    google.setOnLoadCallback(forceReloadChart);
    function forceReloadChart(){
    	reloadChart(true, chartStartDate, chartEndDate);
    	
    }
    
    function addChart(resultList){
    	 globalWriteConsoleLog("Start add Chart:" + resultList);
    	 var panelShowStatistics = $("#panelShowStatistics");
    	 panelShowStatistics.empty();
    	 var templateChartRow = $("#templateChartRow");
    	 var templateChartItem = $("#templateChartItem");
    	 var chartItem =$("#chartItem");
    	 var chartItemNew,chartItemNameNew,templateChartItemNew,resultMod,templateChartRowNew,chartHeaderNew,chartFooterNew  = null;
    	 var chartItemName = "chartItem";
    	 var templateChartTotal = new Array();
    	 var tempNumber = "templateChartRow";
    	 var templateChartNumber = "templateChartItem";
    	 var countNumber,countIndex = 0;
    	 var startDateParam = chartStartDate.YYYYMMDD();
    	 var endDateParam = chartEndDate.YYYYMMDD();

    	 $.each(resultList.chartResultList, function(idx, obj){
    		countIndex += 1;
    		resultMod = idx % 2;
    		if(resultMod === 0){
    			countNumber += 1;
    			templateChartRowNew = templateChartRow.clone();  	
    			templateChartRowNew.appendTo(panelShowStatistics);	
    			templateChartRowNew.attr("id", tempNumber+countNumber);
    			templateChartRowNew.show();	
    		}
    		templateChartItemNew = templateChartItem.clone(); 
    		templateChartItemNew.appendTo(templateChartRowNew);  
    		templateChartItemNew.attr("id", templateChartNumber+countIndex);
    		templateChartItemNew.show();
    		chartItemNew = chartItemName + countIndex;
    		chartHeaderNew = "chartTitle" + countIndex;
    		chartFooterNew = "chartFooter" + countIndex;
    		templateChartItemNew.find("#chartItem").attr("id", chartItemNew);
    		templateChartItemNew.find("#chartTitle").attr("id", chartHeaderNew);
    		templateChartItemNew.find("#chartFooter").attr("id",chartFooterNew)
    		chartResult = globalConvertGoogleChartObjectToArray(obj);
    		showChartResult(chartResult,chartItemNew,chartHeaderNew,chartFooterNew,startDateParam, endDateParam); 
    	 });
    	 
    	 globalWriteConsoleLog("End add Chart");
    }
    function reloadChart(forceReload, startDateParam, endDateParam){    	
    	globalWriteConsoleLog("reloadChart with force param:"+forceReload);
    	if (forceReload || chartResultList===null){    		
			var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = chartStartDate.YYYYMMDD();
        	reportFEParam["endDate"] = chartEndDate.YYYYMMDD()+"235959";
        	reportFEParam["dashBoardType"] = $("#cmbOptionDashboard").val();
        	$.ajax({
    	        url: "/xFace/rest/rep/daily/getDailyByTime",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	addChart(resultList);
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Passenger]", "danger", error);
    	        	$("#panelChartPassengerWait").removeClass("lds-spinner");
	        		$("#panelChartPassengerWait").hide();
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartPassenger = showChartResult(chartPassengerResultList, "chartPassenger", startDateParam, endDateParam);
    	}   	
    }       
    
    function showChartResult(chartResultList, chartElementName,chartHeaderNew,chartFooterNew,startDateParam, endDateParam) {
    	globalWriteConsoleLog("show "+chartElementName+" result");
  		var data = google.visualization.arrayToDataTable(chartResultList["resultList"]);
  		var options = {
    			title: chartResultList["title"],
		        hAxis: {title: chartResultList["hAxisTitle"], titleTextStyle: {color: "red"}},
  		 		vAxis: {title: chartResultList["vAxisTitle"], titleTextStyle: {color: "red"}}
		    };		
  		var chartObject = new google.visualization.ColumnChart($("#"+chartElementName)[0]);
  		console.log("chartObject Value ::" + chartObject);
  		
  		if(chartResultList["title"] != "Unknown"){
  			chartObject.draw(data, options);
  			$("#"+chartHeaderNew).text(chartResultList["title"]);
  		}
  		
  		
  		var title = null;
  		title = chartResultList["title"];
  		$("#"+chartHeaderNew).text(chartResultList["title"]);
  		$("#"+chartFooterNew).text(chartResultList["footer"]);
  		google.visualization.events.addListener(chartObject, "click", chartClickHandler);  
  		
  		function chartClickHandler(event) { 
  			var parts = event.targetID.split("#");
  			var idxLabel = parts.indexOf("label");
  			var idxBar = parts.indexOf("bar");
  			var idxLegendentry = parts.indexOf("legendentry");
  			var redirectURL = null;
  			var dateValue = null;
  			var chartValue = null;
  			chartValue = $("#cmbOptionDashboard").val();
  			
  			if (idxLabel>=0){
  				//label on x axis click
  				idxLabel++;
  				var label = data.getValue(Number(parts[idxLabel]), 0);
  				
  				if(startDateParam===endDateParam){
  					var stringStartTime = null;
  	  				var dateTypeStartTime = null;
  	  				var plusHours = null;
  	  				var currentPlus = null;
  	  				var afterPlusHours = null;
  	  				var startTimePlus = null;
  					stringStartTime = startDateParam + label + "0000";
  					dateTypeStartTime = globalStringYYYYMMDDHHMMSSToDate(stringStartTime);
  					currentPlus = dateTypeStartTime.setHours(dateTypeStartTime.getHours()+1);	
  					afterPlusHours = new Date(currentPlus);
  					startTimePlus = afterPlusHours.YYYYMMDDHHMMSS();
  					if (chartValue==="RNPBT"){
  						//chartPassenger
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus;
  					}else if(chartValue==="RNPGBT"){
  						//chartFace
  						redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus;
  					}else if (chartValue==="RNFBT"){
  						//chartPassengerbyGate
  						redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&gate="+title;
  					}else if (chartValue==="RNFGBT"){
  						//chartFaceByGate
  						redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&gate="+title;
  					}else if (chartValue==="RNPBBT"){
  						//chartPassengerByBoat
  						redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&boat="+title;
					}else{
						globalShowGrowlNotification($("#errorDialogTitle").text(),"Chart for "+title+" not allow to drill down", "danger");
					} 
  				}else{	
	  				if(chartValue==="RNPBT"){
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959";
	  				}else if(chartValue==="RNPGBT"){
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959";
	  				}else if(chartValue==="RNFBT"){
						redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&gate="+title;
	  				}else if(chartValue==="RNFGBT"){
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&gate="+title;
	  				}else if(chartValue==="RNPBBT"){
						redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&boat="+title;
					}else{
						globalShowGrowlNotification($("#errorDialogTitle").text(),"Chart for "+title+" not allow to drill down", "danger");
					} 
  				}	
  				
  			}else if (idxBar>=0){  		
  				//bar click
  				//["bar", "1", "2"] 1=chart no.2, 2=column 3
  				idxBar++;
  				var chartBar = data.getColumnLabel(Number(parts[idxBar])+1);
  				idxBar++;
  				var label = data.getValue(Number(parts[idxBar]), 0);
  				
  				if(startDateParam===endDateParam){
  					var stringStartTime = null;
  	  				var dateTypeStartTime = null;
  	  				var plusHours = null;
  	  				var currentPlus = null;
  	  				var afterPlusHours = null;
  	  				var startTimePlus = null;
  					stringStartTime = startDateParam + label + "0000";
  					dateTypeStartTime = globalStringYYYYMMDDHHMMSSToDate(stringStartTime);
  					currentPlus = dateTypeStartTime.setHours(dateTypeStartTime.getHours()+1);	
  					afterPlusHours = new Date(currentPlus);
  					startTimePlus = afterPlusHours.YYYYMMDDHHMMSS();
  
  					if (chartValue==="RNPBT"){
  						//chartPassenger
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&direct="+chartBar;
	  				}else if (chartValue==="RNPGBT"){
	  					//chartPassengerByGate
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&gate="+title+"&direct="+chartBar;
	  				}else if (chartValue==="RNFBT"){
	  					//chartFace
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&cond="+chartBar;
	  				}else if (chartValue==="RNFGBT"){
	  					//chartFaceByGate
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&gate="+title+"&cond="+chartBar;
	  				}else if (chartValue==="RNPBBT"){
	  					//chartPassengerByBoat
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+stringStartTime+"&enddate="+startTimePlus+"&boat="+title+"&direct="+chartBar;
	  				}   	
  				}else{
	  				if (chartValue==="RNPBT"){
	  					//chartPassenger
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&direct="+chartBar;
	  				}else if (chartValue==="RNPGBT"){
	  					//chartPassengerByGate
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&gate="+title+"&direct="+chartBar;
	  				}else if (chartValue==="RNFBT"){
	  					//chartFace
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&cond="+chartBar;
	  				}else if (chartValue==="RNFGBT"){
	  					//chartFaceByGate
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&gate="+title+"&cond="+chartBar;
	  				}else if (chartValue==="RNPBBT"){
	  					//chartPassengerByBoat
	  					redirectURL = "/xFace/person/alarmHistory?startdate="+startDateParam+"&enddate="+endDateParam+"235959"+"&boat="+title+"&direct="+chartBar;
	  				} 
  				}
  			}else if (idxLegendentry>=0){
  				//sign on top right click (in/out, match/unmatch)
  				idxLegendentry++;
  				//alert("Legendentry click "+data.getColumnLabel(Number(parts[idxLegendentry])+1));
  				if (chartValue==="RNPBT" || chartValue==="RNPGBT" || chartValue==="RNFBT"){
  					redirectURL = "/xFace/person/alarmHistory?direct="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
  				}else if (chartValue==="RNFGBT" || chartValue==="RNPBBT"){
  					redirectURL = "/xFace/person/alarmHistory?cond="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
  				}
  			}
  			if (redirectURL!==null){
  				//disableAllTimer();
  				//start, end date
  				//alert(redirectURL);
  				
  				if (redirectURL.indexOf("startdate")<0){
  					window.open(redirectURL+"&startdate="+chartStartDate.YYYYMMDD()+"&enddate="+chartEndDate.YYYYMMDDHHMMSS(),"_self");
  				}else{
  					window.open(redirectURL,"_self");
  				}
  				
  			}
  		}  		  			         
  		  		
  		return chartObject;
	}
    $(window).resize(function(){    	
    	reloadChart(false, chartStartDate, chartEndDate);
	});
    $("#btnOptionRefresh").on("click", function(event){
    	
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				
    	reloadChart(true, chartStartDate, chartEndDate);
    });
    $("#btnRefresh").on("click", function(event){
    	
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();
    	reloadChart(true, chartStartDate, chartEndDate);
    });    
});














/*
$(document).ready( function () {
	var dtpOptionFrom = $("#dtpOptionFrom"), dtpOptionTo = $("#dtpOptionTo");	
	var chartStartDate = new Date(), chartEndDate = new Date();	
	//start date is 7 day behide 
	var dateOffset = (24*60*60*1000) * 7;
	var chartResultList = null;
	chartStartDate = new Date(chartStartDate.getTime()-dateOffset);	
	//////////////////
	dtpOptionFrom.datetimepicker({
		format: "dd/mm/yyyy",
		todayBtn:  true,
		autoclose: true,
		clearBtn: true,
		startView: 2,
        minView: 2
	}).data("datetimepicker").setDate(globalStringYYYYMMDDToDate((paramStartDate===null?chartStartDate.YYYYMMDD():paramStartDate)));	
	dtpOptionTo.datetimepicker({
		format: "dd/mm/yyyy",
		todayBtn:  true,
		autoclose: true,
		clearBtn: true,
		startView: 2,
        minView: 2	
	}).data("datetimepicker").setDate(globalStringYYYYMMDDToDate((paramEndDate===null?chartEndDate.YYYYMMDD():paramEndDate)));	
	google.load("visualization", "1", {packages:["corechart"]});
    google.setOnLoadCallback(forceReloadChart);
    function forceReloadChart(){
    	reloadChart(true, chartStartDate, chartEndDate);
    }
    function reloadChart(forceReload, startDateParam, endDateParam){    	
    	globalWriteConsoleLog("reloadChart with force param:"+forceReload);
    	if (forceReload || chartResultList===null){    		
			var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = chartStartDate.YYYYMMDD();
        	reportFEParam["endDate"] = chartEndDate.YYYYMMDD()+"235959";
        	reportFEParam["dashBoardType"] = $("#cmbOptionDashboard").val();
        	$.ajax({
    	        url: "/xFace/rest/rep/daily/getDailyByTime",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(multiChartResultList) {
    	        	globalIsSessionExpire(multiChartResultList);
    	        	globalWriteConsoleLog(multiChartResultList);
    	        	//chartResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	//chartPassenger = showChartResult(chartPassengerResultList, "chartPassenger", "Passenger", "panelChartPassengerWait", reloadPassengerChart, startDateParam, endDateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Passenger]", "danger", error);
    	        	$("#panelChartPassengerWait").removeClass("lds-spinner");
	        		$("#panelChartPassengerWait").hide();
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartPassenger = showChartResult(chartPassengerResultList, "chartPassenger", "Passenger", "panelChartPassengerWait", reloadPassengerChart, startDateParam, endDateParam);
    	}   	
    }        
    function showChartResult(chartResultList, chartElementName, chartName, panelChartWaitId, reloadChart, startDateParam, endDateParam) {
    	globalWriteConsoleLog("show "+chartElementName+" result");
//  		var data = google.visualization.arrayToDataTable(chartResultList["resultList"]);
//  		var options = {
//    			//title: chartResultList["title"],
//		        hAxis: {title: chartResultList["hAxisTitle"], titleTextStyle: {color: "red"}},
//  		 		vAxis: {title: chartResultList["vAxisTitle"], titleTextStyle: {color: "red"}}
//		    };		
//  		var chartObject = new google.visualization.ColumnChart($("#"+chartElementName)[0]);
//  		chartObject.draw(data, options);
//  		$("#"+chartElementName+"Footer").text(chartResultList["footer"]);
//  		google.visualization.events.addListener(chartObject, "click", chartClickHandler);  
//  		//eddy
//  		//var chartPassenger = null, chartPassengerByGate = null, chartFace = null, chartFaceByGate = null, chartPassengerByBoat = null;
//  		function chartClickHandler(event) {  			  		
//  			var parts = event.targetID.split("#");
//  			var idxLabel = parts.indexOf("label");
//  			var idxBar = parts.indexOf("bar");
//  			var idxLegendentry = parts.indexOf("legendentry");
//  			var redirectURL = null;
//  			var dateValue = null;
//  			if (idxLabel>=0){
//  				//label on x axis click
//  				idxLabel++;
//  				if (chartObject===chartPassenger || chartObject===chartFace){
//  					dateValue = data.getValue(Number(parts[idxLabel]), 0).replace("-","").replace("-","");
//  					redirectURL = "/xFace/person/alarmHistory?startdate="+dateValue+"&enddate="+dateValue+"235959";						
//				}else if (chartObject===chartPassengerByGate || chartObject===chartFaceByGate){
//					redirectURL = "/xFace/person/alarmHistory?gate="+data.getValue(Number(parts[idxLabel]), 0);
//				}else if (chartObject===chartPassengerByBoat){
//					redirectURL = "/xFace/person/alarmHistory?boat="+data.getValue(Number(parts[idxLabel]), 0);
//				}else{
//					globalShowGrowlNotification($("#errorDialogTitle").text(),"Chart for "+data.getValue(Number(parts[idxLabel]), 0)+" not allow to drill down", "danger");
//				}  				
//  			}else if (idxBar>=0){  		
//  				//bar click
//  				//["bar", "1", "2"] 1=chart no.2, 2=column 3
//  				idxBar++;
//  				var chartBar = data.getColumnLabel(Number(parts[idxBar])+1);
//  				idxBar++;
//  				var label = data.getValue(Number(parts[idxBar]), 0);
//  				//alert("bar click "+chartBar+" label "+label);
//  				if (chartObject===chartPassenger){
//  					dateValue = label.replace("-","").replace("-","");
//  					redirectURL = "/xFace/person/alarmHistory?startdate="+dateValue+"&enddate="+dateValue+"235959"+"&direct="+chartBar;
//  				}else if (chartObject===chartPassengerByGate){
//  	  				redirectURL = "/xFace/person/alarmHistory?gate="+label+"&direct="+chartBar;
//  				}else if (chartObject===chartFace){
//  					dateValue = label.replace("-","").replace("-","");
//  					redirectURL = "/xFace/person/alarmHistory?startdate="+dateValue+"&enddate="+dateValue+"235959"+"&cond="+chartBar;
//  				}else if (chartObject===chartFaceByGate){
//  					redirectURL = "/xFace/person/alarmHistory?gate="+label+"&cond="+chartBar;
//  				}else if (chartObject===chartPassengerByBoat){
//  					redirectURL = "/xFace/person/alarmHistory?boat="+label+"&direct="+chartBar;
//  				}   				
//  			}else if (idxLegendentry>=0){
//  				//sign on top right click (in/out, match/unmatch)
//  				idxLegendentry++;
//  				//alert("Legendentry click "+data.getColumnLabel(Number(parts[idxLegendentry])+1));
//  				if (chartObject===chartPassenger || chartObject===chartPassengerByGate || chartObject===chartPassengerByBoat){
//  					redirectURL = "/xFace/person/alarmHistory?direct="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
//  				}else if (chartObject===chartFace || chartObject===chartFaceByGate){
//  					redirectURL = "/xFace/person/alarmHistory?cond="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
//  				}
//  			}
//  			if (redirectURL!==null){
//  				//disableAllTimer();
//  				//start, end date
//  				if (redirectURL.indexOf("startdate")<0){
//  					window.open(redirectURL+"&startdate="+chartStartDate.YYYYMMDD()+"&enddate="+chartEndDate.YYYYMMDDHHMMSS(),"_self");
//  				}else{
//  					window.open(redirectURL,"_self");
//  				}
//  			}
//  		}  		  			         	
//		$("#"+panelChartWaitId).removeClass("lds-spinner");
//		$("#"+panelChartWaitId).hide();  		
//  		return chartObject;
    	return null;
	}
    
    $(window).resize(function(){    	
    	//reload without call api
    	//reloadPassengerChart(false, chartStartDate, chartEndDate);
    	reloadChart(false, chartStartDate, chartEndDate);
	});
    $("#btnOptionRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				
    	reloadChart(true, chartStartDate, chartEndDate);
    });
    $("#btnRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				
    	reloadPassengerChart(true, chartStartDate, chartEndDate);    	
    });    
});


*/