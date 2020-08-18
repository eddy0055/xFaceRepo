/**
 * Get live play
 * @param {*Camera id} cameraId 
 */
function getLivePlay(cameraId) {
    var cameraCode = cameraId;
    //var wnd = ocx.IVS_OCX_GetSelectWnd();
        
    var streamType = 1;
    var protocolType = 2;
    var direstFirst = 1;
    var multi = 0;

    var pMediaParaxml =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<Content>" +
        "    <RealplayParam>" +
        "        <StreamType>" + streamType + "</StreamType>" +
        "        <ProtocolType>" + protocolType + "</ProtocolType>" +
        "        <DirectFirst>" + direstFirst + "</DirectFirst>" +
        "        <Multicast>" + multi + "</Multicast>" +
        "    </RealplayParam>" +
        "</Content>";

    var result = ocx.IVS_OCX_StartRealPlay(pMediaParaxml, cameraCode, 1);
    
    console.log("IVS_OCX_StartRealPlay:" + result);
}

function stopLivePlay()
{
	if (ocx)
	{
		var wnd = ocx.IVS_OCX_GetSelectWnd();
		if (wnd == "")
		{
			alert(langs[lang]["inputWndFirst"]);
			return;
		} 
		
		var result = ocx.IVS_OCX_StopRealPlay(wnd);
		
		console.log("IVS_OCX_StopRealPlay:" + result); 
	}
}