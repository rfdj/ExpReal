package expreal.erElements;

/**
 * Type of predicate, for example Inform, Dissuade, etc.
 *
 * @author szilas
 */
public class ERPredicateType extends ERObject {

    private String name;

    public ERPredicateType(String _name) {
        name = _name;
    }

    public String getName() {
        return name;
    }

}
