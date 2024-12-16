package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.PatientPaymentDto.PatientPaymentDTO;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.domain.entity.PatientPayment;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.PatientPaymentRepository;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.service.PatientPaymentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PatientPaymentServiceImpl implements PatientPaymentService {
    private PatientPaymentRepository patientPaymentRepository;
    private PatientRepository patientRepository;

    @Override
    public PatientPayment createPatientPayment(PatientPaymentDTO patientPaymentDTO){

        Patient patient = patientRepository.findById(patientPaymentDTO.idPatient())
                .orElseThrow(()-> new RuntimeException("Patient not found by id: "+ patientPaymentDTO.idPatient()));

        //Crear el pago
        PatientPayment patientPayment = new PatientPayment();
        patientPayment.setPaymentMethod(patientPaymentDTO.paymentMethod());
        patientPayment.setPaymentType(patientPaymentDTO.paymentType());
        patientPayment.setPaymentFee(patientPaymentDTO.paymentFee());
        patientPayment.setAmount(patientPaymentDTO.amount());
        patientPayment.setDate(LocalDateTime.now());
        patientPayment.setIdPatient(patient);

        return patientPaymentRepository.save(patientPayment);
    }
    @Override
    public PatientPaymentDTO getPatientPaymentId(Long id){
        //Obtener el pago por el id
        PatientPayment patientPayment = patientPaymentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Pago del paciente no encontrado."));

        return mapToList(patientPayment);
    }

    private PatientPaymentDTO mapToList(PatientPayment patientPayment){
        return new PatientPaymentDTO(
                patientPayment.getPaymentMethod(),
                patientPayment.getPaymentType(),
                patientPayment.getPaymentFee(),
                patientPayment.getAmount(),
                patientPayment.getDate(),
                patientPayment.getIdPatient().getIdPatient()
        );
    }
}
