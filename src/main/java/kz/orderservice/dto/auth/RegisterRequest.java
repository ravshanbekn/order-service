package kz.orderservice.dto.auth;

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
public class RegisterRequest {
    @NotBlank(message = "Username should not be blank")
    private String username;

    @NotBlank(message = "Password should not be blank")
    @Size(min = 8, message = "Password should be at least 8 characters")
    private String password;
}