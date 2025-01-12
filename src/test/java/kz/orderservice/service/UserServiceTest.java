package kz.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import kz.orderservice.converter.UserConverter;
import kz.orderservice.dto.auth.AuthenticationRequestDto;
import kz.orderservice.dto.auth.AuthenticationResponseDto;
import kz.orderservice.dto.auth.RegisterRequestDto;
import kz.orderservice.dto.auth.RegisterResponseDto;
import kz.orderservice.entity.user.Role;
import kz.orderservice.entity.user.User;
import kz.orderservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserConverter userConverter;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;

    private RegisterRequestDto registerRequestDto;
    private User user;
    private RegisterResponseDto registerResponseDto;
    private AuthenticationRequestDto authenticationRequestDto;
    private String jwtToken;
    private SecurityContext securityContextMock;
    private Authentication authenticationMock;
    private UserDetails userDetailsMock;
    private String username;

    @BeforeEach
    void setUp() {
        username = "John";
        jwtToken = "JWT token";
        long userId = 1L;
        String password = "password";
        Role userRole = Role.USER;

        registerRequestDto = RegisterRequestDto.builder()
                .username(username)
                .password(password)
                .build();

        user = User.builder()
                .id(userId)
                .username(username)
                .password(password)
                .role(userRole)
                .build();

        registerResponseDto = RegisterResponseDto.builder()
                .id(userId)
                .username(username)
                .role(userRole)
                .build();

        authenticationRequestDto = AuthenticationRequestDto.builder()
                .username(username)
                .password(password)
                .build();

        securityContextMock = Mockito.mock(SecurityContext.class);
        authenticationMock = Mockito.mock(Authentication.class);
        userDetailsMock = Mockito.mock(UserDetails.class);
    }

    @Test
    @DisplayName("Testing register method for successful execution")
    void testRegisterSuccessful() {
        when(userRepository.existsByUsername(registerRequestDto.getUsername())).thenReturn(false);
        when(userConverter.registerRequestToEntity(registerRequestDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userConverter.entityToRegisterResponse(user)).thenReturn(registerResponseDto);

        RegisterResponseDto registerResponse = userService.register(registerRequestDto);

        verify(userRepository, times(1)).existsByUsername(registerRequestDto.getUsername());
        verify(userRepository, times(1)).save(user);
        verify(userConverter, times(1)).entityToRegisterResponse(user);

        assertEquals(user.getId(), registerResponse.getId());
        assertEquals(user.getRole(), registerResponse.getRole());
    }

    @Test
    @DisplayName("Testing register method when user already exists")
    void testRegisterWhenUserAlreadyExists() {
        when(userRepository.existsByUsername(registerRequestDto.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(registerRequestDto));

        verify(userRepository, times(1)).existsByUsername(registerRequestDto.getUsername());
    }

    @Test
    @DisplayName("Testing authenticate method for successful execution")
    void testAuthenticateSuccessful() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername(authenticationRequestDto.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(jwtToken);

        AuthenticationResponseDto response = userService.authenticate(authenticationRequestDto);

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername(authenticationRequestDto.getUsername());
        verify(jwtService, times(1)).generateToken(user);

        assertEquals(jwtToken, response.getToken());
    }

    @Test
    @DisplayName("Testing authenticate method when authentication fails")
    void testAuthenticateAuthenticationFails() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> userService.authenticate(authenticationRequestDto));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Testing authenticate method when user is not found")
    void testAuthenticateUserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername(authenticationRequestDto.getUsername())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.authenticate(authenticationRequestDto));

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByUsername(authenticationRequestDto.getUsername());
    }

    @Test
    @DisplayName("Testing provideAdminRole method for successful execution with MockedStatic")
    void testProvideAdminRoleSuccessfulWithMockedStatic() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);
            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
            when(userDetailsMock.getUsername()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

            userService.provideAdminRole();

            assertEquals(Role.ADMIN, user.getRole());

            verify(userRepository, times(1)).findByUsername(username);
            verify(userRepository, times(1)).save(user);
        }
    }

    @Test
    @DisplayName("Testing provideAdminRole method when user is not found with MockedStatic")
    void testProvideAdminRoleUserNotFoundWithMockedStatic() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);
            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
            when(userDetailsMock.getUsername()).thenReturn(username);
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> userService.provideAdminRole());

            verify(userRepository, times(1)).findByUsername(username);
        }
    }
}