package app.report;

import app.model.Mission;

public interface MissionReport {
    String build(Mission mission);
}
