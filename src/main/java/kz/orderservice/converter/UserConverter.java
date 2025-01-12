package kz.orderservice.converter;

import kz.orderservice.dto.auth.RegisterRequestDto;
import kz.orderservice.dto.auth.RegisterResponseDto;
import kz.orderservice.entity.user.Role;
import kz.orderservice.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    public User registerRequestToEntity(RegisterRequestDto registerRequestDto) {
        return User.builder()
                .username(registerRequestDto.getUsername())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(Role.USER)
                .build();
    }

    public RegisterResponseDto entityToRegisterResponse(User user) {
        return RegisterResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}