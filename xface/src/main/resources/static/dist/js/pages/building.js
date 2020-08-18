$(document).ready( function () { 
	  var grid = $("#data-table");
	  var sortCol = "buildingId";
	  var sortOrder = "desc";
	  var currentPage = 1;
	  var totalPages = 0;    
	  grid.jqGrid({
		  url:'/rest/getAllLocationBuilding',  
		  mtype: 'GET', //insert data from the data object we created above 		   	
			datatype: 'json',
			reloadGridOptions: { fromServer: true },
			sortname: 'buildingId',
			sortorder: 'desc',
			 view:true,
			 search: true,
			 refresh: true,
		   	colNames:['Building ID','Building Name', 'Location', 'Description','Total Floors','User Updated','User Created'],
		   	colModel:[
		   		{name:'buildingId', index: 'building_id', width:30, align: 'center', sortable: true, editable: false},
		   		{name:'buildingName', index: 'buildingName',  width:100, sortable: true, key: true, editable: true, editrules:{required: true} },
		   		{name:'location', index: 'localtion', width:100, sortable: true, editable: true, editrules:{required: true} },
		   		{name:'buildingDesc', index: 'buildingDesc', width:100, sortable: true, editable: true, editrules:{required: true} },
		   		{name:'numberOfFloors', index: 'numberOfFloors', width:100, sortable: true, editable: true, editrules:{required: true} },
		   		{name:'userUpdated', index: 'userUpdated', width:100, sortable: true, editable: true, editrules:{required: true} },
		   		{name:'userCreated', index: 'userCreated', width:100, sortable: true, editable: true, editrules:{required: true} },	
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
				 	showDialog(null);
			 	 },
				 position:"first"
		}); 
	  function reloadGrid() {
	        $("#data-table").setGridParam(
	         {
	        	page: currentPage - 1,
	            url: '/rest/getAllLocationBuilding?page=' + (currentPage - 1) + '&sort=' + sortCol + ',' + sortOrder, 
	        }).trigger("reloadGrid")          
	    }
	  
	  function showDialog(data) {
	    	var form = $('#formModal');	
	    	if (data != null) {
	    		$('#txt_buildingName').val(data);
	    		// Edit
	    		console.log(data);
	    var json = { "buildingName": data }
	    		getBuilding(json);
	    	}else{
	    		// AddNew
	    		$('#txt_buildingName').val('');
	    	}
	    	form.modal('show');
	    }
	          
	 $('#btn_save').on('click', function(event) {
	     event.preventDefault();       
	        var form = $('#formModal');
	        var x = $('#txt_buildingName').val();
	        var y = $('#txt_location').val();
	        var z = $('#txt_buildingDesc').val();
	         
	         if ( x == null || y == null || z == null ){
	    	    $('#formModal').modal('show');
	         }else{
	    	  var buildingName = $('#txt_buildingName').val();
	   	      var location = $('#txt_location').val();
	   	      var buildingDesc = $('#txt_buildingDesc').val();
	   	      var numberOfFloors = $('#txt_floors').val();
	   	      //create JSON Objejct
	   	      var data = {
	   	            "buildingName": buildingName,
	   	            "location": location,
	   	            "buildingDesc": buildingDesc,
	   	            "numberOfFloors" : numberOfFloors
	   	        };
	   	      var dataObj = JSON.stringify(data); 
		        	 update(dataObj);
	    	}
	    });    
	    
	    function update(dataObj) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/updateLocationBuilding",
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            data: dataObj,
	            success: function(result) {
	            	console.log(result);
	            	if(result.statusCode == "-2"){
	            		 $("#div8").empty();
	            		 $("#div8").append("<font color=red> <b>Can't Save data not complete</b> </font>"); 
	            	}          	
	            	if (result.statusCode == "0"){
	            	     reloadGrid(result);
	            	     $('#formModal').modal('hide');
	            	}
	            },
	            error: function(result) {
	            	console.log(result);
	            }
	        }); 
	    }
	    
	    function getBuilding(json) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/getLocationBuilding",
	            data: JSON.stringify(json),
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            success: function(result) {
	            console.log(result);
	            	 $('#txt_buildingName').val(result.buildingName);
	    	         $('#txt_location').val(result.location);
	    	         $('#txt_buildingDesc').val(result.buildingDesc);
	    	         $('#txt_floors').val(result.numberOfFloors); 		
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
				  console.log(gsr);
				  if(gsr){
					  console.log('delete row:' + gsr);
					  deleteData({ 'buildingName': gsr });
					  
				  } else {
					  alert("Please select Row")
				  }   			
	  			$('#confirm').modal('hide');
	  		});     	
	    }
	    
	    function deleteData(json) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/deleteLocationBuilding",
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
	
});//End Document 
	    
