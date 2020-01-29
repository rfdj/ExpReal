import expreal.erElements.ERArgument;
import expreal.erElements.ERGender;
import expreal.erElements.ERPerson;
import expreal.erElements.ERPredicate;
import expreal.erElements.ERContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * These tests test the switching of dialog within predicates.
 *
 * @author rfdj
 */
public class SwitchDialogTest extends TestHelper {

    private ERContext context = new ERContext();
    private ERPerson frank = new ERPerson("frank", ERGender.MASCULINE);
    private ERPerson julia = new ERPerson("julia", ERGender.FEMININE);

    @Before
    public void setUp() {
        context.addPerson(frank);
        context.addPerson(julia);
    }

    /**
     * Test where the predicate contains an em dash (—) to switch dialogs.
     */
    @Test
    public void emDashTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "switchdialog1"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(frank);
        context.setListener(julia);

        Vector<String> expectedEnglish = new Vector<>();
        expectedEnglish.add("Hi, Julia!");
        expectedEnglish.add("Hello, Frank!");

        Vector<String> expectedFrench = new Vector<>();
        expectedFrench.add("Salut, Julia!");
        expectedFrench.add("Bonjour, Frank!");

        Vector<String> expectedDutch = new Vector<>();
        expectedDutch.add("Hoi, Julia!");
        expectedDutch.add("Hallo, Frank!");

        testEnglishPredicate(predicate, context, expectedEnglish);
        testFrenchPredicate(predicate, context, expectedFrench);
        testDutchPredicate(predicate, context, expectedDutch);
    }

    /**
     * Test where the predicate contains double dashes (--) to switch dialogs.
     */
    @Test
    public void doubleShortDashTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "switchdialog2"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(frank);
        context.setListener(julia);

        Vector<String> expectedEnglish = new Vector<>();
        expectedEnglish.add("Hi, Julia!");
        expectedEnglish.add("Hello, Frank!");

        Vector<String> expectedFrench = new Vector<>();
        expectedFrench.add("Salut, Julia!");
        expectedFrench.add("Bonjour, Frank!");

        Vector<String> expectedDutch = new Vector<>();
        expectedDutch.add("Hoi, Julia!");
        expectedDutch.add("Hallo, Frank!");

        testEnglishPredicate(predicate, context, expectedEnglish);
        testFrenchPredicate(predicate, context, expectedFrench);
        testDutchPredicate(predicate, context, expectedDutch);
    }

    /**
     * Test where the predicate contains both em dashes (—) and double dashes (--) interchangeably.
     */
    @Test
    public void mixedDashTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "switchdialog3"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(frank);
        context.setListener(julia);

        Vector<String> expectedEnglish = new Vector<>();
        expectedEnglish.add("Hi, Julia!");
        expectedEnglish.add("Hello, Frank!");

        Vector<String> expectedFrench = new Vector<>();
        expectedFrench.add("Salut, Julia!");
        expectedFrench.add("Bonjour, Frank!");

        Vector<String> expectedDutch = new Vector<>();
        expectedDutch.add("Hoi, Julia!");
        expectedDutch.add("Hallo, Frank!");

        testEnglishPredicate(predicate, context, expectedEnglish);
        testFrenchPredicate(predicate, context, expectedFrench);
        testDutchPredicate(predicate, context, expectedDutch);
    }

}