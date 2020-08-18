package com.xpand.xface.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.PersonCategory;

@Repository
public interface PersonCategoryDAO extends JpaRepository<PersonCategory, Integer>{
	public PersonCategory findByCategoryName(String categoryName);
	
	@Modifying
	@Query("delete from PersonCategory where categoryId = :categoryId ") 
	public void deleteBypersonCategory(@Param("categoryId") Integer categoryId);
	
	//@Modifying
	//@Query("delete from PersonTitle where titleId = :titleId ") 
	//public void deleteBypersonTitle(@Param("titleId") Integer titleId);
}
