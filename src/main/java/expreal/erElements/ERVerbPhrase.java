package expreal.erElements;

import simplenlg.features.*;
import simplenlg.framework.Language;
import simplenlg.framework.NLGFactory;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;

/**
 * This verb phrase element is used to build SimpleNLG-NL's VPPhraseSpec.
 *
 * @author rfdj
 */
public class ERVerbPhrase extends VPPhraseSpec implements ERPhraseElement {
    public boolean doCapitalise = false;
    private SPhraseSpec parentPhrase;
    private NLGFactory factory;

    public ERVerbPhrase(SPhraseSpec parentPhrase, NLGFactory factory) {
        super(factory);
        this.parentPhrase = parentPhrase;
        this.factory = factory;
    }

    /**
     * Take the string appended to a word in the template and convert them into SimpleNLG-NL features.
     * Those features will determine the realisation of the verb phrase.
     *
     * @param featureString the string of features, concatenated by dots
     */
    @Override
    public void copyFeaturesFromString(String featureString) {
        // Get required features from input string
        String[] features = featureString.split("\\.");

        for (String feature : features) {
            switch (feature) {
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
                case "conditional":
                case "cond":
                    parentPhrase.setFeature(Feature.TENSE, Tense.CONDITIONAL);
                    break;
                case "past":
                    parentPhrase.setFeature(Feature.TENSE, Tense.PAST);

                    if (factory.getLanguage() == Language.FRENCH)
                        // Past + progressive makes imparfait
                        parentPhrase.setFeature(Feature.PROGRESSIVE, true);
                    break;
            }
        }
    }
}
