package com.personneltrackingsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DtoPermissionIU {

    private Long id;

    private String name;

    private String resource;

    private String method;

    private String pathPattern;
    
    private String description;
}
