package com.craft.userAuth.controller;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.dto.UserDto;
import com.craft.userAuth.dto.UserResponse;
import com.craft.userAuth.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserDetailsController {

    @Autowired
    private IUserService userService;

    @GetMapping("/{email}")
//    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email, Authentication authentication) {
        return new ResponseEntity<>(userService.getUserByEmail(email),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserResponse> getAllUsers(
            @RequestParam(value = "pageNo",defaultValue = Constants.Pagination.DEFAULT_PAGE_NUMBER,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = Constants.Pagination.DEFAULT_PAGE_SIZE,required = false) int pageSize
    ) {
        return new ResponseEntity<>(userService.getAllUsers(pageNo,pageSize),
                HttpStatus.OK);
    }
}
