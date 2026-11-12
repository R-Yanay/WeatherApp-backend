# WeatherApp - Architektur und Klassen

## Abhaengigkeitsdiagramm

```
┌─────────────────────────────────────────────────────────┐
│  application.properties                                 │
│  (API-Key, Basis-URL, Port)                             │
└──────────────┬──────────────────────────────────────────┘
               │ @Value liest Werte
               ▼
┌─────────────────────────────────────────────────────────┐
│  WeatherAppApplication.java                             │
│  Aufgabe: Startet den Server (Tomcat) auf Port 8080     │
│  Annotation: @SpringBootApplication                      │
│  Abhaengigkeiten: Keine                                 │
└──────────────┬──────────────────────────────────────────┘
               │ Spring Boot scannt alle @Service, @RestController
               ▼
┌─────────────────────────────────────────────────────────┐
│  WeatherService.java                                    │
│  Aufgabe: Ruft OpenWeatherMap API auf, parst JSON       │
│  Annotation: @Service                                   │
│  Abhaengigkeiten: application.properties, RestTemplate   │
└──────────────┬──────────────────────────────────────────┘
               │ Dependency Injection (Konstruktor)
               ├──────────────────────────────────────────┘
               ▼                  ▼
  ┌────────────────────┐   ┌────────────────────┐
  │ WeatherController  │   │ForecastController   │
  │ Aufgabe: Beantwortet│   │ Aufgabe: Beantwortet│
  │ GET /api/weather   │   │ GET /api/forecast  │
  │ Annotation:        │   │ Annotation:        │
  │ @RestController    │   │ @RestController    │
  └────────┬───────────┘   └────────┬───────────┘
           │                        │
           ▼                        ▼
  ┌────────────────────┐   ┌────────────────────┐
  │ WeatherResponse    │   │ ForecastResponse    │
  │ Aufgabe: Speichert │   │ Aufgabe: Speichert  │
  │ aktuelles Wetter   │   │ 5-Tage-Vorhersage   │
  │ (DTO, keine        │   │ (DTO, keine         │
  │  Annotation)       │   │  Annotation)        │
  └────────────────────┘   └────────────────────┘

  ┌─────────────────────────────────────────────────────┐
  │ WeatherExceptionHandler.java                        │
  │ Aufgabe: Faengt Exceptions ab, gibt JSON-Fehler      │
  │ Annotation: @RestControllerAdvice                    │
  │ Abhaengigkeiten: Keine (hoert auf ALLE Controller)    │
  └─────────────────────────────────────────────────────┘
```

---

## Die 7 Klassen im Detail

### 1. WeatherAppApplication.java - Der Startknopf

| Eigenschaft | Wert |
|-------------|------|
| **Package** | `com.wetterapp` |
| **Annotation** | `@SpringBootApplication` |
| **Aufgabe** | Startet den eingebauten Tomcat-Server auf Port 8080 |
| **Braucht** | Nichts |
| **Wird gebraucht von** | Nichts (Spring Boot startet sie automatisch) |

**Warum diese Klasse?** Ohne sie startet der Server nicht. Sie ist der Einstiegspunkt - wie `main()` in jedem Java-Programm.

---

### 2. WeatherService.java - Der Uebersetzer

| Eigenschaft | Wert |
|-------------|------|
| **Package** | `com.wetterapp.service` |
| **Annotation** | `@Service` |
| **Aufgabe** | Ruft OpenWeatherMap API auf, parst die JSON-Antwort |
| **Braucht** | `application.properties` (API-Key), `RestTemplate` (HTTP-Client) |
| **Wird gebraucht von** | `WeatherController`, `ForecastController` |

**Warum diese Klasse?** Hier passiert die eigentliche Arbeit: API-Aufruf und JSON-Parsing. Ohne sie gaebe es keine Wetterdaten.

---

### 3. WeatherController.java - Der Empfangschef (aktuelles Wetter)

| Eigenschaft | Wert |
|-------------|------|
| **Package** | `com.wetterapp.controller` |
| **Annotation** | `@RestController`, `@RequestMapping("/api/weather")` |
| **Aufgabe** | Beantwortet `GET /api/weather?city=Zurich` |
| **Braucht** | `WeatherService` (Dependency Injection), `WeatherResponse` (DTO) |
| **Wird gebraucht von** | Browser, JavaFX GUI |

**Warum diese Klasse?** Sie faengt die URL-Anfrage ab und gibt JSON zurueck. Ohne sie koennte niemand die API aufrufen.

---

### 4. ForecastController.java - Der Empfangschef (Vorhersage)

| Eigenschaft | Wert |
|-------------|------|
| **Package** | `com.wetterapp.controller` |
| **Annotation** | `@RestController`, `@RequestMapping("/api/forecast")` |
| **Aufgabe** | Beantwortet `GET /api/forecast?city=Zurich` |
| **Braucht** | `WeatherService` (Dependency Injection), `ForecastResponse` (DTO) |
| **Wird gebraucht von** | Browser, JavaFX GUI |

**Warum diese Klasse?** Gleiche Aufgabe wie WeatherController, aber fuer die 5-Tage-Vorhersage. Zwei Controller, zwei Endpunkte.

---

### 5. WeatherResponse.java - Der Datenbehaelter (aktuelles Wetter)

| Eigenschaft | Wert |
|-------------|------|
| **Package** | `com.wetterapp.dto` |
| **Annotation** | Keine! |
| **Aufgabe** | Speichert: Stadt, Temperatur, Luftfeuchtigkeit, Wind, Beschreibung, Icon |
| **Braucht** | Nichts |
| **Wird gebraucht von** | `WeatherController`, `WeatherService` |

**Warum diese Klasse?** Sie ist ein reiner Datenbehaelter (DTO). Spring Boot wandelt sie automatisch zu JSON.

---

### 6. ForecastResponse.java - Der Datenbehaelter (Vorhersage)

| Eigenschaft | Wert |
|-------------|------|
| **Package** | `com.wetterapp.dto` |
| **Annotation** | Keine! |
| **Aufgabe** | Speichert: Datum, Min/Max Temperatur, Beschreibung, Icon |
| **Braucht** | Nichts |
| **Wird gebraucht von** | `ForecastController`, `WeatherService` |

**Warum diese Klasse?** Gleiche Aufgabe wie WeatherResponse, aber fuer die Vorhersage.

---

### 7. WeatherExceptionHandler.java - Das Sicherheitsnetz

| Eigenschaft | Wert |
|-------------|------|
| **Package** | `com.wetterapp.exception` |
| **Annotation** | `@RestControllerAdvice` |
| **Aufgabe** | Faengt Exceptions ab und gibt freundliche JSON-Fehlermeldungen |
| **Braucht** | Nichts |
| **Wird gebraucht von** | Allen Controllern (automatisch) |

**Warum diese Klasse?** Wenn die API kracht oder die Stadt nicht gefunden wird, faengt sie den Fehler ab und gibt eine freundliche Meldung statt eines haesslichen Stacktraces.

---

### 8. application.properties - Die Konfiguration

| Eigenschaft | Wert |
|-------------|------|
| **Aufgabe** | Speichert API-Key, Basis-URL, Server-Port |
| **Wird gelesen von** | `WeatherService` (ueber `@Value`) |

**Warum diese Datei?** Der API-Key ist ein Geheimnis. Indem er in einer separaten Datei steht, die in `.gitignore` ist, landet er nicht auf GitHub.

---

## Warum brauchst du so viele Klassen?

| Klasse | Konnte man sie weglassen? | Warum nicht? |
|--------|--------------------------|-------------|
| `WeatherAppApplication` | Nein | Ohne sie startet der Server nicht |
| `WeatherService` | Theoretisch ja | Du koenntest die Logik in den Controller packen, aber das waere schlechte Praxis |
| `WeatherController` | Nein | Ohne ihn gibt es keine URL-Endpunkte |
| `ForecastController` | Theoretisch ja | Du koenntest die Vorhersage in WeatherController packen, aber dann wird die Klasse zu gross |
| `WeatherResponse` | Nein | Spring Boot braucht ein Objekt, um JSON zu erstellen |
| `ForecastResponse` | Nein | Gleiche Logik wie WeatherResponse |
| `WeatherExceptionHandler` | Ja | Die App laeuft ohne sie, aber die Fehlermeldungen waeren haesslich |

---

## Der Datenfluss

```
1. Browser:  GET /api/weather?city=Zurich
         ↓
2. WeatherAppApplication hat den Server gestartet
         ↓
3. WeatherController faengt die URL ab
         ↓
4. WeatherController ruft WeatherService auf (Dependency Injection)
         ↓
5. WeatherService liest API-Key aus application.properties
         ↓
6. WeatherService ruft OpenWeatherMap API auf (RestTemplate)
         ↓
7. WeatherService parst JSON und fuellt WeatherResponse
         ↓
8. WeatherController gibt WeatherResponse als JSON an Browser
         ↓
9. Browser zeigt: {"city":"Zurich","temperature":22.5,...}

Fehlerfall:
WeatherExceptionHandler faengt Exception ab → gibt JSON-Fehlermeldung
```

---

## Vergleich: Schach (JavaFX) vs. WeatherApp (Spring Boot)

| Aspekt | Schach (JavaFX) | WeatherApp (Spring Boot) |
|--------|----------------|--------------------------|
| 1 Klasse startet die App | 1 Klasse startet den Server |
| 1 View-Klasse zeigt alles | 2 Controller-Klassen beantworten URLs |
| 1 Controller-Klasse steuert alles | 1 Service-Klasse macht die Logik |
| DTOs nur fuer Speichern/Laden | DTOs sind das Hauptformat (JSON) |
| Du siehst die GUI | Du siehst nur JSON-Text |

**Spring Boot scheint mehr Klassen zu brauchen, weil:**
1. **Jede URL braucht einen Controller** - im Schachspiel gibt es nur ein Fenster, hier gibt es Endpunkte
2. **Logik wird getrennt** - Service macht die Arbeit, Controller gibt nur die Antwort
3. **DTOs sind zentral** - jedes Datenobjekt braucht eine eigene Klasse

**Aber:** Die Klassen sind klein und haben eine klare Aufgabe. Das macht den Code wartbar und testbar.
