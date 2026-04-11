package app.parser;

import app.exception.MissionParseException;
import app.input.MissionInput;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonMissionParser implements MissionParser {
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String getFormatName() {
        return "JSON";
    }

    @Override
    public boolean supports(File file) {
        return file.getName().toLowerCase().endsWith(".json");
    }

    @Override
    public MissionInput parse(File file) throws MissionParseException {
        try {
            return mapper.readValue(file, MissionInput.class);
        } catch (IOException e) {
            throw new MissionParseException("Не удалось разобрать JSON: " + e.getMessage(), e);
        }
    }
}
