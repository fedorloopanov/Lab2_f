package app.parser;

import app.exception.MissionParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MissionParserRegistry {
    private final List<MissionParserCreator> creators = new ArrayList<>();

    public MissionParserRegistry register(MissionParserCreator creator) {
        creators.add(creator);
        return this;
    }

    public MissionParser getParser(File file) throws MissionParseException {
        for (MissionParserCreator creator : creators) {
            MissionParser parser = creator.createParser();
            if (parser.supports(file)) {
                return parser;
            }
        }
        throw new MissionParseException("Формат файла не поддерживается: " + file.getName());
    }

    public String supportedFormats() {
        return creators.stream()
                .map(MissionParserCreator::getFormatName)
                .collect(Collectors.joining(", "));
    }
}
