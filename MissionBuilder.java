package app.report;

import app.model.Mission;
import app.model.Sorcerer;
import app.model.Technique;

import java.util.Map;

public class DetailedReportDecorator extends MissionReportDecorator {
    public DetailedReportDecorator(MissionReport component) {
        super(component);
    }

    @Override
    public String build(Mission mission) {
        StringBuilder sb = new StringBuilder(super.build(mission));

        sb.append("\nУчастники:\n");
        if (mission.getSorcerers().isEmpty()) {
            sb.append("- Нет данных\n");
        } else {
            for (Sorcerer sorcerer : mission.getSorcerers()) {
                sb.append("- ").append(safe(sorcerer.getName()))
                        .append(" (").append(safe(sorcerer.getRank())).append(")\n");
            }
        }

        sb.append("\nТехники:\n");
        if (mission.getTechniques().isEmpty()) {
            sb.append("- Нет данных\n");
        } else {
            for (Technique technique : mission.getTechniques()) {
                sb.append("- ").append(safe(technique.getName()))
                        .append(", тип: ").append(safe(technique.getType()))
                        .append(", владелец: ").append(safe(technique.getOwner()))
                        .append(", урон: ").append(technique.getDamage() == null ? "—" : technique.getDamage())
                        .append("\n");
            }
        }

        if (!mission.getExtraFields().isEmpty()) {
            sb.append("\nДополнительные блоки:\n");
            for (Map.Entry<String, Object> entry : mission.getExtraFields().entrySet()) {
                appendValue(sb, entry.getKey(), entry.getValue(), 0);
            }
        }
        return sb.toString();
    }
}
