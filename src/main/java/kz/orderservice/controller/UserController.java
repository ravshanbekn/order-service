package kz.orderservice.controller;

import jakarta.validation.Valid;
import kz.orderservice.dto.auth.AuthenticationRequest;
import kz.orderservice.dto.auth.AuthenticationResponse;
import kz.orderservice.dto.auth.RegisterRequest;
import kz.orderservice.dto.auth.RegisterResponse;
import kz.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity
                .ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid AuthenticationRequest request) {
        return ResponseEntity
                .ok(userService.authenticate(request));
    }

    @PutMapping("/admin")
    public ResponseEntity<Void> getAdminRole(){
        userService.provideAdminRole();
        return ResponseEntity.ok()
                .build();
    }
}