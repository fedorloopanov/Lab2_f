package app.service;

import app.model.Mission;

public final class MissionStatistics {
    private MissionStatistics() {
    }

    public static int participantCount(Mission mission) {
        return mission.getSorcerers() == null ? 0 : mission.getSorcerers().size();
    }

    public static int techniqueCount(Mission mission) {
        return mission.getTechniques() == null ? 0 : mission.getTechniques().size();
    }
}
