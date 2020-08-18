  $(document).ready( function () { 
	  var grid = $("#data-table");
	  var sortCol = "roleName";
	  var sortOrder = "desc";
	  var currentPage = 1;
	  var totalPages = 0;
	 	 	    	  
	  grid.jqGrid({
		   	url: '/rest/getRoleInfoList',
		    mtype: 'GET', //insert data from the data object we created above 		   	
			datatype: 'json',
			reloadGridOptions: { fromServer: true },
			sortname: 'roleName',
			sortorder: 'desc',
		   	colNames:['ID','Role Name', 'Default Page'],
		   	colModel:[
		   		{name:'roleId', index: 'roleId', width:30, align: 'center', sortable: true, editable: false},
		   		{name:'roleName', index: 'roleName',  width:100, sortable: true, key: true, editable: true, editrules:{required: true} },
		   		{name:'defaultPage', index: 'defaultPage', width:100, sortable: true, editable: true, editrules:{required: true} },
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
				 	$('#checkedListBox').empty();
				 	$('#checkedListBox1').empty();
				 	
			 	 },
				 position:"first"
		}); 
	  	
    function reloadGrid() {
        $("#data-table").setGridParam(
        		{
        			page: currentPage - 1,
        			url: '/rest/getRoleInfoList?page=' + (currentPage - 1) + '&sort=' + sortCol + ',' + sortOrder, 
        }).trigger("reloadGrid") 
    }	
    
    function showDialog(data) {
    	var form = $('#formModal');
    	
    	if (data != null) {
    		// Edit
    		$('#txt_rolename').val(data);            
    	}  else {
    		// Add
    		$('#txt_rolename').val('');
    		$('#txt_defaultpage').val('');
    	}
    	
    	$('#checkedListBox').empty();
    	PopulateCheckBoxList(data);
    	    	    	
    	form.modal('show');
    }
    
    function PopulateCheckBoxList(data) {
    	if (data != null) {
    		// Edit mode, get role detail list   	
    		var data = { 'roleName': data };
    		
    		getRoleDetailInfo(data);
            getRoleInfo(data);
    	} else {
    		getPermissionList();
    	}
    }
    
    function ajaxSucceedRole(result) {
    	//$('#txt_defaultpage').val(result.defaultPage);
    	
        $.ajax({
            type: "GET",
            url: "/rest/getAllPermissionList",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(json) {
            	BindCheckBoxList(result, json);
            },
            error: AjaxFailed
        });
    }
    
    function AjaxSucceeded(result) {
        BindCheckBoxList(result);
    }
    
    function AjaxFailed(result) {
        alert(result.responseJSON.message);
    }
    function BindCheckBoxList(result, permissionList) {
        var items = result;
        CreateCheckBoxList(items, permissionList);
    }
    
    


        
    
    function CreateCheckBoxList(checkboxlistItems, permissionList) {
    	//call data permissionGroup
    	$(permissionList).each(function () {
    		if(this.permissionGroup=="ADMIN"){
          	      $('#Group').html('<li class="list-group-item">' + this.permissionGroup + '</li>');	   
    		    }  
    		  if (this.permissionGroup == "ADMIN"){
            	  $('#checkedListBox').append('<li class="list-group-item" data-id="' + this.permissionId + '">' + this.permissionGUI + '</li>');		
                }		
              if(this.permissionGroup == "REPORT"){
             	  $('#Group1').html('<li class="list-group-item">' + this.permissionGroup + '</li>');	
                }
              if(this.permissionGroup == "REPORT"){
            	  $('#checkedListBox1').append('<li class="list-group-item" data-id="' + this.permissionId + '">' + this.permissionGUI + '</li>');
               }
    		
                //$('#checkedListBox').append('<li class="list-group-item" data-id="' + this.permissionId + '">' + this.permissionGUI + '</li>');		
        });           
        
        $('.list-group.checked-list-box .list-group-item').each(function () {
            // Settings
            var $widget = $(this),
                $checkbox = $('<input type="checkbox" class="hidden" />'),
                color = ($widget.data('color') ? $widget.data('color') : "primary"),
                style = ($widget.data('style') == "button" ? "btn-" : "list-group-item-"),
                settings = {
                    on: {
                        icon: 'glyphicon glyphicon-check'
                    },
                    off: {
                        icon: 'glyphicon glyphicon-unchecked'
                    }
                };
                
            	// For edit mode
            	if (checkboxlistItems != null) {
    				for (x = 0; x < checkboxlistItems.length; x++) {
    					if (checkboxlistItems[x].permissionList.permissionGUI == $widget.context.textContent) {
    						$checkbox.prop('checked', true);
    					}
    				};
            	}
            
      	      $widget.css('cursor', 'pointer')
      	      $widget.append($checkbox);
      	
      	      // Event Handlers
      	      $widget.on('click', function () {
      	          $checkbox.prop('checked', !$checkbox.is(':checked'));
      	          $checkbox.triggerHandler('change');
      	          updateDisplay();
      	      });
      	      $checkbox.on('change', function () {
      	          updateDisplay();
      	      });    
      	      
              // Actions
              function updateDisplay() {
                  var isChecked = $checkbox.is(':checked');

                  // Set the button's state
                  $widget.data('state', (isChecked) ? "on" : "off");

                  // Set the button's icon
                  $widget.find('.state-icon')
                      .removeClass()
                      .addClass('state-icon ' + settings[$widget.data('state')].icon);

                  // Update the button's color
                  if (isChecked) {
                      $widget.addClass(style + color + ' active');
                  } else {
                      $widget.removeClass(style + color + ' active');
                  }
              }

              // Initialization
              function init() {
                  
                  if ($widget.data('checked') == true) {
                      $checkbox.prop('checked', !$checkbox.is(':checked'));
                  }
                  
                  updateDisplay();

                  // Inject the icon if applicable
                  if ($widget.find('.state-icon').length == 0) {
                      $widget.prepend('<span class="state-icon ' + settings[$widget.data('state')].icon + '"></span>');
                  }
              }
              init();  	      
        });            
    };
      
    $('#btn_save').on('click', function(event) {
        event.preventDefault();
        
        var roleDetails = new Array();
        $("#checkedListBox li.active").each(function(idx, li) {
            //checkedItems[counter] = $(li).data('id'); //$(li).attr('data-id');
            //counter++;
        	roleDetails.push( { 'roleDetailId': null, 'permissionList': { 'permissionId': $(li).data('id') } } );
        });
        
        var roleName = $('#txt_rolename').val();
        var defaultPage = $('#txt_defaultpage').val();
        var data = {
        		'roleName': roleName,
        		'defaultPage': defaultPage,
        		'roleDetailInfos': roleDetails
        };

        update(data);
    });    
    
    function getRoleDetailInfo(json) {
        $.ajax({
            type: "POST",
            url: "/rest/getRoleDetailInfoList",
            data: JSON.stringify(json),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: ajaxSucceedRole,
            error: AjaxFailed
        });      	
    }
    
    function getRoleInfo(json) {
        $.ajax({
            type: "POST",
            url: "/rest/getRoleInfo",
            data: JSON.stringify(json),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(result) {
            	$('#txt_defaultpage').val(result.defaultPage);
            },
            error: function(result) {
            	alert(result);
            }
        });    	
    }
    function getPermissionList() {
        $.ajax({
            type: "GET",
            url: "/rest/getAllPermissionList",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function(json) {
            	BindCheckBoxList(null, json);
            },
            error: AjaxFailed
        });      	
    }
    
    function update(json) {
        $.ajax({
            type: "POST",
            url: "/rest/updateRoleInfo",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(json),
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
            	
            	reloadGrid();
            },
            error: AjaxFailed
        });    	    	
    }
    
    function confirmDelete() {
	  	$('#confirm').modal({ backdrop: 'static', keyboard: false })
  		.on('click', '#delete-btn', function(){
			  var gsr = grid.getGridParam('selrow');
			  if(gsr){
				  console.log('delete row:' + gsr);
				
				  deleteData({ 'roleName': gsr });
			  } else {
				  alert("Please select Row")
			  }   			
  			$('#confirm').modal('hide');
  		});     	
    }
    
    function deleteData(json) {
        $.ajax({
            type: "POST",
            url: "/rest/deleteRoleInfo",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            data: JSON.stringify(json),
            success: function(result) {
            	reloadGrid();
            },
            error: AjaxFailed
        }); 				  		  	
    }
});    