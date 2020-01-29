package expreal.erElements;

import java.util.HashMap;

/**
 * Arguments are used to define $variables in the templates, for instance ERArgument("task", "haveADrink")
 * tells ExpReal to replace any mention of '$task' with the value of the 'haveADrink' template.
 */
public class ERArgument extends ERObject {

    String name;
    String value;
    HashMap<String, String> attributes;

    public ERArgument(String _name, String _value) {
        name = _name;
        value = _value;
        attributes = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void addAttribute(String name, String value) {
        attributes.put(name, value);
    }

    public String getAttribute(String name) {
        return attributes.get(name);
    }

    public HashMap<String, String> getAttributes() {
        return attributes;
    }

    public String toString() {
        return name + ":" + value;
    }
}
