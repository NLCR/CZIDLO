<%-- 
    Document   : test
    Created on : 4.3.2012, 19:54:37
    Author     : Martin Řehánek
--%>

<%@page import="java.util.Enumeration"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            ServletContext resolverContext = getServletContext().getContext("/resolver");
            String path = null;
            boolean ok = false;
            if (resolverContext != null) {
                path = resolverContext.getContextPath();
                RequestDispatcher dis = resolverContext.getRequestDispatcher("/");
                ok = (dis != null);
                if (ok) {
                    //response.sendRedirect("/resolver?q=urn:nbn:123");
                }
            }
            Enumeration<String> attrs = getServletContext().getAttributeNames();
        %>
        referer: <%= request.getHeader("referer")%>
    </body>
</html>
