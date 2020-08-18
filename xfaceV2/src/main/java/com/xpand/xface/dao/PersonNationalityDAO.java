package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.PersonNationality;

@Repository
public interface PersonNationalityDAO extends JpaRepository<PersonNationality, Integer>{
	public PersonNationality findByNationalityCode(String nationalityCode);
	public PersonNationality findByNationalityName(String nationalityName);
	
	@Modifying
	@Query("delete from PersonNationality where nationalityId = :nationalityId ") 
	public void deleteByPersonNationality(@Param("nationalityId") Integer nationalityId);
}
