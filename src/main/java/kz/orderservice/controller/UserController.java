package kz.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kz.orderservice.dto.ErrorResponseDto;
import kz.orderservice.dto.auth.AuthenticationRequestDto;
import kz.orderservice.dto.auth.AuthenticationResponseDto;
import kz.orderservice.dto.auth.RegisterRequestDto;
import kz.orderservice.dto.auth.RegisterResponseDto;
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

    @Operation(
            summary = "User registration",
            description = "Registers a new user with the provided details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully registered",
                            content = @Content(
                                    schema = @Schema(implementation = RegisterResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registration data",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto request) {
        return ResponseEntity
                .ok(userService.register(request));
    }

    @Operation(
            summary = "User login",
            description = "Authenticates a user and returns an authentication token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentication successful",
                            content = @Content(
                                    schema = @Schema(implementation = AuthenticationResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid login credentials",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody @Valid AuthenticationRequestDto request) {
        return ResponseEntity
                .ok(userService.authenticate(request));
    }

    @Operation(
            summary = "Assign admin role",
            description = "Assigns the admin role to the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Admin role assigned successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request")
            }
    )
    @PutMapping("/admin")
    public ResponseEntity<Void> getAdminRole(){
        userService.provideAdminRole();
        return ResponseEntity.ok()
                .build();
    }
}