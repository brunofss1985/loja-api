package com.loja.loja_api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PasswordChangeDto {
    private String currentPassword;
    private String newPassword;
}