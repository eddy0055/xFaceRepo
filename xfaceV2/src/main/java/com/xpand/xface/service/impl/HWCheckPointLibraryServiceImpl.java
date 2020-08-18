package com.xpand.xface.service.impl;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.xpand.xface.service.HWCheckPointLibraryService;

@SessionScope
@Component
public class HWCheckPointLibraryServiceImpl implements HWCheckPointLibraryService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	@Autowired
//	HWCheckPointLibraryDAO hwCheckPointLibraryDAO; 
	
//	@Override
//	public List<HWCheckPointLibrary> getNeverCreateLibrary(HWVCM hwVCM) {
//		return this.hwCheckPointLibraryDAO.findNeverCreateLibrary(hwVCM);
//	}
//
//	@Override
//	public List<HWCheckPointLibrary> getNeverCreateCheckPoint(HWVCM hwVCM) {
//		return this.hwCheckPointLibraryDAO.findNeverCreateCheckPoint(hwVCM);
//	}
	
//	@Override
//	public ResultStatus updateLibraryId(String transactionId, HWCheckPointLibrary hwCheckPointLibrary) {
//		ResultStatus result = new ResultStatus();
//		try {
//			this.hwCheckPointLibraryDAO.updateLibraryId(hwCheckPointLibrary.getLibraryId(), hwCheckPointLibrary.getChkponlibId());			
//		}catch (Exception ex) {
//			Logger.error(this,LogUtil.getLogError(transactionId, "error while update libraryId:"+hwCheckPointLibrary.getLibraryId()+" id:"+hwCheckPointLibrary.getChkponlibId()+" error:"+ex.toString(), ex));
//			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, "hwCheckPointLibrary.libraryId:"+hwCheckPointLibrary.getLibraryId());
//		}
//		return result;
//	}
//
//	@Override
//	public ResultStatus updateCheckPointId(String transactionId, HWCheckPointLibrary hwCheckPointLibrary) {
//		ResultStatus result = new ResultStatus();
//		try {
//			this.hwCheckPointLibraryDAO.updateCheckPointId(hwCheckPointLibrary.getCheckPointId(), hwCheckPointLibrary.getChkponlibId());			
//		}catch (Exception ex) {
//			Logger.error(this,LogUtil.getLogError(transactionId, "error while update checkPointId:"+hwCheckPointLibrary.getCheckPointId()+" id:"+hwCheckPointLibrary.getChkponlibId()+" error:"+ex.toString(), ex));			
//			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, null);
//		}
//		return result;
//	}

//	@Override
//	public HWCheckPointLibrary getOneObject() {
//		// TODO Auto-generated method stub		
//		List<HWCheckPointLibrary> checkPointLib = this.hwCheckPointLibraryDAO.findAllByOrderBychkponlibIdAsc();
//		if (checkPointLib.size()>0) {
//			return checkPointLib.get(0);
//		}else {
//			return null;
//		}
//	}

}
