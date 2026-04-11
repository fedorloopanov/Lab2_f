package app.report;

import app.model.Mission;
import app.model.Technique;
import app.service.MissionStatistics;

public class StatisticalReportGenerator implements MissionReportGenerator {
    @Override
    public String getReportName() {
        return "Статистический отчет";
    }

    @Override
    public String generate(Mission mission) {
        StringBuilder sb = new StringBuilder();

        long totalTechniqueDamage = 0;
        for (Technique technique : mission.getTechniques()) {
            totalTechniqueDamage += technique.getDamage();
        }

        double avgTechniqueDamage = mission.getTechniques().isEmpty()
                ? 0
                : (double) totalTechniqueDamage / mission.getTechniques().size();

        sb.append("СТАТИСТИЧЕСКИЙ ОТЧЕТ\n\n");
        sb.append("ID миссии: ").append(safe(mission.getMissionId())).append("\n");
        sb.append("Количество участников: ").append(MissionStatistics.participantCount(mission)).append("\n");
        sb.append("Количество техник: ").append(MissionStatistics.techniqueCount(mission)).append("\n");
        sb.append("Суммарный урон от техник: ").append(totalTechniqueDamage).append("\n");
        sb.append("Средний урон техники: ").append(String.format("%.2f", avgTechniqueDamage)).append("\n");
        sb.append("Материальный ущерб миссии: ").append(mission.getDamageCost()).append("\n");

        return sb.toString();
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}
