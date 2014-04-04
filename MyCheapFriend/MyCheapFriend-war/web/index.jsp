<%-- 
    Document   : index
    Created on : Oct 23, 2009, 10:21:25 PM
    Author     : Waseem Ilahi
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<title>Servlet LoginUser</title>
</head>
<body>
<center>
MyCheapFriend Administrator Console
<br>
<form name="login" action="LoginHandler" method="POST">
Enter your Phone Number:
<input type="text" name="Phone" MAXLENGTH = "10" size="10">
<br>Enter your Password:
<input type="password" name="Password" MAXLENGTH = "6" size="6">
<br>
<input type="submit" value="Submit">
</form>
</center>
</body>
</html>