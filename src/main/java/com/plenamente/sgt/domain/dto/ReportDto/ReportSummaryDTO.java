package com.plenamente.sgt.domain.dto.ReportDto;

import com.plenamente.sgt.domain.entity.Report;

import java.time.LocalDateTime;

public record ReportSummaryDTO(
        Long idReport,
        String fileUrl,
        String fileName,
        String contentType,
        Long fileSize,
        LocalDateTime uploadAt,
        Integer treatmentMonth
) {
    public static ReportSummaryDTO fromReport(Report report) {
        return new ReportSummaryDTO(
                report.getIdReport(),
                report.getFileUrl(),
                report.getFileName(),
                report.getContentType(),
                report.getFileSize(),
                report.getUploadAt(),
                report.getTreatmentMonth()
        );
    }
}