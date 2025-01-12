package kz.orderservice.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {

    @Schema(description = "JWT token generated upon successful authentication",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjg2MjQ2ODAwLCJleHAiOjE2ODYyNTA0MDB9.Pq7H3oMe-FJwAX2NO_3XTNNkB5yCmYnSPZVfD2kUIxU")
    private String token;
}
