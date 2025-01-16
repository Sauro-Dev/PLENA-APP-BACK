package com.plenamente.sgt.domain.dto.MedicalHistoryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public abstract class DocumentMetadataDto {
    private String name;
    private String description;
    private String fileUrl;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadAt;
}