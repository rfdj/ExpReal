package expreal.erRealizer;

/**
 * Interface with the constants of ExpReal
 *
 * @author szilas
 */
public interface ERconstants {

    /** separator in the authoring file (spreadsheet): */
    char columnSeparator = 59;

    /** list of separators for the variables */
    String textVariableSeparators = "[}  ,.:;!?…\"-']"; //DO NOT EDIT THE SPACES (there are two kinds of spaces, on of which, the 2nd, is the "insecable" one

    /** prefix of the variables (different from the arguments, which are prefixed with a $ */
    String variablePrefix = "%";

    /** String that indicates, at the beginning of a dialog turn, that the speaker and the listener are exchanged */
    String switchDialog = "#switchDialog";

    /** Maximum distance before needing to use a definite description again (often just the name) */
    int refExpLongDistance = 2;

    /** name of the tag in the context indicating the persons mentioned previously (for pronoun replacement) */
    enum contextKeys {MENTIONED_PERSONS}

}
