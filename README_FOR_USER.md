package app.parser;

import app.exception.MissionParseException;

import java.io.File;
import java.util.Map;

public interface MissionParser {
    boolean supports(File file);
    String getFormatName();
    Map<String, Object> parse(File file) throws MissionParseException;
}
