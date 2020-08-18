package com.xpand.xface.dao;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.xpand.xface.entity.UserInfo;

@Repository
public interface UserInfoDAO extends JpaRepository<UserInfo, Integer>{
	public UserInfo findByUserName(String userName);
	public UserInfo findByUserId(Integer userId);
	public UserInfo findByUserNameAndPassword(String userName, String password);
	
	@Modifying
	@Query("delete from UserInfo u where u.userId = ?1")
	public void delete(Integer userId);
		
	@Modifying
	@Query("update UserInfo u set u.logInServer = ?1 where u.userId = ?2")
	public void updateLogInServer(String serverIp, Integer userId);
}
