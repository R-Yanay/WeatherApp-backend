package com.wetterapp.service;

import com.wetterapp.dto.ForecastResponse;
import com.wetterapp.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service fuer Wetterdaten von OpenWeatherMap API.
 * Ruft die externe API auf und parst die JSON-Antwort.
 */
@Service
public class WeatherService {

    @Value("${openweather.api-key}")
    private String apiKey;

    @Value("${openweather.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ruft Wetterdaten fuer eine Stadt von OpenWeatherMap ab.
     *
     * @param city Stadtname (z. B. "Zurich")
     * @return WeatherResponse mit Temperatur, Luftfeuchtigkeit, etc.
     * @throws RuntimeException wenn die API nicht erreichbar ist oder die Stadt nicht gefunden wird
     */
    public WeatherResponse getWeatherByCity(String city) {
        String url = String.format("%s/weather?q=%s&appid=%s&units=metric&lang=de",
                baseUrl, city, apiKey);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return parseJsonResponse(response.getBody(), city);
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                throw new RuntimeException("Stadt '" + city + "' nicht gefunden.", e);
            }
            throw new RuntimeException("Fehler beim Abrufen der Wetterdaten: " + e.getMessage(), e);
        }
    }

    /**
     * Parst die JSON-Antwort von OpenWeatherMap in ein WeatherResponse Objekt.
     * Einfacher JSON-Parser ohne externe Bibliothek.
     */
    private WeatherResponse parseJsonResponse(String json, String city) {
        WeatherResponse weather = new WeatherResponse();
        weather.setCity(city);

        // Temperatur extrahieren: "temp": 22.5
        String tempStr = extractJsonValue(json, "\"main\":{\"temp\":");
        if (tempStr != null) {
            weather.setTemperature(Double.parseDouble(tempStr));
        }

        // Feels like extrahieren
        String feelsLikeStr = extractJsonValue(json, "\"feels_like\":");
        if (feelsLikeStr != null) {
            weather.setFeelsLike(Double.parseDouble(feelsLikeStr));
        }

        // Luftfeuchtigkeit extrahieren
        String humidityStr = extractJsonValue(json, "\"humidity\":");
        if (humidityStr != null) {
            weather.setHumidity(Integer.parseInt(humidityStr));
        }

        // Windgeschwindigkeit extrahieren
        String windStr = extractJsonValue(json, "\"speed\":");
        if (windStr != null) {
            weather.setWindSpeed(Double.parseDouble(windStr));
        }

        // Beschreibung extrahieren: "description": "leicht bewölkt"
        String descStr = extractJsonString(json, "\"description\":");
        if (descStr != null) {
            weather.setDescription(descStr);
        }

        // Icon extrahieren: "icon": "02d"
        String iconStr = extractJsonString(json, "\"icon\":");
        if (iconStr != null) {
            weather.setIcon(iconStr);
        }

        return weather;
    }

    /**
     * Extrahiert einen numerischen Wert aus JSON.
     * Beispiel: "\"temp\": 22.5" -> "22.5"
     */
    private String extractJsonValue(String json, String key) {
        int startIndex = json.indexOf(key);
        if (startIndex == -1) return null;

        startIndex += key.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) endIndex = json.indexOf("}", startIndex);

        return json.substring(startIndex, endIndex).trim();
    }

    /**
     * Extrahiert einen String-Wert aus JSON.
     * Beispiel: "\"description\": \"leicht bewölkt\"" -> "leicht bewölkt"
     */
    private String extractJsonString(String json, String key) {
        int startIndex = json.indexOf(key);
        if (startIndex == -1) return null;

        startIndex = json.indexOf("\"", startIndex + key.length()) + 1;
        int endIndex = json.indexOf("\"", startIndex);

        if (endIndex == -1) return null;
        return json.substring(startIndex, endIndex);
    }

    /**
     * Ruft die 5-Tage-Vorhersage fuer eine Stadt von OpenWeatherMap ab.
     * Die API liefert Daten alle 3 Stunden (40 Eintraege). Diese Methode
     * fasst die Daten auf 5 Tage zusammen (Min/Max Temperatur pro Tag).
     *
     * @param city Stadtname (z. B. "Zurich")
     * @return Liste von ForecastResponse (einer pro Tag, 5 Tage)
     * @throws RuntimeException wenn die API nicht erreichbar ist oder die Stadt nicht gefunden wird
     */
    public List<ForecastResponse> getForecastByCity(String city) {
        String url = String.format("%s/forecast?q=%s&appid=%s&units=metric&lang=de",
                baseUrl, city, apiKey);

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return parseForecastResponse(response.getBody(), city);
        } catch (RestClientException e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                throw new RuntimeException("Stadt '" + city + "' nicht gefunden.", e);
            }
            throw new RuntimeException("Fehler beim Abrufen der Vorhersage: " + e.getMessage(), e);
        }
    }

    /**
     * Parst die 5-Tage-Vorhersage JSON-Antwort.
     * Fasst 40 Eintraege (alle 3 Stunden) auf 5 Tage zusammen.
     */
    private List<ForecastResponse> parseForecastResponse(String json, String city) {
        // Map: Datum (z. B. "2026-11-12") -> Liste von Temperaturen
        Map<String, List<Double>> dayTemps = new HashMap<>();
        Map<String, String> dayDescriptions = new HashMap<>();
        Map<String, String> dayIcons = new HashMap<>();

        // JSON enthält eine "list"-Array mit 40 Eintraegen
        // Jeder Eintrag hat: "dt_txt": "2026-11-12 12:00:00", "main": {"temp": 22.5}, "weather": [{"description": "...", "icon": "..."}]

        // Finde den Anfang der list-Array
        int listStart = json.indexOf("\"list\":");
        if (listStart == -1) {
            throw new RuntimeException("Unerwartetes JSON-Format der Vorhersage-API.");
        }
        listStart = json.indexOf("[", listStart);
        int listEnd = json.lastIndexOf("]");

        String listContent = json.substring(listStart, listEnd + 1);

        // Zerlege in einzelne Eintraege (jeder beginnt mit "{")
        String[] entries = listContent.split("\\},\\s*\\{");

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEEE", java.util.Locale.GERMANY);

        for (String entry : entries) {
            // Datum extrahieren: "dt_txt": "2026-11-12 12:00:00"
            String dtStr = extractJsonString(entry, "\"dt_txt\":");
            if (dtStr == null) continue;

            // Nur das Datum (ohne Uhrzeit) extrahieren
            String datePart = dtStr.substring(0, 10); // "2026-11-12"

            // Temperatur extrahieren
            String tempStr = extractJsonValue(entry, "\"temp\":");
            if (tempStr == null) continue;
            double temp = Double.parseDouble(tempStr);

            // Beschreibung extrahieren
            String desc = extractJsonString(entry, "\"description\":");
            String icon = extractJsonString(entry, "\"icon\":");

            // Zum Tag hinzufügen
            dayTemps.computeIfAbsent(datePart, k -> new ArrayList<>()).add(temp);
            if (desc != null) dayDescriptions.put(datePart, desc);
            if (icon != null) dayIcons.put(datePart, icon);
        }

        // Erstelle die Antwort-Liste (sortiert nach Datum)
        List<ForecastResponse> forecast = new ArrayList<>();
        dayTemps.keySet().stream()
                .sorted()
                .limit(5)
                .forEach(date -> {
                    List<Double> temps = dayTemps.get(date);
                    double tempMin = temps.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                    double tempMax = temps.stream().mapToDouble(Double::doubleValue).max().orElse(0);

                    // Datum in lesbares Format umwandeln (z. B. "Montag")
                    LocalDate localDate = LocalDate.parse(date);
                    String dayName = localDate.format(outputFormatter);

                    forecast.add(new ForecastResponse(
                            dayName,
                            Math.round(tempMin * 10.0) / 10.0,
                            Math.round(tempMax * 10.0) / 10.0,
                            dayDescriptions.getOrDefault(date, ""),
                            dayIcons.getOrDefault(date, "")
                    ));
                });

        return forecast;
    }
}
