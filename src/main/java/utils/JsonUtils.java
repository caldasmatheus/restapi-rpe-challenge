package utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    public static String convertToJson(Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter objeto para JSON", e);
        }
    }
}