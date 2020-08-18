var globalImageLoader = null;
function globalIsSessionExpire(responseText){
	if ((typeof responseText === "string" || responseText instanceof String) 
			&& responseText.indexOf("xxyyinvalidSessionxxyy")>-1){
		window.open("/xFace/error/invalidsession","_self");
		globalWriteConsoleLog("session expire");
		return true;
	}else{
		return false;
	}
}
function globalShowGrowlNotification(title, message, type, errorObject){		
	if (typeof errorObject!=="undefined" &&
				(errorObject.statusCode().status===401 || errorObject.statusCode().status===0 
				||globalIsSessionExpire(errorObject.statusCode().responseText))){
		//Unauthorized redirect to invalid session
		window.open("/xFace/error/invalidsession","_self");
		//globalWriteConsoleLog("session expire");
	}else{
		globalClearLoader();
		$.notify({
			title: title+":",
			message: message
		},{
			timer: globalAlertTimer,
			type: type,
			delay: globalAlertDelay
		});
	}
}
function globalDisplayError(errorMsg, errorObject){
	if (typeof errorObject!=="undefined" &&
			(errorObject.statusCode().status===401 || errorObject.statusCode().status===0 
					||globalIsSessionExpire(errorObject.statusCode().responseText))){
		//Unauthorized redirect to invalid session
		window.open("/xFace/error/invalidsession","_self");
	}else{
		globalClearLoader();
		var divErrorMsg = $("#divDisplayError");
		divErrorMsg.empty();
		if (errorMsg===""){		    		    
	    	return true;	
	    }else{		    	
	    	divErrorMsg.append("<div class='alert alert-danger'><a href='#' class='close' data-dismiss='alert' aria-label='close'>&times;</a><strong>Error!</strong> "+errorMsg+"</div>");
	    	return false;
	    }			
	}
}
function globalRemoveError(){
	var divErrorMsg = $("#divDisplayError");
	divErrorMsg.empty();			
}
function globalWriteConsoleLog(logMsg){
	console.log(logMsg);
}
//handle tab click to show information
function globalHandleTabClick(event){
	var tabClick = $(event.delegateTarget);
	globalChangeTab(tabClick);
}    

//change tab to expectedActiveTab
function globalChangeTab(expectedActiveTab){	
	if (!expectedActiveTab.parent().hasClass("active")){
		globalShowActiveTab(expectedActiveTab);
	}
}
//handle tab close button click to close tab
function globalHandleCloseTabClick(event){       		
	globalHandleCloseTab($(event.delegateTarget));
}

//close tab by receive li of menu which user need to close
function globalHandleCloseTab(btnCloseTab){       		
	var liCloseTab = btnCloseTab.parent().parent();
	liCloseTab.remove();    	
	var keyId = btnCloseTab.attr("data-div-content");    	    	
	var divCloseTab = $("#divTabContent").find("#"+keyId);
	divCloseTab.remove();
	globalWriteConsoleLog("handleCloseTabClick");
	if (!$("#ulTabMenu").find("#liAllData").parent().hasClass("active")){
		//not refresh if active tab is main tab
		globalShowActiveTab($("#ulTabMenu").find("#liAllData"));
	}    	 
}

//show active tab and refresh information if this is main tab
function globalShowActiveTab(activeTab){
	globalWriteConsoleLog("in globalShowActiveTab");
	$("#ulTabMenu li.active").removeClass("active");
	activeTab.parent().addClass("active");
	$("#divTabContent div.active").removeClass("in active");    	
	$("#divTabContent").find("#"+activeTab.attr("data-div-content")).addClass("in active");
	if (activeTab.is($("#ulTabMenu").find("#liAllData"))){
		//var dataTable = $("#data-table");
		$("#data-table").DataTable().ajax.reload(null, false);
	}
	globalWriteConsoleLog("out globalShowActiveTab");
}
/////////////////////
//show dialog add/edit
function globalShowTab(dataCode, tabTitle, tabTemplate) {
	globalWriteConsoleLog("in globalShowTab");    	
	var divTabContent = $("#divTabContent");	
	//check dataCode tab already open
	var ulTabMenu = $("#ulTabMenu");
	var numberOfElement = ulTabMenu.find("#li"+dataCode).length;
	if (numberOfElement===0){
		divTabContent.append("<div id='divTabContent"+dataCode+"' class='tab-pane fade'></div>");
		divTabContent.find("#divTabContent"+dataCode).append(tabTemplate);
		tabTemplate.show();
    	ulTabMenu.append("<li><a id='li"+dataCode+"' href='#"+dataCode+"' data-div-content='divTabContent"+dataCode+"'><button class='close closeTab' type='button' data-div-content='divTabContent"+dataCode+"' id='btnDataCode"+dataCode+"'> ×</button>"+tabTitle+"</a></li>");    	
    	$("#ulTabMenu").find("#li"+dataCode).on('click',function(event){
    		globalHandleTabClick(event);
    	});        	
    	$("#ulTabMenu").find("#btnDataCode"+dataCode).on('click',function(event){
    		globalHandleCloseTabClick(event);
    	});
	}
	globalWriteConsoleLog("showTab");
	globalShowActiveTab(ulTabMenu.find("#li"+dataCode));
	globalWriteConsoleLog("out showTab");
} 

Object.defineProperty(Date.prototype, "YYYYMMDDHHMMSS", {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? "0" : "") + n;
        }

        return this.getFullYear() +
               pad2(this.getMonth() + 1) + 
               pad2(this.getDate()) +
               pad2(this.getHours()) +
               pad2(this.getMinutes()) +
               pad2(this.getSeconds());
    }
});
Object.defineProperty(Date.prototype, "YYYYMMDDHHMM", {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? "0" : "") + n;
        }

        return this.getFullYear() +
               pad2(this.getMonth() + 1) + 
               pad2(this.getDate()) +
               pad2(this.getHours()) +
               pad2(this.getMinutes());
    }
});
Object.defineProperty(Date.prototype, "DDMMYYYY_HHMM", {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? "0" : "") + n;
        }

        return pad2(this.getDate())+"/"+pad2(this.getMonth() + 1)+"/"+this.getFullYear()+" "+ 
               pad2(this.getHours())+":"+pad2(this.getMinutes());
    }
});
Object.defineProperty(Date.prototype, "DDMMYYYY_HHMMSS", {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? "0" : "") + n;
        }

        return pad2(this.getDate())+"/"+pad2(this.getMonth() + 1)+"/"+this.getFullYear()+" "+ 
               pad2(this.getHours())+":"+pad2(this.getMinutes())+":"+pad2(this.getSeconds());
    }
});
Object.defineProperty(Date.prototype, 'DD_MM_YYYY', {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? '0' : '') + n;
        }

        return pad2(this.getDate())+"/"+pad2(this.getMonth() + 1)+"/"+this.getFullYear();               
    }
});
Object.defineProperty(Date.prototype, 'YYYYMMDD', {
    value: function() {
        function pad2(n) {  // always returns a string
            return (n < 10 ? '0' : '') + n;
        }

        return this.getFullYear()+pad2(this.getMonth() + 1)+pad2(this.getDate());               
    }
});

function globalStringYYYYMMDDHHMMSSToDate(stringDate){
	//stringDate = 20190117235959
	var sYear = Number(stringDate.substr(0,4));
	var sMonth = Number(stringDate.substr(4,2));
	var sDay = Number(stringDate.substr(6,2));
	var sHH = Number(stringDate.substr(8,2));
	var sMM = Number(stringDate.substr(10,2));
	var sSS = Number(stringDate.substr(12,2));
	return new Date(sYear, sMonth-1, sDay, sHH, sMM, sSS);	
}

function globalStringYYYYMMDDToDate(stringDate){
	//stringDate = 20190117235959
	var sYear = Number(stringDate.substr(0,4));
	var sMonth = Number(stringDate.substr(4,2));
	var sDay = Number(stringDate.substr(6,2));
	var sHH = Number(stringDate.substr(8,2));
	return new Date(sYear, sMonth-1, sDay, sHH, 0, 0);	
}

//////////////////////////////////////////
/*
function globalStringYYYYMMDDTogetHours(stringDate){
	//stringDate = 20190117235959
	var sYear = Number(stringDate.substr(0,4));
	var sMonth = Number(stringDate.substr(4,2));
	var sDay = Number(stringDate.substr(6,2));
	var sHours = Number(stringDate.substr(8,2));
	
	return new Date(sYear, sMonth - 1, sDay, sHours , 0, 0);	
}
*/

$(document).ready(function () {
	$(".subMenuLink").click(function(evt) {
        evt.preventDefault();
        window.open($(this).data("url"),"_self");
        
	});
	if ($("#ulTabMenu").length>0){
		//default allRole tab click
		$("#ulTabMenu").find("#liAllData").on('click',function(event){
			globalHandleTabClick(event);
		});
	}		
	//menuLink, treeview
//	$(".menuLink").removeClass("active");
//	$(".subMenuLink").removeClass("active");
//	if (globalActiveMenu!==null){
//		$("#"+globalActiveMenu).addClass("active");
//		$("#"+globalActiveSubMenu).addClass("active");
//		$("#"+globalActiveSubMenu).parent().show();
//	}
});
		
//////////////////////
//show loader 
function globalAddLoader(){
	globalImageLoader.show();  
}
/////////////////////
//clear loader
function globalClearLoader(){
	globalImageLoader.fadeOut("slow");
}
/////////////////////////
//convert string date dd/mm/yyyy to yyyymmdd
function globalConvertDDMMYY_To_YYYYMMDD(stringDate){
	if (stringDate.length===10){
		return stringDate.substr(6,4)+stringDate.substr(3,2)+stringDate.substr(0,2);
	}else{
		return stringDate;
	}
}

//check data in csv data
function globalCheckArrayContainValue(dataList, checkValue){
	var returnValue = false;
	if (dataList === "" || dataList === null || (typeof dataList === "undefined")){
		returnValue = false;
	}else{
		var dataArrayList = dataList.split(",");
		dataArrayList.forEach(function(element) {
			if (element === checkValue){
				returnValue = true;
			}
		});
	}		
	return returnValue;
}
//array to string with csv
function globalArrayToString(dataArrayList){
	var returnValue = "";
	if (dataArrayList === "" || dataArrayList === null || (typeof dataArrayList === "undefined")){
		returnValue = "";
	}else{		
		dataArrayList.forEach(function(element) {			
			returnValue += element+",";			
		});
		if (returnValue.length > 0){
			returnValue = returnValue.substr(0, returnValue.length-1);
		}
	}		
	return returnValue;
}

//to check is session expire
function globalCheckSessionExpire(callbackFunction){
	globalWriteConsoleLog("in globalCheckSessionExpire");
	$.ajax({
        url: "/xFace/rest/cfg/isSessionExpire",
        type: "GET",        
        success: function(response) {
        	//do nothing
        	globalIsSessionExpire(response);
        	globalWriteConsoleLog("session not expire callbackFunction()");
        	callbackFunction();
        },
        error: function(error){
        	globalWriteConsoleLog("session expire, redirect to related page");
        	if (error.statusCode().status===401 || error.statusCode().status===0 || 
        			globalIsSessionExpire(error.statusCode().responseText)){
        		window.open("/xFace/error/invalidsession","_self");
        	}
        }
    });
}

//get session id of websocket
function globalGetWebSocketSessionId(url){
	var sessionId = url;
	var tmp1 = sessionId.lastIndexOf("/");
	sessionId = sessionId.substring(0, tmp1);
	tmp1 = sessionId.lastIndexOf("/");
	sessionId = sessionId.substring(tmp1+1);
	return sessionId;
}

//convert google chart object to array and display chart to screen
function globalConvertGoogleChartObjectToArray(resultList){
	var arrayResultList = [];
	var cntRow = -1;
	var cntCol = -1;    	
	$.each(resultList.chartResultList, function(i, itemFirst){
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
	returnResult["footer"] = resultList.footer;
	returnResult["hAxisTitle"] = resultList.hAxisTitle;
	returnResult["vAxisTitle"] = resultList.vAxisTitle;
	returnResult["resultList"] = arrayResultList;
	return returnResult;
}