package app.report;

import app.model.Mission;
import app.model.Sorcerer;
import app.model.Technique;

public class DetailedReportGenerator implements MissionReportGenerator {
    @Override
    public String getReportName() {
        return "Детализированный отчет";
    }

    @Override
    public String generate(Mission mission) {
        StringBuilder sb = new StringBuilder();

        sb.append("ДЕТАЛИЗИРОВАННЫЙ ОТЧЕТ ПО МИССИИ\n\n");
        sb.append("Идентификатор: ").append(safe(mission.getMissionId())).append("\n");
        sb.append("Дата проведения: ").append(safe(mission.getDate())).append("\n");
        sb.append("Локация: ").append(safe(mission.getLocation())).append("\n");
        sb.append("Итог миссии: ").append(safe(mission.getOutcome())).append("\n");
        sb.append("Материальный ущерб: ").append(mission.getDamageCost()).append("\n");

        if (mission.getCurse() != null) {
            sb.append("\n[ЦЕЛЕВОЕ ПРОКЛЯТИЕ]\n");
            sb.append("Название: ").append(safe(mission.getCurse().getName())).append("\n");
            sb.append("Уровень угрозы: ").append(safe(mission.getCurse().getThreatLevel())).append("\n");
        }

        sb.append("\n[УЧАСТНИКИ ОПЕРАЦИИ]\n");
        int index = 1;
        for (Sorcerer sorcerer : mission.getSorcerers()) {
            sb.append(index++)
                    .append(". ")
                    .append(safe(sorcerer.getName()))
                    .append(" — ранг: ")
                    .append(safe(sorcerer.getRank()))
                    .append("\n");
        }

        sb.append("\n[ПРИМЕНЕННЫЕ ТЕХНИКИ]\n");
        index = 1;
        for (Technique technique : mission.getTechniques()) {
            sb.append(index++)
                    .append(". ")
                    .append(safe(technique.getName()))
                    .append("\n   Тип: ")
                    .append(safe(technique.getType()))
                    .append("\n   Владелец: ")
                    .append(safe(technique.getOwner()))
                    .append("\n   Урон: ")
                    .append(technique.getDamage())
                    .append("\n");
        }

        if (!mission.getAdditionalDetails().isBlank()) {
            sb.append("\n[ПРИМЕЧАНИЕ]\n");
            sb.append(mission.getAdditionalDetails()).append("\n");
        }

        return sb.toString();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
