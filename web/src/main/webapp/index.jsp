
<%@page import="org.springframework.web.context.WebApplicationContext"%>
<%@page import="org.springframework.context.ApplicationContext"%>
<%@page import="org.mule.galaxy.web.GwtManager"%>
<%@page import="java.util.List"%>
<%@page import="org.mule.galaxy.web.GwtPlugin"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Comparator"%><html>
    <head>
        <title>MuleSource | Galaxy</title>
        <link type="text/css" rel="stylesheet" href="column-view.css" />
        <link type="text/css" rel="stylesheet" href="extjsresources/css/gxt-all.css"/>
        <link type="text/css" rel="stylesheet" href="application.css" />
    </head>

    <!--                                           -->
    <!-- The body can have arbitrary html, or      -->
    <!-- you can leave the body empty if you want  -->
    <!-- to create a completely dynamic ui         -->
    <!--                                           -->
    <body>
        <script language='javascript'>
          var plugins = new Array();

          // Registers a callback method to load a plugin when showPlugin is called
          function registerPlugin(token,instance,callbackMethod) {
              plugins[token] = callbackMethod;
          }

          // Call out to the GWT plugin function 
          function showPlugin(token) {
              var fn = plugins[token];
	          if (fn) {
		          fn();
	          }
	          else alert("Plugin for token " + token + " was not found.");
          }
        </script>
        <%
            ApplicationContext ctx = (ApplicationContext) application.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
                			GwtManager mgr = (GwtManager)ctx.getBean("gwtManager");
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
            for (Iterator itr = modules.iterator(); itr.hasNext();) {
                GwtPlugin mod = (GwtPlugin) itr.next();
                out.write("<script language='javascript' src='");
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
