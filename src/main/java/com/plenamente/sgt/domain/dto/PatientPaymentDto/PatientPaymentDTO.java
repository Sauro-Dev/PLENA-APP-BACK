package com.plenamente.sgt.domain.dto.PatientPaymentDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record PatientPaymentDTO(
        @NotBlank String paymentMethod,
        @NotBlank String paymentType,
        @NotNull Integer paymentFee,
        @NotNull Double amount,
        @NotNull LocalDateTime date,
        @NotNull Long idPatient
){
}
