package com.plenamente.sgt.domain.dto.ReportDto;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.DocumentMetadataDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ReportDto extends DocumentMetadataDto {
    private Long idReport;
    private Long medicalHistoryId;
    private Integer treatmentMonth;
}