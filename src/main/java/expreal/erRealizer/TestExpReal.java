package expreal.erRealizer;

import expreal.erElements.*;

import java.util.Vector;

/**
 * Class for testing, with a main function.
 *
 * @author szilas, rfdj
 */
public class TestExpReal {

    public static void main(String[] args) {

        ExpressiveActionRealizer er = new ExpressiveActionRealizer("Example.csv", ERLanguage.ENGLISH);

        /* construction of a predicate */
        Vector<ERArgument> argumentsRandom = new Vector<>();
        argumentsRandom.add(new ERArgument("task", "haveAdrink"));
        ERPredicate predicateRandom = new ERPredicate("PickRandom", argumentsRandom);

        /* construction of a context */
        ERPerson player = new ERPerson("julia", ERGender.FEMININE);
        ERPerson patient = new ERPerson("frank", ERGender.MASCULINE);

        ERContext context = new ERContext(player, patient);
        context.setSpeaker(player);
        context.setListener(patient);

        Vector<String> txts = er.getTexts(predicateRandom, context);

        System.out.println("Test with " + predicateRandom);
        for (String s : txts) {
            System.out.println(s);
        }

    }

}
