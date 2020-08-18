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
            "url": "/xFace/rest/cfg/getHtmlPageInfoList",
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
            { "data": "pageCode", "name":"pageCode"},
            { "data": "pageURL", "name":"pageURL" },
            { "data": "pageDesc", "name":"pageDesc" }
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
    	if ($("#txtPageCode").val()===""){
    		globalDisplayError("Please enter Page code");
    		result = false;
    	}else if ($("#txtPageDesc").val()===""){
			globalDisplayError("Please enter Page Description");
			result = false;
		}else if ($("#txtPageUrl").val()===""){
			globalDisplayError("Please enter Page URL");
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
    		actionCommand = "ADD";
    		globalClearLoader();
    		$("#txtPageCode").val("").attr("disabled", false);
    		$("#txtPageDesc").val("");
    		$("#txtPageUrl").val("");
    		formModal.modal("show");
    	}else{    		
    		// Edit
    		actionCommand = "EDIT";
    		globalClearLoader();
    		$("#txtPageCode").val(data.pageCode).attr("disabled", true);
    		$("#txtPageDesc").val(data.pageDesc);
    		$("#txtPageUrl").val(data.pageURL);
    		globalClearLoader();
    		formModal.modal("show");
    		globalWriteConsoleLog("data.pageCode : " + data.pageCode);
    		globalWriteConsoleLog("data.pageDesc : " + data.pageDesc);
    		globalWriteConsoleLog("data.pageUrl : " + data.pageURL);
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
		var pageHtmlInfo = createPageHtmlInfo();
		globalWriteConsoleLog("CreateInfo pageCode :" + pageHtmlInfo.pageCode);
		globalWriteConsoleLog("CreateInfo pageDesc :" + pageHtmlInfo.pageDesc);
		globalWriteConsoleLog("CreateInfo pageURL :" + pageHtmlInfo.pageURL);
		globalWriteConsoleLog("CreateInfo actionCommand :" + pageHtmlInfo.actionCommand);
		if(pageHtmlInfo === null){
			globalDisplayError("Create Fail pageHtmlInfo Data not null");
		}else{
			submitUpdateRequest(pageHtmlInfo);
		}
		globalWriteConsoleLog("out updateData");
	}
	
	//from screen create personCertificateInfo  
	function createPageHtmlInfo(){
		var pageHtmlDetail = {};
		pageHtmlDetail["pageCode"] = $("#txtPageCode").val();
		pageHtmlDetail["pageDesc"] = $("#txtPageDesc").val();
		pageHtmlDetail["pageURL"] = $("#txtPageUrl").val();
		pageHtmlDetail["actionCommand"] = actionCommand;
		return pageHtmlDetail;	
	}
	
	
	//submit request to web service
	function submitUpdateRequest(pageHtmlInfo){				
		globalWriteConsoleLog("in submitUpdateRequest(pageHtmlinfo)");
		globalWriteConsoleLog(pageHtmlInfo);
		$.ajax({
            url:"/xFace/rest/cfg/updatePageHtml",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(pageHtmlInfo),
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
		globalWriteConsoleLog("out submitUpdateRequest(pageHtmlInfo)");
	}
	
	
	//delete data
	function submitDeleteRequest(htmlPageCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(htmlPageCode, confirmDeleteModal)");
		globalWriteConsoleLog(htmlPageCode);
		$.ajax({
            url: "/xFace/rest/cfg/deleteHtmlPage",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(htmlPageCode),
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
		globalWriteConsoleLog("out submitDeleteRequest(htmlPageCode, confirmDeleteModal)");
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
			$(this).attr("data-page-code", data.pageCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		$("#panelTreeview-Permission").scrollTop(0);
		formModal.modal("hide");
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var pageCode = $(this).data("pageCode");
        globalWriteConsoleLog("delete page code:"+ pageCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({pageCode: pageCode}, confirmDeleteModal);
	});
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var pageCode = $("#btnDelete").attr("data-page-code");
        $(".title", this).text(pageCode);
        $(".btn-ok", this).data("pageCode", pageCode);        
    });
});
	
	