package com.xpand.xface.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
@Entity
@Table(name="tbl_html_page")
public class HtmlPageInfo extends EntityBase implements Serializable {
	private static final long serialVersionUID = 1L;	
	public static final int PAGE_TYPE_HTML = 0;
	public static final int PAGE_TYPE_REST_API = 1;
	public static final String HTML_PAGE_FORCE_CHANGE_PWD_CODE = "FCHANGE_PWD";
	@Id
	@Column(name="pageId", nullable=false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer pageId;
	
	@Column(name="pageURL", length=100 ,nullable=false)
	private String pageURL;
	@Column(name="pageType", nullable=false)	
	private Integer pageType; //0 = html, 1 = rest	
	@Column(name="pageDesc", length=100 ,nullable=false)
	private String pageDesc;
	@Column(name="pageCode", length=30 ,nullable=false)
	private String pageCode;
	@OneToMany(mappedBy="htmlPageInfo", fetch=FetchType.LAZY)
	private Set<RoleInfo> roleInfoList;
	
	public HtmlPageInfo() {}
	
	public HtmlPageInfo(HtmlPageInfo htmlPageInfo) {
		this.pageId = htmlPageInfo.pageId;
		this.pageCode = htmlPageInfo.pageCode;
		this.pageDesc = htmlPageInfo.pageDesc;
		this.pageType = htmlPageInfo.pageType;
		this.pageURL = htmlPageInfo.pageURL;
	}
	
	public Integer getPageId() {
		return pageId;
	}
	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}
	public String getPageURL() {
		return pageURL;
	}
	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}
	public Integer getPageType() {
		return pageType;
	}
	public void setPageType(Integer pageType) {
		this.pageType = pageType;
	}
	public Set<RoleInfo> getRoleInfoList() {
		return roleInfoList;
	}
	public void setRoleInfoList(RoleInfo roleInfoList) {
		this.roleInfoList = (Set<RoleInfo>) roleInfoList;
	}
	public String getPageDesc() {
		return pageDesc;
	}
	public void setPageDesc(String pageDesc) {
		this.pageDesc = pageDesc;
	}
	public String getPageCode() {
		return pageCode;
	}
	public void setPageCode(String pageCode) {
		this.pageCode = pageCode;
	}
	
}
