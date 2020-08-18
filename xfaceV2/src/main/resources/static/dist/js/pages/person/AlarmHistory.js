$(document).ready( function () {
	var dtpOptionFrom = $("#dtpOptionFrom"), dtpOptionTo = $("#dtpOptionTo");
	var timerReloadData = null;
	var shouldSetTimer = true;
	var refreshInterval = 5000;
	var maximumEndDate = null;
	var maximumStartDate = null;
	var panelShowHistory = $("#panelShowHistory");	
	var historyOptions = {};
	var formGotoPage = $("#formGotoPage");
	var formAlarmInfo = $("#formAlarmInfo");	
	globalImageLoader =  $("#imageLoader");		
	//////////////////
	dtpOptionFrom.datetimepicker({
		format: "dd/mm/yyyy hh:ii",
		todayBtn:  true,
		autoclose: true
	//}).data("datetimepicker").setDate(globalStringYYYYMMDDToDate(new Date().YYYYMMDD()));
	}).data("datetimepicker").setDate(globalStringYYYYMMDDToDate((paramStartDate===null?new Date().YYYYMMDD():paramStartDate)));
	
	dtpOptionTo.datetimepicker({
		format: "dd/mm/yyyy hh:ii",
		todayBtn:  true,
		autoclose: true
	//}).data("datetimepicker").setDate(new Date());
	}).data("datetimepicker").setDate(globalStringYYYYMMDDHHMMSSToDate((paramEndDate===null?new Date().YYYYMMDDHHMMSS():paramEndDate)));
	
	///split
	$(".spinner .btn:first-of-type").on("click", function() {
		var btn = $(this);
	    var input = btn.closest(".spinner").find("input");
	    if (input.attr("max") == undefined || parseInt(input.val()) < parseInt(input.attr("max"))) {    
	    	input.val(parseInt(input.val(), 10) + 1);
	    } else {
	    	btn.next("disabled", true);
	    }
	});
	$(".spinner .btn:last-of-type").on("click", function() {
		var btn = $(this);
	    var input = btn.closest(".spinner").find("input");
	    if (input.attr("min") == undefined || parseInt(input.val()) > parseInt(input.attr("min"))) {    
	    	input.val(parseInt(input.val(), 10) - 1);
	    } else {
	    	btn.prev("disabled", true);
	    }
	});	
	if (paramStartDate===null){		
		showHistoryOptions();
		//set default timeportion
		$("#cmbOptionTimePortion").val("60");
		$("#cmbOptionTimePortion").selectpicker("refresh");
		reloadGateInfo(null);
		reloadBoat(null);
	}else{
		//send parameter from landing page
		$("#cmbOptionDurationType").val("1");
		$("#cmbOptionDurationType").selectpicker("refresh");
		//fixed time portion to 24hour
		$("#cmbOptionTimePortion").val("1440");
		$("#cmbOptionTimePortion").selectpicker("refresh");
		if (paramMatchCondition!==null){
			$("#cmbOptionMatch").val(paramMatchCondition);
			$("#cmbOptionMatch").selectpicker("refresh");
		}		
		if (paramDirection!==null){
			$("#cmbOptionDirection").val(paramDirection);
			$("#cmbOptionDirection").selectpicker("refresh");
		}
		//reload gate then gate reload boat and boat reload page data	
		reloadGateInfo(paramGateInfoCodeList);	
	}	
	
	//clear all param which send by another page after first reload if call from another page
	function clearPageParameter(){
		paramGateInfoCodeList = null;		
		paramBoatCodeList = null;
    	paramMatchCondition = null;
    	paramStartDate = null;
    	paramEndDate = null;
    	paramDirection = null;
	}
	
	//reload data from backend
	function reloadPageData(){
		//reload history result page
		globalAddLoader();
		showHistoryOptions();
		$.ajax({
	        url: "/xFace/rest/rep/getAlarmPortionList",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 
	        data: JSON.stringify(createReloadParameter()),
	        success: function(queryAlarmPortionResultList) {
	        	globalIsSessionExpire(queryAlarmPortionResultList);
	        	globalWriteConsoleLog(queryAlarmPortionResultList);
	        	showFaceToScreen(queryAlarmPortionResultList);
	        	if (paramStartDate!==null){
	        		clearPageParameter();
	        	}	        	
	        	globalClearLoader();
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	function reloadPartData(startDate, endDate, currentPage){
		//reload some part of page
		//eddy
		//globalAddLoader();
		var panelResult = $("#panelResult_"+endDate);
		if (panelResult.length > 0){
			panelResult.addClass("txtLoader");
		}		
		$.ajax({
	        url: "/xFace/rest/rep/getAlarmPortionPartList",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 
	        data: JSON.stringify(createReloadPartParameter(startDate, endDate, currentPage)),
	        success: function(queryAlarmResultList) {
	        	globalIsSessionExpire(queryAlarmResultList);
	        	globalWriteConsoleLog(queryAlarmResultList);
	        	showFaceToPartScreen(queryAlarmResultList, false);	        		        	
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	//globalClearLoader();
	        	if (panelResult.length > 0){
	        		panelResult.removeClass("txtLoader");
	        	}
	        }
	    });
	}
	function reloadAlarmInfo(alarmId){
		//show alarm data		
		globalAddLoader();
		$.ajax({
	        url: "/xFace/rest/rep/getAlarmById",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 
	        data: JSON.stringify(createReloadAlarmInfoParameter(alarmId)),
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
	
	function reloadGateInfo(gateInfoSelectedCodeList){
		//show alarm data				
		$.ajax({
	        url: "/xFace/rest/cfg/getAllHWGate",
	        type: "GET",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 	        
	        success: function(gateInfoList) {
	        	globalIsSessionExpire(gateInfoList);
	        	globalWriteConsoleLog(gateInfoList);
	        	showGateInfoList(gateInfoList, gateInfoSelectedCodeList);	        	
	        	if (paramStartDate!==null){
	        		reloadBoat(paramBoatCodeList);
	        	}
	        },
	        error: function(error){	        	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	function reloadBoat(boatSelectedCodeList){
		//show alarm data				
		$.ajax({
	        url: "/xFace/rest/cfg/getAllBoat",
	        type: "GET",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 	        
	        success: function(boatList) {
	        	globalIsSessionExpire(boatList);
	        	globalWriteConsoleLog(boatList);
	        	showBoatList(boatList, boatSelectedCodeList);	    		
	        	if (paramStartDate!==null){
	        		reloadPageData();
	        	}
	        },
	        error: function(error){	        	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
	        	globalClearLoader();
	        }
	    });
	}
	
	///////////////////////
	//call by timer
	function decisionReloadData(){		
		var now = new Date();
		var allowReloadData = false;
		//if start or end date is = today or duration type is 0
		if (historyOptions["durationTypeValue"] === "0"){
			allowReloadData = true;
		}else{
 			var nowYYYYMMDD = now.YYYYMMDD();
 			if (nowYYYYMMDD >= historyOptions["dtpFromValue"] && nowYYYYMMDD <= historyOptions["dtpToValue"]){
 				allowReloadData = true;
 			} 				
		}
		if (allowReloadData){
			var tmpStartDate = maximumStartDate;
			var tmpEndDate = maximumEndDate;
			var tmpStartDate2 = null;
			var tmpEndDate2 = null;
			var tmpDate = null;
			var timePortionMillisec = Number(historyOptions["timePortionValue"])*60*1000;
			//calculate new max star and end in case of duration type = from/to
			if (historyOptions["durationTypeValue"] === "1"){											
				while (now.YYYYMMDDHHMMSS() < tmpEndDate){
					tmpDate = globalStringYYYYMMDDHHMMSSToDate(tmpEndDate);
					tmpDate = new Date(tmpDate.getTime()-timePortionMillisec);
					tmpEndDate = tmpDate.YYYYMMDDHHMMSS();
				}
				//complete this loop we will get time portion of now
				//then start latest step = end date 
				tmpStartDate2 = tmpEndDate;
				//and end date of latest - 1 step equal to maximumStartDate 
				tmpEndDate = tmpStartDate2
				//and start date of latest - 1 step equal to maximumStartDate(tmpDate) - time portion
				tmpStartDate = new Date(tmpDate.getTime()-timePortionMillisec).YYYYMMDDHHMMSS();
				//and end date of latest step equal to maximumStartDate(tmpDate) + time portion
				tmpEndDate2 = new Date(tmpDate.getTime()+timePortionMillisec).YYYYMMDDHHMMSS();				
				//reload latest -1 step
				reloadPartData(tmpStartDate, tmpEndDate, 1)
				//reload latest step
				reloadPartData(tmpStartDate2, tmpEndDate2, 1)
			}else{			
				globalWriteConsoleLog("==[now minus hour] auto reload latest step data start:"+tmpStartDate+" end:"+tmpEndDate);
				reloadPartData(tmpStartDate, tmpEndDate, 1)
				if (maximumEndDate < now.YYYYMMDDHHMMSS()){				
					maximumStartDate = maximumEndDate;				
					tmpDate = globalStringYYYYMMDDHHMMSSToDate(maximumEndDate);
					tmpDate = new Date(tmpDate.getTime()+timePortionMillisec);
					maximumEndDate = tmpDate.YYYYMMDDHHMMSS();
					globalWriteConsoleLog("==auto reload new step data "+maximumStartDate+", "+maximumEndDate);
					reloadPartData(maximumStartDate, maximumEndDate, 1)				
				}
			}						
		}else{
			globalWriteConsoleLog("==no need to reload data");
		}
	}
	function createReloadParameter(){
		var param = {};
		param["durationType"] = historyOptions["durationTypeValue"];		
		param["timeMinusMinute"] = historyOptions["nowMinusHourValue"];		
		param["timePortion"] = historyOptions["timePortionValue"];
		param["certificateNo"] = historyOptions["certificateNo"];
		param["fullName"] = historyOptions["fullName"];
		param["gateInfoCodeList"] = historyOptions["gateInfoCodeList"];		
		param["boatCodeList"] = historyOptions["boatCodeList"];
		param["matchCondition"] = historyOptions["matchCondition"];
		param["direction"] = historyOptions["direction"];
		param["currentPage"] = 1;
		if (historyOptions["durationTypeValue"]==="1"){
			//start end parameter
			param["startDate"] = historyOptions["dtpFromValue"]; 
			param["endDate"] = historyOptions["dtpToValue"];
		}			
		return param;
	}
	function createReloadPartParameter(startDate, endDate, currentPage){
		//eddy
		var param = {};
		param["startDate"] = startDate;
		param["endDate"] = endDate;
		param["currentPage"] = currentPage;
		param["certificateNo"] = historyOptions["certificateNo"];
		param["fullName"] = historyOptions["fullName"];
		param["gateInfoCodeList"] = historyOptions["gateInfoCodeList"];		
		param["matchCondition"] = historyOptions["matchCondition"];
		param["direction"] = historyOptions["direction"];
		param["boatCodeList"] = historyOptions["boatCodeList"];
		return param;
	}	
	function createReloadAlarmInfoParameter(alarmId){
		var param = {};
		param["alarmId"] = alarmId;
		return param;
	}
	function showHistoryOptions(){
		setupHistoryOptions();
		var optionText = "Duration Type:"+historyOptions["durationTypeDesc"];
		if (historyOptions["durationTypeValue"]==="0"){
			//now minus hour
			optionText += ", Minus:"+historyOptions["nowMinusHourDesc"];
		}else{
			//start end
			optionText += ", Start:"+historyOptions["dtpFromDesc"];
			optionText += ", End:"+historyOptions["dtpToDesc"];
		}
		optionText += ", Time Portion:"+historyOptions["timePortionDesc"];		
		optionText += ", Refresh:"+historyOptions["refreshIntervalDesc"];
		$("#linkPageOption").html("History Option <i>("+optionText+")</i>");
		if (shouldSetTimer && historyOptions["refreshIntervalValue"]!=="0"){			
			shouldSetTimer = false;					
			timerReloadData = setInterval(decisionReloadData, Number(historyOptions["refreshIntervalValue"])); //default 5 minute			
		}		
	}	
	
	//////////////////////////
	//show face data to screen
	function showFaceToScreen(queryAlarmPortionResultList){
		var facePaging = null;
		var faceImage = null;
		if (queryAlarmPortionResultList.result.statusCode==="0"){
			//success
			panelShowHistory.empty();
			maximumStartDate = queryAlarmPortionResultList.maximumStartDate;
			maximumEndDate = queryAlarmPortionResultList.maximumEndDate;			
//			for (var alarmResultList of queryAlarmPortionResultList.queryAlarmResultList) {
			$.each(queryAlarmPortionResultList.queryAlarmResultList, function(i, alarmResultList){
				showFaceToPartScreen(alarmResultList, true);
			});
		}
	}
	//////////////////////////
	//show face data to part screen
	function showFaceToPartScreen(queryAlarmResultList, isfullReload){
		var panelResult = null;		
		var faceArea = null;
		var btnTimePortion = null;		
		var btnNoOfPage = null
		if (queryAlarmResultList.result.statusCode==="0"){					
			panelResult = panelShowHistory.find("#panelResult_"+queryAlarmResultList.endDate);			
			if (panelResult.length===0){
				if (queryAlarmResultList.queryAlarmResultList.length>0){
					panelResult = $(".templateHistoryResult").clone();
					panelResult.removeClass("templateHistoryResult");
					panelResult.attr("id", "panelResult_"+queryAlarmResultList.endDate);
					btnTimePortion = panelResult.find("#panelTimePortion").find("#btnTimePortion");
					btnTimePortion.text(queryAlarmResultList.startDateDisplay+"-"+queryAlarmResultList.endDateDisplay);
					btnTimePortion.data("startDate", queryAlarmResultList.startDate);
					btnTimePortion.data("endDate", queryAlarmResultList.endDate);					
					//set value
					btnNoOfPage = panelResult.find("#panelPageNavigate").find("#btnNoOfPage");
					btnNoOfPage.text(queryAlarmResultList.currentPage+"/"+queryAlarmResultList.maximumPage);
					btnNoOfPage.data("currentPage", queryAlarmResultList.currentPage);
					btnNoOfPage.data("maximumPage", queryAlarmResultList.maximumPage);
					btnNoOfPage.data("startDate", queryAlarmResultList.startDate);
					btnNoOfPage.data("endDate", queryAlarmResultList.endDate);
					//set event on click to link
					btnTimePortion.on("click", function(event) {
						var timePortion = $(this);
						var startDate = timePortion.data("startDate");
						var endDate = timePortion.data("endDate");
						reloadPartData(startDate, endDate, 1);
					});
					panelResult.find("#panelPageNavigate").find("#btnPrevPage").on("click", function(event) {
						var btnNoOfPage = $(this).parent().parent().find("#btnNoOfPage");
						var currentPage = Number(btnNoOfPage.data("currentPage"))-1;								
						var startDate = btnNoOfPage.data("startDate");
						var endDate = btnNoOfPage.data("endDate");						
						reloadPartData(startDate, endDate, currentPage);
					});
					panelResult.find("#panelPageNavigate").find("#btnNextPage").on("click", function(event) {
						var btnNoOfPage = $(this).parent().parent().find("#btnNoOfPage");
						var currentPage = Number(btnNoOfPage.data("currentPage"))+1;								
						var startDate = btnNoOfPage.data("startDate");
						var endDate = btnNoOfPage.data("endDate");						
						reloadPartData(startDate, endDate, currentPage);
					});
					btnNoOfPage.on("click", function(event) {						
						showGotoPageDialog(event, this);
					});
					//end of set event on click to link
					//show control
					if (queryAlarmResultList.maximumPage === 0){
						panelResult.find("#panelPageNavigate").hide();
					}else{ 
						if (queryAlarmResultList.currentPage === 1){
							panelResult.find("#btnPrevPage").parent().hide();
							//col-lg-offset-7 
							panelResult.find("#btnNoOfPage").parent().addClass("col-lg-offset-7");
							panelResult.find("#btnNoOfPage").parent().addClass("col-md-offset-7");
						}else{
							panelResult.find("#btnNoOfPage").parent().removeClass("col-lg-offset-7");
							panelResult.find("#btnNoOfPage").parent().removeClass("col-md-offset-7");
							panelResult.find("#btnPrevPage").parent().addClass("col-lg-offset-7")
							panelResult.find("#btnPrevPage").parent().addClass("col-md-offset-7")
						}
						if (queryAlarmResultList.currentPage === queryAlarmResultList.maximumPage){
							panelResult.find("#btnNextPage").parent().hide();							
						}
						if (queryAlarmResultList.maximumPage === 1){
							//eddy use id = noOfPage only
							//panelResult.find("#noOfPage_"+queryAlarmResultList.endDate).hide();													
							panelResult.find("#btnNoOfPage").parent().hide();
						}																		
					}					
					faceArea = panelResult.find("#faceArea").find("div > div > ul");
					$.each(queryAlarmResultList.queryAlarmResultList, function(i, alarmResultList){
						faceArea.append(generateFaceImageHtml(alarmResultList));
						faceArea.find("#linkFaceImage"+alarmResultList.alarmId).on("click", function(event){							
							faceImageClick(event, this);
						});
					});				
					panelResult.show();
					if (isfullReload){
						panelShowHistory.append(panelResult);											
					}else{
						panelShowHistory.prepend(panelResult);						
						var panelResultList = panelShowHistory.find(".historyResult"); 						
						if (panelResultList.length > noOfTimePortion){
							panelShowHistory.find(".historyResult").last().remove();
						}
					}														
				}				
			}else{
				//existing panel result
				btnNoOfPage = panelResult.find("#btnNoOfPage");
				btnNoOfPage.text(queryAlarmResultList.currentPage+"/"+queryAlarmResultList.maximumPage);
				btnNoOfPage.data("currentPage", queryAlarmResultList.currentPage);				
				if (queryAlarmResultList.maximumPage === 0){
					panelResult.find("#panelPageNavigate").hide();
				}else{
					panelResult.find("#panelPageNavigate").show();
					if (queryAlarmResultList.currentPage === 1){
						panelResult.find("#panelPageNavigate").find("#btnPrevPage").parent().hide();
						panelResult.find("#panelPageNavigate").find("#btnPrevPage").parent().removeClass("col-lg-offset-7");
						panelResult.find("#panelPageNavigate").find("#btnPrevPage").parent().removeClass("col-md-offset-7");
						panelResult.find("#panelPageNavigate").find("#btnNoOfPage").parent().addClass("col-lg-offset-7");
						panelResult.find("#panelPageNavigate").find("#btnNoOfPage").parent().addClass("col-md-offset-7");
					}else{
						panelResult.find("#panelPageNavigate").find("#btnPrevPage").parent().show();
						panelResult.find("#panelPageNavigate").find("#btnPrevPage").parent().addClass("col-lg-offset-7");
						panelResult.find("#panelPageNavigate").find("#btnPrevPage").parent().addClass("col-md-offset-7");
						panelResult.find("#panelPageNavigate").find("#btnNoOfPage").parent().removeClass("col-lg-offset-7");
						panelResult.find("#panelPageNavigate").find("#btnNoOfPage").parent().removeClass("col-md-offset-7");
						
					}
					if (queryAlarmResultList.currentPage === queryAlarmResultList.maximumPage){						
						panelResult.find("#panelPageNavigate").find("#btnNextPage").parent().hide();
					}else{
						panelResult.find("#panelPageNavigate").find("#btnNextPage").parent().show();
					}
					if (queryAlarmResultList.maximumPage === 1){
						btnNoOfPage.hide();						
					}else{
						btnNoOfPage.show();						
					}					
				}				
				faceArea = panelResult.find("#faceArea").find("div > div > ul");
				faceArea.empty();
				$.each(queryAlarmResultList.queryAlarmResultList, function(i, alarmResultList){
					faceArea.append(generateFaceImageHtml(alarmResultList));
					faceArea.find("#linkFaceImage"+alarmResultList.alarmId).on("click", function(event){						
						faceImageClick(event, this);
					});
				});	
				panelResult.removeClass("txtLoader");
			}															
		}		
	}
	
	function generateFaceImageHtml(alarmResultList){
		var alarmDate = globalStringYYYYMMDDHHMMSSToDate(alarmResultList.alarmDate).DDMMYYYY_HHMMSS();
		var uiClassBtn = null;
		var uiAside = null;		
		if (alarmResultList.personId===-1){
			//not match
			uiClassBtn = "ui-btn ui-li-unmatch";
			uiAside = '<p class="ui-li-aside ui-li-aside-unmatch">'+alarmResultList.category+'</p></a></li>';			
		}else{
			//match
			uiClassBtn = "ui-btn ui-btn-icon-right ui-icon-carat-r";
			uiAside = '<p class="ui-li-aside ui-li-aside-match">'+alarmResultList.category+'</p></a></li>';			
		}
		return '<li id="linkFaceImage'+alarmResultList.alarmId+'" class="ui-li-has-thumb"><a href="#" class="'+uiClassBtn+'">' +
				'<img src="'+alarmResultList.livePhoto+'" class="ui-li-thumb">' +				
				'<h2 class="personName"><u>Name:</u>'+alarmResultList.fullName+'</h2>' +
				'<p class="alarmDate"><u>Date:</u>'+alarmDate+'</p>' +
				'<p class="gateInfoName"><u>Gate:</u>'+alarmResultList.gateInfoName+'</p>' +
				'<p class="ipcName"><u>CAM:</u>'+alarmResultList.ipcName+'</p>'+
				'<p class="boat"><u>Boat:</u>'+alarmResultList.boatShortName+'</p>' + uiAside;
				
	}
	function setupHistoryOptions(){
		historyOptions["durationTypeValue"] = $("#cmbOptionDurationType").val();
		historyOptions["durationTypeDesc"] = $("#cmbOptionDurationType :selected").text();
		historyOptions["nowMinusHourValue"] = $("#cmbOptionNowMinusHour").val();
		historyOptions["nowMinusHourDesc"] = $("#cmbOptionNowMinusHour :selected").text();
		var myDate = dtpOptionFrom.data("datetimepicker").getDate();
		historyOptions["dtpFromValue"] = myDate.YYYYMMDDHHMM();		
		historyOptions["dtpFromDesc"] = myDate.DDMMYYYY_HHMM();
		myDate = dtpOptionTo.data("datetimepicker").getDate();
		historyOptions["dtpToValue"] = myDate.YYYYMMDDHHMM();
		historyOptions["dtpToDesc"] = myDate.DDMMYYYY_HHMM();
		historyOptions["timePortionValue"] = $("#cmbOptionTimePortion").val();
		historyOptions["timePortionDesc"] = $("#cmbOptionTimePortion :selected").text();
		historyOptions["refreshIntervalValue"] = $("#cmbOptionRefreshInterval").val();
		historyOptions["refreshIntervalDesc"] = $("#cmbOptionRefreshInterval :selected").text();
		historyOptions["certificateNo"] = $("#txtOptionCertificateNo").val();
		historyOptions["fullName"] = $("#txtOptionFullName").val();		
		historyOptions["matchCondition"] = $("#cmbOptionMatch").val();
		historyOptions["direction"] = $("#cmbOptionDirection").val();		
		//get gate info list
		var gateInfoList = []
		var paramGateInfoList = "";
		if (paramGateInfoCodeList===null){			
			gateInfoList = $("#cmbOptionGateInfo").val();
		}else{
			gateInfoList = paramGateInfoCodeList.split(",");
		}		
	    $.each(gateInfoList, function(i, item){			
	    	paramGateInfoList = paramGateInfoList+ item + ",";
		});
		if (paramGateInfoList.length > 0){
			paramGateInfoList = paramGateInfoList.substr(0, paramGateInfoList.length-1);
		}
		historyOptions["gateInfoCodeList"] = paramGateInfoList;				
		//get boat list
		var boatList = []
		var paramBoatList = "";
		boatList = $("#cmbOptionBoat").val()
	    $.each(boatList, function(i, item){			
	    	paramBoatList = paramBoatList+ item + ",";
		});
		if (paramBoatList.length > 0){
			paramBoatList = paramBoatList.substr(0, paramBoatList.length-1);
		}
		historyOptions["boatCodeList"] = paramBoatList;
	}
	
	function showGotoPageDialog(event, btnNoOfPage){		
		event.preventDefault();
		var noOfPage = $(btnNoOfPage);
		var txtGoToPageNumber = $("#txtGoToPageNumber");
		//eddy
		txtGoToPageNumber.data("panelResultId", "panelResult_"+noOfPage.data("endDate"));
		txtGoToPageNumber.val(noOfPage.data("currentPage"));
		txtGoToPageNumber.attr("max", noOfPage.data("maximumPage"));
		$("#lblOptionShowMaximumPage").text("Maximum page:"+noOfPage.data("maximumPage"));
		globalRemoveError();
		formGotoPage.modal("show");
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
		formAlarmInfo.find("#txtGateInfo").val(queryAlarmResult.gateInfoName);
		formAlarmInfo.find("#txtIPC").val(queryAlarmResult.ipcName);
		formAlarmInfo.find("#txtCategory").val(queryAlarmResult.category);
		formAlarmInfo.find("#txtBoat").val(queryAlarmResult.boatShortName);
		globalClearLoader();
		formAlarmInfo.modal("show");
	}
		
	//bind data to cmb gateinfo 
	function showGateInfoList(gateInfoList, gateInfoSelectedCodeList){
		var cmbOptionGateInfo = $("#cmbOptionGateInfo");		
		var options = [];
		$.each(gateInfoList, function(i, item){			
			options.push('<option value="'+item.gateCode+'">'+item.gateName+'</option>');			
		});
		//show dialog
		cmbOptionGateInfo.html(options);
		if (gateInfoSelectedCodeList!==null){
			cmbOptionGateInfo.val(gateInfoSelectedCodeList.split(","));
		}
		cmbOptionGateInfo.selectpicker("refresh");
	}
	//bind data to cmb boat 
	function showBoatList(boatList, boatSelectedCodeList){
		var cmbOptionBoat = $("#cmbOptionBoat");		
		var options = [];
		$.each(boatList, function(i, item){			
			options.push('<option value="'+item.boatCode+'">'+item.boatShortName+'</option>');			
		});
		//show dialog
		cmbOptionBoat.html(options);
		if (boatSelectedCodeList!==null){
			cmbOptionBoat.val(boatSelectedCodeList.split(","));
		}
		cmbOptionBoat.selectpicker("refresh");
	}
	
	
	//event of object	
	function faceImageClick(event, clickLI){
		event.preventDefault();
		if ($(clickLI).find(".ui-li-unmatch").length === 0){
			//match
			reloadAlarmInfo($(clickLI).attr("id").replace("linkFaceImage",""));
		}		
		//unmatch no show detail screen
	}

	$("#btnOptionRefresh").on("click", function(event){
		//update title		
		shouldSetTimer = true;		
		//clear timer
		clearInterval(timerReloadData);
		reloadPageData();
	});
	$("#historyOption").on("hidden.bs.collapse", function () {
		//restore option
		var gateInfoList = [];
		$("#cmbOptionDurationType").val(historyOptions["durationTypeValue"]);		
		$("#cmbOptionNowMinusHour").val(historyOptions["nowMinusHourValue"]);

		var myDate = globalStringYYYYMMDDHHMMSSToDate(historyOptions["dtpFromValue"]+"00");
		dtpOptionFrom.datetimepicker().data("datetimepicker").setDate(myDate);
		myDate = globalStringYYYYMMDDHHMMSSToDate(historyOptions["dtpToValue"]+"00");
		dtpOptionTo.datetimepicker().data("datetimepicker").setDate(myDate);
		
		$("#cmbOptionTimePortion").val(historyOptions["timePortionValue"]);		
		$("#cmbOptionRefreshInterval").val(historyOptions["refreshIntervalValue"]);						
		$("#txtOptionCertificateNo").val(historyOptions["certificateNo"]);
		$("#txtOptionFullName").val(historyOptions["fullName"]);		
		if (historyOptions["gateInfoCodeList"]===""){
			$("#cmbOptionGateInfo").selectpicker("val", "");						
		}else{			
			gateInfoList = historyOptions["gateInfoCodeList"].split(",");
			$("#cmbOptionGateInfo").selectpicker("val", gateInfoList);			
		}		
		$("#cmbOptionBoat").selectpicker("val", historyOptions["boatCodeList"].split(","));
		$(".selectpicker").selectpicker("refresh");
		if (historyOptions["gateInfoCodeList"]!==""){
			gateInfoList = historyOptions["gateInfoCodeList"].split(",");			
		}
	});	
	$("#btnGoToPage").on("click", function(event){
		event.preventDefault();
		var txtGoToPageNumber = $("#txtGoToPageNumber");
		var pageNumber = Number(txtGoToPageNumber.val());
		if (pageNumber<1 || pageNumber>Number(txtGoToPageNumber.attr("max")) || isNaN(pageNumber)){
			globalDisplayError("Invalid page number.");
		}else{
			var btnNoOfPage = $("#"+txtGoToPageNumber.data("panelResultId")).find("#panelPageNavigate").find("#btnNoOfPage");
			reloadPartData(btnNoOfPage.data("startDate"), btnNoOfPage.data("endDate"), txtGoToPageNumber.val());
			formGotoPage.modal("hide");
		}				
	});
	$("#btnAlarmInfoClose").on("click", function(event){
		formAlarmInfo.modal("hide");
	});		
});