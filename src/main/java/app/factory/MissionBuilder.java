package app.factory;

import app.model.Curse;
import app.model.Mission;
import app.model.Sorcerer;
import app.model.Technique;

import java.util.ArrayList;
import java.util.List;

public class MissionBuilder {
    private String missionId;
    private String date;
    private String location;
    private String outcome;
    private long damageCost;
    private Curse curse;
    private final List<Sorcerer> sorcerers = new ArrayList<>();
    private final List<Technique> techniques = new ArrayList<>();
    private String note;
    private String comment;

    public MissionBuilder withMissionId(String missionId) {
        this.missionId = missionId;
        return this;
    }

    public MissionBuilder withDate(String date) {
        this.date = date;
        return this;
    }

    public MissionBuilder withLocation(String location) {
        this.location = location;
        return this;
    }

    public MissionBuilder withOutcome(String outcome) {
        this.outcome = outcome;
        return this;
    }

    public MissionBuilder withDamageCost(long damageCost) {
        this.damageCost = damageCost;
        return this;
    }

    public MissionBuilder withCurse(String name, String threatLevel) {
        Curse curse = new Curse();
        curse.setName(name);
        curse.setThreatLevel(threatLevel);
        this.curse = curse;
        return this;
    }

    public MissionBuilder addSorcerer(String name, String rank) {
        Sorcerer sorcerer = new Sorcerer();
        sorcerer.setName(name);
        sorcerer.setRank(rank);
        sorcerers.add(sorcerer);
        return this;
    }

    public MissionBuilder addTechnique(String name, String type, String owner, long damage) {
        Technique technique = new Technique();
        technique.setName(name);
        technique.setType(type);
        technique.setOwner(owner);
        technique.setDamage(damage);
        techniques.add(technique);
        return this;
    }

    public MissionBuilder withNote(String note) {
        this.note = note;
        return this;
    }

    public MissionBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Mission build() {
        Mission mission = new Mission();
        mission.setMissionId(missionId);
        mission.setDate(date);
        mission.setLocation(location);
        mission.setOutcome(outcome);
        mission.setDamageCost(damageCost);
        mission.setCurse(curse);
        mission.setSorcerers(new ArrayList<>(sorcerers));
        mission.setTechniques(new ArrayList<>(techniques));
        mission.setNote(note);
        mission.setComment(comment);
        return mission;
    }
}
