package com.rvdijkz.tour.persistence.converter;

import com.rvdijkz.tour.persistence.value.PlacedBibData;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;

@Converter
public class PlacedBibListCsvConverter implements AttributeConverter<List<PlacedBibData>, String> {

    private static final String ITEM_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = ":";

    @Override
    public String convertToDatabaseColumn(List<PlacedBibData> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream()
            .map(item -> item.place() + VALUE_SEPARATOR + item.raceBibNumber())
            .collect(java.util.stream.Collectors.joining(ITEM_SEPARATOR));
    }

    @Override
    public List<PlacedBibData> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return List.of();
        }
        return Arrays.stream(dbData.split(ITEM_SEPARATOR))
            .filter(value -> !value.isBlank())
            .map(this::parseItem)
            .toList();
    }

    private PlacedBibData parseItem(String value) {
        String[] parts = value.split(VALUE_SEPARATOR, 2);
        return new PlacedBibData(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}
