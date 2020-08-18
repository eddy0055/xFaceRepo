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
            "url": "/xFace/rest/userManage/user/getUserInfoList",
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
        	{ "data": "userId","name":"userId"},
        	{ "data": "userName","name":"userName"},
            { "data": "firstName", "name":"firstName"},
            { "data": "lastName", "name":"lastName"},
            { "data": "roleInfo.roleName", "name":"roleInfo.roleName"}
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
		getUserDetail(data);
		getAllRoleInfoList();
		
    });		

	//validate user input
    function validateInputData(){
    	var result = true;
    	if ($("#txtUserName").val()===""){
    		globalDisplayError("Please enter User Name.");
    		result = false;
    	}else if ($("#txtFirstName").val()===""){
			globalDisplayError("Please enter First name.");
			result = false;
    	}else if($("#txtLastName").val()===""){
    		globalDisplayError("Please enter Last name.");
			result = false;
		}
		return result;
    }
    
	//get roleDetailInfo from backend by roleCode
	function getUserDetail(data) {
		globalWriteConsoleLog("in getUserDetail "+data.userName);
        $.ajax({
            type: "POST",
            url: "/xFace/rest/userManage/user/getUserInfo",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({"userName":data.userName}),
            dataType: "json",
            success: function(userDetailInfo) {
            	globalIsSessionExpire(userDetailInfo);
            	showDialog(userDetailInfo);
            	globalClearLoader();
            },
            error: function(error){
            	globalClearLoader();
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);            	
            }
        });
        globalWriteConsoleLog("out getUserDetail");
    }

	//show dialog add&edit
	function showDialog(userDetailInfo) { 
		globalWriteConsoleLog("in showDialog");
		globalRemoveError();
		globalAddLoader(); 
    	if (userDetailInfo === null) {
    		// Add
    		actionCommand = "ADD";
    		globalClearLoader();
    		$("#txtUserName").val("");
    		$("#txtFirstName").val("");
    		$("#txtLastName").val("");    
    		getAllRoleInfoList();
    		$("#chkCheckBox").val("");
    		formModal.modal("show");
    	}else{    		
    		// Edit
    		actionCommand = "EDIT";
    		$("#txtUserName").val(userDetailInfo.userName);
    		$("#txtFirstName").val(userDetailInfo.firstName);
    		$("#txtLastName").val(userDetailInfo.lastName);    
    		$("#cmbDefaultPage").selectpicker("val",userDetailInfo.roleInfo.roleName);
    		var checkBoxValue = userDetailInfo.enabled;
    		console.log("checkBoxValue ::" + checkBoxValue);
    		var checkBoxNum = $("#chkCheckBox");
    		if(userDetailInfo.enabled = 1){
    			$("#chkCheckBox").prop("checked","true"); // Checked
    		}else{
    			$("#chkCheckBox").prop("checked","false"); // UnChecked
    			$("#chkCheckBox").empty();
    		}
    		globalClearLoader();
    		formModal.modal("show");
    	}    	    	    	    	   
    	globalWriteConsoleLog("out showDialog");
    }
	//get rolePageInfo 
	function getAllRoleInfoList() {
		globalWriteConsoleLog("in getAllRoleInfoList");
        $.ajax({
            type: "POST",
            //url: "/xFace/rest/cfg/getAllRole",
            url: "/xFace/rest/userManage/user/getAllRole",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(roleInfoList) {
            	bindRolePageInfoList(roleInfoList);
            },
            error: function(error){
            	globalClearLoader();
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[HtmlPageInfo]", "danger", error);            	
            }
        });
        globalWriteConsoleLog("out getAllRoleInfoList");
    }
	
	//bind page info to screen
	function bindRolePageInfoList(roleInfoList){
		var cmbDefaultPage = $("#cmbDefaultPage");
		var options = [];
		$.each(roleInfoList, function(i, item){		
			options.push("<option value='"+item.roleId+"'>"+item.roleName+"</option>");			
		});
		//show dialog
		cmbDefaultPage.html(options);
		cmbDefaultPage.selectpicker("refresh");
	}
	
	//get roleDetailInfo from backend by roleCode
	function getRoleDetailInfo() {
		globalWriteConsoleLog("in getRoleDetailInfo ");
        $.ajax({
            type: "POST",
            url: "/xFace/rest/userManage/role/getRoleInfoList",
            contentType: "application/json; charset=utf-8",
         
            dataType: "json",
            success: function(roleDetailInfo) {
            	globalIsSessionExpire(roleDetailInfo);
            	formModal.modal("show");            	
            	globalClearLoader();
            },
            error: function(error){
            	globalClearLoader();
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);            	
            }
        });
        globalWriteConsoleLog("out getRoleDetailInfo");
    }
	
	//updateData
	function updateData(){
		globalWriteConsoleLog("in updateData");
		var validateInput = validateInputData();
		if (!validateInput){
			return;
		}
		var userInfoUpdate = createUserInfo();
		globalWriteConsoleLog("Create userInfo userName" + userInfoUpdate.userName);
		globalWriteConsoleLog("Create userInfo firstName" + userInfoUpdate.firstName);
		globalWriteConsoleLog("Create userInfo lastName" + userInfoUpdate.lastName);
		globalWriteConsoleLog("Create userInfo enabled" + userInfoUpdate.enabled);
		globalWriteConsoleLog("Create userInfo userName" + userInfoUpdate.roleId);
		if(userInfoUpdate === null){
			globalDisplayError("Please select user detail");
		}else{
			submitUpdateRequest(userInfoUpdate);
		}
		globalWriteConsoleLog("out updateData");
	}
	
	//from screen create userInfo and roleDetailInfo 
	function createUserInfo(){
		var valueChecked,valueEnabled = null;
		if($('input[name="chkCheckBox"]').is(":checked")){
			valueEnabled = 1;
			globalWriteConsoleLog("valueEnabled" + valueEnabled);
		}else{
			valueEnabled = 0;
			globalWriteConsoleLog("valueEnabled" + valueEnabled);
		}
		
		var cmb1 = $("#cmbDefaultPage").val();
		var roleInfo = {};
		roleInfo["roleId"] = cmb1;
		var userInfo = {};
		userInfo["userName"] = $("#txtUserName").val();
		userInfo["firstName"] = $("#txtFirstName").val();
		userInfo["lastName"] = $("#txtLastName").val();
		userInfo["password"] = $("#txtFirstName").val() + ":" + $("#txtLastName").val();
		userInfo["enabled"] = valueEnabled;
		userInfo["roleInfo"] = roleInfo;
		return userInfo;	
	}
	
	//from screen create roleInfo and roleDetailInfo
	function createRoleInfo(userInfoUpdate){
		var roleInfo = {};
		roleInfo["roleName"] = userInfoUpdate.roleInfo;
		var htmlPageInfo = {};
		htmlPageInfo["pageCode"] = $("#cmbDefaultPage").val();
		roleInfo["htmlPageInfo"] = htmlPageInfo;
		return roleInfo;
	}

	//from screen create roleDetailInfo object
	function createRoleDetailInfo(){
		var roleDetailList = null;
		var permissionCode = null;
		tvPermission.find("ul li").each(function(){
			nodeName = $(this).text();			
			if (nodeName.indexOf("[Group]")<0){
				//detail node
				index1 = nodeName.lastIndexOf("[");
				index2 = nodeName.lastIndexOf("]");
				if (index1>-1 && index2>-1){
					permissionCode = nodeName.substring(index1+1, index2);					
				}else{
					permissionCode = null;
				}			
			}else{
				permissionCode = null;
			}
			if (permissionCode!==null){				
				if ($(this).hasClass("node-checked")){
					if (roleDetailList===null){
						roleDetailList = [];
					}									
					roleDetailList.push( { "roleDetailId": null, "permissionList": { "permissionName": permissionCode } } );
				}					
			}
		});
		return roleDetailList;
	}

	//submit request to web service
	function submitUpdateRequest(updateInfo){				
		globalWriteConsoleLog("in submitUpdateRequest(updateInfo)");
		globalWriteConsoleLog(updateInfo);
		$.ajax({
            url:"/xFace/rest/userManage/user/updateUserInfo",
			type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(updateInfo),
            success: function(result) {
            	globalIsSessionExpire(result);
            	if (result.statusCode==="0"){
            		dataTable.DataTable().ajax.reload(null, false);
            		globalShowGrowlNotification($("#successDialogTitle").text(), $("#successSaveRecord").text(), "success");
            		//$("#panelTreeview-Permission").scrollTop(0);
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
	function submitDeleteRequest(deleteCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(deleteCode, confirmDeleteModal)");
		globalWriteConsoleLog(deleteCode);
		$.ajax({
			//url: user/deleteUserInfo
            url: "/xFace/rest/userManage/user/deleteUserInfo",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(deleteCode),
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

	//handle button save click
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
			$(this).attr("data-role-code", data.roleCode);
		}		
	});
	
	$("#btnClose").on("click", function(event){
		event.preventDefault();
		$("#panelTreeview-Permission").scrollTop(0);
		formModal.modal("hide");
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var deleteUserCode = $(this).data("userCode");
        globalWriteConsoleLog("delete user code:"+deleteUserCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({deleteCode: deleteUserCode}, confirmDeleteModal);
    });
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var deleteCode = $("#btnDelete").attr("data-delete-code");
        $(".title", this).text(deleteCode);
        $(".btn-ok", this).data("deleteCode", deleteCode);        
    });
});