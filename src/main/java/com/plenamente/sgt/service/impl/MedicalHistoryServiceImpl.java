package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.ListMedicalHistory;
import com.plenamente.sgt.domain.dto.MedicalHistoryDto.RegisterMedicalHistory;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.domain.entity.Report;
import com.plenamente.sgt.infra.repository.EvaluationDocumentRepository;
import com.plenamente.sgt.infra.repository.MedicalHistoryRepository;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.infra.repository.ReportRepository;
import com.plenamente.sgt.service.MedicalHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {
    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private EvaluationDocumentRepository evaluationDocumentRepository;

    @Override
    public MedicalHistory createMedicalHistory(RegisterMedicalHistory registerMedicalHistory) {
        MedicalHistory medicalHistory = new MedicalHistory();
        Patient patient = patientRepository.findById(registerMedicalHistory.idPatient())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado con id: " + registerMedicalHistory.idPatient()));
        medicalHistory.setPatient(patient);
        medicalHistory.setName(registerMedicalHistory.name());
        return medicalHistoryRepository.save(medicalHistory);
    }
    @Override
    public List<ListMedicalHistory> getReport_DocumentByPatientId(Long id) {
        List<MedicalHistory> medicalHistories = medicalHistoryRepository.findByPatient_IdPatient(id);
        List<ListMedicalHistory> listMedicalHistories = new ArrayList<>();

        for (MedicalHistory medicalHistory : medicalHistories) {
            List<Report> reports = reportRepository.findByMedicalHistory_IdMedicalHistory(id);
            List<EvaluationDocument> documents = evaluationDocumentRepository.findByMedicalHistory_IdMedicalHistory(id);

            for (Report report : reports) {
                for (EvaluationDocument document : documents) {
                    ListMedicalHistory listMedicalHistory = new ListMedicalHistory(
                            report,
                            document.getName(),
                            document.getDescription(),
                            document.getDocumentType(),
                            document.getArchive(),
                            medicalHistory.getName(),
                            medicalHistory.getIdMedicalHistory()
                    );
                    listMedicalHistories.add(listMedicalHistory);
                }
            }
        }
        return listMedicalHistories;
    }
}
