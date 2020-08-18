$(document).ready(function() {		
	//call function get select list Person Category
	  getAllPersonCategory();
	//call function get select list Person Title 
	  getPersonTitle();
	//call function get select Person Certification
	  getAllPersonCer();
	  runLoading();
	
		function readURL(input) {
		     if (input.files && input.files[0]) {
		         var reader = new FileReader();
		             reader.onload = function (e) {
		             $('#image_upload_preview').attr('src', e.target.result);
		         }
		         reader.readAsDataURL(input.files[0]);
		         }
		    }
		     $("#txt_file").change(function () {
		    	 readURL(this);
		     });
//Hide Error Box
   $("#div8").hide();
		  
//Check image size & alert
   $('#txt_file').bind('change', function() {
	    var f = this.files[0]
//Check File size
	  if (f.size > 5120000 || f.fileSize > 5120000)
		 {
		    //show alert modal
		   $('#ModalFileMax').modal('show');
		   //reset file upload control
		    this.value = null;
		}else{
		   runLoading();
		   $('#loading').html('<img src="/dist/img/load.gif" width="50px" height="50px"> Image Loading .....');
		   setTimeout(function () {
		   $('#loading').html('');
		    }, 1000);
		   }
    });    
		         
// --------------- control btn_save ---------------------------			
     $("#btn_save").click(function() {
    	 	 var fileName = null;
	    	 var data;
	    	 var personTitle = $('#txt_PersonTitle').val();
	   		 var firstName = $('#txt_FirstName').val();
	   		 var lastName = $('#txt_LastName').val(); 
	   		 var PersonCategory = $('#txt_PersonCategory').val();
	   		 var PersonCer = $('#txt_PersonCer').val();
	   		 var CerNo = $('#txt_CerNo').val();
	           
	         var json =  {  
	   	  	   	    "personTitle": {"titleId": personTitle},
	   	  	        "personCategory": {"categoryId": PersonCategory},
	   	  	        "personCertification":{"certificationId": PersonCer},
	   	  	        "certificationNo": CerNo,
	   	  	        "firstName": firstName,
	   	  	        "lastName": lastName,
	   	  	        "personCategory": {"categoryId": PersonCategory}
	   	  	    };
	    	  
	   	  if ($('#txt_file').prop('files').length > 0) {
	   		 fileName = $('#txt_file').prop('files')[0];
	   	  } else {
	   		  console.log('no file');
	   	  }
	   	   	   	 
	   	        var dataObj = JSON.stringify(json);
	    	        data = new FormData();
	    	        data.append('personInfo',dataObj);
	    	        
	    	        if (fileName != null) {
	    	        	data.append('personPhoto',fileName);	
	    	        }
	    	        
	    	  $.ajax({
	    	        url: '/rest/updatePersonInfo',
	    	        processData: false,
	    	        type: 'POST',
	    	        contentType: false, 	    	        
	    	        data: data,
	    	        success: function (data) {
	    	            
	    	        	if(data.statusCode == "-1"){
	    	        		$('#myModal-fail').modal('show');
		            		 
		            		 $("#Message-fail").empty();
		            		 $("#Message-fail").append(data.statusDesc);
		            		
		            	}
	    	        	
	    	      
	    	        	if(data.statusCode == "-2"){
	    	        		console.log(data.statusDesc);
	    	        	  	 $('#myModal-fail').modal('show');
		          
		            		 $("#Message-fail").empty();
		            		 $("#Message-fail").append(data.statusDesc);
		            		
		            	}
		            	
		            	if (data.statusCode == "0"){
		            		//console.log(data);
		    	        	$('#myModal').modal('show');
		            	}
	    	        
	    	        },
	    	        error: function(error) {
	    	        	//alert(error.responseJSON);
	    	        	$('#myModal-fail').modal('show');
	    	        }
	        	});   
	    	});
// ---------- end of btn_save --------------------			
     
// --------- control btn_search ------------------
     $("#btn_search").click(function() {
       //value from search
    	var firstName = $('#txt_FirstName').val();
   		var lastName = $('#txt_LastName').val(); 
   		var CerNo = $('#txt_CerNo').val();
   	
   		var json = { "searchPersonConditionList": [
   			         {
   			        	 "searchField": "certificationNo",
   			        	 "searchValue": CerNo 
   			         },
   			         {
   			        	 "searchField": "firstName",
   			        	 "searchValue": firstName 
   			         },
   			         {
   			        	 "searchField": "lastName",
   			        	 "searchValue": lastName 
   			         }
   			       ]
   		    	};
   			
   	 $.ajax({
         type: "POST",
         url: "/rest/getPersonInfo",
         data: JSON.stringify(json),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         success: function(result) {  
        	var arg = result.personTitle.titleId;
        	 $('#txt_PersonTitle').val(arg);
        	var arg1 = result.personCertification.certificationId;
        	 $('#txt_PersonCategory').val(arg1);
        	 $('#txt_FirstName').val(result.firstName);
	         $('#txt_LastName').val(result.lastName);	        
	         $('#txt_CerNo').val(result.certificationNo);
	         $('#image_upload_preview').attr('src', result.personPhoto);
	      
	         // --------------------- add loading image  --------------
	         $('#loading').html('<img src="/dist/img/load.gif" width="50px" height="50px"> Image Loading .....');
        	 setTimeout(function () {
	                $('#loading').html('');
	            }, 1000);
        	 
         },
         error: function(result) {
        	 $('#SearchModal-fail').modal('show');
         }
        });    	
   	    
     });
  // ------------------ End of btn_search ---------------------	     
	 
     
     
  // --------------- control btn_reset ---------------------------			
       $("#btn_reset").click(function() {
    	 
  	    	 $('#txt_PersonTitle').val(null);
  	   		 $('#txt_FirstName').val(null);
  	   		 $('#txt_LastName').val(null); 
  	   		 $('#txt_PersonCategory').val(null);
  	   		 $('#txt_CerNo').val(null);
  	   	     $('#image_upload_preview').attr('src',"/dist/img/noimage.gif");
  	   		 $('#txt_file').val(null);
       
  	  });
  // ---------- end of btn_reset --------------------  
  
  // ----------- Select List Person Title ----------- 	
      function getPersonTitle(){
		    	$.ajax({
	 		    type: "GET",
		         url: "/rest/getAllPersonTitle",
		 contentType: "application/json; charset=utf-8",
		    dataType: "json",
		     success: function(result) {
		           $.each(result, function (index, value) {
			           $('#txt_PersonTitle').append('<option value="' + value.titleId + '">' + value.titleName + '</option>');
			                 console.log(value.titleName);
		               });      			
		            },
		            error: function(result) {
		            	console.log(result);
		            }
	 	     });
	      }
		    
  // ----------- Select List Person Category ----------- 	     
	 function getAllPersonCategory(){
		    	$.ajax({
			    type: "GET",
		         url: "/rest/getAllPersonCategory",
		 contentType: "application/json; charset=utf-8",
		    dataType: "json",      
		     success: function(result) {
		         $.each(result, function (index, value) {
			        $('#txt_PersonCategory').append('<option value="' + value.categoryId + '">' + value.categoryName + '</option>');
			              console.log(value.categoryName);
		            });      			
		           },
		            error: function(result) {
		            	console.log(result);
		            }
		      });
		   }
	
	 // ----------- Select List Person Category ----------- 	     
	 function getAllPersonCer(){
		    	$.ajax({
			    type: "GET",
		         url: "/rest/getAllPersonCertification",
		 contentType: "application/json; charset=utf-8",
		    dataType: "json",      
		     success: function(result) {
		         $.each(result, function (index, value) {
			        $('#txt_PersonCer').append('<option value="' + value.certificationId + '">' + value.certificationName + '</option>');
			             console.log(value.certificationName);
			             console.log(value.certificationId);
		            });      			
		           },
		            error: function(result) {
		            	console.log(result);
		            }
		      });
		   }
	 
	 
  function sleep(miliseconds) {
      var currentTime = new Date().getTime();
      while (currentTime + miliseconds >= new Date().getTime()) {
       }
      }

  function runLoading() {
    sleep(500);
     }// end runLoading
});//end of Document ready  
	     
	     