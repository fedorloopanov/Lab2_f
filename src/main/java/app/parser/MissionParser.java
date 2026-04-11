package app.parser;

import app.exception.MissionParseException;
import app.input.MissionInput;

import java.io.File;

public interface MissionParser {
    String getFormatName();
    boolean supports(File file);
    MissionInput parse(File file) throws MissionParseException;
}
