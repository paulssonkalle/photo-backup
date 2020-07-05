package com.kallepaulsson.aws.photobackup.config;

import com.kallepaulsson.aws.photobackup.services.BucketService;
import com.kallepaulsson.aws.photobackup.services.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableScheduling
public class Scheduling {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BucketService bucketService;
    private final FileService fileService;

    public Scheduling(BucketService bucketService, FileService fileService) {
        this.bucketService = bucketService;
        this.fileService = fileService;
    }

    // Archive photos at 01:00AM the first day of every month
    @Scheduled(cron = "0 0 1 1 * ?")
    public void archivePhotos() throws IOException {
        log.info("Starting archive photos task");
        final LocalDate yesterday = dateOfYesterday();
        fileService.zipPhotos(String.valueOf(yesterday.getYear()), String.format("%02d", yesterday.getMonthValue()));
        log.info("Finished archive photos task");
    }

    // Upload photos to AWS at 06:00AM the first day of every month
    @Scheduled(cron = "0 0 6 1 * ?")
    public void uploadPhotos() {
        log.info("Starting upload task");
        final LocalDate yesterday = dateOfYesterday();
        bucketService.uploadPhotos(String.valueOf(yesterday.getYear()), String.format("%02d", yesterday.getMonthValue()));
        log.info("Finished upload task");
    }

    private LocalDate dateOfYesterday() {
        return LocalDate.now().minus(1, ChronoUnit.DAYS);
    }
}
