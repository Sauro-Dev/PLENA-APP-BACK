package com.plenamente.sgt.domain.dto.UserDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record ForgotPasswordRequest(
        @NotBlank @Length(max = 20) String username,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotBlank @Length(min = 6) String newPassword
) {}
