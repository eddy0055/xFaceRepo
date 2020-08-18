/**
 * Set login 
 * @param {*username} user 
 * @param {*password} pwd 
 * @param {*vcn ip} ip 
 * @param {*port} port 
 */
function setLogin(user, pwd, ip, port) {
	var result;

	if (ocx)
	{
		result = ocx.IVS_OCX_Login(user, pwd, ip, port, 1);
	}
	
	return result;	

	/*
	if (ocx) {
        var result = ocx.IVS_OCX_Login(user, pwd, ip, port, 1);
        
        return result;
        
        if (result == 0) {
            ocx.IVS_OCX_SetEventReceiver();

            var pReqXml =
                "<?xml version='1.0' encoding='UTF-8'?>" +
                "<Content>" +
                "    <DomainCode>2431dc807b304590a006ff7a36cc26a9</DomainCode>" +
                "    <Subscribe>" +
                "        <SubscriberInfo>" +
                "            <Subscriber>1</Subscriber>" +
                "            <SubscriberID>0</SubscriberID>" +
                "            <UserDomainCode>2431dc807b304590a006ff7a36cc26a9</UserDomainCode>" +
                "        </SubscriberInfo>" +
                "        <SubscribeList>" +
                "            <SubscribeInfo>" +
                "                 <AlarmInCode></AlarmInCode>" +
                "                 <SubscribeType>1</SubscribeType>" +
                "                 <AlarmLevelValueMin></AlarmLevelValueMin>" +
                "                 <AlarmLevelValueMax></AlarmLevelValueMax>" +
                "            </SubscribeInfo>" +
                "        </SubscribeList>" +
                "    </Subscribe>" +
                "</Content>";
            result = ocx.IVS_OCX_SubscribeAlarm(pReqXml);
        }
        
        console.log("Login Result: " + result);
    }
    */
}

/**
 * Set logout
 */
function setLogout()
{
	if (ocx)
	{
		ocx.IVS_OCX_Logout();
	}
}

/**
 * Logout
 */
function logout() {
    try {
        ocx.IVS_OCX_StopAllRealPlay(); // Stop All Playing Live 
        ocx.IVS_OCX_Logout(); // User Logout
        ocx.IVS_OCX_CleanUp(); // Release OCX
        ocx = null;
        $("#ocx").remove(); // Remove OCX From Html Document
    } catch (e) {}
}