package app.report;

import app.model.Mission;

import java.util.List;
import java.util.Map;

public abstract class MissionReportDecorator implements MissionReport {
    protected final MissionReport component;

    protected MissionReportDecorator(MissionReport component) {
        this.component = component;
    }

    @Override
    public String build(Mission mission) {
        return component.build(mission);
    }

    protected String safe(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }

    @SuppressWarnings("unchecked")
    protected void appendValue(StringBuilder sb, String key, Object value, int indent) {
        String prefix = "  ".repeat(indent);
        if (value instanceof Map<?, ?> map) {
            sb.append(prefix).append(key).append(":\n");
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                appendValue(sb, String.valueOf(entry.getKey()), entry.getValue(), indent + 1);
            }
        } else if (value instanceof List<?> list) {
            sb.append(prefix).append(key).append(":\n");
            for (Object item : list) {
                if (item instanceof Map<?, ?> || item instanceof List<?>) {
                    appendValue(sb, "-", item, indent + 1);
                } else {
                    sb.append(prefix).append("  - ").append(item).append("\n");
                }
            }
        } else {
            sb.append(prefix).append(key).append(": ").append(value).append("\n");
        }
    }
}
