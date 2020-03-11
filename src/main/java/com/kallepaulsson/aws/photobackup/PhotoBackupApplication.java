package com.kallepaulsson.aws.photobackup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.kallepaulsson.aws.photobackup.config.properties")
public class PhotoBackupApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhotoBackupApplication.class, args);
    }

}
