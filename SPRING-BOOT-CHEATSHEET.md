# Spring Boot Cheat Sheet für Anfänger

## Die 5 wichtigsten Annotationen

| Annotation | Wo? | Was sie macht |
|-----------|-----|--------------|
| `@SpringBootApplication` | Hauptklasse | Startet den Server (Tomcat) auf Port 8080 |
| `@RestController` | Controller | Beantwortet URLs und gibt JSON zurück |
| `@Service` | Service | Enthält die Logik (API-Aufrufe, Berechnungen) |
| `@GetMapping` | Controller-Methode | Beantwortet GET-Anfragen (Browser-Requests) |
| `@RequestParam` | Controller-Parameter | Extrahiert `?stadt=` aus der URL |

## Die 4 Klassen eines typischen Projekts

| Klasse | Annotation | Aufgabe |
|--------|-----------|---------|
| `App.java` | `@SpringBootApplication` | Startet den Server |
| `Controller.java` | `@RestController` | Fängt URLs ab, gibt JSON zurück |
| `Service.java` | `@Service` | Enthält die Logik |
| `Response.java` | Keine! | Nur ein Datenbehälter (DTO) |

## HTTP-Methoden

| Annotation | HTTP-Methode | Verwendung |
|-----------|-------------|-----------|
| `@GetMapping` | GET | Daten abrufen |
| `@PostMapping` | POST | Neue Daten erstellen |
| `@PutMapping` | PUT | Daten aktualisieren |
| `@DeleteMapping` | DELETE | Daten löschen |

## Dependency Injection

```java
// Controller braucht Service:
public WeatherController(WeatherService weatherService) {
    this.weatherService = weatherService;  // ← Spring Boot füllt das aus!
}
// Du schreibst kein "new WeatherService()"!
```

## Konfiguration lesen

```java
@Value("${openweather.api-key}")  // Liest aus application.properties
private String apiKey;
```

## Exception Handling

```java
@RestControllerAdvice  // Globaler Exception-Handler
public class WeatherExceptionHandler {
    @ExceptionHandler(RuntimeException.class)  // Fängt RuntimeException ab
    public ResponseEntity<Map<String, Object>> handle(RuntimeException e) {
        // Gibt freundliche JSON-Fehlermeldung zurück
    }
}
```

## Der Datenfluss

```
Browser:  GET /api/weather?city=Zurich
    ↓
Controller fängt die URL ab (@GetMapping)
    ↓
Controller ruft Service auf (@Service)
    ↓
Service ruft externe API auf (RestTemplate)
    ↓
Antwort als JSON an Browser zurück (DTO)
```
