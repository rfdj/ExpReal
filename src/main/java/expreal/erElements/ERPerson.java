package expreal.erElements;

import java.util.Arrays;
import java.util.HashMap;

/**
 * This class is used to address a specific person in the ERContext.
 *
 * @author rfdj
 */
public class ERPerson extends ERObject {

    /**
     * The name or the person, as written in the texts.csv file.
     */
    String id;

    /**
     * A list of realised names, to extend/replace those written in the CSV file.
     * This can be used to dynamically add persons to the realizer. Refer to them in the template using their ID and
     * they will be realised using this list.
     */
    String[] realisedNames;

    /**
     * The gender of the person, used for proper realisation.
     */
    ERGender gender;

    /**
     * Properties of the person. Can have any key and a Float as value.
     * Used in conditions in the CSV file.
     * Example usage: personId.aggression > 0.3
     */
    HashMap<String, Float> properties;

    /**
     * A simple person object only requires an ID and a gender.
     *
     * @param id the ID as used in the CSV file
     * @param gender the gender
     */
    public ERPerson(String id, ERGender gender) {
        this.id = id;
        this.gender = gender;
    }

    /**
     * A more complex person has realised names and properties, besides the regular id and gender.
     * The realised names are used instead of looking in the CSV for names. This is useful if the person is
     * added dynamically and only referred to in the CSV by their ID.
     *
     * @param id the ID as used in the CSV file
     * @param gender the gender
     * @param realisedNames a list of names, as they should be realised for each language
     * @param properties any properties to be used in conditions using [personID.propertyName operator value]
     */
    public ERPerson(String id, ERGender gender, String[] realisedNames, HashMap<String, Float> properties) {
        this.id = id;
        this.gender = gender;
        this.realisedNames = realisedNames;
        this.properties = properties;
    }

    public String getId() {
        return id;
    }

    public ERPerson setId(String id) {
        this.id = id;
        return this;
    }

    public ERGender getGender() {
        return gender;
    }

    public ERPerson setGender(ERGender gender) {
        this.gender = gender;
        return this;
    }

    public boolean hasRealisedNames() {
        return (realisedNames != null);
    }

    /**
     * Realised names are used as-is to replace the variable in the template.
     *
     * @param index the index, which should match the order of the languages in the CSV file (starting from 0)
     * @return the realised name
     */
    public String getRealisedName(int index) {
        return realisedNames[index];
    }

    public String[] getRealisedNames() {
        return realisedNames;
    }

    public void setProperties(HashMap<String, Float> properties) {
        this.properties = properties;
    }

    public boolean updateProperty(String key, Float value) {
        if (this.properties == null)
            return false;

        if (this.properties.containsKey(key))
            this.properties.put(key, value);

        return true;
    }

    @Override
    public String toString() {
        return String.format("ERPerson{id: %s, gender: %s, realisedNames: %s, properties: %s}",
                id, gender, Arrays.toString(realisedNames), properties);
    }
}
