package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final JspFactory _jspxFactory = JspFactory.getDefaultFactory();

  private static java.util.List<String> _jspx_dependants;

  private org.glassfish.jsp.api.ResourceInjector _jspx_resourceInjector;

  public java.util.List<String> getDependants() {
    return _jspx_dependants;
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;

    try {
      response.setContentType("text/html;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;
      _jspx_resourceInjector = (org.glassfish.jsp.api.ResourceInjector) application.getAttribute("com.sun.appserv.jsp.resource.injector");

      out.write("\n");
      out.write("\n");
      out.write("\n");
      out.write("<!DOCTYPE html>\n");
      out.write("<html>\n");
      out.write("    <head>\n");
      out.write("        <meta charset=\"UTF-8\">\n");
      out.write("        <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n");
      out.write("        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n");
      out.write("        <link href=\"libraries/css/Template.css\" rel=\"stylesheet\" type=\"text/css\"/>\n");
      out.write("        <title>XML to HTML Test System</title>\n");
      out.write("        <link href=\"css/main.css\" rel=\"stylesheet\">\n");
      out.write("        <!-- Bootstrap -->\n");
      out.write("        <link href=\"libraries/css/bootstrap.min.css\" rel=\"stylesheet\">\n");
      out.write("        <link href=\"https://gitcdn.github.io/bootstrap-toggle/2.2.0/css/bootstrap-toggle.min.css\" rel=\"stylesheet\"> <!-- Just needed for Bootstrap toggle -->\n");
      out.write("        <link href=\"libraries/BootstrapDatePicker/bootstrap-datepicker.min.css\" rel=\"stylesheet\" type=\"text/css\"/>    <!-- Just needed for the Bootstrap Datepicker -->\n");
      out.write("<!--        <link href=\"css/buttons.css\" rel=\"stylesheet\" type=\"text/css\"/>-->  <!-- DMW: Commented out because buttons look weird -->\n");
      out.write("\n");
      out.write("        <link rel=\"stylesheet\" type=\"text/css\" href=\"libraries/css/prettify.css\" />\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.core.js\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.dynamic.js\"></script>   <!-- Just needed for dynamic features -->\n");
      out.write("        \n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.annotate.js\"></script>  <!-- Just needed for annotating -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.context.js\"></script>   <!-- Just needed for context menus -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.effects.js\"></script>   <!-- Just needed for visual effects -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.key.js\"></script>       <!-- Just needed for keys -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.resizing.js\"></script>  <!-- Just needed for resizing -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.tooltips.js\"></script>  <!-- Just needed for tooltips -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.common.zoom.js\"></script>      <!-- Just needed for zoom -->\n");
      out.write("\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.background.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.rect.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.circle.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.image.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.marker1.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.marker2.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.marker3.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.poly.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.text.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.xaxis.js\" type=\"text/javascript\"></script>\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.drawing.yaxis.js\" type=\"text/javascript\"></script>\n");
      out.write("        \n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.bar.js\"></script>              <!-- Just needed for Bar charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.bipolar.js\"></script>          <!-- Just needed for Bi-polar charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.fuel.js\"></script>             <!-- Just needed for Fuel charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.funnel.js\"></script>           <!-- Just needed for Funnel charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.gantt.js\"></script>            <!-- Just needed for Gantt charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.gauge.js\"></script>            <!-- Just needed for Gauge charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.hbar.js\"></script>             <!-- Just needed for Horizontal Bar charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.hprogress.js\"></script>        <!-- Just needed for Horizontal Progress bars -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.line.js\"></script>             <!-- Just needed for Line charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.meter.js\"></script>            <!-- Just needed for Meter charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.odo.js\"></script>              <!-- Just needed for Odometers -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.pie.js\"></script>              <!-- Just needed for Pie AND Donut charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.radar.js\"></script>            <!-- Just needed for Radar charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.rose.js\"></script>             <!-- Just needed for Rose charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.rscatter.js\"></script>         <!-- Just needed for Rscatter charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.scatter.js\"></script>          <!-- Just needed for Scatter charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.thermometer.js\"></script>      <!-- Just needed for Thermometer charts -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.vprogress.js\"></script>        <!-- Just needed for Vertical Progress bars -->\n");
      out.write("        <script src=\"libraries/RGraph/libraries/RGraph.waterfall.js\"></script>        <!-- Just needed for Waterfall charts  -->\n");
      out.write("        \n");
      out.write("        <script src=\"libraries/RGraph/libraries/Custom/RGraph.target.js\" type=\"text/javascript\"></script>\n");
      out.write("\n");
      out.write("    </head>\n");
      out.write("    <body>\n");
      out.write("        <nav class=\"navbar navbar-default navbar-fixed-top\">\n");
      out.write("            <div class=\"container-fluid areas\">\n");
      out.write("                <!-- Brand and toggle get grouped for better mobile display -->\n");
      out.write("                <div class=\"navbar-header\">\n");
      out.write("\n");
      out.write("                    <a class=\"navbar-brand\" href=\"#\"><b>ELS</b></a>\n");
      out.write("                    <h5>XML to HTML Transform Tool</h5>\n");
      out.write("                </div>\n");
      out.write("                <form class=\"navbar-form navbar-left\" role=\"navagate\">\n");
      out.write("                <div class=\"btn-group\">\n");
      out.write("                <button  href=\"#BaseXMLCodeDiv\" class=\"btn display btn-xs\" type=\"checkbox\" data-toggle=\"collapse\">XML<br />Source</button>\n");
      out.write("                <button  href=\"#XMLTemplateCodeDiv\" class=\"btn display btn-xs\" type=\"checkbox\" data-toggle=\"collapse\">XML<br />Template</button>\n");
      out.write("                <button href=\"#XMLCodeDiv\" class=\"btn display btn-xs\" type=\"checkbox\" data-toggle=\"collapse\">XML<br />Translated</button>               \n");
      out.write("                <button href=\"#TemplateCodeDiv\" class=\"btn display btn-xs\" type=\"checkbox\" data-toggle=\"collapse\">HTML<br />Template</button>  \n");
      out.write("                <button href=\"#htmlcode\" class=\"btn display btn-xs\" btn-xs type=\"checkbox\" data-toggle=\"collapse\">HTML<br />Code</button>   \n");
      out.write("                <button href=\"#htmldisplay\" class=\"btn display btn-xs\" type=\"checkbox\" data-toggle=\"collapse\">HTML<br />Display</button>   \n");
      out.write("                \n");
      out.write("                \n");
      out.write("                </div>\n");
      out.write("                    <button id=\"all\" class=\"btn btn-xs\" type=\"button\" onclick=\"toggleAll()\">All</button>   \n");
      out.write("                </form>\n");
      out.write("                <!-- Collect the nav links, forms, and other content for toggling -->\n");
      out.write("\n");
      out.write("                <form class=\"navbar-form navbar-right\" role=\"search\" >\n");
      out.write("                    <div class=\"btn-group\">\n");
      out.write("                        <button type=\"button\" class=\"btn btn-info lookupXML btn-xs\" onclick=\"XMLTemplateSelect($('#BaseXMLCode').val(),'XML')\" >Sel XML<br />Template</button>\n");
      out.write("                        <button type=\"button\" class=\"btn btn-info translateXML btn-xs\" onclick=\"doXMLSubmit('')\">Apply XML<br />Template</button>\n");
      out.write("                        <button type=\"button\" class=\"btn btn-info lookupHTML btn-xs\" onclick=\"XMLTemplateSelect($('#XMLCode').val(),'HTML')\" >Sel HTML<br />Template</button>\n");
      out.write("                        <button type=\"button\" class=\"btn btn-info translateHTML btn-xs\" onclick=\"doHTMLSubmit()\">Apply HTML<br />Template</button>\n");
      out.write("                        <button type=\"button\" class=\"btn btn-info displayHTML btn-xs\" onclick=\"doHTMLDisplay()\">Display<br />HTML</button>\n");
      out.write("                    </div>\n");
      out.write("                 </form>\n");
      out.write("\n");
      out.write("            </div><!-- /.navbar-collapse -->\n");
      out.write("        </div><!-- /.container-fluid -->\n");
      out.write("    </nav>\n");
      out.write("\n");
      out.write("    <div class=\"container\">\n");
      out.write("        <div id=\"TranslateMessage\"></div>\n");
      out.write("        <div class=\"row\">\n");
      out.write("            <div class=\"col-md-12\">                                                                                                                                                                                                                                                                                                                                                                                                                                                                  \n");
      out.write("                \n");
      out.write("                <div id=\"XMLTranslateMessage\"></div>\n");
      out.write("                \n");
      out.write("                <div id=\"BaseXMLCodeDiv\" class=\"form-group collapse\">\n");
      out.write("                    <label>Source XML</label>\n");
      out.write("                    <textarea id=\"BaseXMLCode\" class=\" form-control  XMLText\" rows=\"10\"  ></textarea>\n");
      out.write("                    <div id=\"BaseXMLMessage\" class=\"message\"></div> \n");
      out.write("                </div>\n");
      out.write("                          \n");
      out.write("                \n");
      out.write("                <div id=\"XMLTemplateCodeDiv\" class=\"form-group collapse\">\n");
      out.write("                    <label>XML Template</label> \n");
      out.write("                    <textarea id=\"XMLTemplateCode\" class=\"form-control XMLText\" rows=\"10\" ></textarea>\n");
      out.write("                    <div id=\"XMLTemplateMessage\" class=\"message\"></div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("\n");
      out.write("                <div id=\"XMLCodeDiv\" class=\"form-group collapse\">\n");
      out.write("                    <label>Translated XML</label>\n");
      out.write("                    <textarea id=\"XMLCode\" class=\"form-control HTMLText\" rows=\"10\"  ></textarea>\n");
      out.write("                    <div id=\"XMLMessage\" class=\"message\"></div> \n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("                <div id=\"TemplateCodeDiv\" class=\"form-group collapse\">\n");
      out.write("                    <label>HTML Template</label>\n");
      out.write("                    \n");
      out.write("                    <textarea id=\"TemplateCode\"  class=\"form-control HTMLText\" rows=\"10\"></textarea>\n");
      out.write("                    <div id=\"TemplateMessage\" class=\"message\"></div>\n");
      out.write("                </div>\n");
      out.write("                \n");
      out.write("\n");
      out.write("\n");
      out.write("            </div>\n");
      out.write("            <div class=\"col-md-12\">  \n");
      out.write("                <div id=\"htmlcode\" class=\"collapse\">\n");
      out.write("                <label>HTML Code</label>\n");
      out.write("                    <hr>\n");
      out.write("                    <textarea id=\"HTMLOutput\"  class=\"form-control HTMLText\" rows=\"10\"></textarea>\n");
      out.write("                    <hr>\n");
      out.write("                </div>\n");
      out.write("                <div id=\"htmldisplay\" class=\"collapse\">\n");
      out.write("                    <label>HTML Display</label>\n");
      out.write("                    <hr>\n");
      out.write("                    <div class=\"container-fluid\" id=\"HTMLOutput\"></div>\n");
      out.write("                    <hr>\n");
      out.write("                </div>\n");
      out.write("\n");
      out.write("\n");
      out.write("            </div>\n");
      out.write("        </div>\n");
      out.write("    </div>\n");
      out.write("\n");
      out.write("\n");
      out.write("        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->\n");
      out.write("        <script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.2/jquery.min.js\"></script>\n");
      out.write("        <!-- Include all compiled plugins (below), or include individual files as needed -->\n");
      out.write("        <script src=\"libraries/js/bootstrap.min.js\"></script>   \n");
      out.write("        <script src=\"libraries/js/prettify.js\"></script> \n");
      out.write("        <script src=\"XMLTranslate.js\"></script> \n");
      out.write("        <script src=\"libraries/js/InputFilter.js\" type=\"text/javascript\"></script> <!-- Just needed for the input filter -->\n");
      out.write("        <script src=\"https://gitcdn.github.io/bootstrap-toggle/2.2.0/js/bootstrap-toggle.min.js\"></script>  <!-- Just needed for Bootstrap toggle -->\n");
      out.write("        <script src=\"libraries/BootstrapDatePicker/Moment.js\" type=\"text/javascript\"></script>                      <!-- Just needed for the Bootstrap Datepicker -->\n");
      out.write("        <script src=\"libraries/BootstrapDatePicker/bootstrap-datepicker.min.js\" type=\"text/javascript\"></script>    <!-- Just needed for the Bootstrap Datepicker -->\n");
      out.write("    </body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
