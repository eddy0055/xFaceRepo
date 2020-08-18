/**
 * Init
 */
function ivs_init() {
	setInit();
	setScale();
	setLayout(1);
	//setNormalScreen();
	//setLogin("xpandapp", "Xpand@456", "192.168.2.200", 9900);
	//getLivePlay("02117790000000000101#ab8df621bf3f4d91b61ce8cf5100c01a");
}

function closeSession()
{
	if (ocx)
	{
		try
		{
			event.returnValue = alert(langs[lang]["exitDemo"]);
			logout();
		}
		catch (e)
		{
		}
	}
}

/**
 * Set initial OCX object
 */
function setInit()
{
	var result;
	if (ocx)
	{
		result = ocx.IVS_OCX_Init();

		ocx.IVS_OCX_SetSkin(1);
	}
	
	return result;
}


/**
 * 设置视频播放时，视频画面是否拉伸用以填满播放窗格
 */
function setScale()
{
	if (ocx)
	{
		var scale = $("#scale").val();
		
		var result = ocx.IVS_OCX_SetDisplayScale(scale);
		
		console.log("IVS_OCX_SetDisplayScale:" +result);
	}
}

/**
 * 设置OCX控件全屏显示
 */
function setFullScreen()
{
	if (ocx)
	{
		var result = ocx.IVS_OCX_FullScreenDisplay();

		console.log("IVS_OCX_FullScreenDisplay:" +result);
		
		//$("#resultcode").val("IVS_OCX_FullScreenDisplay:" +result);
	}
}

/**
 * 设置OCX控件退出全屏幕显示，一般情况下使用"Esc"键退出全屏幕显示
 */
function setNormalScreen()
{
	if (ocx)
	{
		var result = ocx.IVS_OCX_NormalScreenDisplay();
		
		//$("#resultcode").val("IVS_OCX_NormalScreenDisplay:" +result);
		console.log("IVS_OCX_NormalScreenDisplay:" +result);
	}
}

/**
 * 设置OCX控件用于播放显示视频的窗格布局
 * @param wndNum 窗格数量
 */
function setLayout(wndNum)
{
	if (ocx)
	{
		var result = 0;
		
		if (wndNum == 1)
		{
			result = ocx.IVS_OCX_SetWndLayout(11);  // "11" 代表仅一个窗格布局模式
		}
		else if (wndNum == 6)
		{
			result = ocx.IVS_OCX_SetWndLayout(63);  // "63" 代表1大5小布局模式
		}
		else if (wndNum == 9)
		{
			result = ocx.IVS_OCX_SetWndLayout(92);  // "92" 代表九宫格布局模式
		}
		
		console.log("IVS_OCX_SetWndLayout:" + result);
		//$("#resultcode").val("IVS_OCX_SetWndLayout:" + result);
	}
}

/**
 * 获取空闲的(没有播放视频的)播放窗格编号
 */
function getFreeWnd()
{
	if (ocx)
	{
		var freeWnd = ocx.IVS_OCX_GetFreeWnd();
		
		console.log("IVS_OCX_GetFreeWnd:" + freeWnd);
		
		return freeWnd;
	}
}

/**
 * 获取鼠标选中的播放窗格编号
 */
function getSelectedWnd()
{
	if (ocx)
	{
		var selectedWnd = ocx.IVS_OCX_GetSelectWnd();
		
		console.log("SelectedWnd: " + selectedWnd);
		
		return selectedWnd;
	}
}

/**
 * 获取选中的播放窗格中，正在播放的视频对应的摄像机编码
 */
function getCameraByWnd()
{
	if (ocx)
	{
		var selectedWnd = ocx.IVS_OCX_GetSelectWnd();
		
		var camera = ocx.IVS_OCX_GetCameraByWnd(selectedWnd);
		
		console.log("IVS_OCX_GetCameraByWnd:" + camera);
		
		return camera;
	}
}

/**
 * 获取空闲的(没有播放视频的)播放窗格编号
 */

function setWndType()
{
	if (ocx) 
	{
		var wnd = $("#setWndInput").val();
		var wndType = $("#wndTypeSelect").val();
		
		var result = ocx.IVS_OCX_SetWndType(wnd, wndType);
	}
}

function getWndType()
{
	if (ocx) 
	{
		var wnd = $("#getWndInput").val();
		var wndType = ocx.IVS_OCX_GetWndType(wnd);
		
		$("#wndTypeInput").val(wndType);
	}
}


function setWndDrag()
{
	if (ocx)
	{
		var wnd = $("#setWndDragInput").val();
		var enable = $("#setWndDragSelect").val();
		
		var result = ocx.IVS_OCX_SetWndDrag(wnd, enable);
	}
}

function enableExchangePane()
{
	if (ocx)
	{
		var enable = $("#exchangePaneSelect").val();
		
		var result = ocx.IVS_OCX_EnableExchangePane(enable);
	}
}


function getMouseWnd()
{
	if (ocx)
	{
		var result = ocx.IVS_OCX_GetMouseWnd();
		$("#getMouseWndInput").val(result);
	}
}

function getPaneWnd()
{
	if (ocx)
	{
		var wnd = $("#getMouseWndInput").val();
		var result = ocx.IVS_OCX_GetPaneWnd(wnd);
		$("#getPaneWndInput").val(result);
	}
}

function getDomainRoute()
{
	if (ocx)
	{
		var result = ocx.IVS_OCX_GetDomainRoute();
		$("#getDomainRouteInput").val(result);
	}
}

function getDeviceGroup()
{
	if (ocx)
	{
		var xmlDoc = $.parseXML(ocx.IVS_OCX_GetDomainRoute());
		xmlDoc = $(xmlDoc);
		var domainCode = xmlDoc.find("DomainCode").text();
		
		var reqXml = "<Content>";
		reqXml += "    <DomainCode>" + domainCode + "</DomainCode>";
		reqXml += "    <GroupID>0</GroupID>";  // 0为默认的根节点ID
		reqXml += "</Content>";
		var result = ocx.IVS_OCX_GetDeviceGroup(reqXml);
		$("#getDeviceGroupInput").val(result);
	}
}
 
function getNVRList()
{
	if (ocx)
	{
		var xmlDoc = $.parseXML(ocx.IVS_OCX_GetDomainRoute());
		xmlDoc = $(xmlDoc);
		var domainCode = xmlDoc.find("DomainCode").text();
		
		var reqXml = "<Content>";
		reqXml += "    <DomainCode>" + domainCode + "</DomainCode>";
		reqXml += "    <NVRType>0</NVRType>";  // 0为查询全部类型
		reqXml += "    <PageInfo>";
		reqXml += "        <FromIndex>1</FromIndex>";
		reqXml += "        <ToIndex>10</ToIndex>";
		reqXml += "    </PageInfo>";
		reqXml += "</Content>";
		var result = ocx.IVS_OCX_GetNVRList(reqXml);
		$("#getNVRListInput").val(result);
	}
}
