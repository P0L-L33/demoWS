package com.appsdeveloperblog.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.service.AddressService;
import com.appsdeveloperblog.app.ws.service.UserService;
import com.appsdeveloperblog.app.ws.shared.dto.AddressDto;
import com.appsdeveloperblog.app.ws.shared.dto.UserDto;
import com.appsdeveloperblog.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appsdeveloperblog.app.ws.ui.model.response.AddressesRest;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.model.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.model.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.model.response.UserRest;



@RestController
@RequestMapping("users") // http://localhost:8080/users/
public class UserController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	AddressService addressService;
	
	@Autowired
	AddressService addressesService;
	
	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_ATOM_XML_VALUE, 
			MediaType.APPLICATION_JSON_VALUE}) //will return first media type if Accept header is not provided
	public UserRest getUser(@PathVariable String id) 
	{
		UserRest returnValue = new UserRest();
		
		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, returnValue);
		
		return returnValue;
	}
	
	@GetMapping(produces = { MediaType.APPLICATION_ATOM_XML_VALUE, 
			MediaType.APPLICATION_JSON_VALUE})
	public List<UserRest> getUser(@RequestParam(value = "page", defaultValue="0") int page, 
			@RequestParam(value = "limit", defaultValue = "25") int limit) 
	{
		List<UserRest> returnValue = new ArrayList<>();
		
		List<UserDto> users = userService.getUsers(page, limit);
		
		for(UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		
		return returnValue;
		//return "Thank you for flying Montero today";
	}
	
	@PostMapping(
			consumes = { MediaType.APPLICATION_ATOM_XML_VALUE, 
					MediaType.APPLICATION_JSON_VALUE},
			produces = { MediaType.APPLICATION_ATOM_XML_VALUE, 
					MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception 
	{
		UserRest returnValue = new UserRest();
		
		//if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		//UserDto userDto = new UserDto();
		//BeanUtils.copyProperties(userDetails, userDto);
		
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);
		
		UserDto createdUser = userService.createUser(userDto);
		//BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);
		
		
		return returnValue;
	}
	
	
	@PutMapping(
			path = "/{id}",
			consumes = { MediaType.APPLICATION_ATOM_XML_VALUE, 
					MediaType.APPLICATION_JSON_VALUE},
			produces = { MediaType.APPLICATION_ATOM_XML_VALUE, 
					MediaType.APPLICATION_JSON_VALUE}
			)
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails)
	{
		UserRest returnValue = new UserRest();
		
		//if(userDetails.getFirstName().isEmpty()) throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		
		UserDto updatedUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updatedUser, returnValue);
		
		return returnValue;
	}
	
	@DeleteMapping(
			path = "/{id}",
			produces = { MediaType.APPLICATION_ATOM_XML_VALUE, 
					MediaType.APPLICATION_JSON_VALUE}
			)
	public OperationStatusModel deleteUser(@PathVariable String id)
	{
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		
		userService.deleteUser(id);
		
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		
		return returnValue;
	}
	
	
	// http://localhost:8080/demo-ws-app/users/asdasdasdasd/addresses/ 
	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_ATOM_XML_VALUE, 
			MediaType.APPLICATION_JSON_VALUE}) //will return first media type if Accept header is not provided
	public List<AddressesRest> getUserAddresses(@PathVariable String id) 
	{
		
		List<AddressesRest> returnValue = new ArrayList<>();
		
		List<AddressDto> addressesDto = addressesService.getAddresses(id);
		
		if(addressesDto != null && !addressesDto.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
			ModelMapper modelMapper = new ModelMapper();
			returnValue = modelMapper.map(addressesDto, listType);
		}
		
		
		return returnValue;
	}
	
	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE, "application/hal+json" })
	public AddressesRest getUserAddress(@PathVariable String userId, @PathVariable String addressId) {
		AddressDto addressesDto = addressService.getAddress(addressId);

		ModelMapper modelMapper = new ModelMapper();
		
		return modelMapper.map(addressesDto, AddressesRest.class);
	}
}
