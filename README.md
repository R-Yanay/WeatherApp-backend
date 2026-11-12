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

### Aktuelles Wetter abfragen

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

### 5-Tage-Vorhersage abfragen

```
GET http://localhost:8080/api/forecast?city=Zurich
```

**Antwort (JSON):**

```json
[
  {"date":"Montag","tempMin":18.2,"tempMax":24.5,"description":"leicht bewölkt","icon":"02d"},
  {"date":"Dienstag","tempMin":16.0,"tempMax":22.1,"description":"bewölkt","icon":"04d"},
  ...
]
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

## Datenfluss

So verarbeitet Spring Boot eine Anfrage — immer gleich, in 5 Schritten:

```
1. Browser:  GET /api/weather?city=Zurich
         ↓
2. Tomcat-Server empfaengt die Anfrage
         ↓
3. Spring Boot matcht die URL: @RequestMapping + @GetMapping → WeatherController
         ↓
4. Controller ruft Service auf (Dependency Injection)
         ↓
5. Service ruft OpenWeatherMap API auf (RestTemplate)
         ↓
6. Service parst JSON und fuellt WeatherResponse (DTO)
         ↓
7. Controller gibt WeatherResponse zurueck
         ↓
8. Jackson (automatisch) wandelt das Objekt zu JSON
         ↓
9. Browser zeigt: {"city":"Zurich","temperature":22.5,...}
```

**Die 3 Regeln, die Spring Boot immer befolgt:**

| Regel | Was Spring Boot macht |
|-------|----------------------|
| **URL + HTTP-Methode → Controller** | Matcht die URL automatisch (@RequestMapping + @GetMapping) |
| **Konstruktor → Dependency Injection** | Fueellt den Konstruktor automatisch (@Service) |
| **Java-Objekt → JSON** | Wandelt Getter zu JSON automatisch (Jackson) |

**Du schreibst nur Schritt 4 und 6.** Schritte 1–3 und 7–9 macht Spring Boot automatisch.

## Lernen

Dieses Projekt zeigt wichtige Spring Boot Konzepte:

| Konzept | Wo? |
|---------|-----|
| **REST API** | `@RestController`, `@GetMapping` (2 Endpunkte) |
| **Dependency Injection** | Konstruktor-Injection im Controller |
| **Configuration** | `@Value` fuer API-Key aus `application.properties` |
| **HTTP-Client** | `RestTemplate` fuer API-Anfragen |
| **JSON-Parsing** | Manuelles Parsing der OpenWeatherMap-Antwort |
| **Exception Handling** | `@RestControllerAdvice` fuer globale Fehler |
| **Tests** | JUnit 5 + Mockito fuer Service-Tests |

## Tests

```bash
mvn test
```

## Lebenslauf

Dieses Projekt zeigt:

- **Spring Boot** REST API Entwicklung mit mehreren Endpunkten
- **Externe API-Integration** (OpenWeatherMap) mit JSON-Parsing
- **DTO-Muster** fuer Datenuebertragung
- **Exception Handling** mit freundlichen Fehlermeldungen
- **Testgetriebene Entwicklung** mit JUnit und Mockito
- **Git** Versionskontrolle mit `.gitignore` fuer Secrets

## JavaFX Frontend

Fuer eine grafische Oberflache siehe das begleitende Projekt [WeatherAppGUI](https://github.com/R-Yanay/WeatherApp-frontend). Es ruft diese API auf und zeigt das Wetter in einem Desktop-Fenster an.
