import expreal.erElements.ERArgument;
import expreal.erElements.ERGender;
import expreal.erElements.ERPerson;
import expreal.erElements.ERPredicate;
import expreal.erElements.ERContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * These tests test noun features, such as 'owner nouns' (possessive).
 *
 * @author rfdj
 */
public class NounFeatureTest extends TestHelper {

    private ERContext context = new ERContext();

    @Before
    public void setUp() {
        ERPerson julia = new ERPerson("julia", ERGender.FEMININE);
        ERPerson olivia = new ERPerson("olivia", ERGender.FEMININE);

        context.addPerson(new ERPerson("frank", ERGender.MASCULINE));
        context.addPerson(new ERPerson("paul", ERGender.MASCULINE));
        context.addPerson(new ERPerson("lili", ERGender.FEMININE));
        context.addPerson(new ERPerson("julia", ERGender.FEMININE));
        context.addPerson(new ERPerson("pete", ERGender.MASCULINE));

        context.setSpeaker(julia);
        context.setListener(olivia);
    }

    /**
     * Tests basic localisation of variable values based on selecting the proper row in the input CSV file.
     */
    @Test
    public void localisedNameTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures1"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testEnglishPredicate(predicate, context, "Pete");
        testFrenchPredicate(predicate, context, "Pierre");
        testDutchPredicate(predicate, context, "Pieter");
    }

    /**
     * Tests that add an owner to the subject (friend &lt; %Frank).
     */
    @Test
    public void ownerNounTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures2"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testEnglishPredicate(predicate, context, "Frank's friend");
        testFrenchPredicate(predicate, context, "ami de Frank");
        testDutchPredicate(predicate, context, "Frank's vriend");
    }

    /**
     * Tests with an owner to the subject and a determiner for the subject (the friend &lt; %Frank).
     */
    @Test
    public void ownerNounWithDeterminerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures3"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testEnglishPredicate(predicate, context, "the friend of Frank");
        testFrenchPredicate(predicate, context, "l'ami de Frank");
        testDutchPredicate(predicate, context, "de vriend van Frank");
    }

    /**
     * Tests with an owner to the subject and two premodifiers.
     */
    @Test
    public void ownerNounWithModifiersTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures4"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testEnglishPredicate(predicate, context, "big, red jacket");
        testFrenchPredicate(predicate, context, "grande veste rouge");
        testDutchPredicate(predicate, context, "grote rode jas");
    }

    /**
     * Tests with modifier(s) and no pipes to mark the noun.
     */
    @Test
    public void unmarkedNounTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures5"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testEnglishPredicate(predicate, context, "big, red jacket");
        testFrenchPredicate(predicate, context, "grande veste");
        testDutchPredicate(predicate, context, "grote rode jas");
    }

    /**
     * Tests with an owner to the subject and modifier(s). No pipes to mark the noun.
     */
    @Test
    public void unmarkedNounWithOwnerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures6"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testEnglishPredicate(predicate, context, "Frank's big, red jacket");
        testFrenchPredicate(predicate, context, "grande veste de Frank");
        testDutchPredicate(predicate, context, "Frank's grote rode jas");
    }
}