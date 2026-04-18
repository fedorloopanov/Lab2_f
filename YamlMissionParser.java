package app.parser;

import app.exception.MissionParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EventMissionParser implements MissionParser {
    @Override
    public boolean supports(File file) {
        if (file.getName().contains("A5") || !file.getName().contains(".")) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String firstLine = reader.readLine();
                return firstLine != null && firstLine.contains("|")
                        && (firstLine.startsWith("MISSION_CREATED") || firstLine.startsWith("CURSE_DETECTED"));
            } catch (IOException ignored) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getFormatName() {
        return "EVENT_LOG";
    }

    @Override
    public Map<String, Object> parse(File file) throws MissionParseException {
        Map<String, Object> mission = new LinkedHashMap<>();
        List<Map<String, Object>> sorcerers = new ArrayList<>();
        List<Map<String, Object>> techniques = new ArrayList<>();
        List<Map<String, Object>> timeline = new ArrayList<>();
        Map<String, Object> civilianImpact = new LinkedHashMap<>();
        List<String> attackPatterns = new ArrayList<>();

        Map<String, Object> enemyActivity = new LinkedHashMap<>();
        enemyActivity.put("attackPatterns", attackPatterns);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|");
                switch (parts[0]) {
                    case "MISSION_CREATED" -> {
                        mission.put("missionId", parts[1]);
                        mission.put("date", parts[2]);
                        mission.put("location", parts[3]);
                    }
                    case "CURSE_DETECTED" -> {
                        Map<String, Object> curse = new LinkedHashMap<>();
                        curse.put("name", parts[1]);
                        curse.put("threatLevel", parts[2]);
                        mission.put("curse", curse);
                    }
                    case "SORCERER_ASSIGNED" -> {
                        Map<String, Object> sorcerer = new LinkedHashMap<>();
                        sorcerer.put("name", parts[1]);
                        sorcerer.put("rank", parts[2]);
                        sorcerers.add(sorcerer);
                    }
                    case "TECHNIQUE_USED" -> {
                        Map<String, Object> technique = new LinkedHashMap<>();
                        technique.put("name", parts[1]);
                        technique.put("type", parts[2]);
                        technique.put("owner", parts[3]);
                        technique.put("damage", parseLong(parts[4]));
                        techniques.add(technique);
                    }
                    case "TIMELINE_EVENT" -> {
                        Map<String, Object> event = new LinkedHashMap<>();
                        event.put("timestamp", parts[1]);
                        event.put("type", parts[2]);
                        event.put("description", parts[3]);
                        timeline.add(event);
                    }
                    case "ENEMY_ACTION" -> {
                        if (!enemyActivity.containsKey("behaviorType")) {
                            enemyActivity.put("behaviorType", parts[1]);
                        }
                        attackPatterns.add(parts[2]);
                    }
                    case "CIVILIAN_IMPACT" -> {
                        for (int i = 1; i < parts.length; i++) {
                            String[] pair = parts[i].split("=", 2);
                            if (pair.length == 2) {
                                civilianImpact.put(pair[0], parseLong(pair[1]));
                            }
                        }
                    }
                    case "MISSION_RESULT" -> {
                        mission.put("outcome", parts[1]);
                        if (parts.length > 2) {
                            String[] pair = parts[2].split("=", 2);
                            if (pair.length == 2 && "damageCost".equals(pair[0])) {
                                mission.put("damageCost", parseLong(pair[1]));
                            }
                        }
                    }
                    default -> mission.put("lastUnknownEvent", line);
                }
            }
        } catch (IOException e) {
            throw new MissionParseException("Не удалось разобрать событийный формат: " + e.getMessage(), e);
        }

        if (!sorcerers.isEmpty()) mission.put("sorcerers", sorcerers);
        if (!techniques.isEmpty()) mission.put("techniques", techniques);
        if (!timeline.isEmpty()) mission.put("operationTimeline", timeline);
        if (!civilianImpact.isEmpty()) mission.put("civilianImpact", civilianImpact);
        if (!attackPatterns.isEmpty()) mission.put("enemyActivity", enemyActivity);
        return mission;
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
