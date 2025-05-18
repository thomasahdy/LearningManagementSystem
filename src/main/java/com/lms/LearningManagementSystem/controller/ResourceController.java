package com.lms.LearningManagementSystem.controller;

import com.lms.LearningManagementSystem.model.Resource;
import com.lms.LearningManagementSystem.service.ResourceService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Resource> uploadResource(
            @RequestBody Resource file) throws IOException {
        Resource uploadedResource = resourceService.saveResource(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedResource);
    }

    @GetMapping("/course/{courseId}")
    public List<Resource> getResourcesByCourse(@PathVariable Long courseId) {
        return resourceService.getResourcesByCourse(courseId);
    }


}