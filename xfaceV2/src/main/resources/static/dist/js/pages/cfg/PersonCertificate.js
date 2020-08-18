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
            "url": "/xFace/rest/cfg/getPersonCertificateInfoList",
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
            { "data": "certificateCode", "name":"certificateCode"},
            { "data": "certificateName", "name":"certificateName" },
            { "data": "certificateDesc", "name":"certificateDesc" }
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
    	if ($("#txtCertificateCode").val()===""){
    		globalDisplayError("Please enter Certificate code");
    		result = false;
    	}else if ($("#txtCertificateDesc").val()===""){
			globalDisplayError("Please enter Certificate Description");
			result = false;
		}else if ($("#txtCertificateName").val()===""){
			globalDisplayError("Please enter Certificate Name");
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
    		$("#txtCertificateCode").val("").attr("disabled", false);
    		$("#txtCertificateDesc").val("");
    		$("#txtCertificateName").val("");
    		formModal.modal("show");
    		
    	}else{    		
    		// Edit
    		globalClearLoader();
    		actionCommand = "EDIT";
    		$("#txtCertificateCode").val(data.certificateCode).attr("disabled", true);
    		$("#txtCertificateDesc").val(data.certificateDesc);
    		$("#txtCertificateName").val(data.certificateName);
    		formModal.modal("show");
    		globalWriteConsoleLog("showDialog data.certificateCode " + data.certificateCode);
    		globalWriteConsoleLog("showDialog data.certificateDesc " + data.certificateDesc);
    		globalWriteConsoleLog("showDialog data.certificateName " + data.certificateName);
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
		var personCertificate = createPersonCertificateInfo();
		globalWriteConsoleLog("CreateInfo certificateCode::" + personCertificate.certificateCode);
		globalWriteConsoleLog("CreateInfo certificateDesc::" + personCertificate.certificateDesc);
		globalWriteConsoleLog("CreateInfo certificateName:: " + personCertificate.certificateName);
		globalWriteConsoleLog("CreateInfo actionCommand ::" +   personCertificate.actionCommand);
		if(personCertificate === null){
			globalDisplayError("Create Fail Person Certificate Data not null");
		}else{
			submitUpdateRequest(personCertificate);
		}
		globalWriteConsoleLog("out updateData");
	}
	
	//from screen create personCertificateInfo  
	function createPersonCertificateInfo(){
		var certificateInfo = {};
		certificateInfo["certificateCode"] = $("#txtCertificateCode").val();
		certificateInfo["certificateDesc"] = $("#txtCertificateDesc").val();
		certificateInfo["certificateName"] = $("#txtCertificateName").val();
		certificateInfo["actionCommand"] = actionCommand;
		return certificateInfo;	
	}
	
	
	//submit request to web service
	function submitUpdateRequest(personCertificate){				
		globalWriteConsoleLog("in submitUpdateRequest(personCertificateUpdate)");
		globalWriteConsoleLog(personCertificate);
		$.ajax({
            url:"/xFace/rest/cfg/updatePersonCertificate",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(personCertificate),
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
		globalWriteConsoleLog("out submitUpdateRequest(personCertificate)");
	}

	//delete data
	function submitDeleteRequest(certificateCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(certificateCode, confirmDeleteModal)");
		globalWriteConsoleLog(certificateCode);
		console.log("certificate code in submitDeleteRequest ::" + certificateCode);
		$.ajax({
            url: "/xFace/rest/cfg/deletePersonCertificate",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(certificateCode),
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
		globalWriteConsoleLog("out submitDeleteRequest(certificateCode, confirmDeleteModal)");
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
			$(this).attr("data-certificate-code", data.certificateCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		formModal.modal("hide");
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var certificateCode = $(this).data("certificateCode");
        globalWriteConsoleLog("delete certificate code:"+certificateCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({certificateCode: certificateCode}, confirmDeleteModal);
	});
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var certificateCode = $("#btnDelete").attr("data-certificate-code");
        $(".title", this).text(certificateCode);
        $(".btn-ok", this).data("certificateCode", certificateCode);        
    });
});
	
	