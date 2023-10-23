package com.craft.authentication.service;

import com.craft.authentication.dto.request.RegisterRequest;
import com.craft.authentication.dto.response.UserDto;
import com.craft.authentication.dto.SuccessResponse;
import com.craft.authentication.dto.response.UserResponse;

public interface IUserService {
    SuccessResponse register(RegisterRequest registerRequest) throws Exception;
    SuccessResponse getRecoveryCode(String email) throws Exception;

    UserDto getUserByEmail(String email);

    UserResponse getAllUsers(int pageNo,int pageSize);

}
