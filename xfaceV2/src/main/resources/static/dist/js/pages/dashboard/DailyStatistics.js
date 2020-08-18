$(document).ready( function () {
	var dtpOptionFrom = $("#dtpOptionFrom"), dtpOptionTo = $("#dtpOptionTo");
	var chartPassengerResultList = null, chartPassengerByGateResultList = null, chartFaceResultList = null, chartFaceByGateResultList = null, chartPassengerByBoatResultList = null;
	var chartStartDate = new Date(), chartEndDate = new Date();
	var chartPassenger = null, chartPassengerByGate = null, chartFace = null, chartFaceByGate = null, chartPassengerByBoat = null;
	//start date is 7 day behide 
	var dateOffset = (24*60*60*1000) * 7;
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
    	reloadPassengerChart(true, chartStartDate, chartEndDate);
    	reloadPassengerByGateChart(true, chartStartDate, chartEndDate);
    	reloadFaceChart(true, chartStartDate, chartEndDate);
    	reloadFaceByGateChart(true, chartStartDate, chartEndDate);
    	reloadPassengerByBoatChart(true, chartStartDate, chartEndDate);
    }
    function reloadPassengerChart(forceReload, startDateParam, endDateParam){    	
    	globalWriteConsoleLog("reload Passenger with force param:"+forceReload);
    	if (forceReload || chartPassengerResultList===null){    		
			$("#panelChartPassengerWait").addClass("lds-spinner");
			$("#panelChartPassengerWait").show();    		    		    		
    		var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = chartStartDate.YYYYMMDD();
        	reportFEParam["endDate"] = chartEndDate.YYYYMMDD()+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/daily/getDailyPassenger",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartPassengerResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartPassenger = showChartResult(chartPassengerResultList, "chartPassenger", "Passenger", "panelChartPassengerWait", reloadPassengerChart, startDateParam, endDateParam); 	        		       
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
    function reloadPassengerByGateChart(forceReload, startDateParam, endDateParam){    	
    	globalWriteConsoleLog("reload Passenger by gate with force param:"+forceReload);
    	if (forceReload || chartPassengerByGateResultList===null){    		
			$("#panelChartPassengerByGateWait").addClass("lds-spinner");
			$("#panelChartPassengerByGateWait").show();
    		var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = chartStartDate.YYYYMMDD();
        	reportFEParam["endDate"] = chartEndDate.YYYYMMDD()+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/daily/getDailyPassengerByGate",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartPassengerByGateResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartPassengerByGate = showChartResult(chartPassengerByGateResultList, "chartPassengerByGate", "PassengerByGate", "panelChartPassengerByGateWait", reloadPassengerByGateChart, startDateParam, endDateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[PassengerByGate]", "danger", error);    	        	
	        		$("#panelChartPassengerByGateWait").removeClass("lds-spinner");
	        		$("#panelChartPassengerByGateWait").hide();    	        
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartPassengerByGate = showChartResult(chartPassengerByGateResultList, "chartPassengerByGate", "PassengerByGate", "panelChartPassengerByGateWait", reloadPassengerByGateChart, startDateParam, endDateParam);
    	}   	
    }
    function reloadFaceChart(forceReload, startDateParam, endDateParam){    	
    	globalWriteConsoleLog("reload face with force param:"+forceReload);
    	if (forceReload || chartFaceResultList===null){    		
			$("#panelChartFaceWait").addClass("lds-spinner");
			$("#panelChartFaceWait").show();    		
    		var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = chartStartDate.YYYYMMDD();
        	reportFEParam["endDate"] = chartEndDate.YYYYMMDD()+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/daily/getDailyFace",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartFaceResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartFace = showChartResult(chartFaceResultList, "chartFace", "Face", "panelChartFaceWait", reloadFaceChart, startDateParam, endDateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Face]", "danger", error);    	        	
	        		$("#panelChartFaceWait").removeClass("lds-spinner");
	        		$("#panelChartFaceWait").hide();    	        
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartFace = showChartResult(chartFaceResultList, "chartFace", "Face", "panelChartFaceWait", reloadFaceChart, startDateParam, endDateParam);
    	}   	
    }
    function reloadFaceByGateChart(forceReload, startDateParam, endDateParam){    	
    	globalWriteConsoleLog("reload face by gate with force param:"+forceReload);
    	if (forceReload || chartFaceByGateResultList===null){    		
			$("#panelChartFaceByGateWait").addClass("lds-spinner");
			$("#panelChartFaceByGateWait").show();    		
    		var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = chartStartDate.YYYYMMDD();
        	reportFEParam["endDate"] = chartEndDate.YYYYMMDD()+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/daily/getDailyFaceByGate",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartFaceByGateResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartFaceByGate = showChartResult(chartFaceByGateResultList, "chartFaceByGate", "FaceByGate", "panelChartFaceByGateWait", reloadFaceByGateChart, startDateParam, endDateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[FaceByGate]", "danger", error);    	    
	        		$("#panelChartFaceByGateWait").removeClass("lds-spinner");
	        		$("#panelChartFaceByGateWait").hide();    	        
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartFaceByGate = showChartResult(chartFaceByGateResultList, "chartFaceByGate", "FaceByGate", "panelChartFaceByGateWait", reloadFaceByGateChart, startDateParam, endDateParam);
    	}   	
    }
    
    function reloadPassengerByBoatChart(forceReload, startDateParam, endDateParam){    	
    	globalWriteConsoleLog("reload Passenger boat with force param:"+forceReload);
    	if (forceReload || chartPassengerByBoatResultList===null){    		
			$("#panelChartPassengerByBoatWait").addClass("lds-spinner");
			$("#panelChartPassengerByBoatWait").show();    		
    		var reportFEParam = {};
    		//var now = new Date(); 
        	reportFEParam["startDate"] = chartStartDate.YYYYMMDD();
        	reportFEParam["endDate"] = chartEndDate.YYYYMMDD()+"235959";
        	$.ajax({
    	        url: "/xFace/rest/rep/daily/getDailyPassengerByBoat",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	chartPassengerByBoatResultList = globalConvertGoogleChartObjectToArray(resultList);
    	        	chartPassengerByBoat = showChartResult(chartPassengerByBoatResultList, "chartPassengerByBoat", "PassengerBoat", "panelChartPassengerByBoatWait", reloadPassengerByBoatChart, startDateParam, endDateParam); 	        		       
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[PassengerBoat]", "danger", error);    	        	
	        		$("#panelChartPassengerByBoatWait").removeClass("lds-spinner");
	        		$("#panelChartPassengerByBoatWait").hide();    	        
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");    		
    		chartPassengerByBoat = showChartResult(chartPassengerByBoatResultList, "chartPassengerByBoat", "PassengerBoat", "panelChartPassengerByBoatWait", reloadPassengerByBoatChart, startDateParam, endDateParam);
    	}   	
    }
    
    function showChartResult(chartResultList, chartElementName, chartName, panelChartWaitId, reloadChart, startDateParam, endDateParam) {
    	globalWriteConsoleLog("show "+chartElementName+" result");
  		var data = google.visualization.arrayToDataTable(chartResultList["resultList"]);
  		var options = {
    			//title: chartResultList["title"],
		        hAxis: {title: chartResultList["hAxisTitle"], titleTextStyle: {color: "red"}},
  		 		vAxis: {title: chartResultList["vAxisTitle"], titleTextStyle: {color: "red"}}
		    };		
  		var chartObject = new google.visualization.ColumnChart($("#"+chartElementName)[0]);
  		chartObject.draw(data, options);
  		$("#"+chartElementName+"Footer").text(chartResultList["footer"]);
  		google.visualization.events.addListener(chartObject, "click", chartClickHandler);  
  		//eddy
  		//var chartPassenger = null, chartPassengerByGate = null, chartFace = null, chartFaceByGate = null, chartPassengerByBoat = null;
  		function chartClickHandler(event) {  			  		
  			var parts = event.targetID.split("#");
  			var idxLabel = parts.indexOf("label");
  			var idxBar = parts.indexOf("bar");
  			var idxLegendentry = parts.indexOf("legendentry");
  			var redirectURL = null;
  			var dateValue = null;
  			if (idxLabel>=0){
  				//label on x axis click
  				idxLabel++;
  				if (chartObject===chartPassenger || chartObject===chartFace){
  					dateValue = data.getValue(Number(parts[idxLabel]), 0).replace("-","").replace("-","");
  					redirectURL = "/xFace/person/alarmHistory?startdate="+dateValue+"&enddate="+dateValue+"235959";						
				}else if (chartObject===chartPassengerByGate || chartObject===chartFaceByGate){
					redirectURL = "/xFace/person/alarmHistory?gate="+data.getValue(Number(parts[idxLabel]), 0);
				}else if (chartObject===chartPassengerByBoat){
					redirectURL = "/xFace/person/alarmHistory?boat="+data.getValue(Number(parts[idxLabel]), 0);
				}else{
					globalShowGrowlNotification($("#errorDialogTitle").text(),"Chart for "+data.getValue(Number(parts[idxLabel]), 0)+" not allow to drill down", "danger");
				}  				
  			}else if (idxBar>=0){  		
  				//bar click
  				//["bar", "1", "2"] 1=chart no.2, 2=column 3
  				idxBar++;
  				var chartBar = data.getColumnLabel(Number(parts[idxBar])+1);
  				idxBar++;
  				var label = data.getValue(Number(parts[idxBar]), 0);
  				//alert("bar click "+chartBar+" label "+label);
  				if (chartObject===chartPassenger){
  					dateValue = label.replace("-","").replace("-","");
  					redirectURL = "/xFace/person/alarmHistory?startdate="+dateValue+"&enddate="+dateValue+"235959"+"&direct="+chartBar;
  				}else if (chartObject===chartPassengerByGate){
  	  				redirectURL = "/xFace/person/alarmHistory?gate="+label+"&direct="+chartBar;
  				}else if (chartObject===chartFace){
  					dateValue = label.replace("-","").replace("-","");
  					redirectURL = "/xFace/person/alarmHistory?startdate="+dateValue+"&enddate="+dateValue+"235959"+"&cond="+chartBar;
  				}else if (chartObject===chartFaceByGate){
  					redirectURL = "/xFace/person/alarmHistory?gate="+label+"&cond="+chartBar;
  				}else if (chartObject===chartPassengerByBoat){
  					redirectURL = "/xFace/person/alarmHistory?boat="+label+"&direct="+chartBar;
  				}   				
  			}else if (idxLegendentry>=0){
  				//sign on top right click (in/out, match/unmatch)
  				idxLegendentry++;
  				//alert("Legendentry click "+data.getColumnLabel(Number(parts[idxLegendentry])+1));
  				if (chartObject===chartPassenger || chartObject===chartPassengerByGate || chartObject===chartPassengerByBoat){
  					redirectURL = "/xFace/person/alarmHistory?direct="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
  				}else if (chartObject===chartFace || chartObject===chartFaceByGate){
  					redirectURL = "/xFace/person/alarmHistory?cond="+data.getColumnLabel(Number(parts[idxLegendentry])+1);
  				}
  			}
  			if (redirectURL!==null){
  				//disableAllTimer();
  				//start, end date
  				if (redirectURL.indexOf("startdate")<0){
  					window.open(redirectURL+"&startdate="+chartStartDate.YYYYMMDD()+"&enddate="+chartEndDate.YYYYMMDDHHMMSS(),"_self");
  				}else{
  					window.open(redirectURL,"_self");
  				}
  			}
  		}  		  			         	
		$("#"+panelChartWaitId).removeClass("lds-spinner");
		$("#"+panelChartWaitId).hide();  		
  		return chartObject;
	}
    
    $(window).resize(function(){    	
    	reloadPassengerChart(false, chartStartDate, chartEndDate);
    	reloadPassengerByGateChart(false, chartStartDate, chartEndDate);
    	reloadFaceChart(false, chartStartDate, chartEndDate);
    	reloadFaceByGateChart(false, chartStartDate, chartEndDate);
    	reloadPassengerByBoatChart(false, chartStartDate, chartEndDate);
	});
    $("#btnOptionRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				
    	forceReloadChart();
    });
    $("#btnChartPassengerRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				
    	reloadPassengerChart(true, chartStartDate, chartEndDate);    	
    });
    $("#btnChartPassengerByGateRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				    	
    	reloadPassengerByGateChart(true, chartStartDate, chartEndDate);    	
    });
    $("#btnChartFaceRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				
    	reloadFaceChart(true, chartStartDate, chartEndDate);    	
    });
    $("#btnChartFaceByGateRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				    	
    	reloadFaceByGateChart(true, chartStartDate, chartEndDate);    	
    });
    $("#btnChartPassengerByBoatRefresh").on("click", function(event){
    	chartStartDate = dtpOptionFrom.data("datetimepicker").getDate();
    	chartEndDate = dtpOptionTo.data("datetimepicker").getDate();				    	
    	reloadPassengerByBoatChart(true, chartStartDate, chartEndDate);
    });
    
    
});