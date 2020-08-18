$(document).ready( function () {
	var dataTable = $("#data-table");	
	var rowClick = null;
	var formModal = $("#formModal");
	var tvPermission = $("#treeview-permission");
	var actionCommand = "ADD"; //ADD, EDIT
	var confirmDeleteModal = null;
	globalImageLoader =  $("#imageLoader");
	dataTable.DataTable( {
		"processing": true,
        "serverSide": true,
        "searching": false,
        "aLengthMenu": globalTableStep,
        "ajax": {
            "url": "/xFace/rest/cfg/getPersonTitleInfoList",
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
            { "data": "titleCode", "name":"titleCode"},
            { "data": "titleName", "name":"titleName" },
            { "data": "titleDesc", "name":"titleDesc" }
        ]
    });			 
	//row click
	dataTable.find("tbody").on( "click", "tr", function () {
		if (rowClick===null||rowClick!=this){
			rowClick = this;
		}else{
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
    	if ($("#txtTitleCode").val()===""){
    		globalDisplayError("Please enter Title code");
    		result = false;
    	}else if ($("#txtTitleDesc").val()===""){
			globalDisplayError("Please enter Title Description");
			result = false;
		}else if ($("#txtTitleName").val()===""){
			globalDisplayError("Please enter Title Name");
			result = false;
		}
		return result;
    }

    //show dialog add/edit
	function showDialog(data) { 
		globalWriteConsoleLog("in showDialog");
		globalRemoveError();
		globalAddLoader(); 
		var titleCode = $("#txtTitleCode");
		var titleDesc = $("#txtTitleDesc");
		var titleName = $("#txtTitleName");
		var divTitleCode = $("#divTitleCode");
		var titleCodeClass = divTitleCode.hasClass("inputBoxDisable");
    	if (data === null) {
    		// Add
    		globalClearLoader();
    		actionCommand = "ADD";
    		if( titleCodeClass === true){
    			divTitleCode.removeClass("inputBoxDisable").addClass("inputBox");
    		}
    		titleCode.val("");
    		titleDesc.val("");
    		titleName.val("");
    		formModal.modal("show");
    	}else{    		
    		// Edit
    		globalClearLoader();
    		actionCommand = "EDIT";
    		$("#divTitleCode").removeClass("inputBox").addClass("inputBoxDisable");
    		//titleCode.val(data.titleCode).attr("required",true);
    		//titleCode.find("inputBox").addClass( "inputBoxDisable" );
    		titleCode.val(data.titleCode).attr("readonly",true);
    		titleName.val(data.titleName);
    		titleDesc.val(data.titleDesc);
    		formModal.modal("show");
    		globalWriteConsoleLog("data.titleCode : " + data.titleCode);
    		globalWriteConsoleLog("data.titleDesc : " + data.titleDesc);
    		globalWriteConsoleLog("data.titleName : " + data.titleName);
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
		var personTitleInfo = createPersonTitleInfo();
		globalWriteConsoleLog("Create personTitleInfo titleCode ::" + personTitleInfo.titleCode);
		globalWriteConsoleLog("Create personTitleInfo titleDesc ::" + personTitleInfo.titleDesc);
		globalWriteConsoleLog("Create personTitleInfo titleName ::" + personTitleInfo.titleName);
		globalWriteConsoleLog("Create personTitleInfo actionCommand ::" + personTitleInfo.actionCommand);
		if(personTitleInfo === null){
			globalDisplayError("Create Fail Person Title Info Data not null");
		}else{
			submitUpdateRequest(personTitleInfo);
		}
		globalWriteConsoleLog("out updateData");
	}
	
	//from screen create personTitleInfo  
	function createPersonTitleInfo(){
		var titleInfo = {};
		titleInfo["titleCode"] = $("#txtTitleCode").val();
		titleInfo["titleDesc"] = $("#txtTitleDesc").val();
		titleInfo["titleName"] = $("#txtTitleName").val();
		titleInfo["actionCommand"] = actionCommand;
		return titleInfo;	
	}
	
	//submit request to web service
	function submitUpdateRequest(personTitleInfo){				
		globalWriteConsoleLog("in submitUpdateRequest(personTitleInfo)");
		globalWriteConsoleLog(personTitleInfo);
		$.ajax({
            url:"/xFace/rest/cfg/updatePersonTitle",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(personTitleInfo),
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
		globalWriteConsoleLog("out submitUpdateRequest(persontitleInfo)");
	}

	//delete data
	function submitDeleteRequest(titleCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(titleCode, confirmDeleteModal)");
		globalWriteConsoleLog(titleCode);
		$.ajax({
            url: "/xFace/rest/cfg/deletePersonTitle",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(titleCode),
            success: function(result) {
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
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorDeleteRecord").text(), "danger", error);
            }
        });
		globalWriteConsoleLog("out submitDeleteRequest(titleCode, confirmDeleteModal)");
	}			
	$("#btnSave").on("click", function(event){
		event.preventDefault();
		updateData();		
	});
	$("#btnAdd").on("click", function(event){
		
		event.preventDefault();
		/* Refresh Modal */ 
		
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
			$(this).attr("data-title-code", data.titleCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		formModal.modal("hide");
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var titleCode = $(this).data("titleCode");
        globalWriteConsoleLog("delete certificate code:"+titleCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({personTitleCode: titleCode}, confirmDeleteModal);
	});
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var titleCode = $("#btnDelete").attr("data-title-code");
        $(".title", this).text(titleCode);
        $(".btn-ok", this).data("titleCode", titleCode);        
    });
});
	
	