package com.exam.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exam.dao.UsersMapper;
import com.exam.entity.Users;
import com.exam.service.UsersService;

@Service
public class UsersServiceImpl implements UsersService {
	
	@Autowired
	private UsersMapper usersMapper;

	//2.2查询用户数据 实现
	@Override
	public List<Users> findUserInfo() {
		
		//2.3查询用户数据Dao（数据持久层）
		List<Users> findUserInfo = usersMapper.findUserInfo();
		
		return findUserInfo;
	}

	@Override
	public int deleteByPrimaryKey(String userId) {
		// TODO Auto-generated method stub
		return usersMapper.deleteByPrimaryKey(userId);
	}

	@Override
	public boolean addUser(Users user) {
		try{
			usersMapper.insert(user);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	@Override
	public Users findUserInfoByUserId(String userId) {
		return usersMapper.selectByPrimaryKey(userId);
	}

	@Override
	public int updateUsersInfo(Users user) {
		return usersMapper.updateByPrimaryKeySelective(user);
	}
}
