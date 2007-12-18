<html>
<head>
<title>Login</title>
</head>
<body>
Login

<% if (request.getParameter("login_error") != null) { %>

<div id="error">Your username or password was incorrect!</div>
<% } %>

<form action="/j_acegi_security_check" method="post">
  <div id="label">
    Username: <input name="j_username" value=""/>
  </div>
  <div id="label">
    Password: <input name="j_password" type="password" value=""/>
  </div>
  <input type="submit" value="Login"/>
</form>
</body>
</html>