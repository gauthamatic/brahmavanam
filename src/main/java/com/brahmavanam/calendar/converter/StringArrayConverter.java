package com.brahmavanam.calendar.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter
public class StringArrayConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        return attribute != null ? String.join(",", attribute) : null;
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        return dbData != null ? dbData.split(",") : new String[0];
    }
}