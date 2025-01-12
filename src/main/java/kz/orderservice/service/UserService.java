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
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtService jwtService;
    private final UserConverter userConverter;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new IllegalArgumentException("User with username: %s already exists"
                    .formatted(registerRequestDto.getUsername()) );
        }
        User user = userConverter.registerRequestToEntity(registerRequestDto);
        User savedUser = userRepository.save(user);
        return userConverter.entityToRegisterResponse(savedUser);
    }

    public AuthenticationResponseDto authenticate(AuthenticationRequestDto authenticationRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDto.getUsername(),
                        authenticationRequestDto.getPassword()
                )
        );
        User user = userRepository.findByUsername(authenticationRequestDto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by username: " + authenticationRequestDto.getPassword()));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDto.builder()
                .token(jwtToken)
                .build();
    }

    public void provideAdminRole() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by username: " + username));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }
}