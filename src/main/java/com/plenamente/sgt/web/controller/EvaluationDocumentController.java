package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.RegisterEvaluationDocument;
import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.UpdateEvaluationDocument;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import com.plenamente.sgt.service.EvaluationDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/evaluationDocument")
@RequiredArgsConstructor
public class EvaluationDocumentController {
    private final EvaluationDocumentService evaluationDocumentService;

    @PostMapping("/register")
    public ResponseEntity<EvaluationDocument> createEvaluationDocument(@RequestParam("file") MultipartFile file, RegisterEvaluationDocument evaluationDocument) {
        EvaluationDocument newEvaluationDocument = evaluationDocumentService.createEvaluationDocument(evaluationDocument, file);
        return new ResponseEntity<>(newEvaluationDocument, HttpStatus.CREATED);
    }

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
    @PutMapping("/update/{id}")
    public ResponseEntity<UpdateEvaluationDocument> updateEvaluationDocument(
            @PathVariable Long id,UpdateEvaluationDocument evaluationDocumentUp) {
        UpdateEvaluationDocument updatedEvaluationDocument = evaluationDocumentService.updateEvaluationDocument(id, evaluationDocumentUp);
        return ResponseEntity.ok(updatedEvaluationDocument);
    }
}
