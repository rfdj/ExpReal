package expreal.erRealizer;

import expreal.erElements.*;
import org.tinylog.Logger;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.phrasespec.VPPhraseSpec;
import simplenlg.realiser.Realiser;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


/**
 * Main class of ExpReal
 *
 * @author szilas, rfdj
 */
public class ExpressiveActionRealizer {

    private final ReferringExpressionGenerator refExpGen = new ReferringExpressionGenerator();
    private AnnotatedText annotatedText;
    private AuthoredTemplatesCollection authoredTemplatesCollection;
    private Lexicon lexicon;
    private NLGFactory nlgFactory;
    private Realiser realiser = new Realiser();
    private ERLanguage currentLanguage;

    /**
     * Constructor.
     *
     * @param fileName full name of the .csv author file.
     *                 <p>
     *                 Reads the author file.
     */
    public ExpressiveActionRealizer(String fileName) {
        Logger.tag("EAR").debug(">\tInitializing EAR (default language)...");
        lexicon = Lexicon.getDefaultLexicon();
        nlgFactory = new NLGFactory(lexicon);
        annotatedText = new AnnotatedText(this);

        try {
            authoredTemplatesCollection = new AuthoredTemplatesCollection(fileName, ERLanguage.DEFAULT_LANGUAGE);
            annotatedText.setAuthoredTemplatesCollection(authoredTemplatesCollection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor.
     * Reads the author file.
     *
     * @param fileName full name of the .csv author file.
     * @param language language for this realiser
     */
    public ExpressiveActionRealizer(String fileName, ERLanguage language) {
        Logger.tag("EAR").debug(">\tInitializing EAR with language '{}'...", language);
        currentLanguage = language;

        loadLexicon(language);

        nlgFactory = new NLGFactory(lexicon);
        annotatedText = new AnnotatedText(this);

        try {
            authoredTemplatesCollection = new AuthoredTemplatesCollection(fileName, language);
            annotatedText.setAuthoredTemplatesCollection(authoredTemplatesCollection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the lexicon for the selected language. Defaults to English.
     *
     * @param language the language for which to load the lexicon
     */
    private void loadLexicon(ERLanguage language) {
        Logger.tag("EAR").debug("Loading lexicon for language '{}'...", language);
        switch (language) {
            case DUTCH:
                URI dutchResURI = getResourceUri("/expreal-simplenlg-dutch-lexicon-8k-default.xml");
                lexicon = new simplenlg.lexicon.dutch.XMLLexicon(dutchResURI);
                break;
            case FRENCH:
                URI frenchResURI = getResourceUri("/expreal-simplenlg-french-lexicon.xml");
                lexicon = new simplenlg.lexicon.french.XMLLexicon(frenchResURI);
                break;
            case ENGLISH:
            default:
                URI englishResURI = getResourceUri("/expreal-simplenlg-english-lexicon.xml");
                lexicon = new simplenlg.lexicon.english.XMLLexicon(englishResURI);
        }
    }

    /**
     * Gets a resource based on a file string.
     *
     * @param resourceFileLocation relative resource location
     * @return URI of resource
     */
    private URI getResourceUri(String resourceFileLocation) {
        URL resource = this.getClass().getResource(resourceFileLocation);
        URI resourceURI = null;

        try {
            resourceURI = resource.toURI();
        } catch (Exception e) {
            Logger.tag("EAR").error("Cannot get URI of resource: {}: {}", resourceFileLocation, e);
        }
        return resourceURI;
    }

    /**
     * Returns all the texts for a given predicate, that can then be assembled to create a dialog.
     *
     * @param pred    predicate to be expressed by text
     * @param context context around this predicate
     * @return a series of texts describing the dialog(s) to play in order to express this predicate.
     */
    public Vector<String> getTexts(ERPredicate pred, ERContext context) {
        Logger.tag("EAR").debug("Calling getText for predicate '{}' in context: {}", pred, context);
        Vector<String> result = new Vector<>();

        if (!context.isValid()) return result;

        // Adding local context
        ERContext localContext = new ERContext(context);

        // Variable extractions
        addArgumentsToLocalContext(pred, localContext);

        String text = annotatedText.selectAnnotatedText(pred.getType().getName(), localContext);
        Logger.tag("EAR").debug("Selected annotated text: {}", text);

        // No entry found
        if (text.equals("")) {
            result.add(pred.toString());
            return result;
        }

        processMultiLineDialogs(text, localContext, result);

        Logger.tag("EAR").debug("Final text vector: {}", result);
        return result;
    }

    /**
     * Get the referring expression generator associated with this realizer.
     *
     * @return refExpGen the generator
     */
    public ReferringExpressionGenerator getRefExpGen() {
        return refExpGen;
    }

    /**
     * Split dialogs with multiple lines (split by — or --) and save the interpreted text.
     *
     * @param multipleLinesString the text to split and process
     * @param result              the result in which to store
     * @param localContext        the local context used for interpreting
     */
    private void processMultiLineDialogs(String multipleLinesString, ERContext localContext, Vector<String> result) {
        //NB: split inserts an empty string when the sentence starts with the separator, and between two consecutive separators.
        String[] textParts = multipleLinesString.split("—|--");

        boolean processString = (textParts.length == 1);
        for (String part : textParts) {
            if (processString) {
                result.add(annotatedText.interpret(part, localContext));
                processString = false;
            } else
                processString = true;
        }
    }

    /**
     * Retrieve the arguments from the predicate and add them to the local context.
     *
     * @param pred         the predicate
     * @param localContext the local context to add the arguments to
     */
    private void addArgumentsToLocalContext(ERPredicate pred, ERContext localContext) {
        Vector<ERArgument> args = pred.getArguments();
        for (ERArgument arg : args) {
            localContext.addArgument(arg);
        }
    }

    /**
     * Interpret a text that constitutes a pseudo-clause, that contains grammatical annotations (marked with accolades {})
     *
     * @param clause  string to be analyzed
     * @param context contextual information.
     * @return a text without grammatical annotations (but with variables, prefixed with _)
     */
    String interpretGrammaticalClause(String clause, ERContext context) {
        Logger.tag("EAR").debug("Starting to interpret grammatical clause: {}", clause);
        SPhraseSpec phrase = nlgFactory.createClause();

        String result = clause;

        ArrayList<InputBlock> allInputBlocks = getAllInputBlocks(clause);
        Logger.tag("EAR").debug("Found input blocks: {}", allInputBlocks);

        if (allInputBlocks == null)
            return result;

        ArrayList<ParseInputBlockReturn> parsedInputBlocks = new ArrayList<>();
        ArrayList<String> detectedTypes = new ArrayList<>();

        for (InputBlock block : allInputBlocks) {
            ParseInputBlockReturn parsedBlock = parseInputBlock(block, context, phrase);
            if (detectedTypes.contains(block.getTypeString())
                    && !block.getTypeString().equals("complement")
                    && !block.getTypeString().equals("inf")) {
                Logger.tag("EAR").error("found duplicate '{}' in clause: '{}'", block.getTypeString(), clause);
                return result;
            }

            detectedTypes.add(block.getTypeString());
            Logger.tag("EAR").debug("Parsed input block: {}", parsedBlock);
            parsedInputBlocks.add(parsedBlock);
        }

        // Call realiser to realise the sentence as a whole, so we can get the specific parts later
        realiser.realise(phrase);

        for (ParseInputBlockReturn parsedBlock : parsedInputBlocks) {
            if (parsedBlock.replacementString.equals("") && parsedBlock.nlgElement != null)
                parsedBlock.replacementString = realiser.realise((NLGElement) parsedBlock.nlgElement).getRealisation();

            addDutchReflexivePronoun(parsedBlock);

            if (parsedBlock.isGhostBlock) {
                result = result.replace(parsedBlock.blockString, "");
            } else {
                if (parsedBlock.doCapitalise)
                    result = result.replace(parsedBlock.blockString, capitalise(parsedBlock.replacementString));
                else
                    result = result.replace(parsedBlock.blockString, parsedBlock.replacementString);
            }
        }
        Logger.tag("EAR").debug("Interpreted grammatical clause result: {}", result);

        return result;
    }

    /**
     * Prepend Dutch reflexive verb replacement strings with their pronouns manually.
     * Because of tree rearrangement inside SimpleNLG-NL, the realise() method only returns the verb, not the pronoun.
     *
     * @param parsedBlock the parsedBlock that is checked to be of type 'inf'
     */
    private void addDutchReflexivePronoun(ParseInputBlockReturn parsedBlock) {
        if (currentLanguage == ERLanguage.DUTCH
                && (parsedBlock.type.equals("inf")
                || parsedBlock.type.equals("infinitive"))
                && parsedBlock.hasReflexive) {
            ERVerbPhrase element = (ERVerbPhrase) parsedBlock.nlgElement;
            if (element != null) {
                Lexicon lexicon = nlgFactory.getLexicon();

                Map<String, Object> pronounFeatures = new HashMap<>();
                pronounFeatures.put(Feature.NUMBER, element.getFeature(Feature.NUMBER));
                pronounFeatures.put(Feature.PERSON, element.getFeature(Feature.PERSON));
                pronounFeatures.put(LexicalFeature.REFLEXIVE, true);

                WordElement prefixElement = lexicon.getWord(LexicalCategory.PRONOUN, pronounFeatures);

                if (prefixElement != null) {
                    String prefix = prefixElement.getBaseForm();
                    parsedBlock.replacementString = prefix + " " + parsedBlock.replacementString;
                }
            }
        }
    }

    /**
     * Parse a clause and retrieve all its input blocks ({subject: ...}, {verb: ...}, etc.).
     *
     * @param clause the full phrase string
     * @return an array of input blocks
     */
    private ArrayList<InputBlock> getAllInputBlocks(String clause) {
        ArrayList<InputBlock> result = new ArrayList<>();

        int openingBraceIndex = clause.indexOf("{");
        int braceNetCount = 0;

        while (openingBraceIndex > -1) {

            braceNetCount += 1;

            int closingBraceIndex = clause.indexOf("}", openingBraceIndex);
            if (closingBraceIndex > -1) {
                braceNetCount -= 1;

                String blockString = clause.substring(openingBraceIndex, closingBraceIndex + 1);
                InputBlock block = new InputBlock(blockString);
                block.parse();

                result.add(block);
            }

            openingBraceIndex = clause.indexOf("{", openingBraceIndex + 1);
        }

        if (braceNetCount != 0) {
            Logger.tag("EAR").error("Number of opening braces does not match number of closing braces in clause: {}", clause);
            return null;
        }

        return result;
    }

    /**
     * Parses the input blocks (e.g. {subject:}) and either populates the NLGElement with the word and its features or
     * returns a String that should be used instead of realising.
     *
     * @param inputBlock the input block
     * @param context    the current context
     * @return ParseInputBlockReturn with the block to be replaced and a replacement (NLGElement or String)
     */
    private ParseInputBlockReturn parseInputBlock(InputBlock inputBlock, ERContext context, SPhraseSpec phrase) {

        String elementType = inputBlock.getTypeString();

        //// PARSE VERB ////
        if (elementType.equals("verb")) {
            ERVerbPhrase verb = new ERVerbPhrase(phrase, nlgFactory, lexicon);
            boolean hasReflexive = false;

            // Detect and use reflexive pronouns
            String[] verbParts = inputBlock.getValueString().split(" ");
            for (String part : verbParts) {

                WordElement word = lexicon.getWord(part, LexicalCategory.PRONOUN);
                if (word != null && word.hasFeature(LexicalFeature.REFLEXIVE)) {
                    verb.setObject(word);
                    hasReflexive = true;
                } else {
                    verb.setHead(lexicon.getWord(part, LexicalCategory.VERB));
                }

                verb.copyFeaturesFromString(inputBlock.getMainNounFeatureString());

            }
            phrase.setVerb(verb);

            return new ParseInputBlockReturn(inputBlock, verb, "", hasReflexive);
        }

        //// PARSE INFINITIVE VERB ////
        if (elementType.equals("inf") || elementType.equals("infinitive")) {
            ERVerbPhrase infiniteVerb = new ERVerbPhrase(phrase, nlgFactory, lexicon);
            boolean hasReflexive = false;

            // Detect and use reflexive pronouns
            String[] verbParts = inputBlock.getValueString().split(" ");
            for (String part : verbParts) {

                WordElement word = lexicon.getWord(part, LexicalCategory.PRONOUN);
                if (word != null && word.hasFeature(LexicalFeature.REFLEXIVE)) {
                    infiniteVerb.setObject(word);
                    hasReflexive = true;
                } else {
                    infiniteVerb.setHead(lexicon.getWord(part, LexicalCategory.VERB));
                }
            }

            // Copy person, number and gender. The pronoun should agree with the subject, but may resort to the verb phrase itself.
            NLGElement subject = phrase.getSubject();
            if (subject != null) {
                infiniteVerb.setFeature(Feature.PERSON, phrase.getSubject().getFeature(Feature.PERSON));
                infiniteVerb.setFeature(Feature.NUMBER, phrase.getSubject().getFeature(Feature.NUMBER));
                infiniteVerb.setFeature(LexicalFeature.GENDER, phrase.getSubject().getFeature(LexicalFeature.GENDER));
            } else {
                infiniteVerb.setFeature(Feature.PERSON, phrase.getFeature(Feature.PERSON));
                infiniteVerb.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
                infiniteVerb.setFeature(LexicalFeature.GENDER, phrase.getFeature(LexicalFeature.GENDER));
            }

            infiniteVerb.setFeature(Feature.FORM, Form.INFINITIVE);

            VPPhraseSpec mainVerb = (VPPhraseSpec) phrase.getVerbPhrase();
            mainVerb.addComplement(infiniteVerb);

            return new ParseInputBlockReturn(inputBlock, infiniteVerb, "", hasReflexive);
        }

        //// PARSE NOUN PHRASE ////
        ERNounPhrase nounPhrase = new ERNounPhrase(nlgFactory, lexicon);
        ERNounPhrase ownerNounPhrase = new ERNounPhrase(nlgFactory, lexicon);

        // The key is one of the following: subject, object, indirectobject, compliment.
        // The value is in the form of: modifier |mainNoun| modifier < ownerNoun
        // where modifiers and the possessive noun (owner) are optional. When no modifiers are used, the pipes (||)
        // are unnecessary.
        // Example inputs:
        // {subject: %frank},
        // {object: quick |fox|},
        // {object: la grande |veste| rouge < %julia}
        // {indirectobject: lazy |dog.plural| < %frank}

        NounPhraseBlockParser parser = new NounPhraseBlockParser(inputBlock, context, phrase, elementType, nounPhrase,
                ownerNounPhrase, refExpGen, annotatedText).invoke();
        if (parser == null) return null;

        String ownerNounString = parser.getOwnerNounString();
        String premodifierString = parser.getPremodifierString();
        String postmodifierString = parser.getPostmodifierString();
        ERMentionedEntity entity = parser.getEntity();

        nounPhrase.addPremodifiers(premodifierString);
        nounPhrase.addPostmodifiers(postmodifierString);
        if (!ownerNounString.equals(""))
            nounPhrase.addOwnerNoun(ownerNounPhrase);

        // Add the gender and number to the mentioned entity
        entity.setGender(nounPhrase.getFeatureAsString(LexicalFeature.GENDER));
        entity.setNumber(nounPhrase.getFeatureAsString(Feature.NUMBER));

        return new ParseInputBlockReturn(inputBlock, nounPhrase, "", false);
    }

    /**
     * Mark the next round of generating referring expressions as the start of a new 'thread', as described by McCoy &amp; Strube.
     * This will reset the mention distance counts and therefore use a definite description.
     */
    public void markThreadChange() {
        refExpGen.doThreadChange();
    }

    /**
     * Capitalise the first character of a string.
     *
     * @param original the lowercase string
     * @return the capitalised string
     */
    private String capitalise(String original) {
        if (original == null || original.equals(""))
            return "";
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    /**
     * Return type that contains the string marking the entire input blockString that needs to be replaced and optionally
     * a string with which to replace the blockString.
     */
    private class ParseInputBlockReturn {
        ERPhraseElement nlgElement;
        String type, blockString, replacementString;
        boolean hasReflexive, isGhostBlock, doCapitalise;

        ParseInputBlockReturn(InputBlock inputBlock, ERPhraseElement _nlgElement,
                              String _replacementString, boolean _hasReflexive) {
            type = inputBlock.getTypeString();
            blockString = inputBlock.getRawString();
            nlgElement = _nlgElement;
            replacementString = _replacementString;
            hasReflexive = _hasReflexive;
            isGhostBlock = inputBlock.isGhostBlock();
            if (_nlgElement instanceof ERVerbPhrase)
                doCapitalise = ((ERVerbPhrase) _nlgElement).doCapitalise;
            else
                doCapitalise = ((ERNounPhrase) _nlgElement).doCapitalise;
        }

        @Override
        public String toString() {
            return "ParseInputBlockReturn{" +
                    ", type='" + type + '\'' +
                    ", blockString='" + blockString + '\'' +
                    ", replacementString='" + replacementString + '\'' +
                    '}';
        }
    }
}
