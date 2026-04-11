package app.report;

import app.model.Mission;

public class RiskReportGenerator implements MissionReportGenerator {
    @Override
    public String getReportName() {
        return "Отчет по рискам";
    }

    @Override
    public String generate(Mission mission) {
        StringBuilder sb = new StringBuilder();

        sb.append("ОТЧЕТ ПО РИСКАМ\n\n");
        sb.append("ID миссии: ").append(safe(mission.getMissionId())).append("\n");
        sb.append("Локация: ").append(safe(mission.getLocation())).append("\n");
        sb.append("Уровень угрозы проклятия: ").append(
                mission.getCurse() == null ? "—" : safe(mission.getCurse().getThreatLevel())
        ).append("\n");
        sb.append("Количество участников: ").append(mission.getSorcerers().size()).append("\n");
        sb.append("Количество техник: ").append(mission.getTechniques().size()).append("\n");
        sb.append("Материальный ущерб: ").append(mission.getDamageCost()).append("\n\n");

        sb.append("Оценка риска: ").append(defineRiskLevel(mission)).append("\n");
        sb.append("Комментарий: ").append(buildComment(mission)).append("\n");

        return sb.toString();
    }

    private String defineRiskLevel(Mission mission) {
        String threat = mission.getCurse() == null ? "" : safe(mission.getCurse().getThreatLevel()).toLowerCase();

        if ("special".equals(threat) || "special-grade".equals(threat) || mission.getDamageCost() >= 1_000_000) {
            return "Высокий";
        }
        if ("high".equals(threat) || mission.getDamageCost() >= 100_000) {
            return "Средний";
        }
        return "Низкий";
    }

    private String buildComment(Mission mission) {
        String risk = defineRiskLevel(mission);
        return switch (risk) {
            case "Высокий" -> "Миссия требует повышенного контроля и детального анализа.";
            case "Средний" -> "Желательно провести дополнительную оценку последствий операции.";
            default -> "Существенных признаков повышенного риска не выявлено.";
        };
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
