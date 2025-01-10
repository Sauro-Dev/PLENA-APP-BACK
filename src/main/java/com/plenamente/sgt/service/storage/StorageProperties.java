package com.plenamente.sgt.service.storage;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "nginx")
@Validated
@Getter
@Setter
public class StorageProperties {

    private Path location;

    @NotNull
    @Value("${nginx.storage.location:/usr/share/nginx/html/static}")
    private String storageLocation;

    @NotNull
    @Value("${nginx.server.url:http://nginx_sgt}")
    private String serverUrl;

    @PostConstruct
    public void init() {
        this.location = Paths.get(storageLocation);
    }

}
