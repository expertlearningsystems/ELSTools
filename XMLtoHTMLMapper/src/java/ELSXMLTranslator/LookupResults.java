/*
 * Used to return the results of parse operation
 */
package ELSXMLTranslator;

import org.w3c.dom.Element;

/**
 *
 * @author skon
 */
public class LookupResults {
    
    public Element template;     // The resulting DOM document
    public boolean success;     // The status of the translation
    public String message;      // The error message (if any) for the translation
    
}
