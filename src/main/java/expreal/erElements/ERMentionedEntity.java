package expreal.erElements;

/**
 * Mentioned entities are used in generating referring expressions.
 * This class allows us to keep track of the entities and there distinguishing properties,
 *
 * @author rfdj
 */
public class ERMentionedEntity {

    private String id;
    private String name;
    private String gender;
    private String number;
    private int mentionDistance;

    public ERMentionedEntity(String id, int mentionDistance) {
        this.id = id;
        this.name = id.split("<", 2)[0].replace("%", "");
        this.mentionDistance = mentionDistance;
    }

    public ERMentionedEntity(String id, String name, int mentionDistance) {
        this.id = id;
        this.name = name;
        this.mentionDistance = mentionDistance;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getMentionDistance() {
        return mentionDistance;
    }

    public void setMentionDistance(int distance) {
        this.mentionDistance = distance;
    }

    public void incrementMentionDistance() {
        this.mentionDistance++;
    }

    public String toString() {
        return "ERMentionedEntity{ id: " + id + ", name: " + name + ", gender: " + gender
                + ", number: " + number + ", mentionDistance: " + mentionDistance + " }";
    }
}
