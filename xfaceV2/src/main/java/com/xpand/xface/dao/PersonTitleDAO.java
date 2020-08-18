package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.xpand.xface.entity.PersonTitle;

@Repository
public interface PersonTitleDAO extends JpaRepository<PersonTitle, Integer>{
	public PersonTitle findByTitleCode(String titleCode);
	
	@Modifying
	@Query("delete from PersonTitle where titleId = :titleId ") 
	public void deleteByPersonTitle(@Param("titleId") Integer titleId);
}
