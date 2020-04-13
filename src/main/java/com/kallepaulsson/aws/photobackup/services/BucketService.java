package com.kallepaulsson.aws.photobackup.services;

import com.kallepaulsson.aws.photobackup.config.properties.AwsProperties;
import com.kallepaulsson.aws.photobackup.config.properties.BackupPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.StorageClass;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class BucketService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final S3AsyncClient s3;
    private final AwsProperties awsProperties;
    private final BackupPaths backupPaths;

    public BucketService(S3AsyncClient s3, AwsProperties awsProperties, BackupPaths backupPaths) {
        this.s3 = s3;
        this.awsProperties = awsProperties;
        this.backupPaths = backupPaths;
    }

    public void uploadPhotos() {
        final LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
        final String year = String.valueOf(yesterday.getYear());
        final String month = String.format("%02d", yesterday.getMonthValue());

        final String bucketName = awsProperties.getBucket().getName();
        final Path fileToUpload = backupPaths.getBackupPath().resolve(year + "_" + month + ".zip");
        final String filename = fileToUpload.getFileName().toString();

        log.info("Starting upload of {}", filename);
        s3.putObject(builder -> builder
                .bucket(bucketName)
                .key(filename)
                .storageClass(StorageClass.DEEP_ARCHIVE), AsyncRequestBody.fromFile(fileToUpload))
                .whenComplete((response, exception) -> handleCompletedUpload(response, exception, fileToUpload))
                .join();
    }

    private void handleCompletedUpload(PutObjectResponse response, Throwable exception, Path file) {
        if (response != null) {
            log.info("Uploaded {} successfully", file.getFileName());
        } else {
            log.error("Failed to upload {}", file.getFileName(), exception);
        }
    }
}
