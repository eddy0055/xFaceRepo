$(document).ready( function () {
	var dataTable = $("#data-table");	
	var rowClick = null;
	var formModal = $("#formModal");
	var actionCommand = "ADD"; //ADD, EDIT
	var confirmDeleteModal = null;
	globalImageLoader =  $("#imageLoader");
	dataTable.DataTable( {
		"processing": true,
        "serverSide": true,
        "searching": false,
        "aLengthMenu": globalTableStep,
        "ajax": {
            "url": "/xFace/rest/cfg/getAppCfgInfoList",
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
            { "data": "appDesc", "name":"appDesc"},
            { "data": "appValue1", "name":"appValue1"},
            { "data": "appValue2", "name":"appValue2" },
            { "data": "appValue3", "name":"appValue3" }
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
	
	
	//show dialog add/edit
	function showDialog(data) { 
		globalWriteConsoleLog("in showDialog");
		globalRemoveError();
		globalAddLoader(); 
		// Edit
		
		actionCommand = "EDIT";
		$("#txtAppKey").val(data.appKey).attr("disabled", "disabled");
		$("#txtAppDesc").val(data.appDesc);
		$("#txtAppValue1").val(data.appValue1);
		$("#txtAppValue2").val(data.appValue2);
		$("#txtAppValue3").val(data.appValue3);
		//Check appLobValue
		var check = data.appLobValue;
		if(check == "" || check == null || check == undefined){
			$("#imgUploadPerview").attr("src","/xFace/dist/img/noimage.gif");
		}else{
			$("#imgUploadPerview").attr("src",data.appLobValue);
		}
		$("#lblUploadFile").val("");
		$("#txtAppKey").focus();
		globalClearLoader();
    	//getPermission(roleInfo);  
    	getAppCfgDetailInfo(data);
    	globalWriteConsoleLog("out showDialog");
    }	
	
	// getAppCfgDetailInfo
	function getAppCfgDetailInfo(data) {
		globalWriteConsoleLog("in getAppKeyDetailInfo "+data.appKey);
        $.ajax({
        	type: "POST",
        	url: "/xFace/rest/cfg/getAppKeyDetail",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({"appKey":data.appKey}),
            dataType: "json",
            success:function(appKeyDetailInfo){
            	formModal.modal("show");            	
            	globalClearLoader();
            },
            error: function(error){
            	globalClearLoader();
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);            	
            }
        });
        globalWriteConsoleLog("out getAppKeyDetailInfo");
    }

	//updateData
	function updateData(){
		globalWriteConsoleLog("in updateData");
		var AppCfgInfoUpdate = createAppCfgInfo();
		if (AppCfgInfoUpdate===null){
			globalDisplayError("Please fill Empty value.");
		}else{
			var appConfigDetailInfo = createAppCfgInfo();
			submitUpdateRequest(appConfigDetailInfo);
		}		
		globalWriteConsoleLog("out updateData");
	}
	
	// Create AppConfig 
	function createAppCfgInfo(){
		var appCfgInfoUpdate = {};
			appCfgInfoUpdate["appKey"] = $("#txtAppKey").val();
			appCfgInfoUpdate["appDesc"] = $("#txtAppDesc").val();
			appCfgInfoUpdate["appValue1"] = $("#txtAppValue1").val();
			appCfgInfoUpdate["appValue2"] = $("#txtAppValue2").val();
			appCfgInfoUpdate["appValue3"] = $("#txtAppValue3").val();
			data = new FormData();
			data.append("applicationCfgInfo", JSON.stringify(appCfgInfoUpdate));                
			data.append("personPhoto", $('#btnUploadFile').prop("files")[0]);	
        return data;
	}
	
	//submit request to web service
	function submitUpdateRequest(updateInfo){				
		globalWriteConsoleLog("in submitUpdateRequest(updateInfo)");
		globalWriteConsoleLog(updateInfo);				
		$.ajax({
            url: "/xFace/rest/cfg/updateAppKeyInfo",
            type: "POST",
            contentType: false,            
            processData: false,
            data: updateInfo,
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

	//handle button save click
	$("#btnSave").on("click", function(event){
		event.preventDefault();
		updateData();		
	});
	
	$("#btnEdit").on("click", function(event){
		var data = dataTable.DataTable().row(".selected").data();	
		if (data==null){
			globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorSelectGridData").text(), "danger");
		}else{
			showDialog(data);
		}		
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