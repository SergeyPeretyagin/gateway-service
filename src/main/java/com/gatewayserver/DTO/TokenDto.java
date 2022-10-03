package com.gatewayserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Token Dto")
public class TokenDto {
    @Schema(description = "Client id", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0OGM5NTRiZi05MDUzLTQ1MTgtOWJlMy01MmRhMmRjZGZhMWMiLCJleHAiOjE2NjEzMTY4MDl9.7A9kgfCMg5kRLwUNDsTJrZ3J2aMHiqphWZM14u2-r-k" )
    private String accessToken;
    @Schema(description = "Client password", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI0OGM5NTRiZi05MDUzLTQ1MTgtOWJlMy01MmRhMmRjZGZhMWMiLCJleHAiOjE2NjEzMTgwMTB9.xFh9gs-i4dVayQECdUa5ReTkbmzz8zcS5HGnSDfXyQ4")
    private String refreshToken;


    @Override
    public String toString() {
        return "TokenDto{" +
                "accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
