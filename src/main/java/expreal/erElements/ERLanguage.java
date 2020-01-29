package expreal.erElements;

/**
 * This enum lists the languages available for use in ExpReal.
 *
 * @author rfdj
 */
public enum ERLanguage {
    ENGLISH("en"),
    FRENCH("fr"),
    DUTCH("nl");


    public static final ERLanguage DEFAULT_LANGUAGE = ENGLISH;
    public final String code;

    ERLanguage(String code) {
        this.code = code;
    }
}