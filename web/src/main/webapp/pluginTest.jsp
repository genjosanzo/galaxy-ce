
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.mule.galaxy.web.WebManager"%>
<%@page import="java.util.List"%>
<%@page import="org.mule.galaxy.web.GwtPlugin"%>
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
        <link type="text/css" rel="stylesheet" href="extjsresources/css/gxt-all.css"/>
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
            List<GwtPlugin> modules = new ArrayList<GwtPlugin>(mgr.getGwtPlugins());
            Collections.sort(modules, new Comparator<GwtPlugin>() {
                public int compare(GwtPlugin p1, GwtPlugin p2) {
                    if ("core".equals(p1.getName())) {
                        return 1;
                    }
                    if ("core".equals(p2.getName())) {
                        return -1;
                    }

                    return p1.getName().compareTo(p2.getName());
                }
            });
            for (GwtPlugin mod : modules) {
                if (!"core".equals(mod.getName())) {
	                out.write("<script language='javascript' src='");
	                out.write(mod.getModuleName());
	                out.write("/");
	                out.write(mod.getModuleName());
	                out.write(".nocache.js'></script>");
                }
            }
        %>
        <div id="plugin"/>
        <!-- OPTIONAL: include this if you want history support -->
        <iframe src="javascript:''" id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>

    </body>
</html>
