package com.tpms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tpms.dto.UserDto;
import com.tpms.entity.Role;
import com.tpms.entity.User;
import com.tpms.exception.ResourceNotFoundException;
import com.tpms.repository.RoleRepository;
import com.tpms.repository.UserRepository;
import com.tpms.service.UserService;

/**
 * 
 * @author kiran.swain
 *
 */
@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;
	
	@Autowired
    private RoleRepository roleRepository;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public User saveUser(UserDto user) {
		
	
		User u1 = new User();
		u1.setUserFullName(user.getUserFullName());
		u1.setUserName(user.getUserName());
		u1.setPhoneNo(user.getPhoneNo());
		u1.setEmail(user.getEmail());
		
		
		if(user.getUserId() != 0) {
			User existUser=userRepository.findById(user.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User Not found with id = " + user.getUserId()));
				 u1.setPassword(existUser.getPassword());
                 u1.setIsFirstLogin(existUser.getIsFirstLogin());
		         u1.setUserId(user.getUserId());
		}
		
		else {
			u1.setIsFirstLogin(true);
			u1.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		Role role = roleRepository.findById(user.getRoleId()).get();
		if(role != null) {
			u1.setRole(role);
		}else {
			throw new RuntimeException("Role with ID " + user.getRoleId() + " not found");
		}
		u1.setDeletedFlag(false);
		
		return userRepository.save(u1);
	}

	@Override
	public List<User> getUserDetails() {
		
		return userRepository.findAll();
	}

	@Override
	public UserDto getUserById(Integer userId) {
		UserDto user=new UserDto();
		User user1= userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User Not found with id = " + userId));
		user.setUserName(user1.getUserName());
		user.setUserFullName(user1.getUserFullName());
		user.setRoleId(user1.getRole().getRoleId());
		user.setPhoneNo(user1.getPhoneNo());
		user.setEmail(user1.getEmail());
		
		return user;
	}

	@Override
	public void deleteUserById(Integer intUserId,Boolean deletedFlag) {

		userRepository.deleteUser(intUserId,deletedFlag);
	}

	@Override
	public String getStatusOfDuplicacyCheck(String userName) {
		
		String result="";
		Integer count=userRepository.getDuplicateCount(userName);
		if(count>0) {
			result="Exist";
		}
		else
			result="NotExist";
		return result;
	}

}
