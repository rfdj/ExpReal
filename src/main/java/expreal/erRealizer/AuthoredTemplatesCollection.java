package expreal.erRealizer;

import expreal.erElements.ERLanguage;
import org.tinylog.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Vector;

/**
 * AuthoredTemplates is a collection of texts as written by authors in the CSV file.
 * They are the templates that are realised.
 *
 * @author szilas
 * @author rfdj
 */
public class AuthoredTemplatesCollection {

    //This hash table contains, for a given predicate (or type, or narrative act), all possible "conditional texts" — each containing a condition and a sentence.
    Hashtable<String, Vector<ConditionalAnnotatedText>> textData;


    /**
     * Constructor.
     * Builds the internal text data from the spreadsheet.
     *
     * @param fileName the location of the CSV file with the authored texts
     * @param language the language for selecting the authoredTexts
     */
    public AuthoredTemplatesCollection(String fileName, ERLanguage language) throws IOException {
        InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

        if (fis == null) {
            Logger.tag("ATC").error("Could not get author file as resource: {}", fileName);
            return;
        }

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(fis, StandardCharsets.UTF_8));

        readCollection(bufferedReader, language);
    }


    /**
     * Constructor.
     * Builds the internal text data from the spreadsheet.
     *
     * @param file     the CSV file with the authored texts
     * @param language the language for selecting the authoredTexts
     */
    public AuthoredTemplatesCollection(File file, ERLanguage language) throws IOException {
        if (file != null) {
            if (!file.exists()) {
                Logger.tag("ATC").error("Could not get author file as file: {}", file.getAbsolutePath());
                return;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            readCollection(bufferedReader, language);
        }
    }

    /**
     * Read the authored texts file.
     *
     * @param bufferedReader the reader containing the authored texts file
     */
    public void readCollection(BufferedReader bufferedReader, ERLanguage language) {
        String currentLine;
        int lineIndex = 0;
        String currentKey;
        ConditionalAnnotatedText currentValue;
        textData = new Hashtable<>();


        try {
            while ((currentLine = bufferedReader.readLine()) != null) {
                lineIndex++;
                String[] columns = currentLine.split(";");

                //empty line (columns contain nothing or # characters)
                if (currentLine.replace(";", "").replace("#", "").isEmpty())
                    continue;

                if (columns[0].startsWith("#")) //commented line
                    continue;

                if (columns.length != 5) { //
                    Logger.tag("ATC").error("Problem in author file: line {} has {} columns instead of 5.", lineIndex, columns.length);
                } else {
                    // Columns: 0 Narrative act; 1 Condition; 2 EN; 3 FR; 4 NL
                    currentKey = columns[0];
                    String annotatedText = columns[language.ordinal() + 2];

                    if (annotatedText.equals("")) {
                        Logger.tag("ATC").error("No annotated text for language '{}' at line {}: {}", language, lineIndex, currentLine);
                        continue;
                    }
                    // If the cell starts with #, another narrative act with equal or more general conditions will be used.
                    if (annotatedText.startsWith("#")) {
                        continue;
                    }

                    currentValue = new ConditionalAnnotatedText(columns[1], annotatedText);

                    Vector<ConditionalAnnotatedText> condTexts = textData.get(currentKey);
                    if (condTexts == null) {
                        condTexts = new Vector<>();
                        condTexts.add(currentValue);
                        textData.put(currentKey, condTexts);
                    } else {
                        condTexts.add(currentValue);
                    }
                }
            }
        } catch (IOException e) {
            Logger.tag("ATC").error("Could not read authored texts file. Exception: {}", e);
        }
    }


    /**
     * Returns all conditional texts corresponding to a given entry (e.g. name of a narrative act)
     *
     * @param entry the entry to look up
     * @return a vector with all annotated texts. null if nothing found.
     */
    public Vector<ConditionalAnnotatedText> getConditionalAnnotatedTexts(String entry) {
        return textData.get(entry);
    }


    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String key : textData.keySet()) {
            s.append(key);
            s.append(textData.get(key));
        }
        return s.toString();
    }


}
