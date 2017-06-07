/*
 * Used to return the results of translate operation
 */
package ELSXMLTranslator;

import org.w3c.dom.Element;

/**
 *
 * @author skon
 */
public class TranslateResults {
    
    public Element output;         // The String resulting from the translations
    public boolean success;     // The status of the translation
    public String translateMessage;      // The error message (if any) for the translatio
        
}
