package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDetailsDto;
import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.RegisterEvaluationDocument;
import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.UpdateEvaluationDocument;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import com.plenamente.sgt.service.EvaluationDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/evaluationDocument")
@RequiredArgsConstructor
public class EvaluationDocumentController {
    private final EvaluationDocumentService evaluationDocumentService;

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<EvaluationDocument> createEvaluationDocument(@RequestParam("file") MultipartFile file, RegisterEvaluationDocument evaluationDocument) {
        EvaluationDocument newEvaluationDocument = evaluationDocumentService.createEvaluationDocument(evaluationDocument, file);
        return new ResponseEntity<>(newEvaluationDocument, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadEvaluationDocument(@PathVariable Long id) {
        EvaluationDocument document = evaluationDocumentService.downloadEvaluationDocument(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(document.getDocumentType()));
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(document.getName())
                .build());
        return new ResponseEntity<>(document.getArchive(), headers, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateEvaluationDocument> updateEvaluationDocument(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestBody UpdateEvaluationDocument evaluationDocumentUp) {
        UpdateEvaluationDocument updatedDocument = evaluationDocumentService.updateEvaluationDocument(id, evaluationDocumentUp, file);
        return ResponseEntity.ok(updatedDocument);
    }

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
    @GetMapping("/select/{id}")
    public ResponseEntity<EvaluationDocumentDetailsDto> selectEvaluationDocument(@PathVariable Long id) {
        EvaluationDocumentDetailsDto document = evaluationDocumentService.findEvaluationDocumentById(id);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('THERAPIST', 'ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<EvaluationDocumentDetailsDto>> getDocumentsByMedicalHistory(
            @RequestParam Long medicalHistoryId) {
        List<EvaluationDocumentDetailsDto> documents = evaluationDocumentService.findDocumentsByMedicalHistoryId(medicalHistoryId);
        return ResponseEntity.ok(documents);
    }
}
