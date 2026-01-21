package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ASSET")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FILENAME", length = 255, nullable = false)
    private String filename;

    @Column(name = "PATH", length = 4000)
    private String path;

    @Lob
    @Column(name = "CONTENT")
    private byte[] content;

    @Column(name = "CONTENT_TYPE", length = 255)
    private String contentType;

    @Column(name = "SIZE")
    private Long size;

    @Column(name = "CREATED_AT")
    private OffsetDateTime createdAt;
}

