package com.kallepaulsson.aws.photobackup.services;

import com.kallepaulsson.aws.photobackup.config.properties.BackupPaths;
import com.kallepaulsson.aws.photobackup.models.BackupDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BackupPaths backupPaths;

    public FileService(BackupPaths backupPaths) {
        this.backupPaths = backupPaths;
    }

    public void zipPhotos(String year, String month) throws IOException {
        log.info(String.format("Zipping photos for %s-%s", year, month));
        final Path sourceFolder = backupPaths.getPhotosPath().resolve(year).resolve(month);
        final Path destinationZip = backupPaths.getBackupPath().resolve(year + "_" + month + ".zip");

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceFolder, (file) -> Files.isRegularFile(file));
             ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(
                     Files.newOutputStream(
                             destinationZip, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
                     )

             ))) {

            directoryStream.forEach(sourceFile -> {
                final Path zipPath = Paths.get(year).resolve(month).resolve(sourceFile.getFileName());
                try {
                    zos.putNextEntry(new ZipEntry(zipPath.toString()));
                    Files.copy(sourceFile, zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    log.error("Failed to zip {} to {}", sourceFile, zipPath, e);
                }
            });
        }
    }

    public void zipPhotos(List<BackupDate> dates) throws IOException {
        for (BackupDate date : dates) {
            zipPhotos(date.getYear(), date.getMonth());
        }
    }
}
