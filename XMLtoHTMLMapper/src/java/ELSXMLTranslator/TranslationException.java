/*
 * James Skon, 2015
 * Expert Learning System
 */
package ELSXMLTranslator;

/**
 *
 * @author skon
 */

 
public class TranslationException {
    public static void main(String[] a){
        try{
            TranslationException.myTest(null);
        } catch(PathLookupFailure mae){
            System.out.println("Inside catch block: "+mae.getMessage());
        }
    }
     
    static void myTest(String str) throws PathLookupFailure{
        if(str == null){
            throw new PathLookupFailure("String val is null");
        }
    }
}
 
class PathLookupFailure extends Exception {
 
    private String message = "No such path in XML:";
 
    public PathLookupFailure() {
        super();
    }
 
    public PathLookupFailure(String message) {
        super(message);
        this.message = message;
    }
 
    public PathLookupFailure(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }
}