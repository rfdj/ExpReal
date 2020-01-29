package expreal.erRealizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Tools {

    /**
     * An extension of String.indexOf to a regular expression.
     *
     * @param string      haystack
     * @param regexpMatch regexp needle
     * @return index if not, -1 if not found
     */
    static int indexOf(String string, String regexpMatch, int fromIndex) {
        Pattern p = Pattern.compile(regexpMatch);
        Matcher m = p.matcher(string);
        if (m.find(fromIndex)) {
            return m.start();
        }
        return -1;
    }

}
