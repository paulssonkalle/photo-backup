package com.kallepaulsson.aws.photobackup.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.backup.paths")
public class BackupPaths {
    private Path basePath;
    private Path photosPath;
    private Path monthlyPath;
    private Path awsPath;

    public Path getBasePath() {
        return basePath;
    }

    public void setBasePath(Path basePath) {
        this.basePath = basePath;
    }

    public Path getPhotosPath() {
        return photosPath;
    }

    public void setPhotosPath(Path photosPath) {
        this.photosPath = photosPath;
    }

    public Path getMonthlyPath() {
        return monthlyPath;
    }

    public void setMonthlyPath(Path monthlyPath) {
        this.monthlyPath = monthlyPath;
    }

    public Path getAwsPath() {
        return awsPath;
    }

    public void setAwsPath(Path awsPath) {
        this.awsPath = awsPath;
    }
}
