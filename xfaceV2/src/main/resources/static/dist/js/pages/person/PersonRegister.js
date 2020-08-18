$(document).ready( function () {	
	var dataTable = $("#data-table");	
	var rowClick = null;	
	var tvPermission = $("#treeview-permission");
	var actionCommand = "ADD"; //ADD, EDIT
	var confirmDeleteModal = null;
	var dtpSearchRegisterDate = $("#dtpSearchRegisterDate")
	var formModal = null;	
	var defaultTitleCode = null, defaultCertificateTypeCode = null, defaultCategoryCode = null, defaultNationalityCode;
	globalImageLoader =  $("#imageLoader");
	formModal = $("#formModal");
	dtpSearchRegisterDate.datetimepicker({
		format: "dd/mm/yyyy",
		todayBtn:  true,
		autoclose: true,
		clearBtn: true,
		startView: 2,
        minView: 2
	});		
	//clear dtpSaerchRegisterDate have to do 2 step which clear view and date of _setDate function
	if (paramRegisterDate===null){
		dtpSearchRegisterDate.data("datetimepicker")._setDate(null, "view");
		dtpSearchRegisterDate.data("datetimepicker")._setDate(null, "date");
	}else{		
		dtpSearchRegisterDate.data("datetimepicker").setDate(globalStringYYYYMMDDToDate(paramRegisterDate));		
	}	
	$(".datetimepicker-dropdown-bottom-right .clear").on("click", function(e) {
		dtpSearchRegisterDate.data("datetimepicker")._setDate(null, "view");
		dtpSearchRegisterDate.data("datetimepicker")._setDate(null, "date");		
	})
	loadNationalityList(paramNationalityCodeList);
	loadTitleList();
	loadCertificateTypeList();
	loadCategoryList();		
	dataTable.DataTable( {
		"processing": true,
        "serverSide": true,
        "searching": false,
        "aLengthMenu": globalTableStep,
        "ajax": {
            "url": "/xFace/rest/person/getPersonInfoList",
            "dataType": "json",
            "contentType" : "application/json; charset=utf-8",            
            "type": "POST",            
            "paging": true,                       
            "data": function (data) {
            	data.certificateNo = $("#txtSearchCertificateNo").val();
            	data.fullName = $("#txtSearchFullName").val();
            	data.nationalityCodeList = paramNationalityCodeList===null?globalArrayToString($("#cmbSearchNationality").val()):paramNationalityCodeList;
            	data.personRegisterDate = (dtpSearchRegisterDate.data("datetimepicker").getDate()===null?"":dtpSearchRegisterDate.data("datetimepicker").getDate().YYYYMMDD());
                return JSON.stringify(data); // NOTE: you also need to stringify POST payload
            },
            "error": function (error) {            	
            	paramNationalityCodeList = null;
            	paramRegisterDate = null;
            	globalShowGrowlNotification($("#errorDialogTitle").text(), $("#errorGetDataFromServer").text(), "danger", error);            	
            }, 
            "complete": function(data, status) {
            	globalClearLoader();
            	paramNationalityCodeList = null;
            	paramRegisterDate = null;
            }           
        },
        "columns": [
            { "data": "personId", "name":"person_id"},
            { "data": "certificateNo", "name":"certificate_no"},
            { "data": "fullName", "name":"full_name"},
            { "data": "nationality.nationalityName","name" : "nat.nationality_name"},             
            { "data": "personCode", "name":"person_code"},
            { "data": "hwPeopleId", "name":"hw_people_id"}
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
    	//$("#imgUploadPerview").attr("src","/xFace/dist/img/noimage.gif");
    	var result = false;
    	if ($("#txtCertificateNo").val()===""){
    		globalDisplayError("Please enter certificate no");    		
    	}else if ($("#txtFullName").val()===""){
			globalDisplayError("Please enter full name");			
		}else if ($("#btnUploadFile").prop("files").length === 0 && actionCommand==="ADD"){
			globalDisplayError("Please select person photo");			
		}else{
			result = true;
		}
		return result;
    }
	/////////////////////
	//show dialog add/edit
	function showDialog(objectData) { 
		globalWriteConsoleLog("in showDialog");
		globalRemoveError();
		globalAddLoader();				
    	if (objectData === null) {
    		// Add
    		actionCommand = "ADD";
    		$("#txtCertificateNo").val("");
    		$("#txtCertificateNo").removeAttr("disabled");
    		$("#txtFullName").val("");
    		$("#txtPersonCode").val("");
    		$("#txtPeopleId").val("");
    		$("#txtAddressInfo").val("");
    		$("#lblUploadFile").val("");
    		$("#imgUploadPerview").attr("src","/xFace/dist/img/noimage.gif");    		
    		$("#cmbTitle").selectpicker("val", defaultTitleCode);    		
    		$("#cmbCertificateType").selectpicker("val", defaultCertificateTypeCode);    		    	
    		$("#cmbCategory").selectpicker("val", defaultCategoryCode);
    		$("#cmbNationality").selectpicker("val", defaultNationalityCode);
    		$(".selectpicker").selectpicker("refresh");
    		$("#btnUploadFile").val(null);
    		globalClearLoader();
    		formModal.modal("show");
    	}  else {    		
    		// Edit
    		actionCommand = "EDIT";    		
    		loadPersonInfo(objectData.certificateNo);
    	}    	     	
    	globalWriteConsoleLog("out showDialog");
    }
	/////////////////////////////////////
	//get personInfo
	function loadPersonInfo(certificateCode){
		$.ajax({
	        url: "/xFace/rest/person/getPersonInfo",
	        type: "POST",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 
	        data: JSON.stringify({"personCertificateNo":certificateCode}),
	        success: function(personInfo) {
	        	globalIsSessionExpire(personInfo);
	        	globalWriteConsoleLog(personInfo);
	        	bindPersonInfo(personInfo);            	
	        },
	        error: function(error){            		        	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Person Certificate]", "danger", error);            	
	        }
	    });
	}
	/////////////////////////////////////
	// bind person Info
	function bindPersonInfo(personInfo){		
		$("#txtCertificateNo").val(personInfo.certificateNo);
		$("#txtCertificateNo").attr("disabled", "disabled");
		$("#txtFullName").val(personInfo.fullName);
		$("#txtPersonCode").val(personInfo.personCode);
		$("#txtPeopleId").val(personInfo.hwPeopleId);
		$("#txtAddressInfo").val(personInfo.addressInfo);
		$("#imgUploadPerview").attr("src",personInfo.personPhoto);
		$("#lblUploadFile").val("");							
		$("#cmbTitle").selectpicker("val", personInfo.personTitle.titleCode);    		
		$("#cmbCertificateType").selectpicker("val", personInfo.personCertificate.certificateCode);    		    	
		$("#cmbCategory").selectpicker("val", personInfo.personCategory.categoryCode);
		$("#cmbNationality").selectpicker("val", personInfo.nationality.nationalityCode);
		$(".selectpicker").selectpicker("refresh");
		globalClearLoader();
		formModal.modal("show");
	}
	/////////////////////////////////////
	//get list of certificate type
	function loadCertificateTypeList(){
		$.ajax({
            url: "/xFace/rest/cfg/getAllPersonCertificate",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json", 
            success: function(certificateTypeList) {
            	globalIsSessionExpire(certificateTypeList);
            	globalWriteConsoleLog(certificateTypeList);
            	bindCertificateTypeList(certificateTypeList);            	
            },
            error: function(error){            	
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Person Certificate]", "danger", error);            	
            }
        });
	}
	/////////////////////////////////////
	// bindCertificateTypeList
	function bindCertificateTypeList(certificateTypeList){
		var cmbCertificateType = $("#cmbCertificateType");
		var options = [];
		$.each(certificateTypeList, function(i, item){		
			if (defaultCertificateTypeCode===null){
				defaultCertificateTypeCode = item.certificateCode; 
			}
			options.push("<option value='"+item.certificateCode+"'>"+item.certificateName+"</option>");			
		});
		//show dialog
		cmbCertificateType.html(options);
		cmbCertificateType.selectpicker("refresh");
	}
	
	/////////////////////////////////////
	//get list of title
	function loadTitleList(){
		$.ajax({
            url: "/xFace/rest/cfg/getAllPersonTitle",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json", 
            success: function(titleList) {
            	globalIsSessionExpire(titleList);
            	globalWriteConsoleLog(titleList);
            	bindTitleList(titleList);            	
            },
            error: function(error){            	
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Person Title]", "danger", error);            	
            }
        });
	}
	/////////////////////////////////////
	// bindTitleList
	function bindTitleList(titleList){
		var cmbTitle = $("#cmbTitle");	
		var options = [];
		$.each(titleList, function(i, item){			
			if (defaultTitleCode===null){
				defaultTitleCode = item.titleCode;
			}
			options.push("<option value='"+item.titleCode+"'>"+item.titleName+"</option>");			
		});
		//show dialog
		cmbTitle.html(options);				
		cmbTitle.selectpicker("refresh");		    	
	}
	
	/////////////////////////////////////
	//get list of category
	function loadCategoryList(){
		$.ajax({
            url: "/xFace/rest/cfg/getAllPersonCategory",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            dataType: "json", 
            success: function(categoryList) {
            	globalIsSessionExpire(categoryList);
            	globalWriteConsoleLog(categoryList);
            	bindCategoryList(categoryList);            	
            },
            error: function(error){            	
            	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Person Category]", "danger", error);            	
            }
        });
	}
	/////////////////////////////////////
	// bindCategoryList
	function bindCategoryList(categoryList){
		var cmbCategory = $("#cmbCategory");
		var options = [];
		$.each(categoryList, function(i, item){		
			if (defaultCategoryCode===null){
				defaultCategoryCode = item.categoryCode;
			}
			options.push("<option value='"+item.categoryCode+"'>"+item.categoryName+"</option>");
		});
		cmbCategory.html(options);					
		cmbCategory.selectpicker("refresh");		    	
	}	
	/////////////////////////////////////
	//get personNationality
	function loadNationalityList(nationalityCodeList){
		$.ajax({
			url: "/xFace/rest/cfg/getAllPersonNationality",
	        contentType: "application/json; charset=utf-8",
	        dataType: "json", 
	        success: function(nationalityList) {
	        	globalIsSessionExpire(nationalityList);
	        	globalWriteConsoleLog(nationalityList);
	        	bindSearchNationality(nationalityList, nationalityCodeList);    
	        	bindNationality(nationalityList);
	        },
	        error: function(error){            	
	        	globalShowGrowlNotification($("#errorDialogTitle").text(),$("#errorGetDataFromServer").text()+"[Person Certificate]", "danger", error);            	
	        }
	    });
	}
	/////////////////////////////////////
	// bindSearchNationality
	function bindSearchNationality(nationalityList, selectedNationalityList){
		var cmbSearchNationality = $("#cmbSearchNationality");		
		var options = [];
		$.each(nationalityList, function(i, item){
			if (defaultNationalityCode===null){
				defaultNationalityCode = item.nationalityCode;
			}
			options.push("<option value='"+item.nationalityCode+"'>"+item.nationalityName+"</option>");			
		});
		cmbSearchNationality.html(options);
		if (selectedNationalityList!==null){
			cmbSearchNationality.selectpicker("val", selectedNationalityList.split(","));			
		}			
		cmbSearchNationality.selectpicker("refresh");		    	
	}
	/////////////////////////////////////
	// bindNationality (popup from)
	function bindNationality(nationalityList){
		var cmbNationality = $("#cmbNationality");		
		var options = [];
		$.each(nationalityList, function(i, item){			
			options.push("<option value='"+item.nationalityCode+"'>"+item.nationalityName+"</option>");			
		});
		cmbNationality.html(options);
		cmbNationality.selectpicker("refresh");		    	
	}	
	/////////////////////////////////////
	// bindModalPersonNationalityList
	function bindModalPersonNationalityList(nationalityList, nationalityCode){
//		var cmbCategory = $("#cmb");
//		var foundCategory = false;
//		var options = [];
//		$.each(nationalityList, function(i, item){			
//			options.push("<option value='"+item.nationalityCode+"'>"+item.nationalityName+"</option>");
//			if (item.categoryCode===categoryCode){
//				foundCategory = true;
//			}
//		});
//		cmbCategory.html(options);
//		if (foundCategory){
//			cmbCategory.selectpicker("val", categoryCode);			
//		}			
//		cmbCategory.selectpicker("refresh");		    	
	}
	//updateData
	function updateData(){
		globalWriteConsoleLog("in updateData");
		var validateInput = validateInputData();
		if (!validateInput){
			return;
		}
		var personInfo = createPersonInfo();
		submitUpdateRequest(personInfo);			
		globalWriteConsoleLog("out updateData");
	}
	/////////////////////////////////////////
	//create personInfo
	function createPersonInfo(){
		var personInfo = {};
		personInfo["personCode"] = $("#txtPersonCode").val();
		personInfo["personTitleCode"] = $("#cmbTitle").val();
		personInfo["personCertificateCode"] = $("#cmbCertificateType").val();
		personInfo["certificateNo"] = $("#txtCertificateNo").val();
		personInfo["fullName"] = $("#txtFullName").val();
		personInfo["addressInfo"] = $("#txtAddressInfo").val();
		personInfo["personCategoryCode"] = $("#cmbCategory").val();
		personInfo["personNationalityCode"] = $("#cmbNationality").val();
		personInfo["actionCommand"] = actionCommand;	
		data = new FormData();
        data.append("personInfo", JSON.stringify(personInfo));                
        data.append("personPhoto", $('#btnUploadFile').prop("files")[0]);	        
		return data;
	}
	///////////////////////////////////////
	//submit request to web service
	function submitUpdateRequest(updateInfo){				
		globalWriteConsoleLog("in submitUpdateRequest(updateInfo)");
		globalWriteConsoleLog(updateInfo);				
		$.ajax({
            url: "/xFace/rest/person/updatePersonInfo",
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
	/////////////////////////////////////////
	//delete data
	function submitDeleteRequest(deleteCode, confirmDeleteModal){				
		globalWriteConsoleLog("in submitDeleteRequest(deleteCode, confirmDeleteModal):"+deleteCode);		
		$.ajax({
            url: "/xFace/rest/person/deletePersonInfo",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(deleteCode),
            success: function(result) {
            	globalIsSessionExpire(result);
            	globalClearLoader();
            	if (result.statusCode==="0"){            		            		
            		globalShowGrowlNotification($("#successDialogTitle").text(), $("#successDeleteRecord").text(), "success");
            		dataTable.DataTable().ajax.reload(null, false);              		
            	}else{
            		globalShowGrowlNotification($("#errorDialogTitle").text(), result.statusDesc, "danger");
            	}
            },
            error: function(error){            	
            	globalShowGrowlNotification($("#errorDialogTitle").text(), $("#errorDeleteRecord").text(), "danger", error);
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
	$("#btnSearch").on("click", function(event){		
		globalAddLoader();
		if ($("#txtSearchCertificateNo").val()!==""){
			$("#txtSearchFullName").val("");
		}
		dataTable.DataTable().ajax.reload();		
	});
	$("#btnDelete").on("click", function(event){		
		var data = dataTable.DataTable().row(".selected").data();	
		if (data==null){			
			event.preventDefault();
			globalShowGrowlNotification($("#errorDialogTitle").text(), $("#errorSelectGridData").text(), "danger");
			return false;
		}else{
			$(this).attr("data-code", data.certificateNo);
		}		
	});
	
	$("#confirm-delete").on("click", ".btn-ok", function(e) {		
        var confirmDelete = $(e.delegateTarget);        
        var deleteCode = $(this).data("deleteCode");
        globalWriteConsoleLog("delete person certificate no:"+deleteCode);
        globalAddLoader();
        confirmDelete.modal("hide");
		submitDeleteRequest({personCertificateNo: deleteCode}, confirmDeleteModal);
    });
    $("#confirm-delete").on("show.bs.modal", function(e) {
        var deleteCode = $("#btnDelete").attr("data-code");
        $(".title", this).text(deleteCode);
        $(".btn-ok", this).data("deleteCode", deleteCode);            	
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
//$(document).ajaxComplete(function( event, request, settings ) {
//	if (settings.url==="/xFace/rest/cfg/getAllPersonTitle"){
//		loadTitleComplete = true;
//		loadTitleSuccess = true;
//	}
//	if (settings.url==="/xFace/rest/cfg/getAllPersonCertificate"){
//		loadCertificateTypeComplete = true;
//		loadCertificateTypeSuccess = true;
//	}
//	if (settings.url==="/xFace/rest/cfg/getAllPersonCategory"){
//		loadCategoryComplete = true;
//		loadCategorySuccess = true;
//	}
//	finalShowMsgDialog();
//});
//$(document).ajaxError(function( event, request, settings ) {
//	if (settings.url==="/xFace/rest/cfg/getAllPersonTitle"){
//		loadTitleComplete = true;
//	}
//	if (settings.url==="/xFace/rest/cfg/getAllPersonCertificate"){
//		loadCertificateTypeComplete = true;
//	}
//	if (settings.url==="/xFace/rest/cfg/getAllPersonCategory"){
//		loadCategoryComplete = true;
//	}
//	finalShowMsgDialog();
//});	
