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
        	"url": "/xFace/rest/cfg/getPersonNationalityInfoList",
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
        	   { "data": "nationalityCode", "name":"nationalityCode"},
               { "data": "nationalityName", "name":"nationalityName"},
               { "data": "userCreated", "name":"userCreated" }
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
    	if ($("#txtNationalityCode").val()===""){
    		globalDisplayError("Please enter Nationality code");
    		result = false;
    	}
    	if($("#txtNationalityName").val()===""){
			globalDisplayError("Please enter Nationality name ");
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
    		$("#txtNationalityCode").val("").attr("disabled", false);
    		$("#txtNationalityName").val("");
    		formModal.modal("show");
    		
    	}else{    		
    		// Edit
    		globalClearLoader();
    		actionCommand = "EDIT";
    		$("#txtNationalityCode").val(data.nationalityCode).attr("disabled", true);
    		$("#txtNationalityName").val(data.nationalityName);
    		formModal.modal("show");
    		globalWriteConsoleLog("data.nationalityCode" + data.nationalityCode);
    		globalWriteConsoleLog("data.nationalityName" + data.nationalityName);
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
		var personNationality = createPersonNationalityInfo();
		globalWriteConsoleLog("Create personNationality code :" + personNationality.nationalityCode);
		globalWriteConsoleLog("Create personNationnality Name :" + personNationality.nationalityName);
		globalWriteConsoleLog("Create actionCommand :" + personNationality.actionCommand);
		if(personNationality === null){
			globalDisplayError("Create Fail Person Nationality Data not null");
		}else{
			submitUpdateRequest(personNationality);
		}
		globalWriteConsoleLog("out updateData");
	}
	//from screen create Nationality
	function createPersonNationalityInfo(){
		var nationalityInfo = {};
		nationalityInfo["nationalityCode"] = $("#txtNationalityCode").val();
		nationalityInfo["nationalityName"] = $("#txtNationalityName").val();
		nationalityInfo["actionCommand"] = actionCommand;
		return nationalityInfo;	
	}
	
	
	//submit request to web service
	function submitUpdateRequest(nationalityInfo){				
		globalWriteConsoleLog("in submitUpdateRequest(nationalityInfo)");
		globalWriteConsoleLog(nationalityInfo);
		$.ajax({
            url:"/xFace/rest/cfg/updatePersonNationality",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(nationalityInfo),
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
		globalWriteConsoleLog("out submitUpdateRequest(nationalityInfo)");
	}

	//delete data
	function submitDeleteRequest(nationalityCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(nationalityCode, confirmDeleteModal)");
		globalWriteConsoleLog(nationalityCode);
		$.ajax({
            url: "/xFace/rest/cfg/deletePersonNationality",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(nationalityCode),
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
		globalWriteConsoleLog("out submitDeleteRequest(nationalityCode, confirmDeleteModal)");
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
			$(this).attr("data-nationality-code", data.nationalityCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		$("#panelTreeview-Permission").scrollTop(0);
		formModal.modal("hide");
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var nationalityCode = $(this).data("nationalityCode");
        globalWriteConsoleLog("delete Nationality code:"+ nationalityCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({personNationalityCode: nationalityCode}, confirmDeleteModal);
	});
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var nationalityCode = $("#btnDelete").attr("data-nationality-code");
        $(".title", this).text(nationalityCode);
        $(".btn-ok", this).data("nationalityCode", nationalityCode);        
    });
});