package com.kallepaulsson.aws.photobackup.controllers;

import com.kallepaulsson.aws.photobackup.config.properties.BackupProperties;
import com.kallepaulsson.aws.photobackup.models.BackupDate;
import com.kallepaulsson.aws.photobackup.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "/zip")
public class ZipController {

    private final Logger log = LoggerFactory.getLogger(ZipController.class);

    private final FileService fileService;
    private final BackupProperties backupProperties;

    public ZipController(FileService fileService, BackupProperties backupProperties) {
        this.fileService = fileService;
        this.backupProperties = backupProperties;
    }

    @PostMapping
    public ResponseEntity<?> zip() {
        final List<BackupDate> dates = new ArrayList<>();

        for (int i = backupProperties.getCheckInterval() - 1; i >= 1; i--) {
            dates.add(new BackupDate(YearMonth.now().minusMonths(i)));
        }

        try {
            fileService.zipPhotos(dates);
        } catch (IOException e) {
            log.error("Failed to zip files.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok().build();
    }
}
