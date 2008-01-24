<html>
    <head>
        <title>Mule Galaxy Login</title>
        <link type="text/css" rel="stylesheet" href="login.css" />
    </head>
    <body>
    <div id="body">


     <form action="./j_acegi_security_check" method="post">
        <div class="loginBox">
                <div class="loginTop"><img src="images/galaxy_small_logo.png" style="float: right; margin-top: 1px; " border="0"/></div>
                
                <div class="loginLeft"></div>
                  
                <div class="loginRight">
                    <div class="loginContent">
                    <%
                        if (request.getParameter("login_error") != null) {
                    %>

                    <div class="error">
                        Your username or password was incorrect!
                    </div>

                    <%
                    }
                    %>

                    <div class="label">
                        Username:
                        <input name="j_username" value="" />
                    </div>
                    <div class="label">
                        Password:
                        <input name="j_password" type="password" value="" />
                    </div>
                    <div class="loginButton">
                      <input type="submit" value="Login" />
                    </div>
                    </div>
                </div>
                  
                <div class="loginBottom">
                  Powered by MuleSource
                </div>
        </div>
        
    </form>
    </div>
    </body>
</html>