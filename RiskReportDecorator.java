package app.parser;

import app.exception.MissionParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YamlMissionParser implements MissionParser {
    @Override
    public boolean supports(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".yaml") || name.endsWith(".yml");
    }

    @Override
    public String getFormatName() {
        return "YAML";
    }

    @Override
    public Map<String, Object> parse(File file) throws MissionParseException {
        Map<String, Object> mission = new LinkedHashMap<>();
        List<Map<String, Object>> currentList = null;
        Map<String, Object> currentItem = null;
        Map<String, Object> currentBlock = null;
        String currentBlockName = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                int indent = indent(line);
                String trimmed = line.trim();

                if (indent == 0) {
                    if (currentItem != null && currentList != null) {
                        currentList.add(new LinkedHashMap<>(currentItem));
                        currentItem = null;
                    }
                    if (currentList != null && currentBlockName != null) {
                        mission.put(currentBlockName, new ArrayList<>(currentList));
                        currentList = null;
                    }
                    if (currentBlock != null && currentBlockName != null) {
                        mission.put(currentBlockName, new LinkedHashMap<>(currentBlock));
                        currentBlock = null;
                    }

                    int sep = trimmed.indexOf(':');
                    if (sep < 0) {
                        continue;
                    }
                    String key = trimmed.substring(0, sep).trim();
                    String value = trimmed.substring(sep + 1).trim();
                    if (value.isEmpty()) {
                        currentBlockName = key;
                    } else {
                        mission.put(key, parseMaybeNumberOrBoolean(value));
                        currentBlockName = null;
                    }
                } else if (indent == 2) {
                    if (trimmed.startsWith("- ")) {
                        if (currentItem != null && currentList != null) {
                            currentList.add(new LinkedHashMap<>(currentItem));
                        }
                        if (currentList == null) {
                            currentList = new ArrayList<>();
                        }
                        currentItem = new LinkedHashMap<>();
                        currentBlock = null;
                        String rest = trimmed.substring(2).trim();
                        if (rest.contains(":")) {
                            int sep = rest.indexOf(':');
                            String key = rest.substring(0, sep).trim();
                            String value = rest.substring(sep + 1).trim();
                            currentItem.put(key, parseMaybeNumberOrBoolean(value));
                        }
                    } else {
                        if (currentBlock == null) {
                            currentBlock = new LinkedHashMap<>();
                        }
                        int sep = trimmed.indexOf(':');
                        if (sep < 0) continue;
                        String key = trimmed.substring(0, sep).trim();
                        String value = trimmed.substring(sep + 1).trim();
                        currentBlock.put(key, parseMaybeNumberOrBoolean(value));
                    }
                } else if (indent == 4 && currentItem != null) {
                    int sep = trimmed.indexOf(':');
                    if (sep < 0) continue;
                    String key = trimmed.substring(0, sep).trim();
                    String value = trimmed.substring(sep + 1).trim();
                    currentItem.put(key, parseMaybeNumberOrBoolean(value));
                }
            }

            if (currentItem != null && currentList != null) {
                currentList.add(new LinkedHashMap<>(currentItem));
            }
            if (currentList != null && currentBlockName != null) {
                mission.put(currentBlockName, currentList);
            }
            if (currentBlock != null && currentBlockName != null) {
                mission.put(currentBlockName, currentBlock);
            }
            return mission;
        } catch (IOException e) {
            throw new MissionParseException("Не удалось разобрать YAML: " + e.getMessage(), e);
        }
    }

    private int indent(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }
        return count;
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
