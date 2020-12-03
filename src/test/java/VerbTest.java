import expreal.erElements.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * These tests test verb inflection and features.
 *
 * @author rfdj
 */
public class VerbTest extends TestHelper {

    private ERContext context = new ERContext();
    private ERPerson frank = new ERPerson("frank", ERGender.MASCULINE);
    private ERPerson paul = new ERPerson("paul", ERGender.MASCULINE);
    private ERPerson pete = new ERPerson("pete", ERGender.MASCULINE);
    private ERPerson lili = new ERPerson("lili", ERGender.FEMININE);
    private ERPerson julia = new ERPerson("julia", ERGender.FEMININE);

    @Before
    public void setUp() {
        context.addPerson(frank);
        context.addPerson(paul);
        context.addPerson(pete);
        context.addPerson(lili);
        context.addPerson(julia);
    }

    /**
     * Tests inflection in present simple, active.
     */
    @Test
    public void presentSimpleTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(pete);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "I run.");
        testFrenchPredicate(predicate, context, "Je cours.");
        testDutchPredicate(predicate, context, "Ik ren.");
    }

    //TODO
//    /**
//     * Tests inflection in present simple, active.
//     */
//    @Test
//    public void pastSimpleTest() {
//        Vector<ERArgument> arguments = new Vector<>();
//        arguments.add(new ERArgument("#speaker", "pete"));
//        arguments.add(new ERArgument("test", "verbs"));
//        ERPredicate predicate = new ERPredicate("InformIntention", arguments);
//
//        context.put("speaker", "pete");
//
//        testEnglishPredicate(predicate, context, "I ran.");
//        testFrenchPredicate(predicate, context, "J'ai couru.");
//        testDutchPredicate(predicate, context, "Ik rende.");
//    }

    /**
     * Tests the infinitive input block.
     */
    @Test
    public void infinitiveTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-infinitive"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(pete);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "I want to dance.");
        testFrenchPredicate(predicate, context, "Je veux danser.");
        testDutchPredicate(predicate, context, "Ik wil dansen.");
    }

    /**
     * Tests the infinitive input block with reflexive verbs.
     */
    @Test
    public void infinitiveReflexiveTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-infinitivereflexive"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(pete);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "I want to amuse myself.");
        testFrenchPredicate(predicate, context, "Je veux m'amuser.");
        testDutchPredicate(predicate, context, "Ik wil me amuseren.");
    }

    /**
     * Tests the infinitive input block with reflexive verbs, 3rd person.
     */
    @Test
    public void infinitiveReflexiveThirdPersonTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-infinitivereflexive"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "Pete wants to amuse himself.");
        testFrenchPredicate(predicate, context, "Pierre veut s'amuser.");
        testDutchPredicate(predicate, context, "Pieter wil zich amuseren.");
    }

    /**
     * Tests the conditional tense. Currently only useful for French.
     */
    @Test
    public void conditionalTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-conditional"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "Lili, it would be nice if you could sit down on the chair!");
        testFrenchPredicate(predicate, context, "Lili, ce serait parfait si tu pourrais t'assoir sur la chaise!");
        testDutchPredicate(predicate, context, "Lili, het zou fijn zijn als jij op de stoel zou kunnen gaan zitten!");
    }

    /**
     * Tests the agreement between a parent clause and its nested child, such as $task.
     */
    @Test
    public void parentChildAgreementTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-parentchildagreement"));
        arguments.add(new ERArgument("wish", "wantToDance"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "I want to dance.");
        testFrenchPredicate(predicate, context, "Je veux danser.");
        testDutchPredicate(predicate, context, "Ik wil dansen.");
    }

    /**
     * Tests the agreement between a parent clause and its nested child, such as $task. This child has a reflexive verb.
     */
    @Test
    public void parentChildAgreementReflexiveTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-parentchildagreement"));
        arguments.add(new ERArgument("wish", "wantToAmuseYourself"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "I want to amuse myself.");
        testFrenchPredicate(predicate, context, "Je veux m'amuser.");
        testDutchPredicate(predicate, context, "Ik wil me amuseren.");
    }

    /**
     * Tests the agreement between a parent clause and its nested child, such as $task. This child has a reflexive verb.
     */
    @Test
    public void frenchMasculineParticipleTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-frenchparticiple"));
        arguments.add(new ERArgument("wish", "wantToAmuseYourself"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);

        testFrenchPredicate(predicate, context, "Je suis fatigué.");
    }

    /**
     * Tests the agreement between a parent clause and its nested child, such as $task. This child has a reflexive verb.
     */
    @Test
    public void frenchFeminineParticipleTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "verbs-frenchparticiple"));
        arguments.add(new ERArgument("wish", "wantToAmuseYourself"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testFrenchPredicate(predicate, context, "Je suis fatiguée.");
    }
}