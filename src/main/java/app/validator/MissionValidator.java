package app.validator;

import app.exception.MissionParseException;
import app.model.Mission;

public class MissionValidator {
    public void validate(Mission mission) throws MissionParseException {
        if (mission == null) {
            throw new MissionParseException("Файл не содержит корректную миссию.");
        }
        if (isBlank(mission.getMissionId())) {
            throw new MissionParseException("Не найден missionId.");
        }
        if (isBlank(mission.getDate())) {
            throw new MissionParseException("Не найдена дата миссии.");
        }
        if (isBlank(mission.getLocation())) {
            throw new MissionParseException("Не найдена локация миссии.");
        }
        if (isBlank(mission.getOutcome())) {
            throw new MissionParseException("Не найден итог миссии.");
        }
        if (mission.getCurse() == null) {
            throw new MissionParseException("Не найден блок curse.");
        }
        if (isBlank(mission.getCurse().getName())) {
            throw new MissionParseException("Не найдено название проклятия.");
        }
        if (isBlank(mission.getCurse().getThreatLevel())) {
            throw new MissionParseException("Не найден уровень угрозы проклятия.");
        }
        if (mission.getSorcerers() == null || mission.getSorcerers().isEmpty()) {
            throw new MissionParseException("Не найден список sorcerers.");
        }
        if (mission.getTechniques() == null || mission.getTechniques().isEmpty()) {
            throw new MissionParseException("Не найден список techniques.");
        }
        if (mission.getDamageCost() < 0) {
            throw new MissionParseException("Материальный ущерб не может быть отрицательным.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
