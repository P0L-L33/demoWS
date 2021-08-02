package com.appsdeveloperblog.app.ws.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.UserRepository;
import com.appsdeveloperblog.app.ws.io.entity.UserEntity;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.Utils;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;


@Service
public class UserServiceImplementation implements UserService {

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	Utils utils;
	
	//test comment
	@Override
	public UserDto createUser(UserDto user) {
		
		UserEntity storedUserDetailsCheck = userRepository.findByEmail(user.getEmail());
		if(storedUserDetailsCheck != null) throw new RuntimeException("Record already exists");
		
		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(user, userEntity);
		
		
		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword("test");
		userEntity.setEmailVerificationStatus(false);
		
		UserEntity storedUserDetails = userRepository.save(userEntity);
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(storedUserDetails, returnValue);
		
		return returnValue;
	}

}
