# Spring Boot - Wichtige Regeln und Best Practices

Diese Datei fasst die wichtigsten Regeln und Best Practices zusammen, die bei der Verwendung von Spring Boot beachtet werden sollten.

---

## 1. Secrets niemals im Code speichern

API-Keys, Passwörter und Datenbank-URLs gehoeren **niemals** in den Code oder auf GitHub.

| Falsch | Richtig |
|--------|---------|
| `private String apiKey = "abc123";` im Code | `@Value("${openweather.api-key}")` aus `application.properties` |
| `application.properties` auf GitHub | `.gitignore` + `application.properties` |

**In der Produktion** verwendet man Environment Variables:

```properties
openweather.api-key=${OPENWEATHER_API_KEY}
```

---

## 2. DTOs duerfen keine Logik enthalten

Ein DTO (Data Transfer Object) speichert **nur Daten**. Kein API-Aufruf, keine Berechnung, keine Datenbank-Abfrage.

| Falsch | Richtig |
|--------|---------|
| `WeatherResponse` ruft API auf | `WeatherService` ruft API auf, fuellt `WeatherResponse` |

**Ein DTO braucht immer:**
- Einen **leeren Konstruktor** (fuer Jackson/JSON)
- **Getter** fuer jedes Feld (fuer JSON-Ausgabe)
- **Setter** fuer jedes Feld (fuer JSON-Eingabe)
- **Keine Annotationen** (reine Java-Klasse)

---

## 3. Tests ohne echte API-Anfragen

Unit-Tests sollten **niemals** echte API-Anfragen machen - sie sind langsam, unzuverlaessig, und kosten ggf. Geld.

Verwende **Mockito**, um die API zu simulieren:

```java
@Test
void testWeatherService() {
    // Mock die API-Antwort
    String mockJson = "{\"main\":{\"temp\":22.5}}";
    when(restTemplate.getForEntity(anyString(), eq(String.class)))
        .thenReturn(new ResponseEntity<>(mockJson, HttpStatus.OK));
    
    // Teste die Logik
    WeatherResponse result = service.getWeatherByCity("Zurich");
    assertEquals(22.5, result.getTemperature());
}
```

---

## 4. Immer Konstruktor-Injection verwenden

Verwende **Konstruktor-Injection**, nicht `@Autowired` auf Feldern.

| Falsch | Richtig |
|--------|---------|
| `@Autowired private WeatherService service;` | `public WeatherController(WeatherService service) { ... }` |

**Warum?** Konstruktor-Injection macht die Abhaengigkeit **explizit** und Tests **einfacher**.

---

## 5. Keine Logik im Controller

Der Controller sollte **nur** die Anfrage entgegennehmen und die Antwort zurueckgeben. Keine Berechnungen, keine API-Aufrufe.

| Falsch | Richtig |
|--------|---------|
| Controller ruft API auf | Controller ruft Service auf |
| Controller parst JSON | Service parst JSON |
| Controller macht Berechnungen | Service macht Berechnungen |

**Der Controller ist wie ein Kellner:** Er nimmt die Bestellung (URL), gibt sie an den Koch (Service) weiter, und bringt das fertige Essen (JSON) zum Gast (Browser).

---

## 6. Globalen Exception-Handler verwenden

Verwende **`@RestControllerAdvice`** fuer alle Controller, nicht `try-catch` in jedem Controller.

Ein zentraler Handler ist **konsistenter** - alle Fehler sehen gleich aus:

```java
@RestControllerAdvice
public class WeatherExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("error", "Wetterabfrage fehlgeschlagen");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
```

---

## 7. HTTP-Status-Codes korrekt verwenden

| Situation | Status-Code |
|-----------|------------|
| Alles OK | `200 OK` |
| Nicht gefunden | `404 Not Found` |
| Server-Fehler | `500 Internal Server Error` |
| Ungueltige Eingabe | `400 Bad Request` |

**Beispiel:**

```java
if (e.getMessage().contains("nicht gefunden")) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
}
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
```

---

## 8. Projektstruktur nach Aufgaben trennen

Die meisten Spring Boot-Projekte verwenden eine Ordner-Struktur nach **Aufgaben**:

```
com.beispiel.app/
├── controller/     ← HTTP-Anfragen (URLs)
├── service/        ← Logik (API-Aufrufe, Berechnungen)
├── repository/     ← Datenbank (Spring Data JPA)
├── dto/            ← Datenuebertragung (JSON)
├── model/          ← Entitaeten (Datenbank-Tabellen)
├── exception/      ← Fehlerbehandlung
└── config/         ← Konfiguration
```

**Jeder Ordner hat eine klare Verantwortung:**

| Ordner | Aufgabe | Analogie |
|--------|---------|----------|
| `controller` | Beantwortet HTTP-Anfragen | Kellner |
| `service` | Enthaelt die Logik | Koch |
| `dto` | Speichert Daten fuer JSON | Teller |
| `exception` | Faengt Fehler ab | Manager |

---

## 9. Mapping-Annotationen verstehen

| Annotation | Was sie macht |
|-----------|--------------|
| `@RequestMapping` | Legt die **Basis-URL** fest |
| `@GetMapping` | Beantwortet **GET**-Anfragen (Daten abrufen) |
| `@PostMapping` | Beantwortet **POST**-Anfragen (neue Daten erstellen) |
| `@PutMapping` | Beantwortet **PUT**-Anfragen (Daten aktualisieren) |
| `@DeleteMapping` | Beantwortet **DELETE**-Anfragen (Daten loeschen) |

**Eine Analogie - Ein Restaurant:**

| Annotation | Restaurant-Vergleich |
|-----------|---------------------|
| `@RequestMapping("/api/weather")` | Die **Adresse** des Restaurants |
| `@GetMapping` | **Menue ansehen** - Daten abrufen |
| `@PostMapping` | **Bestellung aufgeben** - Neues erstellen |
| `@PutMapping` | **Bestellung aendern** - Ersetzen |
| `@DeleteMapping` | **Bestellung stornieren** - Loeschen |

---

## 10. Wichtige Begriffe verstehen

| Begriff | Deutsche Bedeutung | Was es macht |
|---------|-------------------|-------------|
| **Service** | **Dienst** / Dienstleistung | Die Klasse, die die eigentliche Arbeit macht |
| **RestTemplate** | **REST-Vorlage** | Eine fertige Vorlage, um HTTP-Anfragen an REST-APIs zu senden |
| **DTO** | **Datenuebertragungsobjekt** | Ein reiner Datenbehaelter ohne Logik |
| **Dependency Injection** | **Abhaengigkeitsinjektion** | Spring Boot erstellt und verbindet Objekte automatisch |
| **Jackson** | - | Der eingebauten JSON-Uebersetzer in Spring Boot |

---

## 11. DTO + JSON Regeln

> **Jedes DTO, das mit JSON arbeitet, bekommt immer einen leeren Konstruktor.**

| Regel | Ausnahme? |
|-------|-----------|
| DTO + Spring Boot + JSON → leeren Konstruktor | Keine Ausnahme |
| DTO + nur intern (kein JSON) | Optional |
| DTO + Datenbank (JPA) | Leeren Konstruktor noetig |

**Warum?** Jackson (Spring Boot's JSON-Uebersetzer) braucht den leeren Konstruktor, um ein Objekt aus JSON zu erstellen:

```java
// Jackson macht intern:
WeatherResponse weather = new WeatherResponse();  // ← Leerer Konstruktor
weather.setCity("Zurich");                        // ← Setter
weather.setTemperature(22.5);                     // ← Setter
```

---

## Zusammenfassung

| Regel | Wichtigkeit |
|-------|------------|
| Secrets nicht im Code | 🔴 Hoch |
| DTOs ohne Logik | 🔴 Hoch |
| Tests ohne echte API | 🟡 Mittel |
| Konstruktor-Injection | 🟡 Mittel |
| `.gitignore` fuer Secrets | 🔴 Hoch |
| Keine Logik im Controller | 🟡 Mittel |
| Globaler Exception-Handler | 🟡 Mittel |
| HTTP-Status-Codes | 🟢 Niedrig |

**Diese Regeln machen deinen Code wartbar, testbar und sicher.**
