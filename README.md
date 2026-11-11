# Weather App

Eine Spring Boot REST API, die Wetterdaten von [OpenWeatherMap](https://openweathermap.org) abruft und als JSON liefert.

## Features

- **REST API** mit Endpunkt `GET /api/weather?city=Zurich`
- **OpenWeatherMap API** Integration mit HTTP-Client
- **JSON-Parsing** der Wetterdaten (Temperatur, Luftfeuchtigkeit, Wind, Beschreibung)
- **Exception Handling** mit `@RestControllerAdvice` fuer freundliche Fehlermeldungen
- **Tests** mit JUnit 5 und Mockito

## Technologie

- **Java 17**
- **Spring Boot 3.2.0**
- **Maven**
- **JUnit 5** + **Mockito**

## Einrichtung

### 1. API-Key holen

1. Gehe zu [openweathermap.org/api](https://openweathermap.org/api)
2. Registriere dich (kostenlos)
3. Kopiere deinen API-Key aus dem Dashboard

### 2. API-Key konfigurieren

Oeffne `src/main/resources/application.properties` und ersetze den Platzhalter:

```properties
openweather.api-key=DEIN-API-KEY-HIER
```

**Wichtig:** Die Datei ist in `.gitignore` — dein API-Key landet nicht auf GitHub.

### 3. App starten

```bash
mvn spring-boot:run
```

Die App laeuft auf `http://localhost:8080`.

## Nutzung

### Wetter fuer eine Stadt abfragen

```
GET http://localhost:8080/api/weather?city=Zurich
```

**Antwort (JSON):**

```json
{
  "city": "Zurich",
  "temperature": 22.5,
  "feelsLike": 21.0,
  "humidity": 65,
  "windSpeed": 3.6,
  "description": "leicht bewölkt",
  "icon": "02d"
}
```

### Fehlerfaelle

**Stadt nicht gefunden:**

```
GET http://localhost:8080/api/weather?city=UnexistierendeStadt
```

```json
{
  "timestamp": "2026-11-11T20:00:00",
  "error": "Wetterabfrage fehlgeschlagen",
  "message": "Stadt 'UnexistierendeStadt' nicht gefunden."
}
```

## Architektur

```
WeatherApp/
├── controller/
│   └── WeatherController.java    # REST Endpunkte
├── service/
│   └── WeatherService.java       # OpenWeatherMap API Verbindung
├── dto/
│   └── WeatherResponse.java      # JSON Datenstruktur
├── exception/
│   └── WeatherExceptionHandler.java # Globale Fehlerbehandlung
└── WeatherAppApplication.java    # Spring Boot Einstieg
```

## Lernen

Dieses Projekt zeigt wichtige Spring Boot Konzepte:

| Konzept | Wo? |
|---------|-----|
| **REST API** | `@RestController`, `@GetMapping` |
| **Dependency Injection** | Konstruktor-Injection im Controller |
| **Configuration** | `@Value` fuer API-Key aus `application.properties` |
| **HTTP-Client** | `RestTemplate` fuer API-Anfragen |
| **Exception Handling** | `@RestControllerAdvice` fuer globale Fehler |
| **Tests** | JUnit 5 + Mockito fuer Service-Tests |

## Tests

```bash
mvn test
```

## Lebenslauf

Dieses Projekt zeigt:

- **Spring Boot** REST API Entwicklung
- **Externe API-Integration** (OpenWeatherMap)
- **JSON-Parsing** und DTO-Muster
- **Exception Handling** mit freundlichen Fehlermeldungen
- **Testgetriebene Entwicklung** mit JUnit und Mockito
- **Git** Versionskontrolle mit `.gitignore` fuer Secrets
