package com.kallepaulsson.aws.photobackup.controllers;

import com.kallepaulsson.aws.photobackup.models.requests.BackupRequest;
import com.kallepaulsson.aws.photobackup.services.BucketService;
import com.kallepaulsson.aws.photobackup.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping(path = "/backup")
public class BackupController {

    private final Logger log = LoggerFactory.getLogger(BackupController.class);

    private final FileService fileService;
    private final BucketService bucketService;

    public BackupController(FileService fileService, BucketService bucketService) {
        this.fileService = fileService;
        this.bucketService = bucketService;
    }

    @PostMapping
    public ResponseEntity<?> backup(@Valid @RequestBody BackupRequest request) {
        try {
            fileService.zipPhotos(request.getYear(), request.getMonth());
        } catch (IOException e) {
            log.error("Failed to zip photos.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        bucketService.uploadPhotos(request.getYear(), request.getMonth());
        return ResponseEntity.ok().build();
    }
}
