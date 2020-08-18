package com.xpand.xface.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name="tbl_ipc_group_detail")
public class IPCGroupDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ipcgdId", nullable=false)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer ipcgdId;
		
	@ManyToOne(cascade = CascadeType.REFRESH, fetch=FetchType.EAGER)
	@JoinColumn(name="ipcgId")
	private IPCGroup ipcGroup;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name="ipcId")
	private HWIPC hwIPC;

	public Integer getIpcgdId() {
		return ipcgdId;
	}

	public void setIpcgdId(Integer ipcgdId) {
		this.ipcgdId = ipcgdId;
	}

	@JsonIgnore
	public IPCGroup getIpcGroup() {
		return ipcGroup;
	}

	public void setIpcGroup(IPCGroup ipcGroup) {
		this.ipcGroup = ipcGroup;
	}
	
	public HWIPC getHwIPC() {
		return hwIPC;
	}	
	public void setHwIPC(HWIPC hwIPC) {
		this.hwIPC = hwIPC;
	}

	@Override
	public String toString() {
		return "IPCGroupDetail [ipcgdId=" + ipcgdId + ", hwIPC=" + hwIPC.toString() + "]";
	}
}
