package app.report;

import app.model.Mission;

public interface MissionReportGenerator {
    String getReportName();
    String generate(Mission mission);
}
