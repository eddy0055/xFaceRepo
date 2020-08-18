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
        	"url": "/xFace/rest/cfg/getLocationMapList",
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
        	   { "data": "mapCode", "name":"mapCode"},
               { "data": "mapName", "name":"mapName"},
               { "data": "mapDesc", "name":"mapDesc" },
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
    	if ($("#txtMapCode").val()===""){
    		globalDisplayError("Please enter Map code");
    		result = false;
    	}else if ($("#txtMapName").val()===""){
			globalDisplayError("Please enter Map Name");
			result = false;
    	}else if($("#txtMapDesc").val()===""){
    		globalDisplayError("Please enter Map Description");
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
    		$("#txtMapCode").val("").attr("disabled", false);
    		$("#txtMapName").val("");
    		$("#txtMapDesc").val("");
    		$("#imgUploadPerview").attr("src","/xFace/dist/img/noimage.gif");
    		formModal.modal("show");
    	}else{    		
    		// Edit
    		globalClearLoader();
    		actionCommand = "EDIT";
    		$("#txtMapCode").val(data.mapCode).attr("disabled", true);
    		$("#txtMapName").val(data.mapName);
    		$("#txtMapDesc").val(data.mapDesc);
    		var check = data.mapPhoto;
    		if(check == "" || check == null || check == undefined){
    			$("#imgUploadPerview").attr("src","/xFace/dist/img/noimage.gif");
    		}else{
    			$("#imgUploadPerview").attr("src",data.mapPhoto);
    		}
    		$("#lblUploadFile").val("");
    		globalClearLoader();
    		formModal.modal("show");
    		globalWriteConsoleLog("showDialog data.mapCode :" + data.mapCode);
    		globalWriteConsoleLog("showDialog data.mapCode :" + data.mapCode);
    		globalWriteConsoleLog("showDialog data.mapCode :" + data.mapCode);
    	}    	    	    	    	   
    	globalWriteConsoleLog("out showDialog");
    }
		
	//updateData
	function updateData(){
		globalWriteConsoleLog("in updateData");
		var locationMapInfo = createMapInfo();
		if (locationMapInfo===null){
			globalDisplayError("Please fill Empty value.");
		}else{
			submitUpdateRequest(locationMapInfo);
		}		
		globalWriteConsoleLog("out updateData");
	}

	//create Map Info
	function createMapInfo(){
		var mapInfoUpdate = {};
		mapInfoUpdate["mapCode"] = $("#txtMapCode").val();
		mapInfoUpdate["mapName"] = $("#txtMapName").val();
		mapInfoUpdate["mapDesc"] = $("#txtMapDesc").val();
		mapInfoUpdate["actionCommand"] = actionCommand;
		globalWriteConsoleLog("create mapCode :" + mapInfoUpdate.mapCode);
		globalWriteConsoleLog("create mapName :" + mapInfoUpdate.mapName);
		globalWriteConsoleLog("create mapDesc :" + mapInfoUpdate.mapDesc);
		data = new FormData();
		data.append("mapInfo", JSON.stringify(mapInfoUpdate));                
		data.append("mapPhoto", $('#btnUploadFile').prop("files")[0]);
		globalWriteConsoleLog("create mapPhoto :" + data.mapPhoto);
    return data;
	}
	
	//submit request to web service
	function submitUpdateRequest(data){				
		globalWriteConsoleLog("in submitUpdateRequest(updateInfo)");
		globalWriteConsoleLog(data);				
		$.ajax({
            url: "/xFace/rest/cfg/updateLocationMap",
            type: "POST",
            contentType: false,            
            processData: false,
            data: data,
            success: function(result) {
            	globalIsSessionExpire(result);
            	if (result.statusCode==="0"){            		
            		dataTable.DataTable().ajax.reload(null, false);            		
            		globalShowGrowlNotification($("#successDialogTitle").text(),$("#successSaveRecord").text(), "success");
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
	
	//delete data location Map
	function submitDeleteRequest(mapCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(deleteCode, confirmDeleteModal)");
		globalWriteConsoleLog(mapCode);
		$.ajax({
            url: "/xFace/rest/cfg/deleteLocationMap",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(mapCode),
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
		globalWriteConsoleLog("out submitDeleteRequest(mapCode, confirmDeleteModal)");
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
			$(this).attr("data-mapCode-code", data.mapCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		formModal.modal("hide");
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var mapCode = $(this).data("mapCode");
        globalWriteConsoleLog("delete Map code:"+mapCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({mapCode: mapCode}, confirmDeleteModal);
	});
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var mapCode = $("#btnDelete").attr("data-mapCode-code");
        $(".title", this).text(mapCode);
        $(".btn-ok", this).data("mapCode", mapCode);        
    });
    $("#btnUploadFile").bind("change", function() {
    	var imgFile = this.files[0]
    	//Check File size
    	if (imgFile.size > 5120000 || imgFile.fileSize > 5120000){
    		//show error file size
    		globalShowGrowlNotification($("#errorDialogTitle").text(), "Image file size should not more than 5Mb", "danger");
		    this.value = null;
		}else if (this.files && imgFile) {
			var reader = new FileReader();
		    reader.onload = function (e) {
		    	$("#imgUploadPerview").attr("src", e.target.result);
		    }
		    reader.readAsDataURL(imgFile);
		}				  
    });   
});