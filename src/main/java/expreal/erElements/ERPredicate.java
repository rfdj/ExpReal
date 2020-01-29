package expreal.erElements;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Predicate, for example Inform(speaker,listener,content)
 *
 * @author szilas
 */
public class ERPredicate extends ERObject {

    ERPredicateType type;
    Vector<ERArgument> arguments;

    /**
     * alternative internal storage of the arguments, enabling a quick access
     */
    private Hashtable<String, ERArgument> argumentTable;

    public ERPredicate(String _type, Vector<ERArgument> _arguments) {
        type = new ERPredicateType(_type);
        arguments = _arguments;

        argumentTable = new Hashtable<>();

        for (ERArgument arg : arguments) {
            argumentTable.put(arg.getName(), arg);
        }
    }

    public ERPredicateType getType() {
        return type;
    }

    public ERArgument getArgument(String name) {
        return argumentTable.get(name);
    }

    public boolean containsArgument(String name) {
        return argumentTable.containsKey(name);
    }

    public Vector<ERArgument> getArguments() {
        return arguments;
    }

    public String toString() {
        String argString = arguments.toString().replaceFirst("\\[", "(").replaceFirst("\\]", ")");
        return type.getName() + argString;
    }
}
