package ru.mip3x.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "import_operations")
public class ImportOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status;

    @Column(name = "file_bucket")
    private String fileBucket;

    @Column(name = "file_object_key", length = 512)
    private String fileObjectKey;

    @Column(name = "file_original_name")
    private String fileOriginalName;

    @Column(name = "file_content_type")
    private String fileContentType;

    @Column(name = "file_size")
    private Long fileSize;

    private Integer addedCount;

    private String errorMessage;

    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
