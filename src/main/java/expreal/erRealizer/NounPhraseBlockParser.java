package expreal.erRealizer;

import expreal.erElements.ERContext;
import expreal.erElements.ERMentionedEntity;
import expreal.erElements.ERNounPhrase;
import expreal.erElements.ERPerson;
import org.tinylog.Logger;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * This parses a noun phrase block, like {subject: ...} into a structured class.
 *
 * @author rfdj
 */
public class NounPhraseBlockParser {
    private InputBlock inputBlock;
    private ERContext context;
    private SPhraseSpec phrase;
    private String elementType;
    private ERNounPhrase nounPhrase;
    private ERNounPhrase ownerNounPhrase;
    private ReferringExpressionGenerator refExpGen;
    private AnnotatedText annotatedText;
    private String ownerNounString;
    private String premodifierString;
    private String postmodifierString;
    private ERMentionedEntity entity;

    /**
     * Construct the parser.
     *
     * @param inputBlock the input block object
     * @param context the context object
     * @param phrase the sentence object to which this block should be added
     * @param elementType the type of element, e.g. 'subject' or 'object'
     * @param nounPhrase an empty noun phrase to build up
     * @param ownerNounPhrase an empty noun phrase to build up for the owner noun
     * @param refExpGen the referring expression generator with the mention history
     * @param annotatedText the original annotated text
     */
    public NounPhraseBlockParser(InputBlock inputBlock, ERContext context, SPhraseSpec phrase, String elementType, ERNounPhrase nounPhrase, ERNounPhrase ownerNounPhrase, ReferringExpressionGenerator refExpGen, AnnotatedText annotatedText) {
        this.inputBlock = inputBlock;
        this.context = context;
        this.phrase = phrase;
        this.elementType = elementType;
        this.nounPhrase = nounPhrase;
        this.ownerNounPhrase = ownerNounPhrase;
        this.refExpGen = refExpGen;
        this.annotatedText = annotatedText;
    }

    public String getOwnerNounString() {
        return ownerNounString;
    }

    public String getPremodifierString() {
        return premodifierString;
    }

    public String getPostmodifierString() {
        return postmodifierString;
    }

    public ERMentionedEntity getEntity() {
        return entity;
    }

    /**
     * Invoke the parser.
     * Parses the input block and builds the noun phrase(s) accordingly.
     *
     * @return a parsed noun phrase block
     */
    public NounPhraseBlockParser invoke() {
        String mainNounString = inputBlock.getMainNounString();
        String mainNounFeatureString = inputBlock.getMainNounFeatureString();
        ownerNounString = inputBlock.getOwnerNounString();
        String ownerNounFeatureString = inputBlock.getOwnerNounFeatureString();
        premodifierString = inputBlock.getPremodifierString();
        postmodifierString = inputBlock.getPostmodifierString();

        // Initialize mention distance entry. The key is based on main noun and owner information.
        String mentionDistanceKey = mainNounString + "<" + ownerNounString;
        if (refExpGen.mentionDistances.get(mentionDistanceKey) == null)
            refExpGen.initMentionDistance(mentionDistanceKey, mainNounString);
        entity = refExpGen.mentionDistances.get(mentionDistanceKey);

        // Realise main noun
        if (!mainNounString.equals("")) {
            String personName;
            Gender personGender = null;

            if (mainNounString.startsWith(ERconstants.variablePrefix)) {
                mainNounString = mainNounString.substring(1);

                if (mainNounString.contains("*")) {
                    String attribute = context.getPerson(mainNounString).getName();
                    if (attribute == null) {
                        Logger.tag("NPBP").error("Attribute '{}' not found in the context: {}", mainNounString, context);
                        return this;
                    }
                    mainNounString = attribute;
                }

                ERPerson personFromContext = context.getPerson(mainNounString);
                if (personFromContext == null) {
                    Logger.tag("NPBP").error("Variable '{}' not found in context: {}", mainNounString, context);
                    return this;
                }

                // Get the gender
                personGender = Gender.valueOf(personFromContext.getGender().toString());

                // Get the name corresponding to the %variable
                personName = annotatedText.selectAnnotatedText(mainNounString, context);

            } else {
                personName = mainNounString;
            }

            nounPhrase.populateWith(personName, personGender);
            nounPhrase.copyFeaturesFromString(mainNounFeatureString);
            nounPhrase.addToPhrase(phrase, elementType);

            // PRONOMINALISATION
            refExpGen.setPronominalFeatures(context, nounPhrase, mainNounString);

            // Generate anaphoric expression if necessary
            if (!nounPhrase.getFeatureAsBoolean(Feature.PRONOMINAL)
                    && refExpGen.hasMentionDistance(mentionDistanceKey)) {
                refExpGen.generateReferringExpression(mentionDistanceKey, nounPhrase, context);
            }
        }

        // Realise owner noun
        if (!ownerNounString.equals("")) {
            String personName;
            Gender personGender = null;

            if (ownerNounString.startsWith("%")) {
                ownerNounString = ownerNounString.substring(1);

                if (ownerNounString.contains("*")) {
                    String attribute = context.getPerson(ownerNounString).getName();
                    if (attribute == null) {
                        Logger.tag("NPBP").error("Attribute (owner noun) '{}' not found in context: {}", ownerNounString, context);
                        return this;
                    }
                    ownerNounString = attribute;
                }

                ERPerson personFromContext = context.getPerson(ownerNounString);
                if (personFromContext == null) {
                    Logger.tag("NPBP").error("Variable (owner noun) '{}' not found in context: {}", ownerNounString, context);
                    return this;
                }

                // Get the gender
                personGender = Gender.valueOf(personFromContext.getGender().toString());

                // Get the name corresponding to the %variable
                personName = annotatedText.selectAnnotatedText(ownerNounString, context);

            } else {
                personName = ownerNounString;
            }

            ownerNounPhrase.populateWith(personName, personGender);

            // Add the SimpleNLG features
            ownerNounPhrase.copyFeaturesFromString(ownerNounFeatureString);
            ownerNounPhrase.setFeature(Feature.POSSESSIVE, true);

            // PRONOMINALISATION
            refExpGen.setPronominalFeatures(context, ownerNounPhrase, ownerNounString);

            String ownerNounKey = "%" + ownerNounString + "<";

            // Generate anaphoric expression if necessary
            if (!ownerNounPhrase.getFeatureAsBoolean(Feature.PRONOMINAL)) {

                // If the entity has been mentioned before
                if (refExpGen.hasMentionDistance(mentionDistanceKey)) {
                    refExpGen.generateReferringExpression(mentionDistanceKey, ownerNounPhrase, context);

                    // Else if we can pronominalise the owner, do that. E.g. 'his house'
                } else if (refExpGen.hasMentionDistance(ownerNounKey)) {
                    refExpGen.generateReferringExpression(ownerNounKey, ownerNounPhrase, context);
                }
            }
        }
        return this;
    }
}

