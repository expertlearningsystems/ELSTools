/*
 * Used to return the results of parse operation
 */
package ELSXMLTranslator;

import org.w3c.dom.Document;

/**
 *
 * @author skon
 */
public class ParseResults {
    
    public Document doc;         // The resulting DOM document
    public boolean success;     // The status of the translation
    public String message;      // The error message (if any) for the translation
    
}
