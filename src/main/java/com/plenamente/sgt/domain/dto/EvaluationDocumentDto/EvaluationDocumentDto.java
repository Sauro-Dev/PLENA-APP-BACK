package com.plenamente.sgt.domain.dto.EvaluationDocumentDto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDocumentDto {
    private Long idDocument;
    private Long medicalHistoryId;
    private String name;
    private String description;
    private String fileUrl;
    private String evaluationType;
    private LocalDateTime evaluationDate;
    private LocalDateTime uploadedAt;
}