package expreal.erElements;

/**
 * Phrase elements like noun phrases and verb phrases are ExpReal's own mapping of SimpleNLG-NL's phrase elements.
 */
public interface ERPhraseElement {

    boolean doCapitalise = false;

    void copyFeaturesFromString(String featureString);

}
