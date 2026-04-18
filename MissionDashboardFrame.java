package app.report;

import app.model.Mission;

public class RiskReportDecorator extends MissionReportDecorator {
    public RiskReportDecorator(MissionReport component) {
        super(component);
    }

    @Override
    public String build(Mission mission) {
        StringBuilder sb = new StringBuilder(super.build(mission));
        String threatLevel = mission.getCurse() == null ? null : mission.getCurse().getThreatLevel();
        String publicExposure = extractNestedValue(mission, "civilianImpact", "publicExposureRisk");
        String escalation = extractNestedValue(mission, "enemyActivity", "escalationRisk");

        sb.append("\nОценка рисков:\n");
        sb.append("- Уровень угрозы проклятия: ").append(safe(threatLevel)).append("\n");
        sb.append("- Риск раскрытия: ").append(safe(publicExposure)).append("\n");
        sb.append("- Риск эскалации: ").append(safe(escalation)).append("\n");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private String extractNestedValue(Mission mission, String blockName, String key) {
        Object block = mission.getExtraFields().get(blockName);
        if (block instanceof java.util.Map<?, ?> map) {
            Object value = map.get(key);
            return value == null ? null : String.valueOf(value);
        }
        return null;
    }
}
