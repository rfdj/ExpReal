package expreal.erRealizer;

import expreal.erElements.ERArgument;
import expreal.erElements.ERContext;
import expreal.erElements.ERObject;
import expreal.erElements.ERPerson;
import org.tinylog.Logger;

public class Condition extends ERObject {

    String firstOperand;
    String secondOperand;
    OperatorType operator;

    public Condition(String s) {
        String[] twoParts = s.split("!?[=>]");

        if (twoParts.length == 1 && twoParts[0].trim().startsWith("@")) {
            firstOperand = twoParts[0].replaceFirst("\\$", ""); //$ is a special character.
            operator = OperatorType.EQUALITY;
            secondOperand = "true";
        } else if (twoParts.length != 2) {
            Logger.tag("COND").error("Problem in author file: invalid condition field: {}", s);
        } else {
            firstOperand = twoParts[0].replaceFirst("\\$", ""); //$ is a special character.
            secondOperand = twoParts[1];
            if (s.contains("!="))
                operator = OperatorType.DIFFERENCE;
            else if (s.contains("="))
                operator = OperatorType.EQUALITY;
            else if (s.contains("!>"))
                operator = OperatorType.NOT_CONTAINING;
            else if (s.contains(">"))
                operator = OperatorType.CONTAINING;
            else
            Logger.tag("COND").error("Invalid operand in condition: {}", s);
        }
    }

    /**
     * Build a Condition based on key, operator and value directly.
     * Defaults to "=true" if not operator and value are provided.
     *
     * @param key          the key
     * @param operatorType what operator to use (EQUALITY, CONTAINING etc.), defaults to {@link OperatorType#EQUALITY}
     * @param value        the value
     */
    public Condition(String key, OperatorType operatorType, String value) {
        firstOperand = key;
        operator = operatorType;
        secondOperand = value;

        if (operatorType == null)
            operator = OperatorType.EQUALITY;
        if (secondOperand.equals(""))
            secondOperand = "true";
    }

    /**
     * Determine if the condition is verified, according to the context.
     *
     * @param context the context in which to verify the condition
     * @return true if the condition is verified.
     */
    public boolean isVerified(ERContext context) {
        ERObject object = context.getObjectByKey(firstOperand);

        String val;

        switch (operator) {
            case EQUALITY:
                if (object == null) return false;
                val = getValueOfContextualObject(object);
                return val.equals(secondOperand);
            case DIFFERENCE:
                if (object == null) return true;
                val = getValueOfContextualObject(object);
                return !val.equals(secondOperand);
            case CONTAINING:
                if (object == null) return false;
                val = getValueOfContextualObject(object);
                val = val.substring(1, val.length() - 1);
                String[] allElements = val.split(",");
                for (String currentElt : allElements) {
                    if (currentElt.equals(secondOperand)) {
                        return true;
                    }
                }
                return false;
            case NOT_CONTAINING:
                if (object == null) return true;
                val = getValueOfContextualObject(object);
                if (val.length() < 2) return true;
                val = val.substring(1, val.length() - 1);
                if (val.equals("")) return true;
                String[] allElements2 = val.split(",");
                for (String currentElt : allElements2) {
                    if (currentElt.equals(secondOperand))
                        return false;
                }
                return true;
            default:
                return true;
        }
    }

    private String getValueOfContextualObject(ERObject object) {
        if (object != null) {
            if (object instanceof Condition) {
                return ((Condition) object).getSecondOperand();
            } else {
                return getObjectName(object);
            }
        }
        return "";
    }

    /**
     * Get the name of an ERObject (without adding a getName() method to the class).
     *
     * @param object the object to retrieve the name from
     * @return the name
     */
    private String getObjectName(ERObject object) {
        String result;
        if (object instanceof ERPerson)
            result = ((ERPerson) object).getName();
        else if (object instanceof Condition)
            result = ((Condition) object).getFirstOperand();
        else
            result = ((ERArgument) object).getValue();
        return result;
    }

    public String getFirstOperand() {
        return firstOperand;
    }

    public String getSecondOperand() {
        return secondOperand;
    }

    public String toString() {
        return firstOperand + operator + secondOperand;
    }

    public enum OperatorType {EQUALITY, DIFFERENCE, CONTAINING, NOT_CONTAINING}

}
