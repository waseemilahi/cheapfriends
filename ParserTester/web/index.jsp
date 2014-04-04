<%-- 
    Document   : index
    Created on : Oct 23, 2009, 12:06:21 PM
    Author     : michaelglass
--%>
<jsp:useBean id="message" class="mycheapfriend.Message"/>
<jsp:setProperty name="message" property="*"/>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <style type="text/css">
            label
            {
                width: 4em;
                float: left;
                text-align: right;
                margin-right: 0.5em;
                display: block
            }

            .submit input
            {
                margin-left: 4.5em;
            }
            .fail
            {
               background-color:red;
               color:white;
            }
            .pass
            {
                background-color:green;
                color:white;
            }
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <p><%= message.status() %></p>
        <form action=".">
            <fieldset>
                <legend>Check a message!</legend>
                <p><label for="from">From: </label><input  class="<%= message.validFrom() ? "pass" : "fail" %>" type="text" name="from" id="from" value="<%= message.getFrom()%>"/></p>
                <p><label for="to">To: </label><input class="<%= message.validTo() ? "pass" : "fail" %>"  type="text" name="to" id="to" value="<%= message.getTo()%>" /></p>
                <p><label for="subject">Subject:</label><input class="<%= message.validSubject() ? "pass" : "fail" %>" type="text" name="subject" id="subject" value="<%=message.getSubject() %>" /></p>
                <p><label for="body">Body:</label><textarea class="<%= message.validBody() ? "pass" : "fail" %>" name="body" id="body" cols="80" rows="15"><%=message.getBody()%></textarea></p>
                <input type="submit" class="submit" />
            </fieldset>

        </form>
    </body>
</html>
