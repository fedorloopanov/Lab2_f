package app.processor;

import app.model.Mission;

public record ProcessedMission(Mission mission, String formatName, String reportName, String reportText) {
}
