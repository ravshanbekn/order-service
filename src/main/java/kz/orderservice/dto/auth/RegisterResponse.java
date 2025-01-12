package kz.orderservice.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import kz.orderservice.entity.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    @Schema(description = "Unique identifier of the registered user", example = "1")
    private Long id;

    @Schema(description = "Username of the registered user", example = "John")
    private String username;

    @Schema(description = "Role assigned to the registered user", example = "USER")
    private Role role;
}