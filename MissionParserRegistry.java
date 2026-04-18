package app.parser;

import app.exception.MissionParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MissionParserRegistry {
    private final List<MissionParser> parsers = new ArrayList<>();

    public MissionParserRegistry register(MissionParser parser) {
        parsers.add(parser);
        return this;
    }

    public MissionParser getParser(File file) throws MissionParseException {
        for (MissionParser parser : parsers) {
            if (parser.supports(file)) {
                return parser;
            }
        }
        throw new MissionParseException("Формат файла не поддерживается: " + file.getName());
    }

    public String supportedFormats() {
        return parsers.stream()
                .map(MissionParser::getFormatName)
                .collect(Collectors.joining(", "));
    }
}
