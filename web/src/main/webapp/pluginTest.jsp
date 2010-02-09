
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.mule.galaxy.web.WebManager"%>
<%@page import="java.util.List"%>
<%@page import="org.mule.galaxy.web.GwtModule"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%><html>
<%
    ApplicationContext ctx = (ApplicationContext) application.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    WebManager mgr = (WebManager)ctx.getBean("webManager");
    boolean hostedMode = Boolean.valueOf(System.getProperty("hostedMode"));
%>
    <head>
        <title>MuleSoft | <% out.write(mgr.getProductName()); %></title>
        <link type="text/css" rel="stylesheet" href="column-view.css" />
        <link type="text/css" rel="stylesheet" href="gxtresources/css/gxt-all.css"/>
        <link type="text/css" rel="stylesheet" href="<% out.write(mgr.getProductCss()); %>" />
    </head>

    <!--                                           -->
    <!-- The body can have arbitrary html, or      -->
    <!-- you can leave the body empty if you want  -->
    <!-- to create a completely dynamic ui         -->
    <!--                                           -->
    <body>
        <script language='javascript'>
          // Registers a callback method to load a plugin when showPlugin is called
          function registerPlugin(token,instance,callbackMethod) {
              callbackMethod();
          }
        </script>
        <%
            List<GwtModule> modules = new ArrayList<GwtModule>(mgr.getGwtModules());
            for (GwtModule mod : modules) {
                if (!mod.isCore()) {
	                out.write("<script language='javascript' src='");
	                out.write(mod.getName());
	                out.write("/");
	                out.write(mod.getName());
	                out.write(".nocache.js'></script>");
                }
            }
        %>
        <div id="plugin"/>
        <!-- OPTIONAL: include this if you want history support -->
        <iframe src="javascript:''" id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>

    </body>
</html>
