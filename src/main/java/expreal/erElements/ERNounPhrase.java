package expreal.erElements;

import simplenlg.features.*;
import simplenlg.framework.*;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends SimpleNLG-NL's NPPhraseSpec to help with building and populating a NPPhraseSpec as required by ExpReal.
 *
 * @author rfdj
 */
public class ERNounPhrase extends NPPhraseSpec implements ERPhraseElement {
    public boolean doCapitalise = false;
    private NLGFactory factory;
    private Lexicon lexicon;

    public ERNounPhrase(NLGFactory factory, Lexicon lexicon) {
        super(factory);
        this.factory = factory;
        this.lexicon = lexicon;
    }

    /**
     * Create a SimpleNLG-NL nounPhraseSpec noun phrase from a personName and a gender.
     *
     * @param personName   the head noun
     * @param personGender the gender of the new noun phrase
     */
    public void populateWith(String personName, Gender personGender) {
        WordElement head = lexicon.getWord(personName, LexicalCategory.NOUN);
        if (personGender != null)
            head.setFeature(LexicalFeature.GENDER, personGender);
        else
            head.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
        this.setHead(head);
    }

    /**
     * Copies the features given in the input (e.g. poss.spec) and adds the appropriate feature to the NLGElement.
     *
     * @param featureString the source String
     */
    public void copyFeaturesFromString(String featureString) {
        // Get required features from input string
        String[] features = featureString.split("\\.");

        for (String feature : features) {
            switch (feature) {
                case "poss":
                    this.setFeature(Feature.POSSESSIVE, true);
                    break;
                case "spec":
                    this.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.SPECIFIER);
                    break;
                case "singular":
                    this.setFeature(Feature.NUMBER, NumberAgreement.SINGULAR);
                    break;
                case "plural":
                    this.setFeature(Feature.NUMBER, NumberAgreement.PLURAL);
                    break;
                case "masculine":
                case "m":
                    this.setFeature(LexicalFeature.GENDER, Gender.MASCULINE);
                    break;
                case "feminine":
                case "f":
                    this.setFeature(LexicalFeature.GENDER, Gender.FEMININE);
                    break;
                case "neuter":
                case "n":
                    this.setFeature(LexicalFeature.GENDER, Gender.NEUTER);
                    break;
                case "common":
                    this.setFeature(LexicalFeature.GENDER, Gender.COMMON);
                    break;
                case "capitalise":
                case "capitalize":
                case "c":
                    this.doCapitalise = true;
                    break;
            }
        }
    }

    /**
     * Adds a noun phrase to a SPhrase according to its type (grammatical role).
     *
     * @param phrase      the phrase to add the noun phrase to
     * @param elementType the type of constituent
     */
    public void addToPhrase(SPhraseSpec phrase, String elementType) {
        if (this.getNoun() != null) {
            switch (elementType) {
                case "subject":
                    phrase.setSubject(this);
                    break;
                case "object":
                    phrase.setObject(this);
                    break;
                case "indirectobject":
                case "indirectObject":
                    phrase.setIndirectObject(this);
                    break;
                case "complement":
                    phrase.addComplement(this);
                    break;
            }
        }
    }

    /**
     * Adds the ownerNoun to the noun phrase using proper lexical and syntactic realisation.
     *
     * @param ownerNounPhrase the owner noun phrase to update
     */
    public void addOwnerNoun(NPPhraseSpec ownerNounPhrase) {
        Language currentLanguage = lexicon.getLanguage();

        // In French, use a complement if there is no pronominalisation. Otherwise, use a specifier.
        if (currentLanguage == Language.FRENCH) {
            if (ownerNounPhrase.getFeatureAsBoolean(Feature.PRONOMINAL)) {
                // This replaces any determiner already present.
                // Result: e.g. "mon meilleur ami"

                Map<String, Object> possPronFeatures = new HashMap<>();
                possPronFeatures.put(Feature.POSSESSIVE, true);
                possPronFeatures.put(Feature.PERSON, ownerNounPhrase.getFeature(Feature.PERSON));

                // Get determiner based on features and add more features
                WordElement determiner = lexicon.getWord(LexicalCategory.DETERMINER, possPronFeatures);
                determiner.setFeature(Feature.NUMBER, ownerNounPhrase.getFeature(Feature.NUMBER));
                determiner.setFeature(Feature.PERSON, ownerNounPhrase.getFeature(Feature.PERSON));

                // Use the gender of the main noun.
                // This gender can be set in the input using a feature (e.g. ami.feminine or short: ami.f).
                determiner.setFeature(LexicalFeature.GENDER, this.getFeature(LexicalFeature.GENDER));

                NPPhraseSpec poss = factory.createNounPhrase(determiner);
                poss.setFeature(Feature.POSSESSIVE, true);
                this.setSpecifier(poss);
            } else {
                // Result: e.g. "meilleur ami de Julia"
                NLGElement ofPreposition = lexicon.getWordByID("E0043621");
                PPPhraseSpec pp = factory.createPrepositionPhrase(ofPreposition);
                pp.setObject(ownerNounPhrase);
                ownerNounPhrase.setFeature(Feature.POSSESSIVE, false);
                this.addComplement(pp);
            }
        } else { //If not French
            if (this.getSpecifier() != null
                    && this.getSpecifier().isA(LexicalCategory.DETERMINER)) {
                // Result: e.g. "the friend of Frank"
                NLGElement ofPreposition = lexicon.getWordByID("E0043621");
                PPPhraseSpec pp = factory.createPrepositionPhrase(ofPreposition);
                pp.setObject(ownerNounPhrase);
                ownerNounPhrase.setFeature(Feature.POSSESSIVE, false);
                this.addComplement(pp);
            } else {
                this.setSpecifier(ownerNounPhrase);
            }
        }
    }

    /**
     * Add the premodifiers to the nounphrase.
     *
     * @param premodifierString the string of modifiers, to be split on whitespace
     */
    public void addPremodifiers(String premodifierString) {
        for (String preMod : premodifierString.split(" ")) {
            if (!preMod.equals("")) {
                if (lexicon.getWord(preMod).isA(LexicalCategory.DETERMINER))
                    this.setSpecifier(preMod);
                else
                    this.addPreModifier(preMod);
            }
        }
    }

    /**
     * Add the postmodifiers to the nounphrase.
     *
     * @param postmodifierString the string of modifiers, to be split on whitespace
     */
    public void addPostmodifiers(String postmodifierString) {
        for (String postMod : postmodifierString.split(" ")) {
            if (!postMod.equals("")) {
                this.addPostModifier(postMod);
            }
        }
    }
}
