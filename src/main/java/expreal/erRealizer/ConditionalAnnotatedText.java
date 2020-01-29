package expreal.erRealizer;

import expreal.erElements.ERContext;

import java.util.Vector;

/**
 * This class represents a text, associated to a set of conditions.
 * It corresponds to elements in the 2nd and 3rd columns in the spreadsheet author file.
 *
 *  @author szilas
 */
public class ConditionalAnnotatedText {

    String conditionString;
    String annotatedText;
    Vector<Condition> conditions;
    /**
     * specificity of the conditions: the more specific, the more adequate the annotated text is.
     */
    int specificity;
    boolean isUserDefinedCondition = false;


    public ConditionalAnnotatedText(String _condition, String _annotatedText) {
        conditionString = _condition;
        annotatedText = _annotatedText;
        conditions = new Vector<>();
        specificity = 0;

        /* get rid of the spaces around comas */
        conditionString = conditionString.replaceAll(" *, *", ",");

        if (conditionString.isEmpty())
            return;

        // the corresponding condition will be added later, in the calling function.
        if (conditionString.contains("@"))
            isUserDefinedCondition = true;

        /* split into individual condition strings */
        String[] condStrings = conditionString.split(",");

        /* create each condition */
        Condition currentCondition;
        for (String currentString : condStrings) {
            currentCondition = new Condition(currentString);
            conditions.add(currentCondition);
            specificity++; //later: could depend on the fact that the current condition concerns a task or a character.
        }
    }


    /**
     * Calculate if the conditions are verified.
     *
     * @param context the context in which the condition is verified
     * @return true if the conditions are all true.
     */
    public boolean verifiedCondition(ERContext context) {
        for (Condition currentCondition : conditions) {
            if (!currentCondition.isVerified(context)) {
                return false;
            }
        }
        return true;
    }


    public void addCondition(Condition cond) {
        conditions.add(cond);
    }


    public String getAnnotatedText() {
        return annotatedText;
    }


    public int getSpecificity() {
        return specificity;
    }


    public boolean isUserDefinedCondition() {
        return isUserDefinedCondition;
    }


    public String toString() {
        return conditionString + "|" + annotatedText;
    }


}
