package expreal.erRealizer;

import expreal.erElements.*;
import org.tinylog.Logger;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

/**
 * The AnnotatedText object handles the templates as written by the authors.
 * It selects the most appropriate template and processes it.
 */
public class AnnotatedText {
    private static Random randomGenerator;
    private final ExpressiveActionRealizer expressiveActionRealizer;
    ReferringExpressionGenerator refExpGen;
    private AuthoredTemplatesCollection authoredTemplatesCollection;


    public AnnotatedText(ExpressiveActionRealizer expressiveActionRealizer) {
        this.expressiveActionRealizer = expressiveActionRealizer;
        this.refExpGen = expressiveActionRealizer.getRefExpGen();
        randomGenerator = new Random();
    }

    public void setAuthoredTemplatesCollection(AuthoredTemplatesCollection authoredTemplatesCollection) {
        this.authoredTemplatesCollection = authoredTemplatesCollection;
    }

    /**
     * Chooses between the different ways to express a given element.
     *
     * @param element      element to be expressed
     * @param localContext context built while generating a natural language sentence
     * @return an annotated text.
     */
    String selectAnnotatedText(String element, ERContext localContext) {

        // Retrieve realised name if applicable
        ERPerson person = localContext.getPerson(element);
        if (person != null && person.hasRealisedNames()) {
            return getRealisedNameForPerson(person);
        }

        /* retrieval of all texts corresponding to the element */
        if (authoredTemplatesCollection == null)
            return "";

        Vector<ConditionalAnnotatedText> condTexts = authoredTemplatesCollection.getConditionalAnnotatedTexts(element);
        if (condTexts == null) {
            Logger.tag("AT").error("No text found for element: '{}'", element);
            return "";
        }
        Logger.tag("AT").debug("All (unverified) conditional annotated texts: {}", condTexts);

        Vector<ConditionalAnnotatedText> filteredCondTexts = getVerifiedConditionalTexts(condTexts, localContext);
        Vector<ConditionalAnnotatedText> specificCondTexts = getMostSpecificTexts(filteredCondTexts);

        if (specificCondTexts.isEmpty()) {
            Logger.tag("AT").error("No text to select from."
                    + "\nConditional texts: {}"
                    + "\nFiltered conditional texts: {}"
                    + "\nElement from which cond. text is extracted: {}" + element, condTexts, filteredCondTexts, element);
            return "";
        }

        int index = randomGenerator.nextInt(specificCondTexts.size());
        ConditionalAnnotatedText selectedCAT = specificCondTexts.get(index);

        return selectedCAT.getAnnotatedText();
    }

    private String getRealisedNameForPerson(ERPerson person) {
        ERLanguage currentLanguage = expressiveActionRealizer.getCurrentLanguage();
        Logger.tag("AT").debug("Used realised name for element: {}", person);
        return person.getRealisedName(currentLanguage.ordinal());
    }


    /**
     * Filter conditional texts based on whether their conditions are met.
     *
     * @param localContext the local context
     * @param condTexts    the conditional texts
     * @return conditional texts of which the conditions are verified to be met
     */
    private Vector<ConditionalAnnotatedText> getVerifiedConditionalTexts(Vector<ConditionalAnnotatedText> condTexts, ERContext localContext) {
        Vector<ConditionalAnnotatedText> filteredCondTexts = new Vector<>();

        for (ConditionalAnnotatedText currentCat : condTexts) {
            if (currentCat.verifiedCondition(localContext))
                filteredCondTexts.add(currentCat);
        }

        Logger.tag("AT").debug("Verified conditional annotated texts: {}", filteredCondTexts);
        return filteredCondTexts;
    }

    /**
     * Filter out less specific text, to return the most specific one.
     *
     * @param filteredCondTexts the texts whose conditions are met
     * @return the most specific text, with the most (important) conditions met
     */
    private Vector<ConditionalAnnotatedText> getMostSpecificTexts(Vector<ConditionalAnnotatedText> filteredCondTexts) {
        int currentSpecificity;
        int maxSpecificity = 0;
        boolean hasUserDefinedCondition = false;
        Vector<ConditionalAnnotatedText> specificCondTexts = new Vector<>();

        for (ConditionalAnnotatedText currentCat : filteredCondTexts) {

            // User-defined @tags override everything else
            if (currentCat.isUserDefinedCondition()) {
                specificCondTexts.clear();
                specificCondTexts.add(currentCat);
                hasUserDefinedCondition = true;
            }

            // Otherwise, use specificity to determine which text to use
            if (!hasUserDefinedCondition) {
                currentSpecificity = currentCat.getSpecificity();
                if (currentSpecificity > maxSpecificity) {
                    specificCondTexts.clear();
                    specificCondTexts.add(currentCat);
                    maxSpecificity = currentSpecificity;
                } else if (currentSpecificity == maxSpecificity) {
                    specificCondTexts.add(currentCat);
                }
            }
        }

        Logger.tag("AT").debug("Most specific conditional annotated text: {}", specificCondTexts);
        return specificCondTexts;
    }


    /**
     * Transforms an annotated text, with only one utterance (text between —), into a text
     *
     * @param annotatedText the annotated text to transform
     * @param context       the context
     * @return a NLG text
     */
    public String interpret(String annotatedText, ERContext context) {

        String newString = annotatedText;

        /* Parsing of #switchDialog */
        if (newString.startsWith(ERconstants.switchDialog)) {
            newString = doSwitchDialog(context, newString);
        }

        /* Parsing of each new variable string starting with a $:
         *
         * parsing of predicate's arguments: $XXX -> %YYY
         * $XXX is a predicate''s argument. YYY is the instantiated value of the argument, with spaces replaced by underscores
         * (NB: In practice, we finally avoided the use of spaces in game's elements)
         */
        newString = expandDollarVariables(context, newString);

        /* Grammatical processing of {} blocks */
        String result = newString;

        if (result.contains("{")) {

            //First step: pseudo-clause isolation
            String[] sentences = splitSentences(newString);

            // Interpret all sentences
            for (int i = 0; i < sentences.length; i++) {
                sentences[i] = interpretSentence(context, sentences[i]);
            }

            result = String.join(" ", sentences);
        }

        /* Recursive replacement of variables: %XXX -> plain text */
        result = percentageVariablesToPlainText(context, result);

        return result;
    }

    /**
     * Realises the grammatical parts of a sentence. It can still include %variables.
     * The sentence can contain subclauses, which will be realised individually.
     *
     * @param context  the current context
     * @param sentence the sentence to realise
     * @return the realised sentence
     */
    private String interpretSentence(ERContext context, String sentence) {
        // Increment mention distance for each mentioned entity. This can be reset when new mentions are detected.
        for (Map.Entry<String, ERMentionedEntity> entry : refExpGen.mentionDistances.entrySet()) {
            refExpGen.updateMentionDistance(entry.getKey());
        }

        // Split sentences into subclauses if needed
        String[] subclauses = splitSubclauses(sentence);

        StringBuilder realisedSubclauses = new StringBuilder();
        for (String subclause : subclauses) {
            realisedSubclauses.append(expressiveActionRealizer.interpretGrammaticalClause(subclause, context));
        }
        return realisedSubclauses.toString();
    }

    /**
     * Swaps the speaker and the listener.
     *
     * @param context the current context
     * @param text    the current string in which to swap
     * @return text with $speaker and $listener swapped
     */
    private String doSwitchDialog(ERContext context, String text) {
        context.setSpeaker(context.getListener());
        context.setListener(context.getSpeaker());

        return text.substring(ERconstants.switchDialog.length());
    }

    /**
     * Splits a string into sentences.
     *
     * @param fullDialogString the dialog with possibly multiple sentences
     * @return one or more sentences
     */
    private String[] splitSentences(String fullDialogString) {
        String sentenceSplitRegExp = "(?<!\\w\\.\\w.)" // adapted from https://regex101.com/r/nG1gU7/27
                + "(?<![A-Z][a-z]\\.)"
                + "(?<=[.?])\\s";

        return fullDialogString.split(sentenceSplitRegExp);
    }

    /**
     * Splits a sentence into subclauses.
     *
     * @param sentence the sentence to split
     * @return the subclauses that form the sentence
     */
    private String[] splitSubclauses(String sentence) {
        String[] subclauses = sentence.split("(\\|)(?=[^}]*(\\{|$))"); // a | with a grammatical block to its right

        if (subclauses.length == 1)
            subclauses = sentence.split("(?=\\{#? ?subject ?:)");
        if (subclauses.length > 1)
            Logger.tag("AT").debug("Multiple subclauses detected: {}", subclauses.length);
        return subclauses;
    }

    /**
     * Parses and replaces dollar variables ($listener, $task, $goal, etc.) in a string with %variable.
     *
     * @param context   the context with which to validate the variable's existence
     * @param newString the string in which the variables will be replaced
     * @return the new string with $variablenames having been replaced by %variablenames
     */
    private String expandDollarVariables(ERContext context, String newString) {
        int currentIndex = 0;
        int currentDollarIndex;

        do {
            currentDollarIndex = newString.indexOf("$", currentIndex);

            if (currentDollarIndex != -1) {

                // Determine the length of the variable
                String textVariable = getTextVariable(newString, currentDollarIndex + 1);
                ERObject contextObject = getObjectFromContext(context, textVariable);

                if (contextObject == null) {
                    // Variable not found in context. Let's try it as a %variable.
                    newString = percentageVariablesToPlainText(context, newString.replace("$", "%"));
                    currentIndex = currentDollarIndex + textVariable.length();
                } else {
                    // Build the replacement string based on the context variable,
                    // replace whitespace with underscores,
                    // replace $ with % for further processing.

                    String replacementString = getReplacementStringFromObject(contextObject);

                    if (textVariable.equals("speaker") || textVariable.equals("listener")) {
                        // $speaker and $listener are replaced by their % variables, e.g. %julia,
                        // which will be realised later.
                        newString = newString.replace("$" + textVariable, "%" + replacementString);
                    } else {
                        refExpGen.updateMentionDistance(replacementString);

                        String at = this.selectAnnotatedText(replacementString, context);

                        replacementString = expandAnnotatedText(context, replacementString, at);

                        String targetString;
                        if (newString.substring(0, currentDollarIndex).endsWith(" de ")
                                && expressiveActionRealizer.startsWithVowelOrIsolatedY(replacementString)) {
                            targetString = " de $" + textVariable;
                            replacementString = " d'" + replacementString;
                        } else if (newString.substring(0, currentDollarIndex).endsWith(" à ")) {
                            if (replacementString.startsWith("le ")) {
                                targetString = " à $" + textVariable;
                                replacementString = " au " + replacementString.substring(3);
                            } else if (replacementString.startsWith("les ")) {
                                targetString = " à $" + textVariable;
                                replacementString = " aux " + replacementString.substring(4);
                            } else {
                                targetString = "$" + textVariable;
                            }
                        } else {
                            targetString = "$" + textVariable;
                        }
                        newString = newString.replace(targetString, replacementString);
                    }
                    currentIndex = currentDollarIndex + replacementString.length();

                }
            }
        } while (currentDollarIndex != -1);

        return newString;
    }

    private String expandAnnotatedText(ERContext context, String replacementString, String at) {
        String result = replacementString;
        if (!at.equals("")) {
            if (at.contains("$")) {
                if (at.contains("$argument")
                        || at.contains("$speaker")
                        || at.contains("$listener")) {
                    at = expandDollarVariables(context, at);
                } else {
                    at = percentageVariablesToPlainText(context, at.replace("$", "%"));
                }
            }
            result = at;
        }
        return result;
    }

    private ERObject getObjectFromContext(ERContext context, String currentArgString) {
        ERObject instantiatedValue = context.getPerson(currentArgString);

        if (instantiatedValue == null)
            instantiatedValue = context.getArgument(currentArgString);
        return instantiatedValue;
    }

    private String getTextVariable(String rawString, int startIndex) {
        int endOfWordIndex = Tools.indexOf(rawString, ERconstants.textVariableSeparators, startIndex);

        if (endOfWordIndex == -1) //special case when the variable is at the end of the string
            endOfWordIndex = rawString.length();

        return rawString.substring(startIndex, endOfWordIndex);
    }

    private String getReplacementStringFromObject(ERObject object) {
        if (object instanceof ERPerson)
            return ((ERPerson) object).getId();
        else
            return ((ERArgument) object).getValue();
    }

    /**
     * Replaces all %variables in a string with their plain text counterparts.
     *
     * @param context    the context from which to get the replacement names
     * @param newString2 the string in which the variables will be replaced
     * @return the new, plain text string
     */
    private String percentageVariablesToPlainText(ERContext context, String newString2) {
        int currentVariablePrefixIndex;
        int currentIndex = 0;

        do {
            currentVariablePrefixIndex = newString2.indexOf(ERconstants.variablePrefix, currentIndex);

            if (currentVariablePrefixIndex != -1) {
                String currentVariableString = getTextVariable(newString2, currentVariablePrefixIndex + 1);

                if (currentVariableString.equals("")) {
                    Logger.tag("AT").error("Variable is empty in text: {}", this);
                    break;
                }

                String at;
                if (currentVariableString.equals("speaker")) {
                    at = this.selectAnnotatedText(context.getSpeaker().getId(), context);
                } else if (currentVariableString.equals("listener")) {
                    at = this.selectAnnotatedText(context.getListener().getId(), context);
                } else if (context.getPersonByRealisedName(currentVariableString) != null) {
                    at = context.getPersonByRealisedName(currentVariableString).getRealisedName(
                            expressiveActionRealizer.getCurrentLanguage().ordinal());
                } else if (context.getArgument(currentVariableString) != null) {
                    String argumentValue = context.getArgument(currentVariableString).getValue();
                    at = this.selectAnnotatedText(argumentValue, context);
                } else {
                    at = this.selectAnnotatedText(currentVariableString, context); // recursive call
                }

                String replacement;
                if (at.equals("")) {
                    replacement = currentVariableString;
                } else {
                    replacement = this.interpret(at, context);
                    newString2 = newString2.replace(ERconstants.variablePrefix + currentVariableString, replacement);
                }
                currentIndex = currentVariablePrefixIndex + replacement.length();
                refExpGen.updateMentionDistance(currentVariableString);
            }
        } while (currentVariablePrefixIndex != -1);

        return newString2;
    }
}