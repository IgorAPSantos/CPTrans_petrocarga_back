package com.cptrans.petrocarga.infrastructure.persistance.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MetadataJsonConverter implements AttributeConverter<Object, String> {
    private static final ObjectMapper mapper = JsonMapper.builder()
    .addModule(new JavaTimeModule())
    .build();


    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try {
            return attribute == null ? null : mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao converter metadata para JSON");
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : mapper.readValue(dbData, Object.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao converter JSON para metadata");
        }
    }
}
