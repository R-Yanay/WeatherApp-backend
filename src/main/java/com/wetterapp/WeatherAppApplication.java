package com.wetterapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Hauptklasse fuer die Wetter-App.
 * Startet den eingebetteten Tomcat-Server auf Port 8080.
 */
@SpringBootApplication
public class WeatherAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherAppApplication.class, args);
    }
}
