package com.gatewayserver.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private String login;
    private String password;

    @Override
    public String toString() {
        return "UserDto{" +
                "email='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
