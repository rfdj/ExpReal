package expreal.erRealizer;

import expreal.erElements.ERContext;
import expreal.erElements.ERMentionedEntity;
import expreal.erElements.ERPerson;
import simplenlg.features.Feature;
import simplenlg.features.Person;
import simplenlg.phrasespec.NPPhraseSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * This generator keeps track of mention distances and generates pronouns when applicable.
 *
 * @author rfdj
 */
class ReferringExpressionGenerator {

    /**
     * This is used for anaphoric expression generation.
     * Mention distance is the number of sentences between the current sentence and the previous mention of an entity.
     * It increases every sentence and is reset whenever an entity is mentioned.
     * <p>
     * This variable contains for every person/entity: a key (String) and its related count (Integer).
     */
    HashMap<String, ERMentionedEntity> mentionDistances = new HashMap<>();

    /**
     * Thread change resets the mention distance counts and will therefore force the use of a definite description.
     */
    private boolean doThreadChange = false;

    ReferringExpressionGenerator() {
    }

    /**
     * Initializes the mention distance of a mentioned entity with a value of 0.
     *
     * @param key the key of the mentioned entity
     */
    void initMentionDistance(String key, String name) {
        mentionDistances.put(key, new ERMentionedEntity(key, name, 0));
    }

    /**
     * Updates the mention distance of a mentioned entity.
     * If the key is not in mentionDistances yet, it will create it with a value of 1.
     *
     * @param key the key of the mentioned entity
     */
    void updateMentionDistance(String key) {
        ERMentionedEntity entity = mentionDistances.get(key);
        if (entity == null)
            entity = new ERMentionedEntity(key, 1);
        else
            entity.incrementMentionDistance();
        mentionDistances.put(key, entity);
    }

    /**
     * Resets the mention distance of a mentioned entity to 1.
     *
     * @param key the key of the mentioned entity
     */
    private void resetMentionDistance(String key) {
        ERMentionedEntity entity = mentionDistances.get(key);
        if (entity != null) {
            entity.setMentionDistance(1);
            mentionDistances.put(key, entity);
        }
    }

    /**
     * Deletes all mention distances.
     */
    private void clearMentionDistances() {
        mentionDistances.clear();
    }

    /**
     * Decides when to use a pronoun and when to a definitive description.
     * Based on the algorithm from:
     * McCoy & Strube (1999) "Generating Anaphoric Expressions: Pronoun or Definite Description?"
     *
     * @param key        the key of the entity, used in mentionDistances
     * @param nounPhrase the nounPhrase
     * @param context    the context
     */
    void generateReferringExpression(String key, NPPhraseSpec nounPhrase, ERContext context) {
        // Pseudo code:
        /*
         * If previous reference was >2 sentences prior:
         *   use definite description
         * else if ref is unambiguous and not its first mention in this sentence:
         *   use pronoun
         * else if thread change:
         *   use definite article
         * else if there is a competing antecedent in current or previous sentence:
         *   realiseCompetingAntecedent()
         * else
         *   use pronoun
         */
        ERMentionedEntity entity = mentionDistances.get(key);
        boolean isAmbiguous = isAmbiguousRefExp(key, context);

        if (entity.getMentionDistance() > ERconstants.refExpLongDistance) {
            // Don't change nounPhrase

        } else if (!isAmbiguous
                && !entity.getId().equals("")
                && entity.getMentionDistance() == 1) {
            nounPhrase.setFeature(Feature.PRONOMINAL, true);

        } else if (doThreadChange) {
            // Don't change nounPhrase
            // Reset mention distance
            clearMentionDistances();
            updateMentionDistance(key);
            doThreadChange = false;

        } else if (isAmbiguous) {
            realiseCompetingAntecedent(key);
        } else {
            nounPhrase.setFeature(Feature.PRONOMINAL, true);
            nounPhrase.setFeature(Feature.PERSON, Person.THIRD);
        }

        resetMentionDistance(key);
    }

    private void realiseCompetingAntecedent(String key) {
        //TODO
    }

    /**
     * Checks whether the referring expression is ambiguous.
     *
     * @param key     the key of the entity to compare with previously mentioned entities
     * @param context the context
     * @return true if there is a competing antecedent
     */
    private boolean isAmbiguousRefExp(String key, ERContext context) {
        // Skip if not an intersentential anaphora
        ERMentionedEntity entity = mentionDistances.get(key);
        if (entity.getMentionDistance() >= 2) return false;

        // Get gender
        String gender = entity.getGender();
        if (gender == null) gender = "";

        if (gender.equals("")) {
            String contextId = entity.getName();

            Object contextEntry = context.getPerson(contextId);
            if (contextEntry != null)
                gender = contextEntry.toString();
        }

        if (gender.equals("")) gender = "MASCULINE";

        // Get number
        String number = entity.getNumber();
        if (number == null) number = "";

        if (number.equals(""))
            number = "SINGULAR";

        // Compare to entities already mentioned in current or last sentence
        for (Map.Entry mention : mentionDistances.entrySet()) {
            ERMentionedEntity currentEntity = (ERMentionedEntity) mention.getValue();

            if (currentEntity == entity // Don't compare to itself
                    || currentEntity.getMentionDistance() >= 2) // Also don't compare it if not in current or last sentence.
                continue;

            // If mentioned entity has ambiguous gender and number
            if (gender.equals(currentEntity.getGender())
                    && number.equals(currentEntity.getNumber()))
                return true;
        }

        return false;
    }

    /**
     * Sets noun phrase to be pronominal if the person is speaking or listening.
     *
     * @param context    the context with the $speaker or $listener
     * @param nounPhrase the noun phrase to update
     * @param nounString the person being checked for their role
     */
    void setPronominalFeatures(ERContext context, NPPhraseSpec nounPhrase, String nounString) {
        if (context != null) {

            if (isEqualToPerson(nounString, context.getSpeaker())) {
                nounPhrase.setFeature(Feature.PERSON, Person.FIRST); //THIRD by default
                nounPhrase.setFeature(Feature.PRONOMINAL, true); //false by default
            } else if (isEqualToPerson(nounString, context.getListener())) {
                nounPhrase.setFeature(Feature.PERSON, Person.SECOND);
                nounPhrase.setFeature(Feature.PRONOMINAL, true); //false by default
            }
        }
    }

    /**
     * Checks if the nounString matches either the id or a realised name of the ERPerson.
     *
     * @param nounString the nounString to check
     * @param person     the person to match it to
     * @return true if the nounString is equal to the person
     */
    private boolean isEqualToPerson(String nounString, ERPerson person) {
        nounString = nounString.toLowerCase();
        String id = person.getId().toLowerCase();

        if (nounString.equals(id))
            return true;

        if (person.hasRealisedNames()) {
            String[] names = person.getRealisedNames();
            for (String name : names)
                if (nounString.equalsIgnoreCase(name))
                    return true;
        }
        return false;
    }

    /**
     * Checks if the key already has a mentionDistance that is not null nor 0.
     *
     * @param mentionDistanceKey the key to search for
     * @return true if the key is found to be not null nor 0
     */
    boolean hasMentionDistance(String mentionDistanceKey) {
        return mentionDistances.get(mentionDistanceKey) != null
                && mentionDistances.get(mentionDistanceKey).getMentionDistance() != 0;
    }

    /**
     * Mark the next generation cycle as having a thread change.
     * The mention distances will be reset and therefore a definite description will be used for the next referring expression.
     */
    void doThreadChange() {
        this.doThreadChange = true;
    }
}