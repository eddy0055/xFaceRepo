package com.xpand.xface.bean;

import com.jcabi.log.Logger;
import com.xpand.xface.util.LogUtil;

public class PersonSummaryInfo {
	private final String summaryDate;
	private final long summaryCnt;
	///////
	public PersonSummaryInfo(String summaryDate, long summaryCnt) {
		Logger.debug(this, LogUtil.getLogDebug("transactionId", "Summary Date:"+summaryDate+" summaryCnt:"+summaryCnt));
		this.summaryDate = summaryDate;
		this.summaryCnt = summaryCnt;		
	}
	public String getSummaryDate() {
		return summaryDate;
	}
	public long getSummaryCnt() {
		return summaryCnt;
	}
}
