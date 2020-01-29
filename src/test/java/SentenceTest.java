import expreal.erElements.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * These tests test sentence structures, such as multiple complements or splitting subclauses.
 *
 * @author rfdj
 */
public class SentenceTest extends TestHelper {

    private ERContext context = new ERContext();
    private ERPerson frank = new ERPerson("frank", ERGender.MASCULINE);
    private ERPerson paul = new ERPerson("paul", ERGender.MASCULINE);
    private ERPerson pete = new ERPerson("pete", ERGender.MASCULINE);
    private ERPerson lili = new ERPerson("lili", ERGender.FEMININE);
    private ERPerson julia = new ERPerson("julia", ERGender.FEMININE);
    private ERPerson olivia = new ERPerson("olivia", ERGender.FEMININE);

    @Before
    public void setUp() {
        context.addPerson(frank);
        context.addPerson(paul);
        context.addPerson(pete);
        context.addPerson(lili);
        context.addPerson(julia);
        context.addPerson(olivia);
    }

    /**
     * Tests with multiple complements in the same clause.
     */
    @Test
    public void multipleComplementsTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "multiple-complements"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(frank);
        context.setListener(paul);

        testEnglishPredicate(predicate, context, "With Julia's gothic look, Olivia will get crazy with the idea of letting Lili spend the evening with Julia!");
        testFrenchPredicate(predicate, context, "TODO");
        testDutchPredicate(predicate, context, "TODO");
    }

    /**
     * Tests to split a sentence into separate clauses using pipes (|) as markers.
     */
    @Test
    public void splitSentenceOnPipesTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "splitsentence-pipes"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(olivia);
        context.setListener(paul);

        testEnglishPredicate(predicate, context, "TODO");
        testFrenchPredicate(predicate, context, "TODO");
        testDutchPredicate(predicate, context, "Omdat Julia rent, moet Frank ook rennen.");
    }

    /**
     * Tests to split a sentence with two subjects into separate clauses.
     */
    @Test
    public void splitSentenceOnSubjectsTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "splitsentence-subjects"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(olivia);
        context.setListener(paul);

        testEnglishPredicate(predicate, context, "Julia runs and Frank walks.");
        testFrenchPredicate(predicate, context, "TODO");
        testDutchPredicate(predicate, context, "TODO");
    }

    /**
     * Tests with a ghost block that should not be in the output.
     */
    @Test
    public void ghostBlockSubjectTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "ghostblock-subject"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(olivia);
        context.setListener(paul);

        testEnglishPredicate(predicate, context, "runs.");
        testFrenchPredicate(predicate, context, "court.");
        testDutchPredicate(predicate, context, "rent.");
    }

    /**
     * Tests with a ghost block that should not be in the output.
     */
    @Test
    public void ghostBlockVerbTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "ghostblock-verb"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(olivia);
        context.setListener(paul);

        testEnglishPredicate(predicate, context, "Julia.");
        testFrenchPredicate(predicate, context, "Julia.");
        testDutchPredicate(predicate, context, "Julia.");
    }

    /**
     * Tests to split a sentence on a ghost block.
     */
    @Test
    public void splitSentenceOnGhostSubjectsTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "splitsentence-ghostsubjects"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(olivia);
        context.setListener(paul);

        testEnglishPredicate(predicate, context, "runs and Frank walks.");
        testFrenchPredicate(predicate, context, "court et Frank marche.");
        testDutchPredicate(predicate, context, "rent en Frank loopt.");
    }
}