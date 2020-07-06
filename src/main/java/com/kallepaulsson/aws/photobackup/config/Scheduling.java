package com.kallepaulsson.aws.photobackup.config;

import com.kallepaulsson.aws.photobackup.config.properties.BackupProperties;
import com.kallepaulsson.aws.photobackup.models.BackupDate;
import com.kallepaulsson.aws.photobackup.services.BucketService;
import com.kallepaulsson.aws.photobackup.services.FileService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class Scheduling {

    private final BucketService bucketService;
    private final FileService fileService;
    private final BackupProperties backupProperties;

    public Scheduling(BucketService bucketService, FileService fileService, BackupProperties backupProperties) {
        this.bucketService = bucketService;
        this.fileService = fileService;
        this.backupProperties = backupProperties;
    }

    // Archive photos every day at 01:00AM
    @Scheduled(cron = "0 0 1 * * *")
    public void checkMissingPhotos() throws IOException {
        final List<BackupDate> dates = new ArrayList<>();

        for (int i = backupProperties.getCheckInterval() - 1; i >= 1; i--) {
            dates.add(new BackupDate(YearMonth.now().minusMonths(i)));
        }

        fileService.zipPhotos(dates);
    }

    // Upload photos to AWS at 06:00AM the first day of every month
    @Scheduled(cron = "0 0 6 1 * ?")
    public void uploadPhotos() {
        final BackupDate monthToUpload = new BackupDate(YearMonth.now().minusMonths(backupProperties.getCheckInterval()));
        bucketService.uploadPhotos(monthToUpload.getYear(), monthToUpload.getMonth());
    }
}
