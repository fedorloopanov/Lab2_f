package app.factory;

import app.exception.MissionParseException;
import app.parser.MissionParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ParserRegistry {
    private final List<MissionParser> parsers = new ArrayList<>();

    public ParserRegistry register(MissionParser parser) {
        parsers.add(parser);
        return this;
    }

    public MissionParser resolve(File file) throws MissionParseException {
        return parsers.stream()
                .filter(parser -> parser.supports(file))
                .findFirst()
                .orElseThrow(() -> new MissionParseException("Формат файла не поддерживается: " + file.getName()));
    }

    public String supportedFormats() {
        return parsers.stream()
                .map(MissionParser::getFormatName)
                .collect(Collectors.joining(", "));
    }
}
