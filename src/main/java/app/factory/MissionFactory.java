package app.factory;

import app.input.MissionInput;
import app.model.Mission;

public class MissionFactory {
    public Mission create(MissionInput input) {
        MissionBuilder builder = new MissionBuilder()
                .withMissionId(input.getMissionId())
                .withDate(input.getDate())
                .withLocation(input.getLocation())
                .withOutcome(input.getOutcome())
                .withDamageCost(input.getDamageCost())
                .withNote(input.getNote())
                .withComment(input.getComment());

        if (input.getCurse() != null) {
            builder.withCurse(input.getCurse().getName(), input.getCurse().getThreatLevel());
        }

        for (MissionInput.SorcererInput sorcerer : input.getSorcerers()) {
            builder.addSorcerer(sorcerer.getName(), sorcerer.getRank());
        }

        for (MissionInput.TechniqueInput technique : input.getTechniques()) {
            builder.addTechnique(
                    technique.getName(),
                    technique.getType(),
                    technique.getOwner(),
                    technique.getDamage()
            );
        }

        return builder.build();
    }
}
