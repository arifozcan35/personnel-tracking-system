package com.personneltrackingsystem.controller;

import com.personneltrackingsystem.dto.DtoWorkIU;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Work Controller", description = "Work CRUD operations")
@RequestMapping("/api/work")
public interface WorkController {

    @GetMapping
    List<DtoWorkIU> getAllWorks();

    @GetMapping("/{workId}")
    DtoWorkIU getOneWork(@PathVariable Long workId);

    @DeleteMapping("/{workId}")
    void deleteWork(@PathVariable Long workId);

}
