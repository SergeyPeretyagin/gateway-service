package com.gatewayserver.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Client data")
public class RequestUserDto {

    @NotNull
    @Pattern(regexp = "44\\d{10}|GB[RDOSPN]\\s?\\d{9}")
    @Schema(description = "Phone number or passport number", example = "GBN998871111")
    private String login;


    @Pattern(regexp = "^(?:(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])" +
            "|(?=.*[0-9])(?=.*[a-z])(?=.*[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~])" +
            "|(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~])" +
            "|(?=.*[0-9])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~])).{5,20}")
    @NotNull
    @Size(min = 5,max = 20)
    @Schema(description = "Client password", example = "Milk12345")
    private String password;

    @Override
    public String toString() {
        return "RequestUserDto{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
