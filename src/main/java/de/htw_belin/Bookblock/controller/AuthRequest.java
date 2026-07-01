package de.htw_belin.Bookblock.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Eingabedaten fuer Registrierung und Login.
 * Die Validierung (@Valid im Controller) sorgt fuer saubere Fehlermeldungen,
 * bevor irgendetwas gespeichert wird.
 */
public record AuthRequest(
        @Email(message = "Bitte eine gueltige E-Mail angeben")
        @NotBlank(message = "E-Mail darf nicht leer sein")
        String email,

        @NotBlank(message = "Passwort darf nicht leer sein")
        @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen haben")
        String password) {
}
