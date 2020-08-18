$(document).ready( function () { 
	  var grid = $("#data-table");
	  var sortCol = "personId";
	  var sortOrder = "desc";
	  var currentPage = 1;
	  var totalPages = 0;
	    
	  grid.jqGrid({
		   	url: '/rest/getAllPersonInfo?page=0&sort=personId,desc', 
		    mtype: 'GET', 		   	
			datatype: 'json',
			reloadGridOptions: { fromServer: true },
			sortname: 'personId',
			sortorder: 'desc',
			 view:true,
			 search: true,
			 refresh: true,
			
		   	colNames:['PersonId','Title','First Name', 'Last Name', 'Certification', 'CertificationNo'],
		   	colModel:[
		   		{name:'personId', index: 'personId', width:60, align: 'center', sortable: true}, 
		   		{name:'personTitle.titleName', index: 'titleId', width:100, align:'center'}, 
		   		{name:'firstName', index: 'firstName', width:100, align: 'center'},	
		   		{name:'lastName', index: 'lastName', width:100, align: 'center'},	
		   		{name:'personCertification.certificationName', index: 'certificationId', width:100, align:'center'}, 
		   		{name:'certificationNo', index: 'certificationNo',key: true, width:100, align:'center'},
		   			   		
		   		
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
		    
      // ----------- Show Only Delete button ------------------	    
	  }).navGrid('#pager',{ 
			 edit:false,
			 add:false,
			 del:false,
			 view:false,
			 search: false,
			 refresh: true,
		}).navButtonAdd('#pager',{
			  caption:"Delete",  
			  onClickButton: function(id){ 			 
				  	confirmDelete();
				  }, 
				  position:"first"			 
		});
					  
		
	  function reloadGrid() {
	        $("#data-table").setGridParam(
	        		{
	        			page: currentPage - 1,
	        			url: '/rest/getAllPersonInfo?page' + (currentPage - 1) + '&sort=' + sortCol + ',' + sortOrder, 
	        }).trigger("reloadGrid")          
	    }
	  
	
	    function confirmDelete() {
		  	$('#confirm').modal({ backdrop: 'static', keyboard: false })
	  		.on('click', '#delete-btn', function(){
				  var gsr = grid.getGridParam('selrow');
				  console.log(gsr);
				  
				  if(gsr){
					  console.log('delete row:' + gsr);
					  //Sent Jason Certification No
					  //{"personCertificationNo": "123456"}
					  deleteData({ 'personCertificationNo': gsr });
				  } else {
					  alert("Please select Row")
				  }   			
	  			$('#confirm').modal('hide');
	  		});     	
	    }
	    
	    function deleteData(json) {
	        $.ajax({
	            type: "POST",
	            url: "/rest/deletePersonInfo",
	            contentType: "application/json; charset=utf-8",
	            dataType: "json",
	            data: JSON.stringify(json),
	            success: function(result) {
	            	//------- Delete Sucess Dialog ------------
	            	$('#Delete-Modal').modal('show');
	            	//------- Reload Grid ---------------------
	            	   reloadGrid();
	            },
	            error: function(result){
	            	//console.log(result);
	            }
	        }); 				  		  	
	    }
	    
}); //end of document ready
	    
