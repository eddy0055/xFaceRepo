$(document).ready( function () {
	var timerRedirect = null;
    var redirectURL = null;
	function validateInput(){
	    var txtOldPwd = $("#oldPwd").val();
	    var txtNewPwd = $("#newPwd").val();
	    var txtConfirmPwd = $("#confirmPwd").val();
	    var errorMsg = "";			    
	    if (txtOldPwd===""){
	    	errorMsg = $("#errorOldPwdBlank").text();
	    }else if (txtNewPwd===""){
	    	errorMsg = $("#errorNewPwdBlank").text();
	    }else if (txtConfirmPwd===""){
	    	errorMsg = $("#errorConfirmPwdBlank").text();
	    }else if (txtNewPwd!==txtConfirmPwd){
	    	errorMsg = $("#errorPwdNotMatch").text();
	    }
	    var divLoginResult = $("#divLoginResult");
	    if (divLoginResult!==null){
	    	divLoginResult.remove();
	    }
	    return globalDisplayError(errorMsg);		    
	}	
	function submitChangePwd() {
		globalWriteConsoleLog("in submitChangePwd");
        $.ajax({
            type: "POST",
            url: "/xFace/rest/userManage/user/forceChangePwd",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(createChangePwd()),
            dataType: "json",
            success: function(resultStatus) {
            	globalIsSessionExpire(resultStatus);            	            
            	if (resultStatus.statusCode==="0"){
            		$("#divDisplayError").empty();
            		$("#divDisplayError").append("<div class='alert alert-success'><a href='#' class='close' data-dismiss='alert' aria-label='close'>&times;</a><strong>Success</strong>Change password success</div>");
            		globalWriteConsoleLog("change pwd success redirect to "+resultStatus.statusParam);            		            	
            		redirectURL = resultStatus.statusParam;
            		timerRedirect = setInterval(redirectPage, 2000); 
            	}else{
            		globalDisplayError(resultStatus.statusDesc);
            	}            	
            },
            error: function(error){
            	globalDisplayError("Error while change password to server");
            }
        });
        globalWriteConsoleLog("out submitChangePwd");
    }
	function redirectPage(){
		clearInterval(timerRedirect);
		window.open(redirectURL,"_self");
	}
	function createChangePwd(){
		var changePwd = {};
		changePwd["oldPwd"] = $("#oldPwd").val();
		changePwd["newPwd"] = $("#newPwd").val();
		return changePwd;
	}	
	$("#btnChangePwd").on("click", function(event){
		if (validateInput()){
			submitChangePwd();
		}
	});
});