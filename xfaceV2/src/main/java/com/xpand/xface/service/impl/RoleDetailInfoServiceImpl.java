
package com.xpand.xface.service.impl;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.RoleDetailInfoDAO;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.service.PermissionListService;
import com.xpand.xface.service.RoleDetailInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.util.CacheName;
import com.xpand.xface.util.LogUtil;

@SessionScope
@Component
public class RoleDetailInfoServiceImpl implements RoleDetailInfoService, Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Autowired
	RoleDetailInfoDAO roleDetailInfoDAO;	
	@Autowired
	RoleInfoService roleInfoService;
	@Autowired
	PermissionListService permissionListService;
	
	@Override
//	@Cacheable(value=CacheName.CACHE_OTHER, key="'roled_'+#roleDetailId")
	public RoleDetailInfo findByRoleDetailId(String transactionId, Integer roleDetailId) {	
		return this.roleDetailInfoDAO.findOne(roleDetailId);
	}
	
	@Override
	public ResultStatus deleteByRoleInfo(String transactionId,  RoleInfo roleInfo) {		
		ResultStatus result = new ResultStatus();
		try {
			Logger.info(this, LogUtil.getLogInfo(transactionId, "delete role detail under role name "+roleInfo.getRoleName()));
			if (roleInfo.getRoleId()==null) {
				Logger.debug(this, LogUtil.getLogDebug(transactionId, "role id is null no need to delete role detail"));
			}else {
				this.roleDetailInfoDAO.deleteByRoleInfo(roleInfo);
				Logger.info(this, LogUtil.getLogInfo(transactionId, "deletev3 role detail under role name "+roleInfo.getRoleName()+" success"));
			}			
		}catch(Exception ex) {
			Logger.error(this, LogUtil.getLogError(transactionId, "error while delete roleDetailInfo of role name "+roleInfo.getRoleName(), ex));
			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, "role detail of role name:"+roleInfo.getRoleName());
		}	
		return result;		
	}


	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'roled_'+#roleId")
	public List<RoleDetailInfo> findByRoleId(String transactionId, Integer roleId) {
		RoleInfo roleInfo = this.roleInfoService.findByRoleId(transactionId, roleId);
		if (roleInfo==null) {
			Logger.info(this, LogUtil.getLogInfo(LogUtil.getWebSessionId(), "cannot find roleId "+roleId+" on the database"));
			return null;
		}else {
			return this.roleDetailInfoDAO.findByRoleInfo(roleInfo);
		}		
	}

	@Override
	@Cacheable(value=CacheName.CACHE_OTHER, key="'roled_'+#roleCode")
	public List<RoleDetailInfo> findByRoleInfoRoleCode(String transactionId, String roleCode) {
		RoleInfo roleInfo = this.roleInfoService.findByRoleCode(transactionId, roleCode);		
		if (roleInfo==null) {
			Logger.info(this, LogUtil.getLogInfo(LogUtil.getWebSessionId(), "cannot find roleCode "+roleCode+" on the database"));
			return null;
		}else {
			return this.roleDetailInfoDAO.findByRoleInfo(roleInfo);
		}		
	}

	@Override
	public RoleInfo resetRoleDetailId(String transactionId, RoleInfo roleInfo) {
		//reset roleDetailId = null
		for (RoleDetailInfo roleDetail: roleInfo.getRoleDetailInfoList()) {
			roleDetail.setRoleInfo(roleInfo);
			roleDetail.setRoleDetailId(null);			
		}
		return roleInfo;
	}

	@Override
	public RoleInfo setPermissionObject(String transactionId, RoleInfo roleInfo) {
		for (RoleDetailInfo roleDetail: roleInfo.getRoleDetailInfoList()) {			
			roleDetail.setPermissionList(this.permissionListService.findByPermissionName(transactionId, roleDetail.getPermissionList().getPermissionName()));			
		}
		return roleInfo;
	}

	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'roled_'+#cacheKey")
	public void removeCacheByKey(String cacheKey) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, key="'roled_'+#cacheId")
	public void removeCacheById(Integer cacheId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	@CacheEvict(value=CacheName.CACHE_OTHER, allEntries=true)
	public void purgeCache() {
		// TODO Auto-generated method stub
	}
	
	//backup code
//	@Override
//	public ResultStatus updateByRoleInfo(String transactionId, RoleInfo roleInfo) {
//		ResultStatus result = new ResultStatus();
//		try {
//			Logger.debug(this, LogUtil.getLogDebug(transactionId, "update role detail under role "+roleInfo.getRoleName()));
//			for (RoleDetailInfo roleDetailInfo: roleInfo.getRoleDetailInfos()) {
//				roleDetailInfo.setRoleInfo(roleInfo);
//				Logger.debug(this, LogUtil.getLogDebug(transactionId, "create new role detail with permission "+roleDetailInfo.getPermissionList().getPermissionName()));
//				this.roleDetailInfoDAO.save(roleDetailInfo);			
//				Logger.debug(this, LogUtil.getLogDebug(transactionId, "create new role detail with permission "+roleDetailInfo.getPermissionList().getPermissionName())+" success");
//			}
//			Logger.info(this, LogUtil.getLogInfo(transactionId, "create role detail "+roleInfo.getRoleDetailInfos().size()+" of role "+roleInfo.getRoleName()+" success"));
//		}catch (Exception ex) {
//			Logger.error(this, LogUtil.getLogError(transactionId, "error while update roleDetailInfo of role name "+roleInfo.getRoleName(), ex));
//			result.setStatusCode(ResultStatus.DB_UPDATE_ERROR_CODE, "role detail of role name:"+roleInfo.getRoleName());			
//		}
//		Logger.info(this, LogUtil.getLogInfo(transactionId, "update roleDetailInfo done with result "+result.toString()));
//		return result;
//	}
}
