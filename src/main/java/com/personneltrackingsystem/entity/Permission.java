package com.personneltrackingsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "permission", schema = "dbpersonel")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "permissionSeq")
    @SequenceGenerator(name = "permissionSeq", allocationSize = 1)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "resource", nullable = false)
    private String resource;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "path_pattern", nullable = false)
    private String pathPattern;

    @Column(name = "description")
    private String description;
} 