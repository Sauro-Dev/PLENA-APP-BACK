package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDto;
import com.plenamente.sgt.service.EvaluationDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluationDocument")
@RequiredArgsConstructor
@Slf4j
public class EvaluationDocumentController {
    private final EvaluationDocumentService documentService;
    private final EvaluationDocumentService evaluationDocumentService;

    @PostMapping("/medical-history/{medicalHistoryId}/upload")
    public ResponseEntity<EvaluationDocumentDto> uploadDocument(
            @PathVariable Long patientId,
            @PathVariable Long medicalHistoryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("evaluationType") String evaluationType) {

        EvaluationDocumentDto dto = new EvaluationDocumentDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setEvaluationType(evaluationType);
        dto.setEvaluationDate(LocalDateTime.now());

        return ResponseEntity.ok(evaluationDocumentService.uploadDocument(patientId, medicalHistoryId, file, dto));
    }

    @GetMapping("/medical-history/{medicalHistoryId}")
    public ResponseEntity<List<EvaluationDocumentDto>> getDocuments(@PathVariable Long medicalHistoryId) {
        return ResponseEntity.ok(documentService.getDocumentsByMedicalHistory(medicalHistoryId));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }
}
