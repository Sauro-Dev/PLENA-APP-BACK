package com.plenamente.sgt.domain.dto.UserDto;

import com.plenamente.sgt.domain.entity.Rol;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

public record RegisterUser(
        @NotBlank @Length(max = 100) String name,
        @Length(max = 100) String paternalSurname,
        @Length(max = 100) String maternalSurname,
        @NotNull @Size(max = 9) String phone,
        @Size(max = 9) String phoneBackup,
        @NotNull @Pattern(regexp = "\\d{8}") String dni,
        @NotBlank @Email @Length(max = 100) String email,
        @Length(max = 100) String address,
        @NotNull LocalDate birthdate,
        @NotBlank @Length(max = 100) String username,
        @NotBlank @Length(max = 100) String password,
        @NotNull Rol role,  // campo para determinar el rol
        Double paymentSession,  // campo opcional para Therapist
        Double paymentMonthly  // campo opcional para Secretary
) {

}
