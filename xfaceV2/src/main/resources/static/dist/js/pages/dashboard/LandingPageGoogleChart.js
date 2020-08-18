$(document).ready(function(){
	var passengerResult = null;
	globalImageLoader =  $("#imageLoader");
	google.load("visualization", "1", {packages:["corechart"]});
    google.setOnLoadCallback(forceReloadPassengerRegister);
    function createParameterPassengerRegister(){
    	
    }
    function forceReloadPassengerRegister(){
    	reloadPassengerRegister(true);
    }
    function convertObjectToArray(resultList){
    	var arrayResultList = [];
    	var cntRow = -1;
    	var cntCol = -1;
    	$.each(resultList.passengerResultList, function(i, itemFirst){
    		cntRow += 1;
    		cntCol = -1;
    		arrayResultList[cntRow] = [];
    		$.each(itemFirst.resultList, function(i, itemSecond){
    			cntCol += 1;  
    			if (cntRow===0 || cntCol===0){
    				//header area
    				arrayResultList[cntRow][cntCol] = itemSecond;
    			}else{
    				//data area
    				arrayResultList[cntRow][cntCol] = Number(itemSecond);
    			}        		
    		});    		
		});
    	var returnResult = {};
    	returnResult["title"] = resultList.title;
    	returnResult["axisTitle"] = resultList.axisTitle;
    	returnResult["passengerResultList"] = arrayResultList;
    	return returnResult;
    }
    function reloadPassengerRegister(forceReload){    	
    	globalWriteConsoleLog("reload passenger register with force param:"+forceReload);
    	if (forceReload || passengerResult===null){
    		globalAddLoader();
    		globalWriteConsoleLog("force call api");
    		var reportFEParam = {};
        	reportFEParam["startDate"] = "20190207";
        	$.ajax({
    	        url: "/xFace/rest/rep/getPassengerRegisterList",
    	        type: "POST",
    	        contentType: "application/json; charset=utf-8",
    	        dataType: "json", 	 
    	        data: JSON.stringify(reportFEParam),
    	        success: function(resultList) {
    	        	globalIsSessionExpire(resultList);
    	        	globalWriteConsoleLog(resultList);
    	        	passengerResult = convertObjectToArray(resultList);
    	        	showGraphResult();	        	
    	        },
    	        error: function(error){            	
    	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
    	        	globalClearLoader();	        	
    	        }
    	    });
    	}else{
    		globalWriteConsoleLog("use existing data");
    		showGraphResult();
    	}   	
    }
    function showGraphResult() {
    	globalWriteConsoleLog("show passenger result");
    	
  		var data = google.visualization.arrayToDataTable(passengerResult["passengerResultList"]);
  		var options = {
    			title: passengerResult["title"],
		        hAxis: {title: passengerResult["axisTitle"], titleTextStyle: {color: "red"}}
		    };		
		var chart = new google.visualization.ColumnChart($("#chartPassengerRegister")[0]);
  		chart.draw(data, options);
  		google.visualization.events.addListener(chart, "select", selectHandler);
  		
  		function selectHandler() {
  			var selection = chart.getSelection();
  			var message = '';  			
  			if (selection.length>0){
  				var item = selection[0];
  				if (item.row !== null && item.column !== null) {
  					message += '{row:' + item.row + ',column:' + item.column+ '} = ' + data.wg[item.row].c[item.column].v
  							+ '  The Category is:' + data.wg[item.row].c[0].v
						+ ' it belongs to : ' + data.vg[item.column].label;
  				}else if(item.column !== null){
  					message = 'it belongs to : ' + data.vg[item.column].label;
  				}else if(item.row !== null){
  					message ='The Category is:' + data.wg[item.row].c[0].v;					
  				}
  			}
  			if (message == '') {
  				message = 'nothing';
  			}
  			alert('You selected ' + message);
  		}
  		globalClearLoader();
	}
	$(window).resize(function(){
		reloadPassengerRegister(false);  		
	});
});	
