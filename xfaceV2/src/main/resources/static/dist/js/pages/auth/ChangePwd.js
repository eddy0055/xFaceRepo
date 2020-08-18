$(document).ready( function () {
	var timerRedirect = null;
    var redirectURL = null;
	function validateInput(){
	    var txtOldPwd = $("#oldPwd").val();
	    var txtNewPwd = $("#newPwd").val();
	    var txtConfirmPwd = $("#confirmPwd").val();
	    var errorMsg = null;			    
	    if (txtOldPwd===""){
	    	errorMsg = $("#errorOldPwdBlank").text();
	    }else if (txtNewPwd===""){
	    	errorMsg = $("#errorNewPwdBlank").text();
	    }else if (txtConfirmPwd===""){
	    	errorMsg = $("#errorConfirmPwdBlank").text();
	    }else if (txtNewPwd!==txtConfirmPwd){
	    	errorMsg = $("#errorPwdNotMatch").text();
	    }	    
	    if (errorMsg===null){
	    	return true;
	    }else{
	    	globalShowGrowlNotification($("#errorDialogTitle").text(), errorMsg, "danger");
	    	return false;
	    }	    		   
	}	
	function submitChangePwd() {
		globalWriteConsoleLog("in submitChangePwd");
        $.ajax({
            type: "POST",
            url: "/xFace/rest/userManage/user/changePwd",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(createChangePwd()),
            dataType: "json",
            success: function(resultStatus) {
            	globalIsSessionExpire(resultStatus);            	            
            	if (resultStatus.statusCode==="0"){
            		globalShowGrowlNotification($("#successDialogTitle").text(), $("#successSaveRecord").text(), "success");
            		redirectURL = resultStatus.statusParam;
            		timerRedirect = setInterval(redirectPage, 2000); 
            	}else{            		
            		globalShowGrowlNotification($("#errorDialogTitle").text(), resultStatus.statusDesc, "danger");
            	}            	
            },
            error: function(error){            	
            	globalShowGrowlNotification($("#errorDialogTitle").text(), "Error while change password to server", "danger");
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