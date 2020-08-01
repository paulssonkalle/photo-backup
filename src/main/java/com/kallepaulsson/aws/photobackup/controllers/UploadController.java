package com.kallepaulsson.aws.photobackup.controllers;

import com.kallepaulsson.aws.photobackup.models.requests.BackupRequest;
import com.kallepaulsson.aws.photobackup.services.BucketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "upload")
public class UploadController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final BucketService bucketService;

    public UploadController(BucketService bucketService) {
        this.bucketService = bucketService;
    }

    @PostMapping
    public void upload(@Valid @RequestBody BackupRequest request) {
        bucketService.uploadPhotos(request.getYear(), request.getMonth());
    }
}
