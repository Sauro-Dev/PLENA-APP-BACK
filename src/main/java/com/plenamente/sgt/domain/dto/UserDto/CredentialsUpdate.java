package com.plenamente.sgt.domain.dto.UserDto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record CredentialsUpdate(
        @NotBlank @Length(max = 20) String username,
        @NotBlank @Length(min = 6) String newPassword
) {
}
