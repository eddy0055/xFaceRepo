$(document).ready( function () { 
	  var grid = $("#data-table");
	  var sortCol = "userName";
	  var sortOrder = "desc";
	  var currentPage = 1;
	  var totalPages = 0;
	    
	  grid.jqGrid({
		   	url: '/rest/getUserInfoList?page=0&sort=userName,desc',
		   	editurl: "/rest/updateUserInfo", 
		    mtype: 'GET', //insert data from the data object we created above 		   	
			datatype: 'json',
			reloadGridOptions: { fromServer: true },
			sortname: 'userName',
			sortorder: 'desc',
			 view:true,
			 search: true,
			 refresh: true,
		   	colNames:['ID','Username', 'First Name', 'Last Name', 'Password', 'Role Name', 'IPC Group Name', 'Enabled'],
		   	colModel:[
		   		{name:'userId', index: 'userId', width:30, align: 'center', sortable: true, editable: false},
		   		{name:'userName', index: 'userName',  width:100, sortable: true, key: true, editable: true, editrules:{required: true} },
		   		{name:'firstName', index: 'firstName', width:100, sortable: true, editable: true, editrules:{required: true} },
		   		{name:'lastName', index: 'lastName', width:100, sortable: true, editable: true, editrules:{required: true} },
		   		{name:'password', width:30, hidden: true, edittype: 'password', editable: true, editrules:{edithidden:true, required: true} },
		   		{
		   			name:'roleInfo.roleName', index: 'roleName', width:100, align: 'center'
		   		},	
		   		{
		   			name:'ipcGroup.groupName', index: 'groupName', width:100, align: 'center'
		   		},		   		
		   		{
		   			name:'enabled', index: 'enabled', width:40, align: 'center', 
		   			editable: true, edittype:'checkbox', editoptions: { value:"True:False"}, 
		   			formatter: "checkbox", formatoptions: {disabled : true}
		   		},
		   	],
		   	jsonReader : {
				root: "content",
				total: "totalPages",
				records: "totalElements",
				page: currentPage,
		    },		      
		   	rowNum:10,
		   	autowidth: true,
		   	shrinkToFit: true,
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
			 edit:false, edittitle: "Edit Role", width: 500,
			 add:false, addtitle: "Add Role", width: 500,
			 del:false,
			 view:false,
			 search: false,
			 refresh: true,
		}).navButtonAdd('#pager',{
			  caption:"Del",  
			  onClickButton: function(id){ 			 
				  	confirmDelete();
				  }, 
				  position:"first"			 
		}).navButtonAdd('#pager',{
			  caption:"Edit",  
			  onClickButton: function(){
				  //$('#txt_roleName').val('');
				  var gsr = grid.getGridParam('selrow');
				  console.log(gsr);
				  if(gsr){
					  grid.GridToForm(gsr,"#formModal");
				  } else {
					  alert("Please select Row")
				  } 				  
				  	showDialog(gsr);
				  	
				  }, 
				  position:"first"	
					  
		}).navButtonAdd('#pager',{
			 caption:"Add", 
			 onClickButton: function(){
				    $('#txt_roleName').empty();
				    $('#txt_txt_ipcRole').empty();
				    getRoleName();
				    getIpcList();
				 	showDialog(null);
			 	 },
				 position:"first"
	     	}); 
	  	
	  function reloadGrid() {
	        $("#data-table").setGridParam(
	        		{
	        			page: currentPage - 1,
	        			url: '/rest/getUserInfoList?page=' + (currentPage - 1) + '&sort=' + sortCol + ',' + sortOrder, 
	        }).trigger("reloadGrid")          
	    }
	  
	    
	    function showDialog(data) {
	    	var form = $('#formModal');
	    			
	    	if (data != null) {
	    		$('#txt_user').val(data);
	    		// Edit
	    		/*
	    		$('#txt_rolename').val(data);    
	    		$('#txt_user').prop("disabled", true);
	    		$('#txt_firstName').val(data);
	    		$('#txt_lastName').val(data);
	    		$('#txt_password').val(data);
	    		*/
	    		$('#txt_roleName').empty();
	   	        $('#txt_ipcRole').empty();
	   	       
	    	
	    		var json = { "userName": data }
	   	        getUserInfo(json);
	    	}  else {
	    		// AddNew
	    		$('#txt_rolename').val('');
	    
	    	}
	    	
	    	$('#checkedListBox').empty();
	    	//PopulateCheckBoxList(data);
	    	    	    	
	    	form.modal('show');
	    }
	    
	    $('#btn_save').on('click', function(event) {
	        event.preventDefault();        
	         var user = $('#txt_user').val();
	         var firstName = $('#txt_firstName').val();
	         var lastName = $('#txt_lastName').val();
	         var password = $('#txt_password').val();
	         var roleName =  $('#txt_roleName option:selected').val();
	         var ipcRole  =  $('#txt_ipcRole option:selected').val();
	        
	        if ($('input[name=check_en]').is(':checked'))  {
	        	var enable = "Enabled";
	        }else{
	        	// Uncheck
	        	var enable = " ";
	        }
	       
	          var data = {
	        	 "userName":user,"password":password,"firstName":firstName,"lastName":lastName,
	        	 "roleInfo":{"roleId":roleName},
	        	 "ipcGroup":{"ipcgId":ipcRole}
	        	 	
	        	 };
	          
	          console.log(data);
	          var dataObj = JSON.stringify(data); 
	          //document.getElementById("demo").innerHTML = dataObj;
	          
	        update(dataObj);
	        console.log(dataObj);
	    });    
	    
	   
	    
	    function update(dataObj) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/updateUserInfo",
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            data: dataObj,
	            success: function(result) {
	            	if(result.statusCode == "-2"){
	            		 $("#div8").empty();
	            		 $("#div8").append("<font color=black> <b>Can't Save data not complete</b> </font>"); 
	            	}
	            	
	            	if (result.statusCode == "0"){
	            	     reloadGrid(result);
	            	     $('#formModal').modal('hide');
	            	}
	            	reloadGrid(result);
	            },
	            error: function(result) {
	            	console.log(result);
	            }
	        }); 
	    }
	       
	        
	    function getRoleName(){
	    	$.ajax({
    		    type: "GET",
	            url: "/rest/getAllRole",
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            
	            success: function(result) {
	            	$.each(result, function (index, value) {
		                 $('#txt_roleName').append('<option value="' + value.roleId + '">' + value.roleName + '</option>');
		                     console.log(value.roleId);
	            	      });      			
	            },
	            error: function(result) {
	            	console.log(result);
	            }
    	  });
	    }
	    
	    function getIpcList(){
	    	$.ajax({
	    		    type: "GET",
		            url: "/rest/getAllIPCGroup",
		            contentType: "application/json; charset=utf-8",
		            dataType: "json",
		            success: function(result) {
		            	$.each(result, function (index, value) {
			                 $('#txt_ipcRole').append('<option value="' + value.ipcgId + '">' + value.groupName + '</option>');
			                     console.log(value.ipcgId);
		            	      });   
		                 },
		            error: function(result) {
		            	console.log(result);
		            }
	    	});

	    }
	    function getUserInfo(json) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/getUserInfo",
	            data: JSON.stringify(json),
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            success: function(result) {
		    		$('#txt_user').val(result.userName);
		    		$('#txt_user').prop("disabled", true);
		    		$('#txt_firstName').val(result.firstName);
		    		$('#txt_lastName').val(result.lastName);
		    		$('#txt_password').val(result.password);	
		            $('#txt_roleName').append('<option value="' + result.roleInfo.roleId + '">' + result.roleInfo.roleName + '</option>');
		            $('#txt_ipcRole').append('<option value="' + result.ipcGroup.ipcgId + '">' + result.ipcGroup.groupName + '</option>');		
		   	         console.log(result.roleInfo.roleId);
	                 console.log(result.ipcGroup.ipcgId);
	              },
	            error: function(result) {
	            	alert(result);
	            }
	        });    	
	    }	
	    
	    
	    function confirmDelete() {
		  	$('#confirm').modal({ backdrop: 'static', keyboard: false })
	  		.on('click', '#delete-btn', function(){
				  var gsr = grid.getGridParam('selrow');
				  if(gsr){
					  console.log('delete row:' + gsr);
					  deleteData({ 'userName': gsr });
				  } else {
					  alert("Please select Row")
				  }   			
	  			$('#confirm').modal('hide');
	  		});     	
	    }
	    
	    function deleteData(json) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/deleteUserInfo",
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            data: JSON.stringify(json),
	            success: function(result) {
	            	reloadGrid();
	            },
	            error: function(result){
	            	console.log(result);
	            }
	        }); 				  		  	
	    }
	    
}); //end of document ready
	    
