<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>

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
        <title>MuleSoft | <%=mgr.getProductName()%></title>
        <link type="text/css" rel="stylesheet" href="column-view.css" />
        <link type="text/css" rel="stylesheet" href="extjsresources/css/gxt-all.css"/>
        <link type="text/css" rel="stylesheet" href="<%=mgr.getProductCss()%>" />
    </head>

    <!--                                           -->
    <!-- The body can have arbitrary html, or      -->
    <!-- you can leave the body empty if you want  -->
    <!-- to create a completely dynamic ui         -->
    <!--                                           -->
    <body>
     <%
            List modules = new ArrayList(mgr.getGwtPlugins());
            Collections.sort(modules, new Comparator() {
                public int compare(Object o1, Object o2) {
                    GwtPlugin p1 = (GwtPlugin) o1;
                    GwtPlugin p2 = (GwtPlugin) o2;
                    
                    if (p1.getName().equals("core")) return 1;
                    if (p2.getName().equals("core")) return -1;
                    
                    return p1.getName().compareTo(p2.getName());
                }
            });
      %>
        <script language='javascript'>
          var productName = "<%=mgr.getProductName()%>";
          var plugins = new Array();
          var pluginTokens = new Array();
          var loadWhenRegister;

          if (!Array.prototype.indexOf)
          {
            Array.prototype.indexOf = function(elt /*, from*/)
            {
              var len = this.length;

              var from = Number(arguments[1]) || 0;
              from = (from < 0)
                   ? Math.ceil(from)
                   : Math.floor(from);
              if (from < 0)
                from += len;

              for (; from < len; from++)
              {
                if (from in this &&
                    this[from] === elt)
                  return from;
              }
              return -1;
            };
          }
          
          // Registers a callback method to load a plugin when showPlugin is called
          function registerPlugin(token,instance,callbackMethod) {
              plugins[token] = callbackMethod;
              if (token = loadWhenRegister) {
                  callbackMethod()
                  loadWhenRegister = null;
              }
          }

          // Call out to the GWT plugin function
          function showPlugin(token) {
              var fn = plugins[token];
              if (fn) {
                  fn();
              } else if (pluginTokens.indexOf(token) >= 0) {
                  loadWhenRegister = token;
              }
              else alert("Plugin for token " + token + " was not found.");
          }
          
          <%
           for (Iterator itr = modules.iterator(); itr.hasNext();) {
              GwtPlugin mod = (GwtPlugin) itr.next();
              if ("core".equals(mod.getName())) {
                  continue;
              }
           %>
           pluginTokens = pluginTokens.concat("<%= mod.getRootToken() %>");
          <%
           }
           %>
        </script>
        <%
            for (Iterator itr = modules.iterator(); itr.hasNext();) {
                GwtPlugin mod = (GwtPlugin) itr.next();
                out.write("<script language='javascript' src='");

                if (!"core".equals(mod.getName()) && !hostedMode) {
                    out.write("plugins/");
                }
                
                out.write(mod.getModuleName());
                out.write("/");
                out.write(mod.getModuleName());
                out.write(".nocache.js'></script>");
            }
        %>

        <!-- OPTIONAL: include this if you want history support -->
        <iframe src="javascript:''" id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>

    </body>
</html>
