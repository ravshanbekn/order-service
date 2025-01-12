package kz.orderservice.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "Username should not be blank")
    @Schema(description = "The username for authentication", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 8, message = "Password should be at least 8 characters")
    @Schema(description = "The password for authentication", example = "password", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}