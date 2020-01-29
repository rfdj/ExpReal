import expreal.erElements.ERArgument;
import expreal.erElements.ERGender;
import expreal.erElements.ERPerson;
import expreal.erElements.ERPredicate;
import expreal.erElements.ERContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * These tests test user-defined conditions, such as @userChoice, @predicateChoice and others.
 *
 * @author rfdj
 */
public class UserDefinedConditionTest extends TestHelper {

    private ERContext context = new ERContext();
    private ERPerson paul = new ERPerson("paul", ERGender.MASCULINE);
    private ERPerson lili = new ERPerson("lili", ERGender.FEMININE);

    @Before
    public void setUp() {
        context.addPerson(paul);
        context.addPerson(lili);
    }

    /**
     * Test with the previously used @userChoice.
     */
    @Test
    public void userChoiceTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "userdefinedconditions1"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);
        context.addUserDefinedCondition("@userChoice", "true");

        testEnglishPredicate(predicate, context, "Button1-EN");
        testFrenchPredicate(predicate, context, "Button1-FR");
        testDutchPredicate(predicate, context, "Button1-NL");
    }

    /**
     * Test with @predicateChoice.
     */
    @Test
    public void predicateChoiceTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "userdefinedconditions2"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);
        context.addUserDefinedCondition("@predicateChoice", "true");

        testEnglishPredicate(predicate, context, "Button2-EN");
        testFrenchPredicate(predicate, context, "Button2-FR");
        testDutchPredicate(predicate, context, "Button2-NL");
    }

    /**
     * Test with @someOtherTag.
     */
    @Test
    public void someOtherTagTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "userdefinedconditions3"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);
        context.addUserDefinedCondition("@someOtherTag", "true");

        testEnglishPredicate(predicate, context, "Button3-EN");
        testFrenchPredicate(predicate, context, "Button3-FR");
        testDutchPredicate(predicate, context, "Button3-NL");
    }

    /**
     * Test with @userChoice and a $variable.
     */
    @Test
    public void userChoiceTaskTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "userdefinedconditionstask1"));
        arguments.add(new ERArgument("beQuiet", "BeQuiet"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);
        context.addUserDefinedCondition("@userChoice", "true");

        testEnglishPredicate(predicate, context, "Please, be quiet!");
        testFrenchPredicate(predicate, context, "S'il vous plait soyez tranquille!");
        testDutchPredicate(predicate, context, "Alsjeblieft, wees stil!");
    }

    /**
     * Test with @predicateChoice and a $variable.
     */
    @Test
    public void predicateChoiceTaskTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "userdefinedconditionstask2"));
        arguments.add(new ERArgument("beQuiet", "BeQuiet"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);
        context.addUserDefinedCondition("@predicateChoice", "true");

        testEnglishPredicate(predicate, context, "Please, be quiet!");
        testFrenchPredicate(predicate, context, "S'il vous plait soyez tranquille!");
        testDutchPredicate(predicate, context, "Alsjeblieft, wees stil!");
    }

    /**
     * Test with @someOtherTag and a $variable.
     */
    @Test
    public void someOtherTagTaskTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "userdefinedconditionstask3"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);
        context.addUserDefinedCondition("@someOtherTag", "true");

        testEnglishPredicate(predicate, context, "Please, be quiet!");
        testFrenchPredicate(predicate, context, "S'il vous plait soyez tranquille!");
        testDutchPredicate(predicate, context, "Alsjeblieft, wees stil!");
    }

    /**
     * Test multiple $variables nested several layers deep.
     */
    @Test
    public void nestedTaskTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nestedvariables"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "Please, be quiet!");
        testFrenchPredicate(predicate, context, "S'il vous plait soyez tranquille!");
        testDutchPredicate(predicate, context, "Alsjeblieft, wees stil!");
    }

    /**
     * Test multiple $variables nested several layers deep.
     */
    @Test
    public void dynamicArgumentTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "dynamicargument"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.clearArguments();
        context.setListener(lili);
        context.addArgument(new ERArgument("argument", "Chair"));

        testEnglishPredicate(predicate, context, "Please, go sit on the chair!");
        testFrenchPredicate(predicate, context, "S'il vous plait, t'assoir sur la chaise!");
        testDutchPredicate(predicate, context, "Alsjeblieft, ga op de stoel zitten!");
    }

    /**
     * Test multiple $variables nested several layers deep.
     */
    @Test
    public void nestedDynamicArgumentTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "nesteddynamicargument"));
        arguments.add(new ERArgument("task", "SitOn"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);
        context.clearArguments();
        context.addArgument(new ERArgument("argument", "Chair"));

        testEnglishPredicate(predicate, context, "Please, go sit on the chair!");
        testFrenchPredicate(predicate, context, "S'il vous plait, t'assoir sur la chaise!");
        testDutchPredicate(predicate, context, "Alsjeblieft, ga op de stoel zitten!");
    }
}