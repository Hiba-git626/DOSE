package com.example.dose.exception;

import java.util.Map;
/** Corps JSON de toutes les erreurs. {@code fields} n'est rempli que pour les erreurs de validation. */
public record ApiError(
        String code,
        String message,
        Map<String, String> fields
) {
}
