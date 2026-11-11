package com.wetterapp.controller;

import com.wetterapp.dto.ForecastResponse;
import com.wetterapp.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller fuer 5-Tage-Wettervorhersage.
 * Bietet den Endpunkt GET /api/forecast?city=Zurich an.
 */
@RestController
@RequestMapping("/api/forecast")
public class ForecastController {

    private final WeatherService weatherService;

    public ForecastController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * 5-Tage-Wettervorhersage fuer eine Stadt abrufen.
     *
     * @param city Stadtname (z. B. "Zurich", "Berlin", "New York")
     * @return Liste von ForecastResponse (einer pro Tag) als JSON
     */
    @GetMapping
    public ResponseEntity<List<ForecastResponse>> getForecast(@RequestParam String city) {
        try {
            List<ForecastResponse> forecast = weatherService.getForecastByCity(city);
            return ResponseEntity.ok(forecast);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("nicht gefunden")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
