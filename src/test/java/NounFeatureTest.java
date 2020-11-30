import expreal.erElements.*;
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

    /**
     * Test with the main noun being replaced by $variable.
     */
    @Test
    public void unmarkedNounWithOwnerByIdTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures8"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        String[] johnNames = new String[3];
        johnNames[0] = "John";
        johnNames[1] = "Jean";
        johnNames[2] = "Jan";
        context.addPerson(new ERPerson("player", ERGender.MASCULINE, johnNames, null));

        testEnglishPredicate(predicate, context, "John's big, red jacket");
        testFrenchPredicate(predicate, context, "grande veste de Jean");
        testDutchPredicate(predicate, context, "Jan's grote rode jas");
    }

    /**
     * Test with the a known person having an emotion value on either side of a threshold.
     */
    @Test
    public void nounWithEmotionValueTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures10"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        ERPerson john = new ERPerson("john", ERGender.MASCULINE);
        john.setProperty("contentedness", 0.8f);

        context.addPerson(john);

        testEnglishPredicate(predicate, context, "John likes it.");
        testFrenchPredicate(predicate, context, "Jean aime ça.");
        testDutchPredicate(predicate, context, "Jan vindt het leuk.");

        john.setProperty("contentedness", 0.3f);

        testEnglishPredicate(predicate, context, "John hates it.");
        testFrenchPredicate(predicate, context, "Jean déteste ça.");
        testDutchPredicate(predicate, context, "Jan haat het.");
    }

    /**
     * Test with the player character having an emotion value on either side of a threshold.
     */
    @Test
    public void playerWithEmotionValueTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures11"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        String[] johnNames = new String[3];
        johnNames[0] = "John";
        johnNames[1] = "Jean";
        johnNames[2] = "Jan";

        ERPerson john = new ERPerson("player", ERGender.MASCULINE, johnNames);
        john.setProperty("contentedness", 0.8f);

        context.addPerson(john);

        testEnglishPredicate(predicate, context, "John likes it.");
        testFrenchPredicate(predicate, context, "Jean aime ça.");
        testDutchPredicate(predicate, context, "Jan vindt het leuk.");

        john.setProperty("contentedness", 0.3f);

        testEnglishPredicate(predicate, context, "John hates it.");
        testFrenchPredicate(predicate, context, "Jean déteste ça.");
        testDutchPredicate(predicate, context, "Jan haat het.");
    }

    /**
     * Test with the player character having an emotion value on either side of a threshold.
     * This time without whitespace in the condition.
     */
    @Test
    public void playerWithEmotionValueWithoutWhitespaceTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nounfeatures12"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        String[] johnNames = new String[3];
        johnNames[0] = "John";
        johnNames[1] = "Jean";
        johnNames[2] = "Jan";

        ERPerson john = new ERPerson("player", ERGender.MASCULINE, johnNames);
        john.setProperty("contentedness", 0.8f);

        context.addPerson(john);

        testEnglishPredicate(predicate, context, "John likes it.");
        testFrenchPredicate(predicate, context, "Jean aime ça.");
        testDutchPredicate(predicate, context, "Jan vindt het leuk.");

        john.setProperty("contentedness", 0.3f);

        testEnglishPredicate(predicate, context, "John hates it.");
        testFrenchPredicate(predicate, context, "Jean déteste ça.");
        testDutchPredicate(predicate, context, "Jan haat het.");
    }


    /**
     * Test with French contraction of 'à le' into 'au'.
     */
    @Test
    public void frenchContractionALeTest() {
        context.clearArguments();
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "noun-contraction-ale"));
        arguments.add(new ERArgument("location", "salon"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testFrenchPredicate(predicate, context, "Aller au salon.");
    }

    /**
     * Test with French contraction of 'de le' into 'du'.
     */
    @Test
    public void frenchContractionDeLeTest() {
        context.clearArguments();
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "noun-contraction-dele"));
        arguments.add(new ERArgument("location", "salon"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testFrenchPredicate(predicate, context, "En direction du salon.");
    }

    /**
     * Test with French contraction of 'à le' into 'au' while using a plain $variable instead of grammatical block.
     */
    @Test
    public void frenchContractionVariableTest() {
        context.clearArguments();
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "noun-contraction-variable"));
        arguments.add(new ERArgument("location", "salon"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testFrenchPredicate(predicate, context, "Aller au salon.");
    }

    /**
     * Test with French elision.
     */
    @Test
    public void frenchElisionTest() {
        context.clearArguments();
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "noun-elision"));
        arguments.add(new ERArgument("task", "goSomewhere"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        testFrenchPredicate(predicate, context, "Ca te dirait d'aller?");
    }
}