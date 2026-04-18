package app.parser;

import app.exception.MissionParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TxtMissionParser implements MissionParser {
    @Override
    public boolean supports(File file) {
        return file.getName().toLowerCase().endsWith(".txt");
    }

    @Override
    public String getFormatName() {
        return "TXT";
    }

    @Override
    public Map<String, Object> parse(File file) throws MissionParseException {
        Map<String, Object> mission = new LinkedHashMap<>();
        List<Map<String, Object>> sorcerers = new ArrayList<>();
        List<Map<String, Object>> techniques = new ArrayList<>();
        Map<String, Object> currentBlock = new LinkedHashMap<>();
        String currentSection = "MISSION";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("[") && line.endsWith("]")) {
                    commitSection(mission, sorcerers, techniques, currentSection, currentBlock);
                    currentSection = line.substring(1, line.length() - 1).trim().toUpperCase(Locale.ROOT);
                    currentBlock = new LinkedHashMap<>();
                    continue;
                }

                int separator = line.indexOf('=');
                if (separator < 0) {
                    continue;
                }
                String key = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                currentBlock.put(key, parseMaybeNumberOrBoolean(value));
            }
            commitSection(mission, sorcerers, techniques, currentSection, currentBlock);
        } catch (IOException e) {
            throw new MissionParseException("Не удалось разобрать TXT: " + e.getMessage(), e);
        }

        if (!sorcerers.isEmpty()) {
            mission.put("sorcerers", sorcerers);
        }
        if (!techniques.isEmpty()) {
            mission.put("techniques", techniques);
        }
        return mission;
    }

    private void commitSection(Map<String, Object> mission,
                               List<Map<String, Object>> sorcerers,
                               List<Map<String, Object>> techniques,
                               String section,
                               Map<String, Object> block) {
        if (block.isEmpty()) {
            return;
        }
        switch (section) {
            case "MISSION" -> mission.putAll(block);
            case "CURSE" -> mission.put("curse", new LinkedHashMap<>(block));
            case "SORCERER" -> sorcerers.add(new LinkedHashMap<>(block));
            case "TECHNIQUE" -> techniques.add(new LinkedHashMap<>(block));
            default -> mission.put(toCamel(section), new LinkedHashMap<>(block));
        }
    }

    private String toCamel(String section) {
        String lower = section.toLowerCase(Locale.ROOT);
        String[] parts = lower.split("[_\\s-]+");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0))).append(parts[i].substring(1));
            }
        }
        if ("environment".equals(sb.toString())) {
            return "environmentConditions";
        }
        return sb.toString();
    }

    private Object parseMaybeNumberOrBoolean(String value) {
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return Boolean.parseBoolean(value);
        }
        try {
            if (value.contains(".")) return Double.parseDouble(value);
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return value;
        }
    }
}
