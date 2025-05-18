package com.lms.LearningManagementSystem.service;

import com.lms.LearningManagementSystem.model.Resource;
import com.lms.LearningManagementSystem.repository.ResourceRepository;
import com.lms.LearningManagementSystem.repository.ResourceRepositoryImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final String uploadDir = "uploads/"; // Directory for file storage

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
        resourceRepository=new ResourceRepositoryImpl();
    }

    public Resource saveResource(Resource file) throws IOException {

        resourceRepository.save(file);

        return file;
    }

    public List<Resource> getResourcesByCourse(Long courseId) {
        return resourceRepository.findByCourseId(courseId);
    }

    public Resource getResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
    }
}