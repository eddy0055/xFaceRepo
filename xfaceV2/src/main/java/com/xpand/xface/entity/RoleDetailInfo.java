package com.xpand.xface.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_role_detail")
public class RoleDetailInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="roleDetailId", nullable=false)
	private Integer roleDetailId;
	
	@ManyToOne
	@JoinColumn(name="permissionId")
	private PermissionList permissionList;

	@ManyToOne
	@OnDelete(action=OnDeleteAction.CASCADE)
	@JoinColumn(name="roleId")
	private RoleInfo roleInfo;
	
	public RoleDetailInfo(RoleDetailInfo roleDetailInfo) {
		if (roleDetailInfo.getRoleDetailId()!=null) {
			this.roleDetailId = roleDetailInfo.getRoleDetailId();			
		}
		this.permissionList  = roleDetailInfo.getPermissionList();
		this.roleInfo = roleDetailInfo.getRoleInfo();
	}

	public RoleDetailInfo() {}
	
	public Integer getRoleDetailId() {
		return roleDetailId;
	}

	public void setRoleDetailId(Integer roleDetailId) {
		this.roleDetailId = roleDetailId;
	}
	
	@JsonIgnore
	public RoleInfo getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(RoleInfo roleInfo) {
		this.roleInfo = roleInfo;
	}

	public PermissionList getPermissionList() {
		return permissionList;
	}

	public void setPermissionList(PermissionList permissionList) {
		this.permissionList = permissionList;
	}

	@Override
	public String toString() {
		return "RoleDetailInfo [roleDetailId=" + roleDetailId + ", permissionList=" + permissionList.toString() + "]";
	}
	
//	public static String arrayToString(Set<RoleDetailInfo> roleDeatilInfos) {
//		StringBuilder sb = new StringBuilder();
//		sb.append(roleDeatilInfos.toString()+",");		
//		return sb.toString();
//	}
}
