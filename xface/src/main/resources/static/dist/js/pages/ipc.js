  $(document).ready( function () { 
	  var grid = $("#data-table");
	  var sortCol = "ipcName";
	  var sortOrder = "desc";
	  var currentPage = 1;
	  var totalPages = 0;
	  
	  grid.jqGrid({
		   	url: '/rest/getHWIPCInfoList?page=0&sort=ipcName,desc',
		   	editurl: "/rest/updateHWIPCInfo", 
		    mtype: 'GET', //insert data from the data object we created above 		   	
			datatype: 'json',
			reloadGridOptions: { fromServer: true },
		   	colNames:['ID','IPC Code', 'IPC IP', 'IPC Name', 'VCN IP', 'Area', 'Receive Alarm', 'Created Date', 'Updated Date'],
		   	colModel:[
		   		{name:'ipcId', index: 'ipcId', width:25, align: 'center', sortable: true, editable: false},
		   		{name:'ipcCode', index: 'ipcCode',  width:50, sortable: true, key: true, editable: true, editrules:{required: true} },
		   		{name:'ipcIP', index: 'ipcIP', width:50, sortable: true, editable: true, editrules:{required: true} },
		   		{name:'ipcName', index: 'ipcName', width:50, sortable: true, editable: true, editrules:{required: true} },
		   		{
		   			name:'vcnInfo.vcnIP', index: 'vcnIP', width:80, align: 'center', editable: true, editrules:{required: true}, 
		   			edittype: 'select', formatter: 'select',
		   			editoptions: { 
		   				dataUrl: '/rest/getAllVcn',
		   				buildSelect: function (response) {
		   				 //var data = typeof response === "string" ? JSON.stringify(response) : response,
		   				 var data = JSON.parse(response);
		   				 s = "<select>";
		   				 $.each(data, function () {
		   				 s += "<option value='" + this.roleId + "'>" + this.roleName + "</option>";
		   				 });
		   				 return s + "</select>";
		   				 }
		   				},
		   		},	
		   		{
		   			name:'areaInfo.areaName', index: 'areaName', width:80, align: 'center', editable: true, formatter:'select',
		   			edittype: 'select', formatter: 'select',
		   			editoptions: { 
		   				dataUrl: '/rest/getAllArea',
		   				buildSelect: function (response) {
		   				 //var data = typeof response === "string" ? JSON.stringify(response) : response,
		   				 var data = JSON.parse(response);
		   				 s = "<select>";
		   				 s += "<option value='0'>–Select–</option>";
		   				 $.each(data, function () {
		   				 s += "<option value='" + this.ipcgId + "'>" + this.groupName + "</option>";
		   				 });
		   				 return s + "</select>";
		   				 }
		   				},
		   		},		   		
		   		{
		   			name:'receiveAlarm', index: 'receiveAlarm', width:25, align: 'center', 
		   			editable: true, edittype:'checkbox', editoptions: { value:"True:False"}, 
		   			formatter: "checkbox", formatoptions: {disabled : true}
		   		},
		   		{name:'createdDate', index: 'createdDate', width:50, sortable: true, editable: false },
		   		{name:'updatedDate', index: 'updatedDate', width:50, sortable: true, editable: false },		   		
		   	],
		   	jsonReader : {
				root: "content",
				total: "totalPages",
				records: "totalElements",
				page: currentPage,
		    },		      
		   	rowNum:20,
		   	autowidth: true,
		   	pager: '#pager',
		    viewrecords: true,
		    onPaging : function(pgButton) {
		    	if (pgButton == 'prev') {
		    		currentPage -= 1;
		    	} else if (pgButton == 'next') {
		    		currentPage += 1;
		    	} else if (pgButton == 'first') {
		    		currentPage = 1;
		    	} else if (pgButton == 'last') {
		    		currentPage = totalPages;
		    	}
		    	
		    	reloadGrid();
		    },		
			onSortCol: function(index, iCol, sortorder)
			{
				sortCol = index;
				sortOrder = sortorder;

				reloadGrid();
			},
		    loadComplete: function(data) {
		        totalPages = data.totalPages;
		    },			
		}).navGrid('#pager',{ 
			 edit:true, edittitle: "Edit IPC", width: 500,
			 add:true, addtitle: "Add IPC", width: 500,
			 del:true,
			 view:false,
			 search: false,
			 refresh: true,
		     beforeRefresh: function () {
		         $(this).jqGrid("setGridParam",{datatype: "json"});
		     }			 
		},
			{
			    //edit parameters
			    ajaxEditOptions: jsonOptions,
			    serializeEditData: createJSON,
			    closeAfterEdit: true,
			    beforeShowForm: function ($form) {			    	
				    setTimeout(function () {
				        $form.closest(".ui-jqdialog").closest(".ui-jqdialog").position({
				            my: 'center',
				            at: 'center',
				            of: window
				        });
				    }, 50);			    	
			    }
			},
			{
			    //add parameters
			    ajaxEditOptions: jsonOptions,
			    serializeEditData: createJSON,
			    closeAfterAdd: true,
			    beforeShowForm: function ($form) {
				    setTimeout(function () {
				        $form.closest(".ui-jqdialog").closest(".ui-jqdialog").position({
				            my: 'center',
				            at: 'center',
				            of: window
				        });
				    }, 50);			    	
			    }			    
			 },
			 {
			     //delete parameters
				 url: '/rest/deleteHWIPCInfo',
				 ajaxEditOptions: jsonOptions,
			     serializeDelData: createJSON,
			    beforeShowForm: function ($form) {
				    setTimeout(function () {
				        $form.closest(".ui-jqdialog").closest(".ui-jqdialog").position({
				            my: 'center',
				            at: 'center',
				            of: window
				        });
				    }, 50);			    	
			    }			     
			 }			 
		);	 
	  	  
	  $.extend(true, $.jgrid.edit, {
		    recreateForm: true,
		    ajaxEditOptions: { contentType: "application/json" },
		});	 
	  
	  var jsonOptions = {
			    type :"POST",
			    contentType :"application/json; charset=utf-8",
			    dataType :"json"
			};

	function createJSON(postdata) {
	    if (postdata.id === '_empty')
	        postdata.id = null; // rest api expects int or null
	    
		if (postdata.oper == 'edit' || postdata.oper == 'add') {
	    	if (postdata.enabled == 'True') {
	    		postdata.enabled = 1;
	    	} else {
	    		postdata.enabled = 0;
	    	}
	    	    	
	    	var roleId = postdata['roleInfo.roleName'];
	    	var ipcgId = postdata['ipcGroup.groupName'];
	    	
	    	if (ipcgId == 0) {
	    		ipcgId = null;
	    	}
	    	
	    	postdata.roleInfo = { "roleId": roleId };
	    	postdata.ipcGroup = { "ipcgId": ipcgId };
	    	
	    	return JSON.stringify(postdata);
		} else if (postdata.oper == 'del') {
			return postdata.id;
		}    
	}	  
	
    function reloadGrid() {
        $("#data-table").setGridParam(
        		{
        			page: currentPage - 1,
        			url: '/rest/getHWIPCInfoList?page=' + (currentPage - 1) + '&sort=' + sortCol + ',' + sortOrder, 
        }).trigger("reloadGrid") 
    }	
});    