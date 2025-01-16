package com.plenamente.sgt.domain.dto.EvaluationDocumentDto;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.DocumentMetadataDto;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationDocumentDto extends DocumentMetadataDto {
    private Long idDocument;
    private Long medicalHistoryId;
}