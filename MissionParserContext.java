package app.parser;

import app.exception.MissionParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class JsonMissionParser implements MissionParser {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(File file) {
        return file.getName().toLowerCase().endsWith(".json");
    }

    @Override
    public String getFormatName() {
        return "JSON";
    }

    @Override
    public Map<String, Object> parse(File file) throws MissionParseException {
        try {
            return mapper.readValue(file, new TypeReference<LinkedHashMap<String, Object>>() {});
        } catch (IOException e) {
            throw new MissionParseException("Не удалось разобрать JSON: " + e.getMessage(), e);
        }
    }
}
