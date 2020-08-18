package com.xpand.xface.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.jcabi.log.Logger;
import com.xpand.xface.exception.DoQueryErrorException;
import com.xpand.xface.service.BaseXFaceThreadService;

public class OtherUtil {
	private static final int MILLISECOND_CHECK_WAIT=10;
	public static void waitMilliSecond(int intValue){
		try{
			Thread.sleep((intValue));
		}catch (Exception ex){}
	}	
	public static void waitMilliSecond(BaseXFaceThreadService baseService, int intValue){
		try{
			//Thread.sleep((intValue));
			if (intValue<=MILLISECOND_CHECK_WAIT) {
				intValue = 1;
			}else {
				intValue = intValue / MILLISECOND_CHECK_WAIT;
			}
			for (int i=0; i<intValue; i++) {
				if (baseService.isServiceRunning()) {
					Thread.sleep((MILLISECOND_CHECK_WAIT));
				}				
			}			
		}catch (Exception ex){}
	}
	public static void waitSecond(int intValue){
		try{
			for (int i=0; i<intValue; i++) {				
				Thread.sleep((1000));
			}			
		}catch (Exception ex){}
	}
	public static void waitSecond(BaseXFaceThreadService baseService, int intValue){
		try{
			for (int i=0; i<intValue; i++) {
				if (baseService.isServiceRunning()) {
					Thread.sleep((1000));
				}else {					
					return;
				}
			}
		}catch (Exception ex){}
	}
	public static void waitSecondXFaceServer(BaseXFaceThreadService baseService, int intValue){
		try{
			for (int i=0; i<intValue; i++) {
				if (baseService.isServiceReStart()) {
					Thread.sleep((1000));
				}else {					
					return;
				}
			}
		}catch (Exception ex){}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object[]> doQuery(Object source, String transactionId, EntityManager entity, String queryStmt, ArrayList<?> paramList) throws DoQueryErrorException{
		 try {
			 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Manual Query:"+queryStmt));
			 Query query = entity.createNativeQuery(queryStmt);			 
			 int intCntParam=1;	         
	         for (Object obj: paramList) {	        	 
	        	 query.setParameter(intCntParam, obj);	        	 
	        	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Parameter["+intCntParam+"]:"+obj));
	        	 intCntParam++;
	         }	         	         
	         List<Object[]> result = query.getResultList();	         
	         Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result OK with "+result.size()+" row return"));
	         return result;
	     } catch (Exception e) {
	    	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result fail with error "+e.toString()));
	    	 throw new DoQueryErrorException(e.toString());
	     }
	}
	@SuppressWarnings("unchecked")
	public static List<Object[]> doQuery(Object source, String transactionId, EntityManager entity, String queryStmt, int param) throws DoQueryErrorException{
		 try {
			 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Manual Query:"+queryStmt));
			 Query query = entity.createNativeQuery(queryStmt);			 			 	        
	         query.setParameter(1, param);	        	         	        
	         List<Object[]> result = query.getResultList();	         
	         Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result OK with "+result.size()+" row return"));
	         return result;
	     } catch (Exception e) {
	    	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result fail with error "+e.toString()));
	    	 throw new DoQueryErrorException(e.toString());
	     }
	}
	@SuppressWarnings("unchecked")
	public static List<Object[]> doQuery(Object source, String transactionId, EntityManager entity, String queryStmt, Object param) throws DoQueryErrorException{
		 try {
			 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Manual Query:"+queryStmt));
			 Query query = entity.createNativeQuery(queryStmt);			 			 	        
	         query.setParameter(1, param);	        	         	        
	         List<Object[]> result = query.getResultList();	         
	         Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result OK with "+result.size()+" row return"));
	         return result;
	     } catch (Exception e) {
	    	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result fail with error "+e.toString()));
	    	 throw new DoQueryErrorException(e.toString());
	     }
	}
	
	
	public static long doQueryCount(Object source, String transactionId, EntityManager entity, String queryStmt, ArrayList<?> paramList) throws DoQueryErrorException{
		 try {
			 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Manual Query:"+queryStmt));
			 Query query = entity.createNativeQuery(queryStmt);			 
			 int intCntParam=1;	         
	         for (Object obj: paramList) {	        	 
	        	 query.setParameter(intCntParam, obj);	        	 
	        	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Parameter["+intCntParam+"]:"+obj));
	        	 intCntParam++;
	         }	         	         
	         @SuppressWarnings("unchecked")
			 List<BigInteger> result = query.getResultList();
	         if (result.size()>0) {
	        	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result OK"));
	        	 return result.get(0).longValue();
	         }else {
	        	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "no record found"));
	        	 return 0;
	         }	         	         	        
	     } catch (Exception e) {
	    	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result fail with error "+e.toString()));
	    	 throw new DoQueryErrorException(e.toString());
	     }
	}	
	
	public static long doQueryCount(Object source, String transactionId, EntityManager entity, String queryStmt) throws DoQueryErrorException{
		 try {
			 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Manual Query:"+queryStmt));
			 Query query = entity.createNativeQuery(queryStmt);			 
			 //int intCntParam=1;	         
	         //for (Object obj: paramList) {	        	 
	        	 //query.setParameter(intCntParam, obj);	        	 
	        	 //Logger.debug(source, LogUtil.getLogDebug(transactionId, "Parameter["+intCntParam+"]:"+obj));
	        	 //intCntParam++;
	         //}	         	         
	         @SuppressWarnings("unchecked")
			 List<BigInteger> result = query.getResultList();
	         if (result.size()>0) {
	        	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result OK"));
	        	 return result.get(0).longValue();
	         }else {
	        	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "no record found"));
	        	 return 0;
	         }	         	         	        
	     } catch (Exception e) {
	    	 Logger.debug(source, LogUtil.getLogDebug(transactionId, "Result fail with error "+e.toString()));
	    	 throw new DoQueryErrorException(e.toString());
	     }
	}	
	
	
	
//	public static LoadAlarmActionResult loadAlarmActionBase(String transactionId, String pathJar, String className) {
//		LoadAlarmActionResult loadResult = new LoadAlarmActionResult();
//		AlarmActionBase action=null;
//		File jarFile = null;
//		try {
//			jarFile = new File(pathJar);
//			if (!jarFile.exists()) {
//				loadResult.getResult().setStatusCode(ResultStatus.ALARM_ACTION_JAR_NOTFOUND_ERROR_CODE, pathJar);
//				return loadResult;
//			}
//			URL fileURL = jarFile.toURI().toURL();
//			String jarURL = "jar:" + fileURL + "!/";
//			URL urls[] = { new URL(jarURL) };
//			URLClassLoader ucl = new URLClassLoader(urls);
//			action = (AlarmActionBase) Class.forName(className, true, ucl).newInstance();
//			loadResult.setAlarmAction(action);
//			loadResult.setUrlClassLoader(ucl);
//		} catch (ClassNotFoundException ex) {
//			loadResult.getResult().setStatusCode(ResultStatus.ALARM_ACTION_CLASS_NOTFOUND_ERROR_CODE, className);
//		} catch (Exception ex) {
//			loadResult.getResult().setStatusCode(ResultStatus.UNEXPECTED_ERROR_CODE, "jar:"+pathJar+",class:"+className+",ex:"+ex.toString());
//		}		
//		return loadResult;
//	}
//	private static LoadAlarmActionResult getInstanceAndMethod(String transactionId, String pathJar, String className) {
////			call by actionMethod.invoke(this.actionInstance, this.transactionId, StringUtil.getJson(personNotification, PersonNotification.class));
//		LoadAlarmActionResult loadResult = new LoadAlarmActionResult();
//		try {		
//			File file = new File(pathJar);		
//			if (!file.exists()) {
//				loadResult.getResult().setStatusCode(ResultStatus.ALARM_ACTION_JAR_NOTFOUND_ERROR_CODE, pathJar);
//				return loadResult;
//			}
//			URL[] urls = new URL[] {file.toURL()};
//			URLClassLoader child = new URLClassLoader(urls, this.getClass().getClassLoader());			
//			Class classToLoad = Class.forName(className, true, child);
//			Method actionMethod = classToLoad.getDeclaredMethod("doAction", String.class, String.class);
//			Object actionInstance = classToLoad.newInstance();
//			loadResult.setAlarmAction(actionInstance);
//			loadResult.setUrlClassLoader(child);
//		}catch(Exception ex) {
//			ex.printStackTrace();
//		}		
//	}
	
}
