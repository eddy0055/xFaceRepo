package com.xpand.xface.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xpand.xface.bean.PersonSummaryInfo;
import com.xpand.xface.entity.PersonInfo;

@Repository
public interface PersonInfoDAO extends JpaRepository<PersonInfo, Integer>,JpaSpecificationExecutor<PersonInfo>{
	public PersonInfo findByPersonId(Integer personId);	
	public PersonInfo findByCertificateNo(String certificateNo);	
	public PersonInfo findByHwPeopleId(String hwPeopleId);
	
	@Query("SELECT new com.xpand.xface.bean.PersonSummaryInfo(DATE_FORMAT(hh.dateCreated,'%Y%m%d%H%i') as dateCreated "
			+ ", COUNT(hh.fullName) as summaryCnt) FROM PersonInfo hh GROUP BY DATE_FORMAT(hh.dateCreated,'%Y%m%d%H%i')")	        
	public List<PersonSummaryInfo> findPersonSummaryInfo(); 

}
