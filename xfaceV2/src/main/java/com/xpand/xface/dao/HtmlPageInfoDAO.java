package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.HtmlPageInfo;

@Repository
public interface HtmlPageInfoDAO extends JpaRepository<HtmlPageInfo, String>{
	HtmlPageInfo findByPageId(Integer pageId);
	HtmlPageInfo findByPageCode(String pageCode);
	HtmlPageInfo findOneByPageCode(String transactionid, String pageCode);
	
	@Modifying
	@Query("delete from HtmlPageInfo where pageId = :pageId ") 
	public void deleteByHtmlPageInfo(@Param("pageId") Integer pageId);

}
