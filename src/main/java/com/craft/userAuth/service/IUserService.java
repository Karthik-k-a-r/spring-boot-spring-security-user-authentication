package com.craft.userAuth.service;

import com.craft.userAuth.dto.RegisterRequest;
import com.craft.userAuth.dto.UserDto;
import com.craft.userAuth.dto.UserResponse;


import java.util.List;

public interface IUserService {
    void register(RegisterRequest registerRequest) throws Exception;
    void getRecoveryCode(String email) throws Exception;

    UserDto getUserByEmail(String email);

    UserResponse getAllUsers(int pageNo,int pageSize);

}
