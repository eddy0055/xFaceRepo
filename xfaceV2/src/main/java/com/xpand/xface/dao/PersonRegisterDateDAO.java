package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.PersonInfo;
import com.xpand.xface.entity.PersonRegisterDate;

@Repository
public interface PersonRegisterDateDAO extends JpaRepository<PersonRegisterDate, Integer>{
	@Modifying
	@Query("DELETE FROM PersonRegisterDate WHERE personInfo = :personInfo ") 
	public void deleteByPersonInfo(@Param("personInfo") PersonInfo personInfo);
}
