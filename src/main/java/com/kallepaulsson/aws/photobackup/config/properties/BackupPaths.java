package com.kallepaulsson.aws.photobackup.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.backup.paths")
public class BackupPaths {
    private Path basePath;
    private Path photosPath;
    private Path backupPath;

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

    public Path getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(Path backupPath) {
        this.backupPath = backupPath;
    }
}
