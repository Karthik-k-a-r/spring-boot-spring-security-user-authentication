package com.craft.authentication.controller;

import com.craft.authentication.constants.Constants;
import com.craft.authentication.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserDetailsController {
    private final IUserService userService;

    @GetMapping("/{email}")
    @PreAuthorize(value = "hasAuthority('ADMIN') or authentication.principal.email.equals(#email)")
    public ResponseEntity<Object> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.getUserByEmail(email),
                HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize(value = "hasAuthority('ADMIN')")
    public ResponseEntity<Object> getAllUsers(
            @RequestParam(value = "pageNo",defaultValue = Constants.Pagination.DEFAULT_PAGE_NUMBER,required = false) int pageNo,
            @RequestParam(value = "pageSize",defaultValue = Constants.Pagination.DEFAULT_PAGE_SIZE,required = false) int pageSize
    ) {
        return new ResponseEntity<>(userService.getAllUsers(pageNo,pageSize),
                HttpStatus.OK);
    }
}
