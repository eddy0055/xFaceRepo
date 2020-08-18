$(document).ready( function () {
	var dataTable = $("#data-table");	
	var rowClick = null;
	var formModal = $("#formModal");
	var tvPermission = $("#treeview-permission");
	var actionCommand = "ADD"; //ADD, EDIT
	var confirmDeleteModal = null;
	var defaultPageCode = null;
	globalImageLoader =  $("#imageLoader");
	dataTable.DataTable( {
		"processing": true,
        "serverSide": true,
        "searching": false,
        "aLengthMenu": globalTableStep,
        "ajax": {
        	//"url": "/xFace/rest/cfg/getBoatInfoList",
        	"url": "/xFace/rest/cfg/getBoatInfo",
            "dataType": "json",
            "contentType" : "application/json; charset=utf-8",            
            "type": "POST",
            "paging": true,                       
            "data": function (data) {
                return JSON.stringify(data); // NOTE: you also need to stringify POST payload
            },
            "error": function (error) {            	
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);
            }, 
            "complete": function(data, status) {
            	globalClearLoader();
            }           
        },
        "columns": [
        	   { "data": "boatCode", "name":"boatCode"},
        	   { "data": "boatName", "name":"boatName"},
               { "data": "cardNo", "name":"cardNo" }
        ]
    });		
	//row click
	dataTable.find("tbody").on( "click", "tr", function () {
		if (rowClick===null||rowClick!=this){
			rowClick = this;
		}else {
			return;
		}
        if ($(this).hasClass("selected") ) {
            $(this).removeClass("selected");
        }else {
        	dataTable.find("tr.selected").removeClass("selected");
            $(this).addClass("selected");
        }
    });
	//row dblclick
	dataTable.find("tbody").on( "dblclick", "tr", function () {
		var data = dataTable.DataTable().row(this).data();
		showDialog(data);
		
    });		

	//validate user input
    function validateInputData(){
    	var result = true;
    	if ($("#txtBoatCode").val()===""){
    		globalDisplayError("Please enter Boat Code");
    		result = false;
    	}else if ($("#txtBoatShortName").val()===""){
			globalDisplayError("Please enter Boat Short Name");
			result = false;	
    	}else if ($("#txtBoatName").val()===""){
			globalDisplayError("Please enter Boat Name");
			result = false;
    	}else if($("#txtCardNo").val()===""){
    		globalDisplayError("Please enter Card No");
			result = false;
		}else if($("#txtZkPin").val()===""){
			globalDisplayError("Please enter Zk Pin");
			result = false;
		}
		return result;
    }

    //show dialog add/edit
	function showDialog(data) { 
		globalWriteConsoleLog("in showDialog");
		globalRemoveError();
		globalAddLoader(); 
    	if (data === null) {
    		// Add
    		globalClearLoader();
    		actionCommand = "ADD";
    		$("#txtBoatCode").val("").attr("disabled", false);
    		$("#txtBoatName").val("");
    		$("#txtBoatShortName").val("");
    		$("#txtCardNo").val("");
    		$("#txtZkPin").val("");
    		formModal.modal("show");
    	}else{    		
    		// Edit
    		globalClearLoader();
    		actionCommand = "EDIT";
    		$("#txtBoatCode").val(data.boatCode).attr("disabled", true);
    		$("#txtBoatName").val(data.boatName);
    		$("#txtBoatShortName").val(data.boatShortName);
    		$("#txtCardNo").val(data.cardNo);
    		$("#txtZkPin").val(data.zkPin);
    		formModal.modal("show");
    		globalWriteConsoleLog("data.boatName :" + data.boatName);
    		globalWriteConsoleLog("data.boatCode :" + data.boatCode);
    		globalWriteConsoleLog("data.boatShortName :" + data.boatShortName);
    		globalWriteConsoleLog("data.cardNo :" + data.cardNo);
    		globalWriteConsoleLog("data.zkPin :" + data.zkPin);
    	}    	    	    	    	   
    	globalWriteConsoleLog("out showDialog");
    }
	
	//updateData
	function updateData(){
		globalWriteConsoleLog("in updateData");
		var validateInput = validateInputData();
		if (!validateInput){
			return;
		}
		var boatInfo = createBoatInfo();
		globalWriteConsoleLog("Check Create BoatInfo boatCode :" + boatInfo.boatCode);
		globalWriteConsoleLog("Check Create BoatInfo boatShortName :" + boatInfo.boatShortName);
		globalWriteConsoleLog("Check Create BoatInfo cardNo :" + boatInfo.cardNo);
		globalWriteConsoleLog("Check Create BoatInfo actionCommand :" + boatInfo.actionCommand);
		globalWriteConsoleLog("Check Create BoatInfo zkPin :" + boatInfo.zkPin);
		if(boatInfo === null){
			globalDisplayError("create boatInfo not sent data to submitUpdateRequest >> null  ");
		}else{
			//call Submit updateRequest
			submitUpdateRequest(boatInfo);
		}
		globalWriteConsoleLog("out updateData");
	}
	
	//from screen create boatInfo 
	function createBoatInfo(){
		var boatInfo = {};
		boatInfo["boatCode"] = $("#txtBoatCode").val();
		boatInfo["boatName"] = $("#txtBoatName").val();
		boatInfo["cardNo"] = $("#txtCardNo").val();
		boatInfo["zkPin"] = $("#txtZkPin").val();
		boatInfo["boatShortName"] = $("#txtBoatShortName").val();
		boatInfo["actionCommand"] = actionCommand;
		return boatInfo;	
	}
	
	//submit request to web service
	function submitUpdateRequest(boatInfoList){				
		globalWriteConsoleLog("in submitUpdateRequest(boatInfo)");
		globalWriteConsoleLog("boatInfoList.boatName =" + boatInfoList.boatName);
		globalWriteConsoleLog("boatInfoList.boatCode =" + boatInfoList.boatCode);
		globalWriteConsoleLog("boatInfoList.boatShortName =" + boatInfoList.boatShortName);
		globalWriteConsoleLog("boatInfoList cardNo =" + boatInfoList.cardNo);
		globalWriteConsoleLog("boatInfoList zkPin =" + boatInfoList.zkPin);
		globalWriteConsoleLog("boatInfoList actionCommand =" + boatInfoList.actionCommand);
		$.ajax({
			url:"/xFace/rest/cfg/updateBoatInfo",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(boatInfoList),
            success: function(result) {
            	globalIsSessionExpire(result);
            	if (result.statusCode==="0"){
            		dataTable.DataTable().ajax.reload(null, false);
            		globalShowGrowlNotification($("#successDialogTitle").text(), $("#successSaveRecord").text(), "success");
            		formModal.modal("hide");            		
            	}else{            		
            		globalDisplayError(result.statusDesc);
            	}
            },
            error: function(error){
            	globalDisplayError($("#errorSaveRecord").text(), error);
            }
        });
		globalWriteConsoleLog("out submitUpdateRequest(boatInfo)");
	}

	//delete data
	function submitDeleteRequest(boatCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(boatCode, confirmDeleteModal)");
		globalWriteConsoleLog("boatCode ::" + boatCode);
		$.ajax({
            //url: "/xFace/rest/cfg/deletePersonCategory",
            url:"/xFace/rest/cfg/deleteBoatInfo",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(boatCode),
            success: function(result) {
            	console.log("result :;" + result);
            	globalWriteConsoleLog("result delete :" + result.statusCode);
            	globalIsSessionExpire(result);
            	globalClearLoader();
            	if (result.statusCode==="0"){
            		globalShowGrowlNotification($("#successDialogTitle").text(),$("#successDeleteRecord").text(), "success");
            		dataTable.DataTable().ajax.reload(null, false);              		
            	}else{
            		globalShowGrowlNotification($("#errorDialogTitle").text(),result.statusDesc, "danger");
            	}
            },
            error: function(error){
            	console.log("error ::" + error);
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorDeleteRecord").text(), "danger", error);
            }
        });
		globalWriteConsoleLog("out submitDeleteRequest(deleteCode, confirmDeleteModal)");
	}			
	$("#btnSave").on("click", function(event){
		event.preventDefault();
		updateData();		
	});
	$("#btnAdd").on("click", function(event){
		event.preventDefault();
		showDialog(null);
	});
	$("#btnEdit").on("click", function(event){
		var data = dataTable.DataTable().row(".selected").data();	
		if (data==null){
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorSelectGridData").text(), "danger");
		}else{
			showDialog(data);
		}		
	});
	$("#btnDelete").on("click", function(event){		
		var data = dataTable.DataTable().row(".selected").data();	
		if (data==null){			
			event.preventDefault();
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorSelectGridData").text(), "danger");
			return false;
		}else{
			$(this).attr("data-boat-code", data.boatCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		formModal.modal("hide");
	});
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var boatCode = $(this).data("boatCode");
        globalWriteConsoleLog("delete Boat code:" + boatCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({boatCode: boatCode}, confirmDeleteModal);
	});
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var boatCode = $("#btnDelete").attr("data-boat-code");
        $(".title", this).text(boatCode);
        $(".btn-ok", this).data("boatCode", boatCode);        
    });
});