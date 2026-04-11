package app.parser;

import app.exception.MissionParseException;
import app.input.MissionInput;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtMissionParser implements MissionParser {
    private static final Pattern SORCERER_PATTERN = Pattern.compile("sorcerer\\[(\\d+)\\]\\.(\\w+)");
    private static final Pattern TECHNIQUE_PATTERN = Pattern.compile("technique\\[(\\d+)\\]\\.(\\w+)");

    @Override
    public String getFormatName() {
        return "TXT";
    }

    @Override
    public boolean supports(File file) {
        return file.getName().toLowerCase().endsWith(".txt");
    }

    @Override
    public MissionInput parse(File file) throws MissionParseException {
        List<String> lines = readAllLines(file);
        if (looksLikeIniFormat(lines)) {
            return parseIniStyle(lines);
        }
        return parseLegacyStyle(lines);
    }

    private List<String> readAllLines(File file) throws MissionParseException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return lines;
        } catch (IOException e) {
            throw new MissionParseException("Не удалось прочитать TXT: " + e.getMessage(), e);
        }
    }

    private boolean looksLikeIniFormat(List<String> lines) {
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isBlank()) {
                continue;
            }
            return line.startsWith("[") || line.contains("=");
        }
        return false;
    }

    private MissionInput parseLegacyStyle(List<String> lines) throws MissionParseException {
        MissionInput input = new MissionInput();
        List<MissionInput.SorcererInput> sorcerers = new ArrayList<>();
        List<MissionInput.TechniqueInput> techniques = new ArrayList<>();

        for (String raw : lines) {
            parseLegacyLine(raw.trim(), input, sorcerers, techniques);
        }

        input.setSorcerers(sorcerers);
        input.setTechniques(techniques);
        return input;
    }

    private void parseLegacyLine(String line,
                                 MissionInput input,
                                 List<MissionInput.SorcererInput> sorcerers,
                                 List<MissionInput.TechniqueInput> techniques) throws MissionParseException {
        if (line.isBlank() || !line.contains(":")) {
            return;
        }

        String[] parts = line.split(":", 2);
        String key = parts[0].trim();
        String value = parts[1].trim();

        switch (key) {
            case "missionId" -> input.setMissionId(value);
            case "date" -> input.setDate(value);
            case "location" -> input.setLocation(value);
            case "outcome" -> input.setOutcome(value);
            case "damageCost" -> input.setDamageCost(parseLong(value, "damageCost"));
            case "note" -> input.setNote(value);
            case "comment" -> input.setComment(value);
            default -> {
                if (key.startsWith("curse.")) {
                    fillCurse(input, key.substring(6), value);
                } else if (!fillLegacySorcerer(sorcerers, key, value)) {
                    fillLegacyTechnique(techniques, key, value);
                }
            }
        }
    }

    private MissionInput parseIniStyle(List<String> lines) throws MissionParseException {
        MissionInput input = new MissionInput();
        List<MissionInput.SorcererInput> sorcerers = new ArrayList<>();
        List<MissionInput.TechniqueInput> techniques = new ArrayList<>();
        List<String> extraBlocks = new ArrayList<>();

        String currentSection = null;
        MissionInput.SorcererInput currentSorcerer = null;
        MissionInput.TechniqueInput currentTechnique = null;

        for (String raw : lines) {
            String line = raw.trim();
            if (line.isBlank() || line.startsWith("#") || line.startsWith(";")) {
                continue;
            }

            if (line.startsWith("[") && line.endsWith("]")) {
                if ("SORCERER".equals(currentSection) && currentSorcerer != null) {
                    sorcerers.add(currentSorcerer);
                    currentSorcerer = null;
                }
                if ("TECHNIQUE".equals(currentSection) && currentTechnique != null) {
                    techniques.add(currentTechnique);
                    currentTechnique = null;
                }

                currentSection = line.substring(1, line.length() - 1).trim().toUpperCase();

                if ("SORCERER".equals(currentSection)) {
                    currentSorcerer = new MissionInput.SorcererInput();
                } else if ("TECHNIQUE".equals(currentSection)) {
                    currentTechnique = new MissionInput.TechniqueInput();
                }

                continue;
            }

            if (!line.contains("=")) {
                continue;
            }

            String[] parts = line.split("=", 2);
            String key = parts[0].trim();
            String value = parts[1].trim();

            if (currentSection == null) {
                continue;
            }

            switch (currentSection) {
                case "MISSION" -> fillMissionSection(input, key, value);
                case "CURSE" -> fillCurse(input, key, value);
                case "SORCERER" -> fillIniSorcerer(currentSorcerer, key, value);
                case "TECHNIQUE" -> fillIniTechnique(currentTechnique, key, value);
                default -> extraBlocks.add("[" + currentSection + "] " + key + "=" + value);
            }
        }

        if ("SORCERER".equals(currentSection) && currentSorcerer != null) {
            sorcerers.add(currentSorcerer);
        }
        if ("TECHNIQUE".equals(currentSection) && currentTechnique != null) {
            techniques.add(currentTechnique);
        }

        input.setSorcerers(sorcerers);
        input.setTechniques(techniques);

        if (!extraBlocks.isEmpty()) {
            String extraText = "Дополнительные блоки файла:\n" + String.join("\n", extraBlocks);
            if (input.getComment() == null || input.getComment().isBlank()) {
                input.setComment(extraText);
            } else {
                input.setComment(input.getComment() + "\n" + extraText);
            }
        }

        return input;
    }

    private void fillMissionSection(MissionInput input, String key, String value) throws MissionParseException {
        switch (key) {
            case "missionId" -> input.setMissionId(value);
            case "date" -> input.setDate(value);
            case "location" -> input.setLocation(value);
            case "outcome" -> input.setOutcome(value);
            case "damageCost" -> input.setDamageCost(parseLong(value, "damageCost"));
            case "note" -> input.setNote(value);
            case "comment" -> input.setComment(value);
            default -> {
            }
        }
    }

    private void fillCurse(MissionInput input, String field, String value) {
        if (input.getCurse() == null) {
            input.setCurse(new MissionInput.CurseInput());
        }

        switch (field) {
            case "name" -> input.getCurse().setName(value);
            case "threatLevel" -> input.getCurse().setThreatLevel(value);
            default -> {
            }
        }
    }

    private boolean fillLegacySorcerer(List<MissionInput.SorcererInput> sorcerers, String key, String value) {
        Matcher matcher = SORCERER_PATTERN.matcher(key);
        if (!matcher.matches()) {
            return false;
        }

        int index = Integer.parseInt(matcher.group(1));
        String field = matcher.group(2);

        while (sorcerers.size() <= index) {
            sorcerers.add(new MissionInput.SorcererInput());
        }

        MissionInput.SorcererInput sorcerer = sorcerers.get(index);
        switch (field) {
            case "name" -> sorcerer.setName(value);
            case "rank" -> sorcerer.setRank(value);
            default -> {
            }
        }
        return true;
    }

    private void fillLegacyTechnique(List<MissionInput.TechniqueInput> techniques, String key, String value)
            throws MissionParseException {
        Matcher matcher = TECHNIQUE_PATTERN.matcher(key);
        if (!matcher.matches()) {
            return;
        }

        int index = Integer.parseInt(matcher.group(1));
        String field = matcher.group(2);

        while (techniques.size() <= index) {
            techniques.add(new MissionInput.TechniqueInput());
        }

        MissionInput.TechniqueInput technique = techniques.get(index);
        switch (field) {
            case "name" -> technique.setName(value);
            case "type" -> technique.setType(value);
            case "owner" -> technique.setOwner(value);
            case "damage" -> technique.setDamage(parseLong(value, "technique[" + index + "].damage"));
            default -> {
            }
        }
    }

    private void fillIniSorcerer(MissionInput.SorcererInput sorcerer, String key, String value) {
        if (sorcerer == null) {
            return;
        }

        switch (key) {
            case "name" -> sorcerer.setName(value);
            case "rank" -> sorcerer.setRank(value);
            default -> {
            }
        }
    }

    private void fillIniTechnique(MissionInput.TechniqueInput technique, String key, String value)
            throws MissionParseException {
        if (technique == null) {
            return;
        }

        switch (key) {
            case "name" -> technique.setName(value);
            case "type" -> technique.setType(value);
            case "owner" -> technique.setOwner(value);
            case "damage" -> technique.setDamage(parseLong(value, "technique.damage"));
            default -> {
            }
        }
    }

    private long parseLong(String value, String fieldName) throws MissionParseException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new MissionParseException("Поле " + fieldName + " должно содержать число.");
        }
    }
}