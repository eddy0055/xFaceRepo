package com.xpand.xface.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.QueueContent;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.util.LogUtil;

@RestController
//@RequestMapping("/rest")
public class RestAlarmController {
	
	public static String CLASS_NAME=RestAlarmController.class.getName();
	@Autowired
	GlobalVarService globalVarService;
	
	@RequestMapping(value="/rest", produces= {"*/*"})
	@ResponseBody
	public String getPersonInfoByAlarm(@RequestBody String content) {
		Logger.debug(this, LogUtil.getLogDebug(LogUtil.getWebSessionId(), "receive alarm from VCM server[Rest]"));
		this.globalVarService.pushContent(QueueContent.CONTENTTYPE_HW_ALARM, content);
		return "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
	}
	@RequestMapping(value="/rest/pushData1", produces= {"*/*"})
	@ResponseBody
	public String getPersonInfoByAlarmPushDataV1(@RequestBody String content) {
		Logger.debug(this, LogUtil.getLogDebug(LogUtil.getWebSessionId(), "receive alarm from VCM server[PushDataV1]"));
		this.globalVarService.pushContent(QueueContent.CONTENTTYPE_HW_ALARM, content);
		return "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
	}
	@RequestMapping(value="/rest/pushData2", produces= {"*/*"})
	@ResponseBody
	public String getPersonInfoByAlarmPushDataV2(@RequestBody String content) {
		Logger.debug(this, LogUtil.getLogDebug(LogUtil.getWebSessionId(), "receive alarm from VCM server[PushDataV2]"));
		this.globalVarService.pushContent(QueueContent.CONTENTTYPE_HW_ALARM, content);
		return "<response><result><errmsg>Success.</errmsg><code>0</code></result></response>";
	}
}
