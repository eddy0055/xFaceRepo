package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="tbl_permission_list")
public class PermissionList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="permissionId", nullable=false)	
	private Integer permissionId;
	
	@Column(name="permissionGroup",length=50,nullable=false)
	private String permissionGroup;
	
	@Column(name="permissionName",length=50,nullable=false)
	private String permissionName;
	
	@Column(name="permissionGUI",length=50,nullable=false)
	private String permissionGUI;
	
	@OneToMany(mappedBy="permissionList")
	private Set<RoleDetailInfo> roleDetailInfoList;

	public Integer getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Integer permissionId) {
		this.permissionId = permissionId;
	}

	public String getPermissionGroup() {
		return permissionGroup;
	}

	public void setPermissionGroup(String permissionGroup) {
		this.permissionGroup = permissionGroup;
	}

	public String getPermissionName() {
		return permissionName;
	}

	public void setPermissionName(String permissionName) {
		this.permissionName = permissionName;
	}

	public String getPermissionGUI() {
		return permissionGUI;
	}

	public void setPermissionGUI(String permissionGUI) {
		this.permissionGUI = permissionGUI;
	}

	@JsonIgnore
	public Set<RoleDetailInfo> getRoleDetailInfoList() {
		return roleDetailInfoList;
	}

	public void setRoleDetailInfoList(Set<RoleDetailInfo> roleDetailInfoList) {
		this.roleDetailInfoList = roleDetailInfoList;
	}

	@Override
	public String toString() {
		return "PermissionList [permissionId=" + permissionId + ", permissionGroup=" + permissionGroup
				+ ", permissionName=" + permissionName + ", permissionGUI=" + permissionGUI + "]";
	}
		
}
