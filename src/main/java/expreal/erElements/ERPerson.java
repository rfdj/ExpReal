package expreal.erElements;

/**
 * This class is used to address a specific person in the ERContext.
 *
 * @author rfdj
 */
public class ERPerson extends ERObject {

    /**
     * The name or the person, as written in the texts.csv file.
     */
    String name;
    ERGender gender;

    public ERPerson(String name, ERGender gender) {
        this.name = name;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public ERPerson setName(String name) {
        this.name = name;
        return this;
    }

    public ERGender getGender() {
        return gender;
    }

    public ERPerson setGender(ERGender gender) {
        this.gender = gender;
        return this;
    }

    @Override
    public String toString() {
        return String.format("ERPerson{name: %s, gender: %s}", name, gender);
    }
}
