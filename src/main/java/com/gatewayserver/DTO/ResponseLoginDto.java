package com.gatewayserver.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseLoginDto {
    private UUID id;
    private String password;

    @Override
    public String toString() {
        return "ResponseDto{" +
                "UUID='" + id + '\'' +
                ", encryptedPassword='" + password + '\'' +
                '}';
    }
}
