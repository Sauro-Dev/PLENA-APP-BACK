package com.plenamente.sgt.domain.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class DocumentMetadata {
    private String name;
    private String description;
    private String fileUrl;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private LocalDateTime uploadAt;
    private String uploadedBy;
}
