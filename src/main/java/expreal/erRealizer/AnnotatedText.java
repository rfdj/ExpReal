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

        // The following feature (now discarded) allowed to have elements' names with spaces (but it prevented the use of _). This is on longer possible.
        //element = element.replace('_', ' ');

        // Retrieve realised name if applicable
        if (localContext.getPerson(element) != null) {
            if (localContext.getPerson(element).hasRealisedNames()) {
                ERLanguage currentLanguage = expressiveActionRealizer.getCurrentLanguage();
                Logger.tag("AT").debug("Used realised name for element: {}", element);
                return localContext.getPerson(element).getRealisedName(currentLanguage.ordinal());
            }
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
        Logger.tag("AT").debug("Verified conditional annotated texts: {}", filteredCondTexts);

        Vector<ConditionalAnnotatedText> specificCondTexts = getMostSpecificTexts(filteredCondTexts);
        Logger.tag("AT").debug("Most specific conditional annotated text: {}", specificCondTexts);

        /* filtering out recently used entries */
        //TODO

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
        return filteredCondTexts;
    }

    /**
     * Filter out less specific text, to return the most specific one.
     *
     * @param filteredCondTexts the texts whose conditions are met
     * @return the most specific text, with the most (important) conditions met
     */
    private Vector<ConditionalAnnotatedText> getMostSpecificTexts(Vector<ConditionalAnnotatedText> filteredCondTexts) {
        int currentSpecifity;
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
                currentSpecifity = currentCat.getSpecificity();
                if (currentSpecifity > maxSpecificity) {
                    specificCondTexts.clear();
                    specificCondTexts.add(currentCat);
                    maxSpecificity = currentSpecifity;
                } else if (currentSpecifity == maxSpecificity) {
                    specificCondTexts.add(currentCat);
                }
            }
        }
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
        for (Map.Entry entry : refExpGen.mentionDistances.entrySet()) {
            refExpGen.updateMentionDistance(entry.getKey().toString());
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
        int currentSeparatorAfterDollarIndex;
        String currentArgString;

        do {
            currentDollarIndex = newString.indexOf("$", currentIndex);

            if (currentDollarIndex != -1) {

                // Determine the length of the variable
                currentSeparatorAfterDollarIndex = Tools.indexOf(
                        newString, ERconstants.textVariableSeparators, currentDollarIndex + 1);

                if (currentSeparatorAfterDollarIndex == -1) //special case when the variable is at the end
                    currentSeparatorAfterDollarIndex = newString.length();

                // Get variable from context
                currentArgString = newString.substring(currentDollarIndex + 1, currentSeparatorAfterDollarIndex);
                ERObject instantiatedValue = context.getPerson(currentArgString);

                if (instantiatedValue == null)
                    instantiatedValue = context.getArgument(currentArgString);

                if (instantiatedValue == null) {
                    // Variable not found in context. Let's try it as a %variable.
                    newString = percentageVariablesToPlainText(context, newString.replace("$", "%"));
                    currentIndex = currentDollarIndex + currentArgString.length();
                } else {
                    // Build the replacement string based on the context variable,
                    // replace whitespace with underscores,
                    // replace $ with % for further processing.

                    String replacementString = "";
                    if (instantiatedValue instanceof ERPerson) {
                        replacementString = ((ERPerson) instantiatedValue).getId();
                    } else {
                        replacementString = ((ERArgument) instantiatedValue).getValue();
                    }

                    if (currentArgString.equals("speaker") || currentArgString.equals("listener")) {
                        // $speaker and $listener are replaced by their % variables, e.g. %julia,
                        // which will be realised later.
                        newString = newString.replace("$" + currentArgString, "%" + replacementString);
                    } else {
                        refExpGen.updateMentionDistance(replacementString);

                        String at = this.selectAnnotatedText(replacementString, context);

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
                            replacementString = at;
                        }

                        String targetString = currentArgString;
                        if (newString.substring(0, currentDollarIndex).endsWith(" de ")
                                && expressiveActionRealizer.startsWithVowelOrIsolatedY(replacementString)) {
                            targetString =      " de $" + currentArgString;
                            replacementString = " d'" + replacementString;
                        } else if (newString.substring(0, currentDollarIndex).endsWith(" à ")) {
                            if (replacementString.startsWith("le ")) {
                                targetString =      " à $" + currentArgString;
                                replacementString = " au " + replacementString.substring(3);
                            } else if (replacementString.startsWith("les ")) {
                                targetString =      " à $" + currentArgString;
                                replacementString = " aux " + replacementString.substring(4);
                            } else {
                                targetString = "$" + currentArgString;
                            }
                        } else {
                            targetString = "$" + currentArgString;
                        }
                        newString = newString.replace(targetString, replacementString);
                    }
                    currentIndex = currentDollarIndex + replacementString.length();

                }
            }
        } while (currentDollarIndex != -1);

        return newString;
    }

    /**
     * Replaces all %variables in a string with their plain text counterparts.
     *
     * @param context    the context from which to get the replacement names
     * @param newString2 the string in which the variables will be replaced
     * @return the new, plain text string
     */
    private String percentageVariablesToPlainText(ERContext context, String newString2) {
        String currentVariableString;
        int currentVariablePrefixIndex;
        int currentSpaceAfterUnderscoreIndex;
        int currentIndex = 0;

        do {
            currentVariablePrefixIndex = newString2.indexOf(ERconstants.variablePrefix, currentIndex);

            if (currentVariablePrefixIndex != -1) {
                currentSpaceAfterUnderscoreIndex = Tools.indexOf(
                        newString2, ERconstants.textVariableSeparators, currentVariablePrefixIndex + 1);

                if (currentSpaceAfterUnderscoreIndex == -1) //special case when the variable is at the end
                    currentSpaceAfterUnderscoreIndex = newString2.length();
                currentVariableString = newString2.substring(currentVariablePrefixIndex + 1, currentSpaceAfterUnderscoreIndex);

                if (currentVariableString.equals("")) {
                    Logger.tag("AT").error("Variable is empty in text: {}", this);
                    break;
                }

                String at = "";
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