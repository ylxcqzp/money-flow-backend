package com.ghost.moneyflowbackend.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getValueAsString();
        if (value == null) {
            return null;
        }
        String text = value.trim();
        if (text.isEmpty()) {
            return null;
        }
        if (text.length() == 10) {
            try {
                return LocalDate.parse(text, DATE_FORMATTER).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                return invalidValue(parser, text);
            }
        }
        try {
            return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
            return invalidValue(parser, text);
        }
    }

    private LocalDateTime invalidValue(JsonParser parser, String text) throws InvalidFormatException {
        throw InvalidFormatException.from(parser, "时间格式不正确，支持 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss", text, LocalDateTime.class);
    }
}
