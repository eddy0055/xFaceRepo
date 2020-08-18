$(document).ready( function () {
	var alertRedInput = "#8C1010";
	var defaultInput = "rgba(10, 180, 180, 1)";
	globalImageLoader =  $("#imageLoader");
	
	$('#userName').attr("autocomplete", "off");
	setTimeout('$("#userName").val("");', 500);
	
	$('#userPwd').attr("autocomplete","off");
	setTimeout('$("#userPwd").val("");', 500);
	//$("#userName").val("");
	//$("#userPwd").val("");
	
	function validateInput(){
		var txtUserName = $("#userName").val();
	    var txtPwd = $("#userPwd").val();
	    var errorMsg = "";
	    if (txtUserName===""){
	    	errorMsg = $("#errorUserNameBlank").text();
	    }else if (txtPwd===""){
	    	errorMsg = $("#errorPwdBlank").text();
	    }
	    var divLoginResult = $("#divLoginResult");
	    if (divLoginResult!==null){
	    	divLoginResult.remove();
	    }
	    return globalDisplayError(errorMsg);		    
	}
	function submitForgetPwd() {
		globalWriteConsoleLog("in submitForgetPwd");
		globalAddLoader();
        $.ajax({
            type: "POST",
            url: "/xFace/rest/userManage/user/forgetPwd",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(createForgetPwd()),
            dataType: "json",
            success: function(resultStatus) {            	            	           
            	if (resultStatus.statusCode==="0"){
            		$("#divDisplayError").empty();
            		$("#divDisplayError").append("<div class='alert alert-success'><a href='#' class='close' data-dismiss='alert' aria-label='close'>&times;</a><strong>Success</strong>temporary password was send to "+$("#userName").val()+"</div>");            		
            	}else{
            		globalDisplayError(resultStatus.statusDesc);
            	}   
            	globalClearLoader();
            },
            error: function(error){            	
            	globalDisplayError("Error while submit request forget password to server");
            	globalClearLoader();
            }
        });
        globalWriteConsoleLog("out submitForgetPwd");
    }
	
	function userNameValidation(usernameInput) {
	    var username = document.getElementById("username");
	    var issueArr = [];
	    if (/[-!@#$%^&*()_+|~=`{}\[\]:";'<>?,.\/]/.test(usernameInput)) {
	        issueArr.push("No special characters!");
	    }
	    if (issueArr.length > 0) {
	        username.setCustomValidity(issueArr);
	        username.style.borderColor = alertRedInput;
	    } else {
	        username.setCustomValidity("");
	        username.style.borderColor = defaultInput;
	    }
	}

	function passwordValidation(passwordInput) {
	    var password = document.getElementById("password");
	    var issueArr = [];
	    if (!/^.{7,15}$/.test(passwordInput)) {
	        issueArr.push("Password must be between 7-15 characters.");
	    }
	    if (!/\d/.test(passwordInput)) {
	        issueArr.push("Must contain at least one number.");
	    }
	    if (!/[a-z]/.test(passwordInput)) {
	        issueArr.push("Must contain a lowercase letter.");
	    }
	    if (!/[A-Z]/.test(passwordInput)) {
	        issueArr.push("Must contain an uppercase letter.");
	    }
	    if (issueArr.length > 0) {
	        password.setCustomValidity(issueArr.join("\n"));
	        password.style.borderColor = alertRedInput;
	    } else {
	        password.setCustomValidity("");
	        password.style.borderColor = defaultInput;
	    }
	}
	
	
	
	function createForgetPwd(){
		var forgetPwd = {};
		forgetPwd["userName"] = $("#userName").val();
		return forgetPwd;
	}
	
	$("#btnForgetPwd").on("click", function(event){	
		event.preventDefault();
		if ($("#userName").val()===""){
			globalDisplayError("Please enter valid user name");
		}else{
			submitForgetPwd();
		}
	});
	$("#formLogin").on("submit", function(e){
        e.preventDefault();
        if (validateInput()){
        	this.submit();
        }                    
    });
	
	
	
	
});