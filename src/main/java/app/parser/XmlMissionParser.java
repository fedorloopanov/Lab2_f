package app.parser;

import app.exception.MissionParseException;
import app.input.MissionInput;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.IOException;

public class XmlMissionParser implements MissionParser {
    private final XmlMapper mapper = new XmlMapper();

    @Override
    public String getFormatName() {
        return "XML";
    }

    @Override
    public boolean supports(File file) {
        return file.getName().toLowerCase().endsWith(".xml");
    }

    @Override
    public MissionInput parse(File file) throws MissionParseException {
        try {
            return mapper.readValue(file, MissionInput.class);
        } catch (IOException e) {
            throw new MissionParseException("Не удалось разобрать XML: " + e.getMessage(), e);
        }
    }
}
