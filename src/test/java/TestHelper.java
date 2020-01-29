import expreal.erElements.ERLanguage;
import expreal.erElements.ERPredicate;
import expreal.erElements.ERContext;
import expreal.erRealizer.ExpressiveActionRealizer;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;

import java.util.Vector;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Helper class for tests.
 *
 * @author rfdj
 */
public class TestHelper {

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    /**
     * Overload method for testPredicate()
     *
     * @param predicate      the predicate
     * @param context        the context
     * @param expectedString in the form of a String, which will be converted into a Vector of Strings
     */
    void testPredicate(ERPredicate predicate, ERContext context, ExpressiveActionRealizer er, String expectedString) {
        Vector<String> expected = new Vector<>();
        expected.add(expectedString);
        testPredicate(predicate, context, er, expected);
    }

    /**
     * Tests a predicate in context using the provided realiser.
     *
     * @param predicate the predicate
     * @param context   the context
     * @param expected  the expected outcome in the form of a Vector<String>
     */
    void testPredicate(ERPredicate predicate, ERContext context, ExpressiveActionRealizer er, Vector<String> expected) {
        if (er == null)
            er = new ExpressiveActionRealizer("test/Tests.csv", ERLanguage.DEFAULT_LANGUAGE);
        Vector<String> texts = er.getTexts(predicate, context);
        collector.checkThat(texts, equalTo(expected));
    }

    /**
     * Overload method for testEnglishPredicate()
     *
     * @param predicate      the predicate
     * @param context        the context
     * @param expectedString in the form of a String, which will be converted into a Vector of Strings
     */
    void testEnglishPredicate(ERPredicate predicate, ERContext context, String expectedString) {
        Vector<String> expected = new Vector<>();
        expected.add(expectedString);
        testEnglishPredicate(predicate, context, expected);
    }

    /**
     * Tests a predicate in context using the English realiser.
     *
     * @param predicate the predicate
     * @param context   the context
     * @param expected  the expected outcome in the form of a Vector<String>
     */
    void testEnglishPredicate(ERPredicate predicate, ERContext context, Vector<String> expected) {
        ExpressiveActionRealizer er = new ExpressiveActionRealizer("Tests.csv", ERLanguage.ENGLISH);
        Vector<String> texts = er.getTexts(predicate, context);
        collector.checkThat(texts, equalTo(expected));
    }

    /**
     * Overload method for testFrenchpredicate()
     *
     * @param predicate      the predicate
     * @param context        the context
     * @param expectedString in the form of a String, which will be converted into a Vector of Strings
     */
    void testFrenchPredicate(ERPredicate predicate, ERContext context, String expectedString) {
        Vector<String> expected = new Vector<>();
        expected.add(expectedString);
        testFrenchPredicate(predicate, context, expected);
    }

    /**
     * Tests a predicate in context using the French realiser.
     *
     * @param predicate the predicate
     * @param context   the context
     * @param expected  the expected outcome in the form of a Vector<String>
     */
    void testFrenchPredicate(ERPredicate predicate, ERContext context, Vector<String> expected) {
        ExpressiveActionRealizer er = new ExpressiveActionRealizer("Tests.csv", ERLanguage.FRENCH);
        Vector<String> texts = er.getTexts(predicate, context);
        collector.checkThat(texts, equalTo(expected));
    }

    /**
     * Overload method for testDutchPredicate()
     *
     * @param predicate      the predicate
     * @param context        the context
     * @param expectedString in the form of a String, which will be converted into a Vector of Strings
     */
    void testDutchPredicate(ERPredicate predicate, ERContext context, String expectedString) {
        Vector<String> expected = new Vector<>();
        expected.add(expectedString);
        testDutchPredicate(predicate, context, expected);
    }

    /**
     * Tests a predicate in context using the Dutch realiser.
     *
     * @param predicate the predicate
     * @param context   the context
     * @param expected  the expected outcome in the form of a Vector<String>
     */
    void testDutchPredicate(ERPredicate predicate, ERContext context, Vector<String> expected) {
        ExpressiveActionRealizer er = new ExpressiveActionRealizer("Tests.csv", ERLanguage.DUTCH);
        Vector<String> texts = er.getTexts(predicate, context);
        collector.checkThat(texts, equalTo(expected));
    }
}
