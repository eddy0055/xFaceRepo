package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.PersonCertificate;

@Repository
public interface PersonCertificateDAO extends JpaRepository<PersonCertificate, Integer>{
	public PersonCertificate findByCertificateCode(String certificateCode);
	
	@Modifying
	@Query("delete from PersonCertificate where certificateId = :certificateId ") 
	public void deleteBypersonCertificate(@Param("certificateId") Integer certificateId);
}
