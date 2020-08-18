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
            "url": "/xFace/rest/userManage/role/getRoleInfoList",
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
            { "data": "roleId", "name":"roleId"},
            { "data": "roleCode", "name":"roleCode"},
            { "data": "roleName", "name":"roleName"},
            { "data": "defaultPage", "name":"defaultPage" }
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
	//////////////////////////////////////
    //validate user input
    function validateInputData(){
    	var result = true;
    	if ($("#txtRoleCode").val()===""){
    		globalDisplayError("Please enter role code.");
    		result = false;
    	}else if ($("#txtRoleName").val()===""){
			globalDisplayError("Please enter role name.");
			result = false;
		}else if ($("#txtDefaultPage").val()===""){
			$("#txtDefaultPage").val("/");
		}
		return result;
    }
	/////////////////////
	//show dialog add/edit
	function showDialog(roleInfo) { 
		globalWriteConsoleLog("in showDialog");
		globalRemoveError();
		globalAddLoader(); 
    	if (roleInfo === null) {
    		// Add
    		actionCommand = "ADD";
    		$("#txtRoleCode").val("");
    		$("#txtRoleCode").removeAttr("disabled");
    		$("#txtRoleName").val("");
    		$("#txtDefaultPage").val("");   
    		$("#txtRoleCode").focus();
    	}  else {    		
    		// Edit
    		actionCommand = "EDIT";
    		$("#txtRoleCode").val(roleInfo.roleCode);
    		$("#txtRoleCode").attr("disabled", "disabled");
    		$("#txtRoleName").val(roleInfo.roleName);
    		$("#txtDefaultPage").val(roleInfo.defaultPage);
    		$("#txtRoleName").focus();
    	}    	
    	getPermission(roleInfo);    	    	    
    	globalWriteConsoleLog("out showDialog");
    }
	/////////////////////	
	//get permission list form backend and bind to screen
	function getPermission(roleInfo) {
		globalWriteConsoleLog("in getPermission");
        $.ajax({
            type: "GET",
            url: "/xFace/rest/userManage/role/getAllPermissionList",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(permissionList) {
            	globalIsSessionExpire(permissionList);
            	bindPermission(roleInfo, permissionList);            	
            },
            error: function(error){
            	globalClearLoader();
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text(), "danger", error);            	
            }
        });
        globalWriteConsoleLog("out getPermission");
    }
	//////////////////////
	//bind permission to treeview then call backend to get roleDetailInfo of roleInfo
	function bindPermission(roleInfo, permissionList){
		tvPermission.treeview({
	        data: convertJsonToArrayTreeview(permissionList),
	        showIcon: false,
	        showCheckbox: true,
	        onNodeChecked: function(event, node) {          
	          //if parent check all child have to check
	        	handleTreeViewCheckBoxAction("checkNode", node);
	        },
	        onNodeUnchecked: function (event, node) {
	          //if parent uncheck all child have to uncheck
	        	handleTreeViewCheckBoxAction("uncheckNode", node);          
	        }
	    });			
		//call to get roleDetailInfo
		if (actionCommand==="ADD"){			
			formModal.modal("show");
			globalClearLoader();
		}else{
			getRoleDetailInfo(roleInfo);
		}		
	}
	///////////////////////
	//convert roleInfo which return from backend to json which accept by dynamic treeview UI
	function convertJsonToArrayTreeview(permissionList){
		var rootNode = [];
		var tvLevel1 = {};
		var tvLevel2 = [];
		var permissionGroup=null;
		$.each(permissionList, function(i, item){			
			if (permissionGroup===null){
				permissionGroup = item.permissionGroup+" [Group]";
				tvLevel1["text"] = permissionGroup;
				tvLevel2.push({text:item.permissionGUI+" ["+item.permissionName+"]"});
			}else if (permissionGroup===(item.permissionGroup+" [Group]")){
				tvLevel2.push({text:item.permissionGUI+" ["+item.permissionName+"]"});
			}else{
				tvLevel1["nodes"] = tvLevel2;
				rootNode.push(tvLevel1);
				tvLevel2 = [];
				tvLevel1 = {};
				permissionGroup = item.permissionGroup+" [Group]";
				tvLevel1["text"] = permissionGroup;
				tvLevel2.push({text:item.permissionGUI+" ["+item.permissionName+"]"});
			}
		});
		tvLevel1["nodes"] = tvLevel2;
		rootNode.push(tvLevel1);
		return rootNode;
	}
	/////////////////////////
	//get roleDetailInfo from backend by roleCode
	function getRoleDetailInfo(roleInfo) {
		globalWriteConsoleLog("in getRoleDetailInfo "+roleInfo.roleCode);
        $.ajax({
            type: "POST",
            url: "/xFace/rest/userManage/role/getRoleDetailInfoList",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({"roleCode":roleInfo.roleCode}),
            dataType: "json",
            success: function(roleDetailInfo) {
            	globalIsSessionExpire(roleDetailInfo);
            	bindRoleDetailInfoWithPermission(roleDetailInfo);            	
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
	////////////////////////////////
	//make check UI by roleDetailInfo return from backend
	function bindRoleDetailInfoWithPermission(roleDetailInfo){
		//bind roleDetailInfo with permission 
		globalWriteConsoleLog("in bindRoleDetailInfoWithPermission");				
		var nodeName = null;
		var permissionCode = null;
		var index1 = 0;
		var index2 = 0;
		var nodeId = 0;
		var parentNodeId = -1;
		var childCheckAll = true;
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
				if (childCheckAll===true){
					if (parentNodeId!=-1){
						//all child node is check then check parent
						tvPermission.treeview("checkNode", [ parseInt(parentNodeId, 10), { silent: true } ]);
					}					
				}
				parentNodeId = $(this).attr("data-nodeid");
				permissionCode = null;								
				childCheckAll = true;
			}
			if (permissionCode!=null){
				//check with permission from roleDetailInfo								
				if (isPermissionBelongToRoleDetailInfo(permissionCode,roleDetailInfo)===true){
					nodeId = $(this).attr("data-nodeid");
					tvPermission.treeview("checkNode", [ parseInt(nodeId, 10), { silent: true } ]);
				}else if (childCheckAll===true){
					childCheckAll = false;
				}
			}
		});
		if (childCheckAll===true){
			if (parentNodeId!=-1){
				//all child node is check then check parent
				tvPermission.treeview("checkNode", [ parseInt(parentNodeId, 10), { silent: true } ]);
			}					
		}
		globalWriteConsoleLog("out bindRoleDetailInfoWithPermission");
	}
	////////////////////////////
	//is permission code below to roleDetailInfo
	//return true if Yes, return false if No
	function isPermissionBelongToRoleDetailInfo(permission, roleDetailInfo){
		globalWriteConsoleLog("in isPermissionBelongToRoleDetailInfo");
		var returnValue = false;
		$.each(roleDetailInfo, function(index,item) {
			globalWriteConsoleLog("item:"+item.permissionList.permissionName+" permission:"+permission);
			if (permission===item.permissionList.permissionName){				
				returnValue = true;
			}
		});		
		globalWriteConsoleLog("out isPermissionBelongToRoleDetailInfo return "+returnValue);
		return returnValue;
	}
	/////////////////////////////
	//handle root checkbox action ("checkNode", "uncheckNode")	
	function handleTreeViewCheckBoxAction(action, actionNode){
		var nodeName = $(actionNode)[0].text;
		if (nodeName.indexOf("[Group]") > -1){
			//root node
			$(actionNode.nodes).each(function(){			
				tvPermission.treeview(action, [ parseInt($(this)[0].nodeId, 10), { silent: true } ]);
			});
		}else if(action==="uncheckNode"){
			//child uncheck then make sure uncheck parent
			var myNode = tvPermission.treeview("getParent", actionNode);			
			tvPermission.treeview(action, [ parseInt(myNode.nodeId, 10), { silent: true } ]);

		}else if(action==="checkNode"){
			var siblingNodes = tvPermission.treeview("getSiblings", actionNode);
			var checkAll = true;
			var myNode = null;
			$(siblingNodes).each(function(){			
				if ($(this)[0].state.checked!==true){
					checkAll = false;
					return false;
				}					
			});
			if (checkAll===true){
				//all child is check then check parent
				myNode = tvPermission.treeview("getParent", actionNode);			
				tvPermission.treeview(action, [ parseInt(myNode.nodeId, 10), { silent: true } ]);
			}
		}
	}
	/////////////////////////////////////
	//updateData
	function updateData(){
		globalWriteConsoleLog("in updateData");
		var validateInput = validateInputData();
		if (!validateInput){
			return;
		}
		var roleInfo = createRoleInfo();
		var roleDetailList = createRoleDetailInfo();
		if (roleDetailList===null){
			globalDisplayError("Please select permission.");
		}else{
			roleInfo["roleDetailInfoList"] = roleDetailList;
			submitUpdateRequest(roleInfo);
		}		
		globalWriteConsoleLog("out updateData");
	}
	///////////////////////////////////////
	//from screen create roleInfo and roleDetailInfo
	function createRoleInfo(){
		var roleInfo = {};
		roleInfo["roleCode"] = $("#txtRoleCode").val();
		roleInfo["roleName"] = $("#txtRoleName").val();
		roleInfo["defaultPage"] = $("#txtDefaultPage").val();
		roleInfo["actionCommand"] = actionCommand;		
		return roleInfo;
	}
	/////////////////////////////////////
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
	///////////////////////////////////////
	//submit request to web service
	function submitUpdateRequest(updateInfo){				
		globalWriteConsoleLog("in submitUpdateRequest(updateInfo)");
		globalWriteConsoleLog(updateInfo);
		$.ajax({
            url: "/xFace/rest/userManage/role/updateRoleInfo",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(updateInfo),
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
	/////////////////////////////////////////
	//delete data
	function submitDeleteRequest(deleteCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(deleteCode, confirmDeleteModal)");
		globalWriteConsoleLog(deleteCode);
		$.ajax({
            url: "/xFace/rest/userManage/role/deleteRoleInfo",
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
	//handle button check all
	$("#btnCheckAll").on("click", function(){
		tvPermission.treeview("checkAll", { silent: true });		
	});
	//handle button check all
	$("#btnUnCheckAll").on("click", function(){
		tvPermission.treeview("uncheckAll", { silent: true });			
	});			
	//handle button save click
	$("#btnSave").on("click", function(event){
		event.preventDefault();
		updateData();		
	});
	$("#btnAdd").on("click", function(event){
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
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {
        var confirmDelete = $(e.delegateTarget);        
        var deleteRoleCode = $(this).data("roleCode");
        globalWriteConsoleLog("delete role code:"+deleteRoleCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({roleCode: deleteRoleCode}, confirmDeleteModal);
    });
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var roleCode = $("#btnDelete").attr("data-role-code");
        $(".title", this).text(roleCode);
        $(".btn-ok", this).data("roleCode", roleCode);        
    });
});