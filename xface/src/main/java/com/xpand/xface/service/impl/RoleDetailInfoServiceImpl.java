
package com.xpand.xface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.bean.ResultStatus;
import com.xpand.xface.dao.RoleDetailInfoDAO;
import com.xpand.xface.entity.RoleDetailInfo;
import com.xpand.xface.entity.RoleInfo;
import com.xpand.xface.service.PermissionListService;
import com.xpand.xface.service.RoleDetailInfoService;
import com.xpand.xface.service.RoleInfoService;
import com.xpand.xface.util.LogUtil;

@Component
public class RoleDetailInfoServiceImpl implements RoleDetailInfoService{
	
	@Autowired
	RoleDetailInfoDAO roleDetailInfoDAO;	
	@Autowired
	RoleInfoService roleInfoService;
	@Autowired
	PermissionListService permissionListService;
	
	@Override
	public RoleDetailInfo findByRoleDetailId(Integer roleDetailId) {	
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
	public List<RoleDetailInfo> findByRoleId(Integer roleId) {
		RoleInfo roleInfo = this.roleInfoService.findByRoleId(roleId);
		if (roleInfo==null) {
			Logger.info(this, LogUtil.getLogInfo(LogUtil.getWebSessionId(), "cannot find roleId "+roleId+" on the database"));
			return null;
		}else {
			return this.roleDetailInfoDAO.findByRoleInfo(roleInfo);
		}		
	}

	@Override
	public List<RoleDetailInfo> findByRoleInfoRoleName(String roleName) {
		RoleInfo roleInfo = this.roleInfoService.findOneByRoleName(roleName);
		if (roleInfo==null) {
			Logger.info(this, LogUtil.getLogInfo(LogUtil.getWebSessionId(), "cannot find roleName "+roleName+" on the database"));
			return null;
		}else {
			return this.roleDetailInfoDAO.findByRoleInfo(roleInfo);
		}		
	}

	@Override
	public RoleInfo resetRoleDetailId(RoleInfo roleInfo) {
		//reset roleDetailId = null
		for (RoleDetailInfo roleDetail: roleInfo.getRoleDetailInfos()) {
			roleDetail.setRoleInfo(roleInfo);
			roleDetail.setRoleDetailId(null);			
		}
		return roleInfo;
	}

	@Override
	public RoleInfo setPermissionObject(RoleInfo roleInfo) {
		for (RoleDetailInfo roleDetail: roleInfo.getRoleDetailInfos()) {
			if (roleDetail.getPermissionList().getPermissionName()==null) {
				roleDetail.setPermissionList(this.permissionListService.findByPermissionId(roleDetail.getPermissionList().getPermissionId()));
			}
		}
		return roleInfo;
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
