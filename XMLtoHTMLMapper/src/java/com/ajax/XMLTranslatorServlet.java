/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ajax;

import ELSXMLTranslator.TranslateResults;
import ELSXMLTranslator.LookupResults;
import ELSXMLTranslator.LookupResultsString;
import ELSXMLTranslator.ManageTemplates;
import ELSXMLTranslator.ParseResults;
import ELSXMLTranslator.SheetTranslateResults;
import ELSXMLTranslator.SheetTranslateResultsString;
import ELSXMLTranslator.TranslateResultsString;
import ELSXMLTranslator.XMLTemplateApplier;
import static ELSXMLTranslator.XMLTemplateApplier.loadXML;
import static ELSXMLTranslator.XMLTemplateApplier.prettyPrint;
import java.lang.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author skon
 */
@WebServlet(name = "XMLTranslatorServlet", urlPatterns = {"/XMLTranslatorServlet"})

public class XMLTranslatorServlet extends HttpServlet {

    private ServletContext context;
    TranslateResults resultHTML = new TranslateResults();
    static String XMLTEMPLATEDIR = "/home/LIMRI/LIMRIDev/XML/XMLTemplates/";
    static String HTMLTEMPLATEDIR = "/home/LIMRI/LIMRIDev/XML/HTMLTemplates/";
    static String XMLERROR = "";
    static String HTMLERROR = "";

    ManageTemplates XMLTemplates;
    ManageTemplates HTMLTemplates;

    Gson gson = new Gson();

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.context = config.getServletContext();

        // Load the template files
        XMLTemplates = new ManageTemplates();
        XMLERROR = XMLTemplates.loadTemplates(XMLTEMPLATEDIR);
        HTMLTemplates = new ManageTemplates();
        HTMLERROR = HTMLTemplates.loadTemplates(HTMLTEMPLATEDIR);
        System.out.println("Init XMLTranslatorServlet");
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //response.setContentType("text/html;charset=UTF-8");

        response.setContentType("application/json");

        String command = request.getParameter("command");
        System.out.println("Command::" + command);

        if (command.equalsIgnoreCase("xml2xml") || command.equalsIgnoreCase("xml2html")) {
            TranslateResultsString result = new TranslateResultsString();
            String template = request.getParameter("template");
            String XML = request.getParameter("xml");

            result = ELSXMLTranslator.XMLTemplateApplier.TranslateString(XML, template);

            if (!result.success) {
                result.success = false;
                result.translateMessage = "Translation failed:" + result.translateMessage;
                result.output = "";
            }

            String jsonResult = gson.toJson(result);

            //System.out.println("JSON::"+jsonResult);
            //try (PrintWriter out = response.getWriter()) {
            PrintWriter out = response.getWriter();
            out.println(jsonResult);
            //}
        } else if (command.equalsIgnoreCase("xml2xmlnested") || command.equalsIgnoreCase("xml2htmlnested")) {
            ManageTemplates templates;
            if (command.equalsIgnoreCase("xml2xmlnested")) {
                templates = XMLTemplates;
            } else {
                templates = HTMLTemplates;
            }
            TranslateResultsString result;
            
            String XML = request.getParameter("xml");   
            
            result = ELSXMLTranslator.XMLTemplateApplier.TranslateNestedString(XML,templates);
            System.out.println("Applier Output:"+result.output);
            if (!result.success) {
                result.success = false;
                result.translateMessage = "Translation failed:" + result.translateMessage;
                result.output = "";
            }
            String jsonResult = gson.toJson(result);

            //System.out.println("JSON::"+jsonResult);
            //try (PrintWriter out = response.getWriter()) {
            PrintWriter out = response.getWriter();
            out.println(jsonResult);
            
        } else if (command.equalsIgnoreCase("findtemplate")) {
            LookupResultsString result = new LookupResultsString();
            result.message = "";
            result.template = "";
            String XML = request.getParameter("xml");
            String type = request.getParameter("type");

            if (type != null) {
            if (type.equalsIgnoreCase("XML") || XMLERROR.length() > 0) {
                result.message = XMLERROR;
            } else if (type.equalsIgnoreCase("HTML") || HTMLERROR.length() > 0) {
                result.message = HTMLERROR;
            }
            }

            if (result.message.length() < 1) {
                if (type.equalsIgnoreCase("XML")) {
                    result = XMLTemplates.lookupXMLTemplateString(XML);
                    if (result.message.contains("not found")) {
                        result.message = "Passthrough";
                        result.success = true;
                        result.type = "XML";
                        result.template = XML;
                    }
                    
                    //System.out.println("XML Template:"+result.template);
                } else if (type.equalsIgnoreCase("HTML")) {
                    result = HTMLTemplates.lookupXMLTemplateString(XML);
                    //System.out.println("HTML template:"+result.template);

                } else {
                    result.success = false;
                    result.message = "Bad Type: " + type;
                }
            } else {
                result.success = false;
            }
            result.type = type;
            String jsonResult = gson.toJson(result);

            //System.out.println("JSON::"+jsonResult);
            //try (PrintWriter out = response.getWriter()) {
            PrintWriter out = response.getWriter();
            out.println(jsonResult);
            //}
        } else if (command.equalsIgnoreCase("sheet")) {
            SheetTranslateResultsString result = new SheetTranslateResultsString();
            SheetTranslateResults resultDOM = new SheetTranslateResults();
            String sheetString = request.getParameter("sheet");

            ParseResults parseResult = XMLTemplateApplier.loadXML(sheetString);
            
            if (!parseResult.success) {
                result.success = false;
                result.translateMessage = "Sheet Parse Error: " + parseResult.message;
            } else {
                resultDOM = HTMLTemplates.ApplyTemplatesToSheet(parseResult.doc.getDocumentElement());
                result.success = resultDOM.success;
                result.translateMessage = resultDOM.translateMessage;
                try {
                    result.output = prettyPrint(resultDOM.output);
                } catch (Exception ex) {
                    result.success = false;
                    result.translateMessage = "Template lookup Error: " + ex.getMessage();
                }
            }
            String jsonResult = gson.toJson(result);

            //System.out.println("JSON::"+jsonResult);
            //try (PrintWriter out = response.getWriter()) {
            PrintWriter out = response.getWriter();
            out.println(jsonResult);   
        }
        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
