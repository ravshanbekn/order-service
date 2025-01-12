package kz.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import kz.orderservice.converter.UserConverter;
import kz.orderservice.dto.auth.AuthenticationRequest;
import kz.orderservice.dto.auth.AuthenticationResponse;
import kz.orderservice.dto.auth.RegisterRequest;
import kz.orderservice.dto.auth.RegisterResponse;
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

    public RegisterResponse register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("User with username: %s already exists"
                    .formatted(registerRequest.getUsername()) );
        }
        User user = userConverter.registerRequestToEntity(registerRequest);
        User savedUser = userRepository.save(user);
        return userConverter.entityToRegisterResponse(savedUser);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        User user = userRepository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Could not find user by username: " + authenticationRequest.getPassword()));
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
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