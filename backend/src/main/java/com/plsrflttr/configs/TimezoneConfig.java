package com.plsrflttr.configs;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
@RequiredArgsConstructor
public class TimezoneConfig {

    @Value("${app.timezone}")
    private String timezone;

    @PostConstruct
    public void init() {

        TimeZone.setDefault(
                TimeZone.getTimeZone(timezone)
        );
    }
}