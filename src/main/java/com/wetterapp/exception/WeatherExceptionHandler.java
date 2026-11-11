package com.wetterapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Globaler Exception Handler fuer die Wetter-App.
 * Fängt alle Exceptions ab und gibt freundliche JSON-Fehlermeldungen zurück.
 */
@RestControllerAdvice
public class WeatherExceptionHandler {

    /**
     * Fängt RuntimeException ab (z. B. Stadt nicht gefunden, API-Fehler).
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("error", "Wetterabfrage fehlgeschlagen");
        error.put("message", e.getMessage());

        HttpStatus status = e.getMessage() != null && e.getMessage().contains("nicht gefunden")
                ? HttpStatus.NOT_FOUND
                : HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(error);
    }
}
