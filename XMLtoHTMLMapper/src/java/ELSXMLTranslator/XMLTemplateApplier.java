/*
 * Author: James Skon, 2015
 * Expert Learning Systems @2015
 @ MVNU
 */
package ELSXMLTranslator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import javax.xml.parsers.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author skon
 */
public class XMLTemplateApplier {

    static String templateErrors = "";

    public enum arrayType {

        none, integer, string
    };

    static XPath xPath = XPathFactory.newInstance().newXPath();

    static XPathExpression dotRepeat;

    static public void init() {
        try {
            dotRepeat = xPath.compile("./@repeat");

        } catch (XPathExpressionException e) {
            System.out.println("XMLTemplateApplier init Error: " + e.getMessage());
        }
    }

    // Load an XML Document
    static public ParseResults loadXML(String XMLString) {

        ParseResults result = new ParseResults();
        result.doc = null;
        result.success = true;
        result.message = "Parse Successful";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setEncoding("US-ASCII");
            is.setCharacterStream(new StringReader(XMLString));

            result.doc = db.parse(is);
            return result;

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            result.success = false;
            result.message = e.getMessage();
            return result;
        }
    }

    public static final String prettyPrint(Node xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "US-ASCII");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        tf.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, "script");
        tf.setOutputProperty(OutputKeys.METHOD, "xml");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        //System.out.println(out.toString());
        return out.toString();
    }

    static public String convertXMLToString(Document doc) throws TransformerConfigurationException, TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        //System.out.println("XML IN String format is: \n" + writer.toString());
        return writer.toString();
    }

    // Tanslate HTML or XML Template using XML data
    static public TranslateResults Translate(Node XML, Element template) {

        TranslateResults translateResult = new TranslateResults();
        translateResult.output = null;
        translateResult.success = true;
        translateResult.translateMessage = "";
        templateErrors = "";

        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

            Node xml = XML.cloneNode(true);
            Document xmlDoc = domBuilder.newDocument();
            xmlDoc.adoptNode(xml);
            xmlDoc.appendChild(xml);

            Node myTemplate = template.cloneNode(true);
            Document myDoc = domBuilder.newDocument();
            myDoc.adoptNode(myTemplate);
            myDoc.appendChild(myTemplate);

            //System.out.println("XML:"+prettyPrint(xml));
            //System.out.println("Template:"+prettyPrint(myTemplate));
            replaceTemplateSlots((Node) xml, (Node) myTemplate);
            //System.out.println("Result:"+prettyPrint(template));
            translateResult.output = (Element) myTemplate;
            translateResult.translateMessage = templateErrors;

            return translateResult;

        } catch (PathLookupFailure e) {
            System.out.println("Path Error: " + e.getMessage());
            e.printStackTrace();
            translateResult.translateMessage = "Path Error: " + e.getMessage();

        } catch (Throwable e) {
            System.out.println("Error Translating!! " + e.getMessage());
            e.printStackTrace();
            translateResult.translateMessage = "Translate Error:" + e.getMessage();
        }
        translateResult.success = false;
        translateResult.translateMessage = "Error in tranlation";
        return translateResult;
    }

    // Translate HTML or XML Template using XML data
    static public TranslateResultsString TranslateString(String XMLString, String TemplateString) {

        TranslateResultsString translateResult = new TranslateResultsString();
        translateResult.output = "";
        translateResult.success = true;
        translateResult.translateMessage = "";
        translateResult.XMLMessage = "";
        translateResult.templateMessage = "";
        templateErrors = "";

        //System.out.println("in:"+TemplateString);
        try {
            //  Load the XML
            ParseResults xml = loadXML(XMLString);
            if (!xml.success) {
                translateResult.success = false;
                translateResult.XMLMessage = "XML Parse Error: " + xml.message;
            }

            // Load the template
            ParseResults template = loadXML(TemplateString);
            if (!template.success) {
                translateResult.success = false;
                translateResult.templateMessage = "Template Parse Error: " + template.message;
            }

            if (!translateResult.success) {
                return translateResult;
            }

            Node templateRoot = template.doc.getDocumentElement();
            Node XMLRoot = xml.doc.getDocumentElement();

            replaceTemplateSlots(XMLRoot, templateRoot);

            String finalHTML = prettyPrint(template.doc);
            //String finalHTML = convertXMLToString(template.doc);

            //System.out.println("out:"+finalHTML);
            translateResult.output = finalHTML;
            translateResult.templateMessage = templateErrors;

            return translateResult;

        } catch (PathLookupFailure e) {
            System.out.println("Path Error: " + e.getMessage());
            translateResult.templateMessage = "Path Error: " + e.getMessage();

        } catch (Exception e) {
            System.out.println("Translate Error!! " + e.getMessage());
            translateResult.translateMessage = "Translate Error:" + e.getMessage();
        }

        return translateResult;
    }

    // Translate HTML or XML trees  using templates
    static public Node TranslateNested(Node XML, ManageTemplates templates) throws Exception {

        // First visit the children
        // Get the list of children
        NodeList nodeList = XML.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            Node newNode = TranslateNested(currentNode, templates);
            Node parent = currentNode.getParentNode();
            Document doc = currentNode.getOwnerDocument();
            newNode = doc.importNode(newNode, true);
            parent.replaceChild(newNode, currentNode);
        }

        if (XML.getNodeType() != Node.TEXT_NODE) {

            LookupResults XMLTemplate = templates.lookupXMLTemplate(XML);
            if (XMLTemplate.success) {
                //System.out.println("Item:" + prettyPrint(XML));
                //System.out.println("Template:" + prettyPrint(XMLTemplate.template));
                TranslateResults translateResult = Translate(XML, XMLTemplate.template);
                if (translateResult.success) {
                    Node newNode = translateResult.output;
                    return newNode;
                    // replace node with new node
                    //Node parent = XML.getParentNode();
                    //Document doc = XML.getOwnerDocument();
                    //newNode = doc.importNode(newNode, true);
                    //parent.replaceChild(newNode, XML);
                    //XML = translateResult.output;
                    //System.out.println("Translation:" + prettyPrint(parent));
                } else {
                    System.out.println("XML Template Apply error" + translateResult.translateMessage);
                }
            }
        }
        return XML;
    }

    // Translate HTML or XML strings using templates
    static public TranslateResultsString TranslateNestedString(String XMLString, ManageTemplates templates) {

        TranslateResultsString translateResult = new TranslateResultsString();
        translateResult.output = "";
        translateResult.success = true;
        translateResult.translateMessage = "";
        translateResult.XMLMessage = "";
        translateResult.templateMessage = "";
        templateErrors = "";

        //System.out.println("input:"+XMLString);
        try {
            //  Load the XML
            ParseResults xml = loadXML(XMLString);
            if (!xml.success) {
                translateResult.success = false;
                translateResult.XMLMessage = "XML Parse Error: " + xml.message;
            }

            if (!translateResult.success) {
                return translateResult;
            }

            TranslateNested(xml.doc, templates);

            String finalHTML = prettyPrint(xml.doc);
            //String finalHTML = convertXMLToString(template.doc);

            //System.out.println("TranslateNestedString:" + finalHTML);
            translateResult.output = finalHTML;
            translateResult.templateMessage = templateErrors;

            return translateResult;

        } catch (Exception e) {
            System.out.println("Translate Error!! " + e.getMessage());
            translateResult.translateMessage = "Translate Error:" + e.getMessage();
        }

        return translateResult;
    }

    public static void TemplateError(String errorMess) {
        templateErrors += errorMess + "<br />";
    }

    // given an xpath and XML node, return a list of nodes.
    public static NodeList getNodesFromPath(Node XML, String path) throws PathLookupFailure {
        try {
            /*try {
             System.out.println("getNodesFromPath:"+path+":"+prettyPrint(XML));
             } catch (Exception ex) {
             Logger.getLogger(XMLTemplateApplier.class.getName()).log(Level.SEVERE, null, ex);
             }*/

            //Get the node
            NodeList nodeList = (NodeList) xPath.compile(path).evaluate(XML, XPathConstants.NODESET);

            if (nodeList != null) {
                return nodeList;
            } else {
                //System.out.println("Path Not Found:" + path);
            }
        } catch (XPathExpressionException ex) {
            TemplateError(ex.getMessage());
            System.out.println("xPath error(" + path + "):" + ex.getMessage());
        }
        return null;
    }

    // given an xpath function call and XML node, return a number.
    public static Double applyXPathFunction(Node XML, String path) throws PathLookupFailure {
        try {
            /*try {
             System.out.println("getNodesFromPath:"+path+":"+prettyPrint(XML));
             } catch (Exception ex) {
             Logger.getLogger(XMLTemplateApplier.class.getName()).log(Level.SEVERE, null, ex);
             }*/

            // get the function result
            Double result = (Double) xPath.compile(path).evaluate(XML, XPathConstants.NUMBER);

            if (result != null) {
                return result;
            } else {
                System.out.println("XPath function not found:" + path);
            }
        } catch (XPathExpressionException ex) {
            TemplateError(ex.getMessage());
            System.out.println("xPath error(" + path + "):" + ex.getMessage());
        }
        return null;
    }

    private static arrayType checkPathForArrayExpansion(String path) {

        if (path.indexOf("[*]") != -1) {
            //System.out.println("Integer Array:"+path);
            return arrayType.integer;
        } else if (path.indexOf("[\"*\"]") != -1) {
            //System.out.println("String Array:"+path);
            return arrayType.string;
        }
        return arrayType.none;
    }

    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            javax.xml.transform.Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    // Get the current Depth of a node in the tree.
    private static String findPath(Node node) {

        String path = node.getNodeName();
        while (node.getParentNode() != null) {
            node = node.getParentNode();
            path = node.getNodeName() + "/" + path;
        }
        return path;
    }

    // create an array from the nodes that match this xpath
    public static String getArrayFromPath(Node XML, String path, arrayType type) throws PathLookupFailure {
        String result = "";
        if (type == arrayType.integer) {
            path = path.replace("[*]", "");
        } else if (type == arrayType.string) {
            path = path.replace("[\"*\"]", "");
        } else {
            TemplateError("Error in path: " + path);
            return "";
        }
        //System.out.println("Trimmed String Array:"+path);

        try {

            NodeList arraynodes;
            String comma = "";

            //Get the nodes
            arraynodes = (NodeList) xPath.compile(path).evaluate(XML, XPathConstants.NODESET);

            int nodeDepth = 0;

            if (arraynodes != null) {
                for (int i = 0; i < arraynodes.getLength(); i++) {

                    //TemplateError("O:"+Integer.toString(i));
                    Node currentNode = arraynodes.item(i);

                    //System.out.println("ArrayPath:" + findPath(currentNode));
                    NodeList nodeList = currentNode.getChildNodes();
                    String textString = "";
                    for (int j = 0; j < nodeList.getLength(); j++) {
                        Node textNode = nodeList.item(j);

                        if (textNode.getNodeType() == Node.TEXT_NODE) {
                            String text = textNode.getNodeValue();
                            //TemplateError("i:"+Integer.toString(j));
                            //TemplateError("Lookup for "+path+" Found:|"+text+"|");

                            textString += text;
                        }
                    }
                    textString = textString.trim();
                    if (type == arrayType.integer) {
                        result += comma + textString;
                    } else {
                        result += comma + "\"" + textString + "\"";
                    }
                    comma = ",";
                }
                result = "[" + result + "]";

            } else {
                // = "<span class=\".bg-danger\">" + path + "<span>";
                //result = path;
                String error = "Path: " + path + " not found.";
                TemplateError(error);
                System.out.println(error);

                //throw new PathLookupFailure(path);
            }
            //TemplateError("result: " + result); 
            return result;

        } catch (XPathExpressionException ex) {
            System.out.println("XPath Error!! " + ex.getMessage());
            TemplateError(ex.getMessage());
        }

        return result;

    }

    // Get the (first) text that matches this xpath
    public static String getTextFromPath(Node XML, String path) throws PathLookupFailure {

        // Check if this is an array lookup, if so call other routine to get string, then return
        arrayType type = checkPathForArrayExpansion(path);
        if (type != arrayType.none) {
            return getArrayFromPath(XML, path, type);
        }
        // BK: Check if this is an XPath function
        if (path.indexOf("...") == 0) { // call XPath function
            Double num = applyXPathFunction(XML, path.substring(3));
            if (num != null) {
                //System.out.println("XPath function " + path + " returned " + num);
                return "" + num;
            } else {
                System.out.println("XPath function error: " + path + " returned null.");
                return "";
            }
        }

        String result = "";
        try {

            Node node;

            //Get the node
            node = (Node) xPath.compile(path).evaluate(XML, XPathConstants.NODE);

            if (node != null) {
                NodeList nodeList = node.getChildNodes();

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node currentNode = nodeList.item(i);
                    if (currentNode.getNodeType() == Node.TEXT_NODE) {
                        String textNode = currentNode.getNodeValue();
                        //TemplateError("Lookup for "+path+" Found:|"+textNode+"|");

                        result += textNode;
                    }
                }

            } else {
                // = "<span class=\".bg-danger\">" + path + "<span>";
                //result = path;
                String error = "Path: " + path + " not found.";
                TemplateError(error);
                //System.out.println(error);
                //TemplateError("Path: " + path + " not found.");

                //throw new PathLookupFailure(path);
            }
            //TemplateError("result: " + result); 
            return result;

        } catch (XPathExpressionException ex) {
            System.out.println("XPath Error!! " + ex.getMessage());
            TemplateError(ex.getMessage());
        }
        //System.out.println("Here!"+result);ls -la

        return result;

    }

    // remove attribute from node by name
    public static void removeAttribute(Node node, String attName) {
        NamedNodeMap attributes = node.getAttributes();
        attributes.removeNamedItem(attName);
    }

    // Check for repeating node, if so return associated XML path 
    // also remove attribute
    //
    public static String checkForRepeating(Node node) throws XPathExpressionException {
        //NamedNodeMap attributes = node.getAttributes();
        String path = "";

        path = (String) xPath.compile("./@repeat").evaluate(node, XPathConstants.STRING);

        if (path.length() > 0) {
            removeAttribute(node, "repeat");
        }

        return path;
    }

    // Main (recursive) routine for filling a template in from the XML
    // Dealing with repeating elements:
    //  1. Check each element (before descending to it) to see if it has a 'repeat="path"' attribute. 
    //     Clone the template from this node, passing a clone rather then the original down.  remove the original from the tree.
    //  2. For the XML parameter use the repeat path, indexed by path[n], starting with n=1, and incrementing for each intance, 
    //     as the XML origin for recursive calls to the repeating element
    //  3. Upon return from each call, splice the new element in the HTML tree as siblings to the previous
    //
    public static void replaceTemplateSlots(Node XML, Node node) throws Exception {

        Document templateDoc = node.getOwnerDocument();
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = domFactory.newDocumentBuilder();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String elementNode = node.getNodeName();
            //TemplateError("Node:" + elementNode + ":" + XML.getNodeName());

            //System.out.println("Found Element: " + elementNode);
            //calls this method for all the children which is Element
            replaceAttributeSlots(XML, node);
        }
        //Get the next list of children
        NodeList nodeList = node.getChildNodes();

        // A place to collect the the modified children of this node
        List<Node> elementContents = new ArrayList<Node>();

        //Traverse the list, expanding paths (but not recursing into paths), copying text, and recuringing into elements.
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList newNodes = null;
            Boolean change = false;
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.TEXT_NODE) {
                String textNode = currentNode.getNodeValue();
                // Split the string since the Replacement path may be in the string
                String[] textNodeArray = textNode.split(" ");
                String collectText = "";
                String ws = "";
                for (int j = 0; j < textNodeArray.length; j++) {
                    // If this is a path, do the replacement
                    String textElement = textNodeArray[j].trim();
                    //System.out.println("node:"+textElement);

                    if (textElement.indexOf("/") == 0 || textElement.indexOf(".") == 0) {

                        //First at any preceeding text as a single node to the elements contents
                        if (collectText.length() > 0) {
                            elementContents.add(templateDoc.createTextNode(collectText));
                            collectText = "";
                            ws = "";
                        }

                        if (textElement.indexOf("...") == 0) { // call XPath function
                            Double num = applyXPathFunction(XML, textElement.substring(3));
                            if (num != null) {
                                elementContents.add(templateDoc.createTextNode("" + num));
                                //System.out.println("XPath function " + textElement + " returned " + num);
                            }
                        } else {
                            // Find the replacement nodes
                            Node parent = getNodesFromPath(XML, textElement).item(0);
                            if (parent != null) {
                                newNodes = parent.getChildNodes();

                                for (int k = 0; k < newNodes.getLength(); k++) {

                                    elementContents.add(templateDoc.importNode(newNodes.item(k).cloneNode(true), true));
                                }
                            }
                        }

                    } else {
                        //System.out.println("Added to text:"+textElement);
                        collectText += ws + textElement;
                        ws = " ";
                        //elementContents.add(templateDoc.createTextNode(textElement));
                    }

                }
                // put out any remaining text nodes
                if (collectText.length() > 0) {
                    elementContents.add(templateDoc.createTextNode(collectText));

                }
            } else if (currentNode.getNodeType() == Node.CDATA_SECTION_NODE) {
                String textNode = currentNode.getNodeValue();
                Boolean replace = false;
                // Split the string since the Replacement path may be in the string
                String[] textNodeArray = textNode.split(" ");
                String collectText = "";
                String ws = "";
                for (int j = 0; j < textNodeArray.length; j++) {
                    // If this is a path, do the replacement
                    String textElement = textNodeArray[j].trim();
                    if (textElement.length() > 0) {

                        if (textElement.indexOf("/") == 0 || textElement.indexOf(".") == 0) {
                            //System.out.println("path:" + textElement);
                            // Find the replacement strings
                            String replacementString = getTextFromPath(XML, textElement);

                            if (replacementString.length() > 0) {

                                collectText += replacementString;
                                replace = true;
                            }

                        } else {
                            //System.out.println("Added to text:"+textElement);
                            collectText += ws + textElement;
                            ws = " ";
                            //elementContents.add(templateDoc.createTextNode(textElement));
                        }

                    }

                }
                if (replace) {
                    //CDATASection newSection;
                    //newSection = currentNode.getOwnerDocument().createCDATASection(collectText);
                    //elementContents.add(newSection);
                    currentNode.setNodeValue(collectText);
                    elementContents.add(currentNode);
                    //System.out.println("Script:" + newSection.getNodeValue());
                } else {
                    elementContents.add(currentNode);
                }

                // Check if repeated node, and iterate through all siblings if so
            } else if (currentNode != null && currentNode.getNodeType() == Node.ELEMENT_NODE) {
                String repeatPath = checkForRepeating(currentNode);
                if (repeatPath.length() > 0) {
                    String repeatingNodeName = currentNode.getNodeName();
                    //System.out.println("Repeat:" + repeatPath + ":" + repeatingNodeName);

                    // For each matching set of data in the XML, create, fill slots, and add new element
                    //Get the repeated nodes
                    NodeList repeatedXMLList = getNodesFromPath(XML, repeatPath);

                    int repeatCount = repeatedXMLList.getLength();
                    // Make a copy of the node we are using
                    Node nodeTemplate = currentNode.cloneNode(true);
                    //Remove the template from the tree
                    node.removeChild(currentNode);

                    //Iterate through the list
                    for (int j = 0; j < repeatCount; j++) {
                        Node repeatedXML = repeatedXMLList.item(j);
                        //Document searchDoc = domBuilder.newDocument();
                        //searchDoc.adoptNode(repeatedXML);
                        //searchDoc.appendChild(repeatedXML);
                        //System.out.println("repeatedXML:"+prettyPrint(repeatedXML));

                        //clone the node in the template that we are repeating
                        Node clonedNode = nodeTemplate.cloneNode(true);

                        //Replace the slots in the cloned node with the values in this node from XML
                        replaceTemplateSlots(repeatedXML, clonedNode);
                        //Insert node next

                        //node.appendChild(clonedNode);
                        elementContents.add(clonedNode);
                    }
                    //break;

                } else {
                    replaceTemplateSlots(XML, currentNode);
                    elementContents.add(currentNode);

                }
            } else {
                elementContents.add(currentNode);
            }

        }
        // Rebuild the children nodes under the parent
        // Remove the current children

        while (node.hasChildNodes()) {
            node.removeChild(node.getFirstChild());
        }
        for (Node next : elementContents) {
            //System.out.println("Collect:"+next.getNodeName()+":"+next.getNodeValue());
            node.appendChild(next);
        }

    }

    public static void replaceAttributeSlots(Node XML, Node element) throws PathLookupFailure {
        //System.out.println("List attributes for node: " + element.getNodeName());
        // get a map containing the attributes of this node
        NamedNodeMap attributes = element.getAttributes();

        // get the number of nodes in this map
        int numAttrs = attributes.getLength();
        for (int i = 0; i < numAttrs; i++) {
            Boolean change = false;
            Attr attr = (Attr) attributes.item(i);

            String attrName = attr.getNodeName();
            // Ignore the repeat attribute - we don't replace that
            if (!attrName.equalsIgnoreCase("repeat")) {
                String attrValue = attr.getNodeValue();

                String[] attrArray = attrValue.split(" ");
                boolean isPath[] = new boolean[attrArray.length];
                // Store true for each path
                Arrays.fill(isPath, false);

                //lookup each word if a path
                String replacementString = "";
                for (int j = 0; j < attrArray.length; j++) {
                    if (attrArray[j].indexOf("/") == 0 || attrArray[j].indexOf(".") == 0) {
                        change = true;
                        String newAttr = getTextFromPath(XML, attrArray[j]);
                        attrArray[j] = newAttr;
                        isPath[j] = true; // mark as path
                    }

                }
                if (change) {
                    String ws = "";
                    for (int j = 0; j < attrArray.length; j++) {
                        if (!isPath[j] && j > 0 && !isPath[j - 1]) {
                            ws = " ";
                        } else {
                            ws = "";
                        }
                        replacementString += ws + attrArray[j];
                    }
                    replacementString = replacementString.trim();
                    // now replace the attribute value in tree
                    attributes.item(i).setNodeValue(replacementString);
                }

            }

        }
    }

}
