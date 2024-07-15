package org.example.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JSONReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readJsonFile(String filePath, Class<T> valueType) throws IOException {
        return objectMapper.readValue(new File(filePath), valueType);
    }

    public static <T> void writeJsonFile(String filePath, T value) throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), value);
    }
}
