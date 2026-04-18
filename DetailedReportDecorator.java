package app.parser;

import java.io.File;


public abstract class MissionParserCreator {
    protected abstract MissionParser factoryMethod();

    public MissionParser createParser() {
        return factoryMethod();
    }

    public boolean supports(File file) {
        return createParser().supports(file);
    }

    public String getFormatName() {
        return createParser().getFormatName();
    }

    public static MissionParserCreator json() {
        return new JsonCreator();
    }

    public static MissionParserCreator xml() {
        return new XmlCreator();
    }

    public static MissionParserCreator yaml() {
        return new YamlCreator();
    }

    public static MissionParserCreator txt() {
        return new TxtCreator();
    }

    public static MissionParserCreator event() {
        return new EventCreator();
    }

    private static final class JsonCreator extends MissionParserCreator {
        private final MissionParser parser = new JsonMissionParser();

        @Override
        protected MissionParser factoryMethod() {
            return parser;
        }
    }

    private static final class XmlCreator extends MissionParserCreator {
        private final MissionParser parser = new XmlMissionParser();

        @Override
        protected MissionParser factoryMethod() {
            return parser;
        }
    }

    private static final class YamlCreator extends MissionParserCreator {
        private final MissionParser parser = new YamlMissionParser();

        @Override
        protected MissionParser factoryMethod() {
            return parser;
        }
    }

    private static final class TxtCreator extends MissionParserCreator {
        private final MissionParser parser = new TxtMissionParser();

        @Override
        protected MissionParser factoryMethod() {
            return parser;
        }
    }

    private static final class EventCreator extends MissionParserCreator {
        private final MissionParser parser = new EventMissionParser();

        @Override
        protected MissionParser factoryMethod() {
            return parser;
        }
    }
}
