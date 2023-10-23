package com.craft.authentication.controller;

import com.craft.authentication.dto.SuccessResponse;
import com.craft.authentication.dto.request.LoginRequest;
import com.craft.authentication.dto.request.PasswordResetRequest;
import com.craft.authentication.dto.request.RegisterRequest;
import com.craft.authentication.dto.response.AuthenticationResponse;
import com.craft.authentication.service.IAuthService;
import com.craft.authentication.service.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserController.class})
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @MockBean
    private IAuthService iAuthService;

    @MockBean
    private IUserService iUserService;

    @Autowired
    private UserController userController;

    @Test
    void testRegister() throws Exception {
        RegisterRequest registerRequest =
                RegisterRequest.builder()
                        .email("test@example.com")
                        .firstName("test")
                        .lastName("demo")
                        .password("Test@123")
                        .build();

        String content = (new ObjectMapper()).writeValueAsString(registerRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(201));
    }

    @Test
    void testVerifyAccount() throws Exception {
        when(iAuthService.verifyAccount("1234"))
                .thenReturn(SuccessResponse.builder().success("Success").build());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/user/account/verify/{token}",
                "1234");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"success\":\"Success\"}"));
    }

    @Test
    void testAuthenticate() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("Test@123")
                .build();

        when(iAuthService.login(loginRequest))
                .thenReturn(AuthenticationResponse.builder().token("1234").build());


        String content = (new ObjectMapper()).writeValueAsString(loginRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"token\":\"1234\"}"));
    }

    @Test
    void testGetRecoveryCode() throws Exception {
        when(iUserService.getRecoveryCode("test@example.com"))
                .thenReturn(SuccessResponse.builder().success("Success").build());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/v1/user/account/recovery/{email}",
                "test@example.com");
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"success\":\"Success\"}"));
    }

    @Test
    void testResetPassword() throws Exception {
        PasswordResetRequest passwordResetRequest = PasswordResetRequest.builder()
                .newPassword("Demo@123")
                .recoveryCode("1234")
                .build();

        when(iAuthService.resetPassword(passwordResetRequest))
                .thenReturn(SuccessResponse.builder().success("Success").build());

        String content = (new ObjectMapper()).writeValueAsString(passwordResetRequest);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/v1/user/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(userController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"success\":\"Success\"}"));
    }

}

