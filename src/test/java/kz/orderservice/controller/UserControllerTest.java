package kz.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.orderservice.dto.auth.AuthenticationRequestDto;
import kz.orderservice.dto.auth.AuthenticationResponseDto;
import kz.orderservice.dto.auth.RegisterRequestDto;
import kz.orderservice.dto.auth.RegisterResponseDto;
import kz.orderservice.entity.user.Role;
import kz.orderservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RegisterRequestDto registerRequestDto;
    private RegisterResponseDto registerResponseDto;
    private AuthenticationRequestDto authenticationRequestDto;
    private AuthenticationResponseDto authenticationResponseDto;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

        Long userId = 1L;
        String password = "password";
        String username = "username";
        String token = "token";

        registerRequestDto = RegisterRequestDto.builder()
                .username(username)
                .password(password)
                .build();

        registerResponseDto = RegisterResponseDto.builder()
                .role(Role.USER)
                .id(userId)
                .username(username)
                .build();

        authenticationRequestDto = AuthenticationRequestDto.builder()
                .username(username)
                .password(password)
                .build();

        authenticationResponseDto = AuthenticationResponseDto.builder()
                .token(token)
                .build();
    }

    @Test
    @DisplayName("Testing register controller for successful execution")
    void shouldRegisterUserSuccessfully() throws Exception {
        when(userService.register(registerRequestDto)).thenReturn(registerResponseDto);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(registerResponseDto.getId()))
                .andExpect(jsonPath("$.username").value(registerResponseDto.getUsername()))
                .andExpect(jsonPath("$.role").value(registerResponseDto.getRole().name()));

        verify(userService, times(1)).register(registerRequestDto);
    }

    @Test
    @DisplayName("Testing register controller for bad request when input data is invalid")
    void shouldReturnBadRequestForInvalidRegisterData() throws Exception {
        RegisterRequestDto invalidRegisterRequestDto = new RegisterRequestDto();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRegisterRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Testing authenticate controller for successful execution")
    void shouldAuthenticateUserSuccessfully() throws Exception {
        when(userService.authenticate(authenticationRequestDto)).thenReturn(authenticationResponseDto);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(authenticationResponseDto.getToken()));

        verify(userService, times(1)).authenticate(authenticationRequestDto);
    }

    @Test
    @DisplayName("Testing authenticate controller for bad request when login credentials are invalid")
    void shouldReturnBadRequestForInvalidCredentials() throws Exception {
        AuthenticationRequestDto invalidAuthenticationRequest = new AuthenticationRequestDto();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidAuthenticationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Testing getAdminRole controller for successful execution")
    void shouldAssignAdminRoleSuccessfully() throws Exception {
        doNothing().when(userService).provideAdminRole();

        mockMvc.perform(put("/admin"))
                .andExpect(status().isOk());

        verify(userService, times(1)).provideAdminRole();
    }
}