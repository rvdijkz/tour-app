package com.rvdijkz.tour.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@Converter
public class IntegerListCsvConverter implements AttributeConverter<List<Integer>, String> {

    private static final String SEPARATOR = ",";

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
            .map(String::valueOf)
            .collect(java.util.stream.Collectors.joining(SEPARATOR));
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(SEPARATOR))
            .filter(value -> !value.isBlank())
            .map(Integer::valueOf)
            .toList();
    }
}
