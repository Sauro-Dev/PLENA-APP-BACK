package com.plenamente.sgt.domain.dto.ReportDto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReportDto {
    private Long idReport;
    private Long medicalHistoryId;
    private String name;
    private String description;
    private String fileUrl;
    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private String reportType;
    private LocalDateTime uploadedAt;
}

