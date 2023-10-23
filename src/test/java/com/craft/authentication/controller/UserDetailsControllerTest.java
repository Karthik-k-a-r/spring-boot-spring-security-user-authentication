package com.craft.authentication.controller;

import com.craft.authentication.dto.response.UserDto;
import com.craft.authentication.dto.response.UserResponse;
import com.craft.authentication.service.IUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserDetailsController.class})
@ExtendWith(SpringExtension.class)
class UserDetailsControllerTest {
    @MockBean
    private IUserService iUserService;

    @Autowired
    private UserDetailsController userDetailsController;

    @Test
    void testGetUserByEmail() throws Exception {
        when(iUserService.getUserByEmail("test@example.com"))
                .thenReturn(UserDto.builder().email("test@example.com").firstName("test").lastName("demo").build());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/users/{email}",
                "test@example.com");
        MockMvcBuilders.standaloneSetup(userDetailsController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"email\":\"test@example.com\",\"firstName\":\"test\",\"lastName\":\"demo\"}"));
    }

    @Test
    void testGetAllUsers() throws Exception {
        UserResponse.UserResponseBuilder totalPagesResult = UserResponse.builder()
                .last(true)
                .pageNo(1)
                .pageSize(3)
                .totalElements(1L)
                .totalPages(1);
        when(iUserService.getAllUsers(anyInt(), anyInt())).thenReturn(totalPagesResult.users(new ArrayList<>()).build());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/users");
        MockMvcBuilders.standaloneSetup(userDetailsController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"users\":[],\"pageNo\":1,\"pageSize\":3,\"totalElements\":1,\"totalPages\":1,\"last\":true}"));
    }
}

