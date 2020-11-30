import expreal.erElements.*;
import expreal.erRealizer.ExpressiveActionRealizer;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

/**
 * These tests test the handling of pronouns, based on the context.
 *
 * @author rfdj
 */
public class PronounTest extends TestHelper {

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
     * Test where neither the speaker nor the listener is mentioned in the predicate.
     */
    @Test
    public void nonMentionedTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "Frank is Julia's best friend.");
        testFrenchPredicate(predicate, context, "Frank est le meilleur ami de Julia.");
        testDutchPredicate(predicate, context, "Frank is Julia's beste vriend.");
    }

    /**
     * Test with the {subject:} being the speaker.
     */
    @Test
    public void subjectIsSpeakerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(frank);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "I am Julia's best friend.");
        testFrenchPredicate(predicate, context, "Je suis le meilleur ami de Julia.");
        testDutchPredicate(predicate, context, "Ik ben Julia's beste vriend.");
    }

    /**
     * Test with the {subject:} being the listener.
     */
    @Test
    public void subjectIsListenerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(lili);
        context.setListener(frank);

        testEnglishPredicate(predicate, context, "You are Julia's best friend.");
        testFrenchPredicate(predicate, context, "Tu es le meilleur ami de Julia.");
        testDutchPredicate(predicate, context, "Jij bent Julia's beste vriend.");
    }

    /**
     * Test with the {object:} being the speaker.
     */
    @Test
    public void objectIsSpeakerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(paul);

        testEnglishPredicate(predicate, context, "Frank is my best friend.");
        testFrenchPredicate(predicate, context, "Frank est mon meilleur ami.");
        testDutchPredicate(predicate, context, "Frank is mijn beste vriend.");
    }

    /**
     * Test with the {object:} being the listener.
     */
    @Test
    public void objectIsListenerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(paul);
        context.setListener(julia);

        testEnglishPredicate(predicate, context, "Frank is your best friend.");
        testFrenchPredicate(predicate, context, "Frank est ton meilleur ami.");
        testDutchPredicate(predicate, context, "Frank is jouw beste vriend.");
    }

    /**
     * Test where both the speaker and the listener are mentioned in the predicate.
     */
    @Test
    public void bothInPredicateTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(frank);
        context.setListener(julia);

        testEnglishPredicate(predicate, context, "I am your best friend.");
        testFrenchPredicate(predicate, context, "Je suis ton meilleur ami.");
        testDutchPredicate(predicate, context, "Ik ben jouw beste vriend.");
    }

    /**
     * Test where both the speaker and the listener are mentioned in the predicate,
     * but with roles being the opposites of those in bothMentionedTest().
     */
    @Test
    public void bothInPredicateInverseTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(frank);

        testEnglishPredicate(predicate, context, "You are my best friend.");
        testFrenchPredicate(predicate, context, "Tu es mon meilleur ami.");
        testDutchPredicate(predicate, context, "Jij bent mijn beste vriend.");
    }

    /**
     * Test where both the speaker and the listener are mentioned in the predicate.
     */
    @Test
    public void genderBothMentionedTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun-gender"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(frank);

        testEnglishPredicate(predicate, context, "I am your best friend.");
        testFrenchPredicate(predicate, context, "Je suis ta meilleure amie.");
        testDutchPredicate(predicate, context, "Ik ben jouw beste vriendin.");
    }

    /**
     * Test with a reflexive verb that needs the proper pronoun based on gender and number.
     */
    @Test
    public void reflexiveVerbPronounTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun-reflexiveverb"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(frank);

        testEnglishPredicate(predicate, context, "I amuse myself.");
        testFrenchPredicate(predicate, context, "Je m'amuse.");
        testDutchPredicate(predicate, context, "Ik amuseer me.");
    }

    /**
     * Test with a reflexive verb that needs the proper pronoun based on gender and number.
     */
    @Test
    public void reflexiveVerbPronounSecondPersonTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "pronoun-reflexiveverb"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(frank);
        context.setListener(julia);

        testEnglishPredicate(predicate, context, "You amuse yourself.");
        testFrenchPredicate(predicate, context, "Tu t'amuses.");
        testDutchPredicate(predicate, context, "Jij amuseert je.");
    }

    /**
     * Test with a simple repeating referent.
     */
    @Test
    public void refExpSimpleRepetitionTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "refexp-simplerepetition"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(frank);

        testEnglishPredicate(predicate, context, "Paul needs medicine. He takes the medicine.");
        testFrenchPredicate(predicate, context, "Paul a besoin de médicaments. Il prend les médicaments.");
        testDutchPredicate(predicate, context, "Paul heeft medicijnen nodig. Hij neemt de medicijnen.");
    }

    /**
     * Test where a referent is mentioned again after a long distance.
     */
    @Test
    public void refExpLongDistanceTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "refexp-longdistance"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(frank);

        testEnglishPredicate(predicate, context, "Paul needs medicine. The medicine is in the drawer. "
                + "The drawer is closed. Paul opens the drawer. He takes the medicine.");
        testFrenchPredicate(predicate, context, "Paul a besoin de médicaments. Les médicaments sons dans le tiroir. "
                + "Le tiroir est fermé. Paul ouvre le tiroir. Il prend les médicaments.");
        testDutchPredicate(predicate, context, "Paul heeft medicijnen nodig. De medicijnen liggen in de la. "
                + "De la is gesloten. Paul opent de la. Hij neemt de medicijnen.");
    }

    /**
     * Test with a subject that has an owner. The subject is also repeated.
     * Note: the repetition is chosen to be replaced with a pronoun, even though "her drawer" instead of "it" would
     * also be possible.
     */
    @Test
    public void refExpLongDistanceOwnerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "refexp-longdistanceowner"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(frank);

        // Note: 'drawer' is not in the lexicon. To generate 'it', the test source uses the .n feature to set the proper gender.
        testEnglishPredicate(predicate, context, "Paul needs medicine. The medicine is in Lili's drawer. "
                + "It is closed. Paul opens the drawer. He takes the medicine.");
        testFrenchPredicate(predicate, context, "Paul a besoin de médicaments. Les médicaments sons dans le tiroir de Lili. "
                + "Il est fermé. Paul ouvre le tiroir. Il prend les médicaments.");
        testDutchPredicate(predicate, context, "Paul heeft medicijnen nodig. De medicijnen liggen in Lili's la. "
                + "Hij is gesloten. Paul opent de la. Hij neemt de medicijnen.");
    }

    /**
     * Test with two similar entities (both male), one of which is repeated.
     */
    @Test
    public void refExpAmbiguityTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "refexp-ambiguity"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "Frank greeted Pete. Pete smiled.");
        testFrenchPredicate(predicate, context, "Frank a salué Pierre. Pierre a souri.");
        testDutchPredicate(predicate, context, "Frank begroette Pieter. Pieter glimlachte.");
    }

    /**
     * Test with two similar entities (both male), one of which is repeated.
     */
    @Test
    public void refExpAmbiguityOwnerTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "refexp-ambiguityowner"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "Frank's dog greeted Pete's dog. Pete's dog barked.");
        testFrenchPredicate(predicate, context, "Le chien de Frank a salué le chien de Pierre. Le chien de Pierre a aboyé.");
        testDutchPredicate(predicate, context, "Frank's hond begroette Pieter's hond. Pieter's hond blafte.");
    }

    /**
     * Test with two similar entities (both male), one of which is repeated.
     */
    @Test
    public void refExpAmbiguityOwnerFeatureTest() {
        Vector<ERArgument> arguments = new Vector<>();
        arguments.add(new ERArgument("test", "refexp-ambiguityownerfeature"));
        ERPredicate predicate = new ERPredicate("InformIntention", arguments);

        context.setSpeaker(julia);
        context.setListener(lili);

        testEnglishPredicate(predicate, context, "Frank's dog greeted Pete's dog. Pete's dog barked.");
        testFrenchPredicate(predicate, context, "La chienne de Frank a salué la chienne de Pierre. La chienne de Pierre a aboyé.");
        testDutchPredicate(predicate, context, "Frank's hond begroette Pieter's hond. Pieter's hond blafte.");
    }

    /**
     * Test with multiple calls to getTexts(). The mentionDistances should be maintained for proper anaphoric expression generation.
     */
    @Test
    public void refExpInteractiveDialogTest() {
        ExpressiveActionRealizer englishRealizer = new ExpressiveActionRealizer("Tests.csv", ERLanguage.ENGLISH);
        ExpressiveActionRealizer frenchRealizer = new ExpressiveActionRealizer("Tests.csv", ERLanguage.FRENCH);
        ExpressiveActionRealizer dutchRealizer = new ExpressiveActionRealizer("Tests.csv", ERLanguage.DUTCH);

        context.setSpeaker(julia);
        context.setListener(lili);

        Vector<ERArgument> args1 = new Vector<>();
        args1.add(new ERArgument("test", "refexp-interactivedialog1"));
        ERPredicate pred1 = new ERPredicate("InformIntention", args1);

        testPredicate(pred1, context, englishRealizer, "Did you hear about Frank?");
        testPredicate(pred1, context, frenchRealizer, "Avez-vous entendu parler de Frank?");
        testPredicate(pred1, context, dutchRealizer, "Heb je gehoord over Frank?");

        context.clearArguments();

        Vector<ERArgument> args2 = new Vector<>();
        args2.add(new ERArgument("test", "refexp-interactivedialog2"));
        ERPredicate pred2 = new ERPredicate("InformIntention", args2);

        testPredicate(pred2, context, englishRealizer, "What's up with him?");
        testPredicate(pred2, context, frenchRealizer, "Quoi de neuf avec lui?");
        testPredicate(pred2, context, dutchRealizer, "Wat is er met hem?");

        context.clearArguments();

        Vector<ERArgument> args3 = new Vector<>();
        args3.add(new ERArgument("test", "refexp-interactivedialog3"));
        ERPredicate pred3 = new ERPredicate("InformIntention", args3);

        testPredicate(pred3, context, englishRealizer, "He has moved.");
        testPredicate(pred3, context, frenchRealizer, "Il a déménagé.");
        testPredicate(pred3, context, dutchRealizer, "Hij is verhuisd.");

        context.clearArguments();

        Vector<ERArgument> args4 = new Vector<>();
        args4.add(new ERArgument("test", "refexp-interactivedialog4"));
        ERPredicate pred4 = new ERPredicate("InformIntention", args4);

        testPredicate(pred4, context, englishRealizer, "Does he like his new house?");
        testPredicate(pred4, context, frenchRealizer, "Est-ce que il aime sa nouvelle maison?"); // nicer: qu'il
        testPredicate(pred4, context, dutchRealizer, "Vindt hij zijn nieuwe huis leuk?");

        context.clearArguments();

        Vector<ERArgument> args5 = new Vector<>();
        args5.add(new ERArgument("test", "refexp-interactivedialog5"));
        ERPredicate pred5 = new ERPredicate("InformIntention", args5);

        testPredicate(pred5, context, englishRealizer, "He thinks it is beautiful.");
        testPredicate(pred5, context, frenchRealizer, "Il pense que la est belle."); // nicer: qu'elle
        testPredicate(pred5, context, dutchRealizer, "Hij vindt het prachtig.");
    }
}