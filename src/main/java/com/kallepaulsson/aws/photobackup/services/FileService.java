package com.kallepaulsson.aws.photobackup.services;

import com.kallepaulsson.aws.photobackup.config.properties.BackupPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FileService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final BackupPaths backupPaths;

    public FileService(BackupPaths backupPaths) {
        this.backupPaths = backupPaths;
    }


    public void archivePhotos() throws IOException {
        final LocalDate yesterday = LocalDate.now().minus(1, ChronoUnit.DAYS);
        final String year = String.valueOf(yesterday.getYear());
        final String month = String.valueOf(yesterday.getMonthValue());
        copyPhotos(year, month);
        zipPhotos(year, month);
        removePhotos();
    }

    private void copyPhotos(String year, String month) throws IOException {
        final Path sourceFolder = backupPaths.getMonthlyPath();
        final Path destinationFolder = createDestinationFolder(year, month);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceFolder, (file) -> Files.isRegularFile(file))) {
            directoryStream.forEach(sourceFile -> {
                final Path destinationFile = destinationFolder.resolve(sourceFile.getFileName());
                try {
                    Files.copy(sourceFile, destinationFile,
                            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                } catch (IOException e) {
                    log.error("Failed to copy {} to {}", sourceFile, destinationFile, e);
                }
            });
        }
    }

    private void zipPhotos(String year, String month) throws IOException {
        final Path sourceFolder = backupPaths.getMonthlyPath();
        final Path destinationZip = backupPaths.getAwsPath().resolve(year + "_" + month + ".zip");

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(sourceFolder, (file) -> Files.isRegularFile(file));
             ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(destinationZip)))) {
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

    private void removePhotos() throws IOException {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(backupPaths.getMonthlyPath(), (file) -> Files.isRegularFile(file))) {
            for (Path path : directoryStream) {
                Files.delete(path);
            }
        }
    }

    private Path createDestinationFolder(String year, String month) throws IOException {
        return Files.createDirectories(backupPaths.getPhotosPath().resolve(year).resolve(month));
    }
}
