package com.plenamente.sgt.domain.dto.UserDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequest(
        @NotBlank @Size(min = 4, max = 100, message = "La contraseña actual debe tener entre 4 y 100 caracteres")
        String currentPassword,

        @NotBlank @Size(min = 4, max = 100, message = "La nueva contraseña debe tener entre 4 y 100 caracteres")
        String newPassword
) {}
