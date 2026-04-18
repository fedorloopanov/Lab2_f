package app.parser;

import app.exception.MissionParseException;

import java.io.File;
import java.util.Map;

public class MissionParserContext {
    private MissionParser strategy;

    public MissionParserContext(MissionParser strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(MissionParser strategy) {
        this.strategy = strategy;
    }

    public MissionParser getStrategy() {
        return strategy;
    }

    public boolean supports(File file) {
        ensureStrategy();
        return strategy.supports(file);
    }

    public String getFormatName() {
        ensureStrategy();
        return strategy.getFormatName();
    }

    public Map<String, Object> parse(File file) throws MissionParseException {
        ensureStrategy();
        return strategy.parse(file);
    }

    private void ensureStrategy() {
        if (strategy == null) {
            throw new IllegalStateException("Стратегия парсинга не установлена");
        }
    }
}
