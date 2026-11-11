package com.wetterapp.service;

import com.wetterapp.dto.WeatherResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests fuer WeatherService und WeatherResponse DTO.
 * Da keine echte API-Verbindung moeglich ist in Tests,
 * werden die grundsaetzlichen Funktionen getestet.
 */
class WeatherServiceTest {

    @Test
    void weatherService_createdSuccessfully() {
        // Testet, dass der Service korrekt erstellt werden kann
        WeatherService service = new WeatherService();
        assertNotNull(service, "WeatherService sollte erstellt werden koennen");
    }

    @Test
    void weatherResponse_createdCorrectly() {
        // Testet, dass das DTO korrekt funktioniert
        WeatherResponse weather = new WeatherResponse("Zurich", 22.5, 21.0, 65, 3.6, "leicht bewolkt", "02d");

        assertEquals("Zurich", weather.getCity());
        assertEquals(22.5, weather.getTemperature());
        assertEquals(21.0, weather.getFeelsLike());
        assertEquals(65, weather.getHumidity());
        assertEquals(3.6, weather.getWindSpeed());
        assertEquals("leicht bewolkt", weather.getDescription());
        assertEquals("02d", weather.getIcon());
    }

    @Test
    void weatherResponse_setterWork() {
        // Testet, dass die Setter korrekt funktionieren
        WeatherResponse weather = new WeatherResponse();
        weather.setCity("Berlin");
        weather.setTemperature(18.3);
        weather.setFeelsLike(17.0);
        weather.setHumidity(70);
        weather.setWindSpeed(5.2);
        weather.setDescription("bewolkt");
        weather.setIcon("04d");

        assertEquals("Berlin", weather.getCity());
        assertEquals(18.3, weather.getTemperature());
        assertEquals(17.0, weather.getFeelsLike());
        assertEquals(70, weather.getHumidity());
        assertEquals(5.2, weather.getWindSpeed());
        assertEquals("bewolkt", weather.getDescription());
        assertEquals("04d", weather.getIcon());
    }
}
