package app.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Mission {
    private String missionId;
    private String date;
    private String location;
    private String outcome;
    private Long damageCost;
    private Curse curse;
    private final List<Sorcerer> sorcerers = new ArrayList<>();
    private final List<Technique> techniques = new ArrayList<>();
    private final Map<String, Object> extraFields = new LinkedHashMap<>();

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Long getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(Long damageCost) {
        this.damageCost = damageCost;
    }

    public Curse getCurse() {
        return curse;
    }

    public void setCurse(Curse curse) {
        this.curse = curse;
    }

    public List<Sorcerer> getSorcerers() {
        return sorcerers;
    }

    public List<Technique> getTechniques() {
        return techniques;
    }

    public Map<String, Object> getExtraFields() {
        return extraFields;
    }
}
