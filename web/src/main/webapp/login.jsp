<%
    response.setHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
%>
<html>
<head>
    <title>MuleSoft</title>
    <link type="text/css" rel="stylesheet" href="login.css"/>
    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>


    <script type="text/javascript">
        function isIE()
        {
            var browser = navigator.appName;
            var b_version = navigator.appVersion;
            var version = parseFloat(b_version);
            if (browser == "Microsoft Internet Explorer"
                    && (version == 8)) {
                return true;
            }
            return false;
        }
    </script>
</head>

</head>
<body onLoad="document.forms[0].j_username.focus()">
<div id="body">


    <form action="./j_acegi_security_check" method="post">
        <div class="loginBox">
            <div class="loginHeader">
                <img src="galaxy-plugins/images/logo_main.gif" border="0"/>

                <div class="loginHeader-right"></div>
            </div>

            <div class="login-topBand"></div>

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
                    <h2>Sign In</h2>

                    <div class="label">
                        Username:
                        <input name="j_username" value=""/>
                    </div>
                    <div class="label">
                        Password:
                        <input name="j_password" type="password" value=""/>
                    </div>
                    <div class="loginButton">
                        <input type="submit" value="Login"/>
                    </div>

                    <br><br>
                    <script type="text/javascript">
                     if(isIE()) {
                         document.write("IE 8 users, please verify you are operating in 'compatability mode'");
                     }
                    </script>

                </div>
            </div>

            <div class="loginBottom">
                Copyright &copy; 2008-2010 MuleSoft, Inc., All Rights
                Reserved.
            </div>
        </div>

    </form>
</div>
</body>
</html>