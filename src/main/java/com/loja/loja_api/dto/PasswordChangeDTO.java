package com.loja.loja_api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PasswordChangeDTO {
    private String currentPassword;
    private String newPassword;
}