package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.PersonCertification;

@Repository
public interface PersonCertificationDAO extends JpaRepository<PersonCertification, Integer>{
	public PersonCertification findByCertificationName(String certificationName);
	
	@Modifying
	@Query("delete from PersonCertification where certificationId = :certificationId ") 
	public void deleteBypersonCertification(@Param("certificationId") Integer certificationId);
}
