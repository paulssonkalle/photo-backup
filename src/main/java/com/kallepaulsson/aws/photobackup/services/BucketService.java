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

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

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
        final String bucketName = awsProperties.getBucket().getName();
        final Path awsPath = backupPaths.getAwsPath();
        final Set<CompletableFuture<PutObjectResponse>> futureResponses = new HashSet<>();

        try (DirectoryStream<Path> files = Files.newDirectoryStream(awsPath, file -> Files.isRegularFile(file))) {
            files.forEach(file -> {
                final String filename = file.getFileName().toString();

                log.info("Starting upload of {}", filename);
                CompletableFuture<PutObjectResponse> futureResponse = s3.putObject(builder -> builder
                                .bucket(bucketName)
                                .key(filename)
                                .storageClass(StorageClass.DEEP_ARCHIVE), AsyncRequestBody.fromFile(file))
                        .whenComplete((response, exception) -> handleCompletedUpload(response, exception, file));

                futureResponses.add(futureResponse);
            });
        } catch (IOException e) {
            log.error("Failed to get files for {}", awsPath, e);
        }

        CompletableFuture
                .allOf(futureResponses.toArray(new CompletableFuture[0]))
                .join();
    }

    private void handleCompletedUpload(PutObjectResponse response, Throwable exception, Path file) {
        if (response != null) {
            log.info("Uploaded {} successfully", file.getFileName());
            try {
                Files.deleteIfExists(file);
            } catch (IOException e) {
                log.error("Failed to delete {}", file.getFileName(), e);
            }
        } else {
            log.error("Failed to upload {}", file.getFileName(), exception);
        }
    }
}
