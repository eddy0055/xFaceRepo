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
        	"url": "/xFace/rest/cfg/getPersonCategoryInfoList",
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
        	   { "data": "categoryCode", "name":"categoryCode"},
               { "data": "categoryColorCode", "name":"categoryColorCode"},
               { "data": "categoryName", "name":"categoryName" },
               { "data": "categoryDesc", "name":"categoryDesc" }
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
    	if ($("#txtCategoryCode").val()===""){
    		globalDisplayError("Please enter Category code");
    		result = false;
    	}else if ($("#txtCategoryColorCode").val()===""){
			globalDisplayError("Please enter Category Color code.");
			result = false;
    	}else if($("#txtCategoryDesc").val()===""){
    		globalDisplayError("Please enter Category Description");
			result = false;
		}else if ($("#txtCategoryName").val()===""){
			globalDisplayError("Please enter Category Name");
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
    		$("#txtCategoryCode").val("").attr("disabled", false);
    		$("#txtCategoryColorCode").val("");
    		$("#txtCategoryDesc").val("");
    		$("#txtCategoryName").val("");
    		formModal.modal("show");
    		
    	}else{    		
    		// Edit
    		globalClearLoader();
    		actionCommand = "EDIT";
    		$("#txtCategoryCode").val(data.categoryCode).attr("disabled", true);
    		$("#txtCategoryColorCode").val(data.categoryColorCode);
    		$("#txtCategoryDesc").val(data.categoryDesc);
    		$("#txtCategoryName").val(data.categoryName);
    		formModal.modal("show");
    		globalWriteConsoleLog("data.categoryCode :" + data.categoryCode);
    		globalWriteConsoleLog("data.categoryColorCode :" + data.categoryColorCode);
    		globalWriteConsoleLog("data.categoryDesc :" + data.categoryDesc);
    		globalWriteConsoleLog("data.categoryName :" + data.categoryName);
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
		var personCategoryUpdate = createPersonCategoryInfo();
		globalWriteConsoleLog("CreateInfo categoryCode :" + personCategoryUpdate.categoryCode);
		globalWriteConsoleLog("CreateInfo categoryColorCode :" + personCategoryUpdate.categoryColorCode);
		globalWriteConsoleLog("CreateInfo categoryDesc :" + personCategoryUpdate.categoryDesc);
		globalWriteConsoleLog("CreateInfo categoryName : " + personCategoryUpdate.categoryName);
		globalWriteConsoleLog("CreateInfo actionCommand :" + personCategoryUpdate.actionCommand);
		
		if(personCategoryUpdate === null){
			globalDisplayError("Create Fail Person Category Data not null");
		}else{
			submitUpdateRequest(personCategoryUpdate);
		}
		globalWriteConsoleLog("out updateData");
	}
	
	//from screen create userInfo and roleDetailInfo 
	function createPersonCategoryInfo(){
		var categoryInfo = {};
		categoryInfo["categoryCode"] = $("#txtCategoryCode").val();
		categoryInfo["categoryColorCode"] = $("#txtCategoryColorCode").val();
		categoryInfo["categoryDesc"] = $("#txtCategoryDesc").val();
		categoryInfo["categoryName"] = $("#txtCategoryName").val();
		categoryInfo["actionCommand"] = actionCommand;
		return categoryInfo;	
	}
	
	//submit request to web service
	function submitUpdateRequest(personcategoryUpdate){				
		globalWriteConsoleLog("in submitUpdateRequest(personcategoryUpdate)");
		globalWriteConsoleLog(personcategoryUpdate);
		$.ajax({
            url:"/xFace/rest/cfg/updatePersonCategory",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(personcategoryUpdate),
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
		globalWriteConsoleLog("out submitUpdateRequest(updateInfo)");
	}

	//delete data
	function submitDeleteRequest(categoryCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(deleteCode, confirmDeleteModal)");
		globalWriteConsoleLog(categoryCode);
		$.ajax({
            url: "/xFace/rest/cfg/deletePersonCategory",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(categoryCode),
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
			$(this).attr("data-category-code", data.categoryCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		formModal.modal("hide");
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var categoryCode = $(this).data("categoryCode");
        globalWriteConsoleLog("delete Category code:"+categoryCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({categoryCode: categoryCode}, confirmDeleteModal);
	});
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var categoryCode = $("#btnDelete").attr("data-category-code");
        $(".title", this).text(categoryCode);
        $(".btn-ok", this).data("categoryCode", categoryCode);        
    });
});