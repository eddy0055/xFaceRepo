package com.xpand.xface.dao;

import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.UserInfo;

@Repository
public interface UserInfoDAO extends JpaRepository<UserInfo, Integer>{
	public UserInfo findByUserName(String userName);
	public UserInfo findByUserId(Integer userId);
	public UserInfo findByUserNameAndPassword(String userName, String password);
	
	
	
	@Override
	@Modifying
	@Query("delete from UserInfo u where u.userId = :userId")
	public void delete(@Param("userId") Integer userId);
		
	@Modifying
	@Query("update UserInfo u set u.logInServer = :serverIp where u.userId = :userId")
	public void updateLogInServer(@Param("serverIp") String serverIp, @Param("userId") Integer userId);
	
	@Modifying
	@Query("update UserInfo u set u.password = :newPwd, u.passwordExpire = :pwdExpire where u.userId = :userId")
	public void changePwd(@Param("newPwd") String newPwd, @Param("pwdExpire") Date pwdExpire, @Param("userId") Integer userId);
}
