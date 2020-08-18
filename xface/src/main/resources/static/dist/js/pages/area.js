$(document).ready( function () { 
	  var grid = $("#data-table");
	  var sortCol = "areaId";
	  var sortOrder = "desc";
	  var currentPage = 1;
	  var totalPages = 0;
	    
	  grid.jqGrid({
		  url:'/rest/getAllLocationArea',  
		  mtype: 'GET', //insert data from the data object we created above 		   	
			datatype: 'json',
			reloadGridOptions: { fromServer: true },
			sortname: 'areaId',
			sortorder: 'desc',
			 view:true,
			 search: true,
			 refresh: true,
		   	colNames:['Area ID','Area Name', 'Area Desc','Floor Name', 'Floor Id'],
		   	colModel:[
		   		{name:'areaId', index: 'areaId', align: 'center', sortable: false, editable: false},
		   		{name:'areaName', index: 'areaName',align:'center', sortable: true, key: false, editable: true, editrules:{required: true} },
		   		{name:'areaDesc', index: 'areaDesc',align:'center',sortable: true, editable: true, editrules:{required: true} },
		   		{name:'floor.floorName', index: 'floorName',align:'center', sortable: true, editable: true, editrules:{required: true} },
		   		{name:'floor.floorId', index: 'floorId',align:'center', key:false, sortable: true, editable: false,hidden: true},
		   		
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
			 edit:false, edittitle: "Edit Area", width: 500,
			 add:false, addtitle: "Add Area", width: 500,
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
				 $('#txt_floorName').empty();
				    getTower();
				    SelectFloor();
				 	showDialog(null);
			 	 },
				 position:"first"
		}); 
	  function reloadGrid() {
	        $("#data-table").setGridParam(
	        {
	        	page: currentPage - 1,
	            url: '/rest/getAllLocationArea?page=' + (currentPage - 1) + '&sort=' + sortCol + ',' + sortOrder, 
	        }).trigger("reloadGrid")          
	    }
	  
	  function showDialog(data) {
	    	var form = $('#formModal');	
	    	if (data != null) {
	    		$('#txt_areaName').val(data);
	    		// Edit
	    		console.log(data);
	    var json = { "areaName": data }
	    		getBuilding(json);
	    	}else{
	    		// AddNew
	    		$('#txt_areaName').val('');
	    	}
	    	form.modal('show');
	    }
	          
	 $('#btn_save').on('click', function(event) {
	     event.preventDefault();       
	        var form = $('#formModal');
	        var x = $('#txt_areaName').val();
	        var y = $('#txt_areaDesc').val();
	        var z = $('#txt_floorName option:selected').val();
	  
	         if ( x == null || y == null || z == null ){
	    	    $('#formModal').modal('show');
	         }else{
	    	  var areaName = $('#txt_areaName').val();
	   	      var areaDesc = $('#txt_areaDesc').val();
	   	      var floorName = $('#txt_floorName').val();
	   	             
	   	   var data = {
	   	           "areaName": areaName,
	   	           "areaDesc": areaDesc,
	   	           "floor": { "floorId":floorName }  
	   	        };
	   	            
	   	    var dataObj = JSON.stringify(data); 
		          update(dataObj);
	    	}
	    });    
	    
	
	    function update(dataObj) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/updateLocationArea",
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
	            url: "/rest/getLocationArea",
	            data: JSON.stringify(json),
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            success: function(result) {
	
	            	 $('#txt_areaName').val(result.areaName);
	    	         $('#txt_areaDesc').val(result.areaDesc);
	    	         $('#txt_floorName').val(result.floorName);
	    	         	
	            },
	            error: function(result) {
	            	alert(result);
	            }
	        });    	
	    }	
	    
	    
	    function getTower(){
	    	$.ajax({
    		    type: "GET",
	            url: "/rest/getAllLocationBuilding",
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            success: function(result) {
	            	$.each(result, function (index, value) {
		                 $('#txt_buildingName').append('<option value="' + value.buildingName + '">' + value.buildingName + '</option>');
		                     console.log(value.buildingName);
	            	      });      			
	             },
	            error: function(result) {
	            	console.log(result);
	            }
    	  });
	    }
	    
	    
	    
	    
	    function SelectFloor(){
	        $('#txt_buildingName').on('change',function(){
	          $('#txt_floorName').empty(); 
	        var buildingName;  
			var buildingName = $(this).val();
			  console.log(buildingName);
			
			var data = { "buildingName" : buildingName };
			var dataObj = JSON.stringify(data);
			  console.log(dataObj);
			
           	    // run ajax request
           	    $.ajax({
           	         type: "POST",
           	        data : dataObj,
           	          url: "/rest/getLocationFloorByBuildingName",
           	  contentType: "application/json; charset=utf-8",
 	             dataType: "json",
           	        success: function (result) {
           	     	$.each(result.floors, function (index, value) {
		                 $('#txt_floorName').append('<option value="' + value.floorName + '">' + value.floorName + '</option>');
		          
	                });      			
	             },
	            error: function(result) {
	            	console.log(result);
	            }
              });
	        });
	    }
	    
	    
	    
	  
	    
	    function confirmDelete() {
		  	$('#confirm').modal({ backdrop: 'static', keyboard: false })
	  		.on('click', '#delete-btn', function(){
	  			
	  			var gsr = grid.getGridParam('selrow');	
	  			var row = grid.getRowData(gsr);
		    	var arr =[];
		    	
		    	    arr.push("areaName",row["areaName"]);
		    	    arr.push("floorId",row["floor.floorId"]);
		     
		    	var data = { "areaName": row["areaName"], "floorId": row["floor.floorId"] }   
		    	var deleteArr = JSON.parse(JSON.stringify(data));
		    
	  			if(arr){
	  				deleteData(deleteArr);
	  			}else{
	  			   alert("Please select Row");
	  			}
		  		
	  		  $('#confirm').modal('hide');
	  		});     	
	    }
	    
	    function deleteData(json) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/deleteLocationArea",
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
	    	    
});
	    
