package com.plenamente.sgt.domain.dto.UserDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record UpdateUserDto(
        @NotBlank @Length(max = 20) String username,
        @Length(max = 100) String password,
        @NotBlank @Length(max = 100) String name,
        @Length(max = 100) String paternalSurname,
        @Length(max = 100) String maternalSurname,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotBlank @Email @Length(max = 100) String email,
        @Length(max = 200) String address,
        @NotBlank @Length(max = 9) String phone,
        @Length(max = 9) String phoneBackup,
        LocalDate birthdate,
        boolean enabled

) {}
