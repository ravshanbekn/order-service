package kz.orderservice.converter;

import kz.orderservice.dto.auth.RegisterRequest;
import kz.orderservice.dto.auth.RegisterResponse;
import kz.orderservice.entity.user.Role;
import kz.orderservice.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    public User registerRequestToEntity(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
    }

    public RegisterResponse entityToRegisterResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}