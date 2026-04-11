package app.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "mission")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MissionInput {
    @JsonProperty("missionId")
    @JacksonXmlProperty(localName = "missionId")
    private String missionId;

    @JsonProperty("date")
    @JacksonXmlProperty(localName = "date")
    private String date;

    @JsonProperty("location")
    @JacksonXmlProperty(localName = "location")
    private String location;

    @JsonProperty("outcome")
    @JacksonXmlProperty(localName = "outcome")
    private String outcome;

    @JsonProperty("damageCost")
    @JacksonXmlProperty(localName = "damageCost")
    private long damageCost;

    @JsonProperty("curse")
    @JacksonXmlProperty(localName = "curse")
    private CurseInput curse;

    @JsonProperty("sorcerers")
    @JacksonXmlElementWrapper(localName = "sorcerers")
    @JacksonXmlProperty(localName = "sorcerer")
    private List<SorcererInput> sorcerers = new ArrayList<>();

    @JsonProperty("techniques")
    @JacksonXmlElementWrapper(localName = "techniques")
    @JacksonXmlProperty(localName = "technique")
    private List<TechniqueInput> techniques = new ArrayList<>();

    @JsonProperty("note")
    @JacksonXmlProperty(localName = "note")
    private String note;

    @JsonProperty("comment")
    @JacksonXmlProperty(localName = "comment")
    private String comment;

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

    public long getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(long damageCost) {
        this.damageCost = damageCost;
    }

    public CurseInput getCurse() {
        return curse;
    }

    public void setCurse(CurseInput curse) {
        this.curse = curse;
    }

    public List<SorcererInput> getSorcerers() {
        return sorcerers;
    }

    public void setSorcerers(List<SorcererInput> sorcerers) {
        this.sorcerers = sorcerers;
    }

    public List<TechniqueInput> getTechniques() {
        return techniques;
    }

    public void setTechniques(List<TechniqueInput> techniques) {
        this.techniques = techniques;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CurseInput {
        @JsonProperty("name")
        @JacksonXmlProperty(localName = "name")
        private String name;

        @JsonProperty("threatLevel")
        @JacksonXmlProperty(localName = "threatLevel")
        private String threatLevel;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getThreatLevel() {
            return threatLevel;
        }

        public void setThreatLevel(String threatLevel) {
            this.threatLevel = threatLevel;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SorcererInput {
        @JsonProperty("name")
        @JacksonXmlProperty(localName = "name")
        private String name;

        @JsonProperty("rank")
        @JacksonXmlProperty(localName = "rank")
        private String rank;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TechniqueInput {
        @JsonProperty("name")
        @JacksonXmlProperty(localName = "name")
        private String name;

        @JsonProperty("type")
        @JacksonXmlProperty(localName = "type")
        private String type;

        @JsonProperty("owner")
        @JacksonXmlProperty(localName = "owner")
        private String owner;

        @JsonProperty("damage")
        @JacksonXmlProperty(localName = "damage")
        private long damage;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public long getDamage() {
            return damage;
        }

        public void setDamage(long damage) {
            this.damage = damage;
        }
    }
}
