package com.wetterapp.controller;

import com.wetterapp.dto.WeatherResponse;
import com.wetterapp.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller fuer Wetterabfragen.
 * Bietet den Endpunkt GET /api/weather?city=Zurich an.
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Wetterdaten fuer eine Stadt abrufen.
     *
     * @param city Stadtname (z. B. "Zurich", "Berlin", "New York")
     * @return WeatherResponse als JSON
     */
    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam String city) {
        try {
            WeatherResponse weather = weatherService.getWeatherByCity(city);
            return ResponseEntity.ok(weather);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("nicht gefunden")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
