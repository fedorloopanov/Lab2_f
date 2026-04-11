package app.report;

import app.model.Mission;
import app.model.Sorcerer;
import app.model.Technique;
import app.service.MissionStatistics;

public class SummaryReportGenerator implements MissionReportGenerator {
    @Override
    public String getReportName() {
        return "Краткая сводка";
    }

    @Override
    public String generate(Mission mission) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID миссии: ").append(safe(mission.getMissionId())).append("\n");
        sb.append("Дата: ").append(safe(mission.getDate())).append("\n");
        sb.append("Локация: ").append(safe(mission.getLocation())).append("\n");
        sb.append("Итог: ").append(safe(mission.getOutcome())).append("\n");
        sb.append("Материальный ущерб: ").append(mission.getDamageCost()).append("\n");

        if (mission.getCurse() != null) {
            sb.append("\nПроклятие: ").append(safe(mission.getCurse().getName())).append("\n");
            sb.append("Уровень угрозы: ").append(safe(mission.getCurse().getThreatLevel())).append("\n");
        }

        sb.append("\nУчастники: ").append(MissionStatistics.participantCount(mission)).append("\n");
        for (Sorcerer sorcerer : mission.getSorcerers()) {
            sb.append("- ")
                    .append(safe(sorcerer.getName()))
                    .append(" (")
                    .append(safe(sorcerer.getRank()))
                    .append(")\n");
        }

        sb.append("\nТехники: ").append(MissionStatistics.techniqueCount(mission)).append("\n");
        for (Technique technique : mission.getTechniques()) {
            sb.append("- ")
                    .append(safe(technique.getName()))
                    .append(", тип: ")
                    .append(safe(technique.getType()))
                    .append(", владелец: ")
                    .append(safe(technique.getOwner()))
                    .append(", урон: ")
                    .append(technique.getDamage())
                    .append("\n");
        }

        if (!mission.getAdditionalDetails().isBlank()) {
            sb.append("\nДополнительная информация: ")
                    .append(mission.getAdditionalDetails())
                    .append("\n");
        }

        return sb.toString();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
