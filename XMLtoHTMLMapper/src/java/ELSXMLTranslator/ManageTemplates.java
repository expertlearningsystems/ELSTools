/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ELSXMLTranslator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author skon
 */
public class ManageTemplates {

    DocumentBuilderFactory dbf;
    DocumentBuilder DOC_BUILDER;

    // Where the templates are stored
    HashMap<String, Element> TemplateMap;
    
    XPath xPath = XPathFactory.newInstance().newXPath();
    
    XPathExpression scriptPath;
    XPathExpression templatesTemplatePath;
    XPathExpression dotTypePath;
    XPathExpression dotTemplatePath;
    XPathExpression dotIDPath;
    XPathExpression dotItemPath;
    XPathExpression dotLocationPath;
    XPathExpression dotLoadSheetPath;
    XPathExpression dotSelfSubmitPath;
    
    static Pattern comments;;
    

    public ManageTemplates() {
        super();
        TemplateMap = new HashMap<String, Element>();
        
        try {
            // compile xPath extrasion for reuse
            scriptPath = xPath.compile("//script");
            templatesTemplatePath = xPath.compile("/templates/template");
            dotTypePath = xPath.compile("./@type");
            dotTemplatePath = xPath.compile("./@template");
            dotIDPath = xPath.compile("./@id");
            dotItemPath = xPath.compile("//item");
            dotLocationPath = xPath.compile("./@location");
            dotLoadSheetPath = xPath.compile("./@loadsheet");
            dotSelfSubmitPath = xPath.compile("./@selfsubmit");
            //    = xPath.compile("");
            
            
        } catch (XPathExpressionException ex) {
            Logger.getLogger(ManageTemplates.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // compile regx patterns
        comments = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);

    }

    public void stripJavaComments(Document doc) {

        try {
            //Get the node
            NodeList nodeList = (NodeList) scriptPath.evaluate(doc, XPathConstants.NODESET);

            for (int j = 0; j < nodeList.getLength(); j++) {
                Node scriptNode = nodeList.item(j);
                //System.out.println("Strip Comments:"+XMLTemplateApplier.prettyPrint(scriptNode));
                NodeList textList = scriptNode.getChildNodes();

                //Traverse the list, expanding paths (but not recursing into paths), copying text, and recuringing into elements.
                for (int i = 0; i < textList.getLength(); i++) {
                    Node textNode = textList.item(i);
                    if (textNode.getNodeType() == Node.TEXT_NODE) {
                        String script = textNode.getNodeValue();
                        String newScript = comments.matcher(script).replaceAll("");
                        //.replaceAll("[^:]//.*", "");  // The removes // comments, but also //paths.
                        textNode.setNodeValue(newScript);
                        //System.out.println("Script:" + script + " : " + newScript);
                    } else if (textNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                        String script = textNode.getNodeValue();
                        String newScript = comments.matcher(script).replaceAll("").
                                replaceAll("[^:]//.*", "");
                        textNode.setNodeValue(newScript);
                        //System.out.println("Script: (CDATA)" + script + " : " + newScript);
                    }
                }
            }

        } catch (XPathExpressionException ex) {
            System.out.println("Error removing comments:" + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(ManageTemplates.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * loadTemplates()
     *
     * @param directory - Directory containing template XML documents
     *
     * This function looks through a directory for .xml files and passes them on
     * the loadTemplats for actual loading
     */
    public String loadTemplates(String directory) {
        //System.out.println("Load templates!!!"+directory);
        File XMLTemplateDirectory = new File(directory);
        File[] templatesList = XMLTemplateDirectory.listFiles();
        String result = "";

        if (templatesList == null) {
            result = "Templates Directory empty:" + directory;
            System.out.println(result);

            return result;
        }

        String desiredExtension = "xml";
        String extension;
        int startOfExtension;

        Document templates;

        // Create Doc builder
        dbf = DocumentBuilderFactory.newInstance();

        // Ignore any comments in the XML - do not send them to the DisplayClient.
        dbf.setIgnoringComments(true);
        try {
            DOC_BUILDER = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            //Logger.getLogger(ManageTemplates.class.getName()).log(Level.SEVERE, null, ex);
            result = "XML Parsing Error: " + ex.toString();
            System.out.println(result);
            return result;
        }

        // load the template files one at a time, and put the templates into a hashmap
        for (File templatesList1 : templatesList) {
            // Make sure that we are only dealing with files that end in .xml
            String fileName = templatesList1.getName();
            startOfExtension = fileName.lastIndexOf(".");
            if (startOfExtension != -1) {
                extension = fileName.substring(startOfExtension + 1);
                fileName = fileName.substring(0, startOfExtension);
            } else {
                extension = "noExtension";
            }
            if (extension.equals(desiredExtension)) {
                try {
                    templates = DOC_BUILDER.parse(templatesList1);

                    stripJavaComments(templates);
                    System.out.println("Parsing: " + templatesList1.getName());
                    // Load templates from this file into the template hashmap
                    loadTemplateFile(templates);
                } catch (Exception se) {
                    result = "XML Parsing Error(" + templatesList1.getName() + "): " + se.toString();
                    System.out.println(result);
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * loadTemplatesFile()
     *
     * @param templates - a Single Document of templates
     *
     * This function traverses the Document, looking looking at each template If
     * it finds a template attribute, it uses that as the key If not, it uses
     * the type attribute as the key
     *
     * The templates are loaded in the TemplateMap for this instance of this
     * class
     *
     * Duplicates log an error, and are discarded
     *
     */
    public void loadTemplateFile(Document templates) throws Exception {
        String functionName = "loadTemplateItems()";
        //debug.startFunction(functionName);

        try {
            NodeList templateNodes;

            //Get the templates
            templateNodes = (NodeList) templatesTemplatePath.evaluate(templates, XPathConstants.NODESET);

            for (int i = 0; i < templateNodes.getLength(); i++) {

                Node currentNode = templateNodes.item(i);
                String typeValue = dotTypePath.evaluate(currentNode);
                //System.out.println("Template found: " + typeValue + ":" + typeValue.length());
                if (typeValue.length() > 1) {
                    if (!TemplateMap.containsKey(typeValue)) {
                        TemplateMap.put(typeValue, (Element) currentNode);
                    } else {
                        System.out.println("Template Error: " + typeValue + " Duplicate");
                    }
                } else {
                    System.out.println("Template type name empty ");
                }

            }
        } catch (Exception ex) {
            System.out.println("Exception in " + functionName);
            System.out.println(ex.toString());

            //debug.logError(functionName,ex);
            throw ex;
        }
        //debug.endFunction(functionName);
    }

    public Document getOwnerDocument(Node node) {
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            return (Document) node;
        }
        return node.getOwnerDocument();
    }

    /**
     * LookupXMLTemplateString()
     *
     * @param XML - subtree as a string passThrough - If there is no match,
     * simply pass through the source XML
     *
     * Given an chunk of XML, try to look up and return the best matching
     * template This lookup is agnostic as to what the XML tags are, or where
     * the matching keys are It simply first looks for a "template" attribute,
     * and tries to find a template with that
     *
     */
    public LookupResultsString lookupXMLTemplateString(String XML) {
        LookupResultsString results = new LookupResultsString();
        LookupResults results2;
        results.success = false;
        results.message = "No Matching Template";
        results.template = "";
        try {
            ParseResults xmlResult = XMLTemplateApplier.loadXML(XML);

            if (xmlResult.success) {
                Document xmlDoc = xmlResult.doc;
                Element xml = xmlDoc.getDocumentElement();
                results2 = lookupXMLTemplate(xml);
                results.message = results2.message;
                results.success = results2.success;
                if (results2.success) {
                    results.template = XMLTemplateApplier.prettyPrint(results2.template);
                    results.success = true;
                    return results;
                }
                return results;

            } else {
                results.message = xmlResult.message;
                return results;
            }

        } catch (Exception ex) {
            System.out.println("Template lookup Error: " + ex.getMessage());
            results.message = "Template lookup Error: " + ex.getMessage();

        }
        return results;
    }

    /**
     * LookupXMLTemplate()
     *
     * @param xml - subtree as a string, If there is no match, simply pass
     * through the source XML
     *
     * Given an chunk of XML, try to look up and return the best matching
     * template This lookup is agnostic as to what the XML tags are, or where
     * the matching keys are It simply first looks for a "template" attribute,
     * and tries to find a template with that
     *
     */
    public LookupResults lookupXMLTemplate(Node xml) {
        LookupResults results = new LookupResults();
        results.success = false;
        results.message = "No Matching Template";
        results.template = null;
        String templateKey = "";
        Node template;

        try {
            //System.out.println("Template lookup:"+XMLTemplateApplier.prettyPrint(xml));

            // For debugging, lookup ID
            String id = dotIDPath.evaluate(xml);

            templateKey = dotTemplatePath.evaluate(xml);
            if (templateKey.length() < 1) {
                templateKey = dotTypePath.evaluate(xml);
            }
            Node templateCode = null;
            if (templateKey.length() > 0 && TemplateMap.containsKey(templateKey)) {
                template = TemplateMap.get(templateKey);
                NodeList templateChildren = template.getChildNodes();
                // There may be whitespace before template, so find actual template
                for (int i = 0; i < templateChildren.getLength(); i++) {

                    if (templateChildren.item(i).getNodeType() == Node.ELEMENT_NODE) {

                        templateCode = templateChildren.item(i);
                        //System.out.println("Template found:"+XMLTemplateApplier.prettyPrint(templateCode));
                        break;
                    }

                }
                if (templateCode == null) {
                    System.out.println("Error: Template contains no content:" + templateKey);
                    results.message = "Template contains no content:" + templateKey;
                    return results;
                }
                //System.out.println("Template (" + templateKey + ") lookup for: " + id);
                // Make a copy of the node we are using
                results.template = (Element) templateCode.cloneNode(true);
                results.success = true;
                results.message = "";

            } else {
                //System.out.println("Template not found:" + templateKey);
                results.template = null;
                results.success = false;
                results.message = "Template not found:" + templateKey;
            }

        } catch (Exception ex) {
            System.out.println("Template lookup Error: " + ex.getMessage());
            results.message = "Template lookup Error: " + ex.getMessage();

        }
        return results;
    }

    // Apply templates to all items in sheet.
    // output a list of HTML tags in sheet.
    // If there is a location on the sheet, this is the defaut for each item in the sheet
    // For each item, if it has a location, this location over-rides the sheet location
    // The location is added to the HTML tag for each item processed  (if there is a location)
    public SheetTranslateResults ApplyTemplatesToSheet(Element sheet) {
        //System.out.println("ApplyTemplatesToSheet:");
        SheetTranslateResults result = new SheetTranslateResults();
        result.output = null;
        result.translateMessage = "Error: Bad XML";
        result.success = false;

        //Get the nodes

        NodeList arrayNodes;
        try {
            Node sheetLocation = (Node) dotLocationPath.evaluate(sheet, XPathConstants.NODE);

            arrayNodes = (NodeList) dotItemPath.evaluate(sheet, XPathConstants.NODESET);
            //arrayNodes = sheet.getChildNodes();

            Document doc = sheet.getOwnerDocument();

            for (int j = 0; j < arrayNodes.getLength(); j++) {
                Element item = (Element) arrayNodes.item(j);
                //System.out.println("Node:" + item.getNodeName());

                // See if this item is a loadsheet
                // <item type="LoadSheet">XYZ ABC</item>
                Node typeAttribute = (Node) dotTypePath.evaluate(item, XPathConstants.NODE);
                //System.out.println("Type:" + typeAttribute.getNodeValue());

                // See is this is a loadsheet item
                if (typeAttribute != null && typeAttribute.getNodeValue().equalsIgnoreCase("LoadSheet")) {
                    //System.out.println("Found loadsheet");
                    // rename this element to "loadsheet" and pass it along
                    doc.renameNode(item, null, "loadsheet");
                    // We are down with this item, go to next

                    // Other items translated
                } else {
                    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
                    Node tempItem = item.cloneNode(true);
                    Document tempDoc = domBuilder.newDocument();
                    tempDoc.adoptNode(tempItem);
                    tempDoc.appendChild(tempItem);
                    //System.out.println("PerNested:"+XMLTemplateApplier.prettyPrint(tempItem));
                    Node newItem = ELSXMLTranslator.XMLTemplateApplier.TranslateNested(tempItem,this);
                    //System.out.println("Nested:"+XMLTemplateApplier.prettyPrint(newItem));
                    
                    //LookupResults templateResult = lookupXMLTemplate(item);
                    //if (templateResult.success) {
                    //    TranslateResults translateResult = ELSXMLTranslator.XMLTemplateApplier.Translate(item, templateResult.template);
                    //    if (translateResult.success) {
                            // replace node with new node
                    //        Node translatedNode = translateResult.output;

                            Element HTMLNode = doc.createElement("html");

                            // See if this item has a location
                            Node itemLocation = (Node) dotLocationPath.evaluate(item, XPathConstants.NODE);
                            if (itemLocation != null) {
                                String locationStr = itemLocation.getNodeValue();
                                if (!locationStr.isEmpty()) {
                                    HTMLNode.setAttribute("location", locationStr);
                                }
                                // Else if there was a location on the sheet, place it on the html tags
                            } else if (sheetLocation != null) {
                                String locationStr = sheetLocation.getNodeValue();
                                HTMLNode.setAttribute("location", locationStr);
                            }

                            // See if item has a loadSheet but NOT a selfsubmit="yes"
                            // selfsubmits are processed at the HTML layer
                            Node selfSubmit = (Node) dotSelfSubmitPath.evaluate(item, XPathConstants.NODE);
                            if (selfSubmit != null) {
                                String selfSubmitStr = selfSubmit.getNodeValue();
                                if (!selfSubmitStr.equalsIgnoreCase("yes")) {
                                    Node loadSheet = (Node) dotLoadSheetPath.evaluate(item, XPathConstants.NODE);
                                    if (loadSheet != null) {
                                        String loadSheetStr = loadSheet.getNodeValue();
                                        HTMLNode.setAttribute("loadsheet", loadSheetStr);
                                    }
                                }
                            }

                            //HTMLNode = (Element) doc.adoptNode(HTMLNode);
                            Element translatedNode = (Element) doc.adoptNode(newItem);
                            HTMLNode.appendChild(translatedNode);

                            sheet.replaceChild(HTMLNode, item);
                            result.output = sheet;
                            result.success = true;
                            result.translateMessage = "success";

                        /*} else {
                            String message = "XML Template Apply error:" + translateResult.translateMessage;
                            System.out.println(message);
                            result.translateMessage = message;

                            Element textNode = doc.createElement("HTML");
                            Text text = doc.createTextNode("Error rendering item");
                            textNode.appendChild(text);
                            textNode = (Element) doc.importNode(textNode, true);
                            sheet.replaceChild(textNode, item);
                        }*/
                    //}
                }
            }
            result.output = sheet;
            result.success = true;
            result.translateMessage = "okay";
            //System.out.println("NEW SHEET:"+XMLTemplateApplier.prettyPrint(sheet));
            return result;

        } catch (XPathExpressionException ex) {
            System.out.println("Template lookup Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Template transform: " + ex.getMessage());
        }

        return result;
    }

}
