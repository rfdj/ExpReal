package expreal.erRealizer;


import org.tinylog.Logger;

import java.util.Hashtable;

/**
 * Parse an input block {} and make its elements available as properties.
 *
 * @author rfdj
 */
class InputBlock {
    private String rawString;
    private String typeString;
    private String valueString;
    private String mainNounString;
    private String mainNounFeatureString;
    private String ownerNounString;
    private String ownerNounFeatureString;
    private String premodifierString;
    private String postmodifierString;

    /**
     * Ghost blocks are only used to complete the grammatical tree, but are not realised in the output.
     * They serve merely to make sure agreement and similar tasks are performed correctly.
     * Delineated by {# type: value}.
     */
    private boolean isGhostBlock = false;

    InputBlock(String rawString) {
        this.rawString = rawString;
        this.splitTypeAndValue();
    }

    /**
     * Split the type (key) from the value and store them in their appropriate variables.
     */
    private void splitTypeAndValue() {
        if (rawString.startsWith("{") && rawString.endsWith("}")) {
            int typeStartIndex = 1;
            if (rawString.startsWith("{#")) {
                isGhostBlock = true;
                typeStartIndex = 2;
            }
            String[] parts = rawString.split(":", 2);
            if (parts.length > 1) {
                typeString = parts[0]
                        .substring(typeStartIndex)
                        .trim();
                valueString = parts[1]
                        .substring(0, parts[1].length() - 1)
                        .trim();
            }
        } else {
            valueString = rawString;
        }
    }

    String getRawString() {
        return rawString;
    }

    String getTypeString() {
        return typeString;
    }

    String getValueString() {
        return valueString;
    }

    String getMainNounString() {
        return mainNounString;
    }

    String getMainNounFeatureString() {
        return mainNounFeatureString;
    }

    String getOwnerNounString() {
        return ownerNounString;
    }

    String getOwnerNounFeatureString() {
        return ownerNounFeatureString;
    }

    String getPremodifierString() {
        return premodifierString;
    }

    String getPostmodifierString() {
        return postmodifierString;
    }

    boolean isGhostBlock() {
        return isGhostBlock;
    }

    /**
     * Parse the input block and separate its string into the various properties.
     *
     * @return a parsed input block
     */
    InputBlock parse() {
        // Get the individual parts from the valueString
        mainNounFeatureString = "";
        ownerNounString = "";
        ownerNounFeatureString = "";
        premodifierString = "";
        postmodifierString = "";

        // Get ownernoun
        String[] ownerNounSplit = valueString.split("<", 2);
        if (ownerNounSplit.length > 1) { // There is an owner
            valueString = ownerNounSplit[0];            // e.g. modifier |mainnoun| modifier
            ownerNounString = ownerNounSplit[1].trim(); // e.g. %julia
        }

        // Get the required features
        String[] mainNounFeatureStringSplit = valueString.split("\\.", 2);
        if (mainNounFeatureStringSplit.length > 1) { // There are features
            valueString = mainNounFeatureStringSplit[0];                    // e.g. dog
            mainNounFeatureString = mainNounFeatureStringSplit[1].replace("|", "").trim(); // e.g. plural
        }

        String[] ownerNounFeatureStringSplit = ownerNounString.split("\\.", 2);
        if (ownerNounFeatureStringSplit.length > 1) { // There are features
            ownerNounString = ownerNounFeatureStringSplit[0];                   // e.g. julia
            ownerNounFeatureString = ownerNounFeatureStringSplit[1].trim();     // e.g. poss.spec
        }

        // Get the main noun and the modifiers
        String[] mainNounSplit = valueString.split("\\|", 3);
        if (mainNounSplit.length > 1) { // There are pipes
            premodifierString = mainNounSplit[0].trim();    // e.g. grande
            mainNounString = mainNounSplit[1].trim();       // e.g. dog

            if (mainNounSplit.length > 2) {
                postmodifierString = mainNounSplit[2].trim();   // e.g. rouge
                if (postmodifierString.contains("|"))
                    Logger.tag("IB").error("Too many pipes (|) in '{}' in input block: {}", valueString, rawString);
            }
        } else if (valueString.trim().contains(" ")) {
            // If no noun is marked with pipes, assumes the last word before the '<' is the noun.
            premodifierString = valueString.substring(0, valueString.trim().lastIndexOf(" "));  // e.g. grande
            mainNounString = valueString.substring(valueString.trim().lastIndexOf(" ")).trim(); // e.g. dog
        } else {
            mainNounString = valueString.replace("\\|", "").trim();
        }
        return this;
    }

    @Override
    public String toString() {
        return "InputBlock('" + valueString + "', raw:'" + rawString + "')";
    }
}