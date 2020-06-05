package expreal.erElements;


import expreal.erRealizer.Condition;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * The context contains all information that accompanies the predicate information.
 * Information includes who is speaking, who is listening, which roles the player and the patient have,
 * what arguments should be used and what user-defined conditions are defined.
 *
 * @author rfdj
 */
public class ERContext {

    /**
     * A list of all persons in this context, including the speaker, listener, etc.
     */
    private List<ERPerson> persons;

    /**
     * The arguments, such as task names, which can be used as expandable variables or in conditions.
     */
    private List<ERArgument> arguments;

    /**
     * A list of @conditions, which the author can use in the texts file.
     */
    private List<Condition> userDefinedConditions;

    /**
     * Special people, which we might want to find quickly. There are also in the persons list.
     */
    private ERPerson speaker, listener;

    /**
     * Empty constructor.
     */
    public ERContext() {
        persons = new ArrayList<>();
        arguments = new ArrayList<>();
        userDefinedConditions = new ArrayList<>();
    }

    /**
     * As a method of cloning, base this ERContext object on another ERContext object.
     *
     * @param oldContext the context on which to base the new one
     */
    public ERContext(ERContext oldContext) {
        this.speaker = oldContext.speaker;
        this.listener = oldContext.listener;

        this.persons = new ArrayList<>();
        this.persons = oldContext.persons;

        this.arguments = new ArrayList<>();
        this.arguments = oldContext.arguments;

        this.userDefinedConditions = new ArrayList<>();
        this.userDefinedConditions = oldContext.userDefinedConditions;
    }

    /**
     * Validates that the context has persons, a speaker and a listener.
     *
     * @return true if the context is properly set
     */
    public boolean isValid() {
        if (this.getSpeaker() == null) {
            Logger.tag("CTXT").error("Invalid context: missing speaker. Use context.setSpeaker(speakerERPerson).");
            return false;
        }
        if (this.getListener() == null) {
             Logger.tag("CTXT").warn("Possibly incomplete context: missing listener, using speaker as listener, too.\n" +
                    "Might want to use context.setListener(listenerERPerson).");
            this.setListener(this.getSpeaker());
        }
        if (this.getAllPersons().size() < 2) {
            Logger.tag("CTXT").warn("Possibly incomplete context: too few ERPersons. " +
                    "Might want to use context.addPerson(ERPerson) for all people.");
        }
        return true;
    }

    /**
     * True if the person or argument with the key string exists in the current context.
     * Used for verifying conditions, which can contain both persons and arguments.
     *
     * @param key the string to find
     * @return true if found, false if not
     */
    public ERObject getObjectByKey(String key) {
        for (ERPerson person : persons) {
            if (key.equals(person.getId()))
                return person;
        }
        for (ERArgument argument : arguments) {
            if (key.equals(argument.getName()))
                return argument;
        }
        for (Condition userDefinedCondition : userDefinedConditions) {
            if (key.equals(userDefinedCondition.getFirstOperand()))
                return userDefinedCondition;
        }
        return null;
    }

    /**
     * Retrieve a person from the context based on the key.
     *
     * @param key the string ('name') of the person to retrieve
     * @return the person with the key as name
     */
    public ERPerson getPerson(String key) {
        for (ERPerson person : this.persons) {
            if (key.equals(person.getId()))
                return person;
        }
        return null;
    }

    List<ERPerson> getAllPersons() {
        return persons;
    }

    public void addPerson(ERPerson person) {
        persons.add(person);
    }

    public List<ERArgument> getAllArguments() {
        return arguments;
    }

    public void addArgument(ERArgument argument) {
        arguments.add(argument);
    }

    /**
     * Retrieve an argument form the context based on the key.
     *
     * @param key the string ('name') of the argument to retrieve
     * @return the argument with the key as name
     */
    public ERArgument getArgument(String key) {
        for (ERArgument argument : this.arguments) {
            if (key.equals(argument.getName())) {
                return argument;
            }
        }
        return null;
    }

    /**
     * Clear the list of arguments.
     * Call this whenever you've used addArgument() on the same context, but in a different predicate.
     */
    public void clearArguments() {
        arguments = new ArrayList<>();
    }

    public ERPerson getSpeaker() {
        return speaker;
    }

    public void setSpeaker(ERPerson speaker) {
        this.speaker = speaker;
    }

    public ERPerson getListener() {
        return listener;
    }

    public void setListener(ERPerson listener) {
        this.listener = listener;
    }

    public List<Condition> getUserDefinedConditions() {
        return userDefinedConditions;
    }

    public void addUserDefinedCondition(String key, String value) {
        Condition condition = new Condition(key, Condition.OperatorType.EQUALITY, value);
        this.userDefinedConditions.add(condition);
    }

    @Override
    public String toString() {
        return String.format("ERContext{speaker: %s, listener: %s, " +
                        "persons: %s, arguments: %s, userDefinedConditions: %s}",
                speaker, listener, persons, arguments, userDefinedConditions);
    }
}
