package app.service;

import app.exception.MissionParseException;
import app.factory.MissionFactory;
import app.factory.ParserRegistry;
import app.input.MissionInput;
import app.model.Mission;
import app.parser.MissionParser;
import app.validator.MissionValidator;

import java.io.File;

public class MissionLoaderService {
    private final ParserRegistry parserRegistry;
    private final MissionFactory missionFactory;
    private final MissionValidator missionValidator;

    public MissionLoaderService(ParserRegistry parserRegistry,
                                MissionFactory missionFactory,
                                MissionValidator missionValidator) {
        this.parserRegistry = parserRegistry;
        this.missionFactory = missionFactory;
        this.missionValidator = missionValidator;
    }

    public LoadedMission load(File file) throws MissionParseException {
        MissionParser parser = parserRegistry.resolve(file);
        MissionInput input = parser.parse(file);
        Mission mission = missionFactory.create(input);
        missionValidator.validate(mission);
        return new LoadedMission(mission, parser.getFormatName());
    }

    public String supportedFormats() {
        return parserRegistry.supportedFormats();
    }
}
