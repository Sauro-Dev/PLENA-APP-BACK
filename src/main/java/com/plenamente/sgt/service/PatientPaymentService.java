package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.PatientPaymentDto.PatientPaymentDTO;
import com.plenamente.sgt.domain.entity.PatientPayment;

public interface PatientPaymentService {

    //Metodo para crear un pago de paciente
    PatientPayment createPatientPayment(PatientPaymentDTO patientPaymentDTO);
    //Metodo para buscar el pago del paciente por su id
    PatientPaymentDTO getPatientPaymentId(Long id);
}
