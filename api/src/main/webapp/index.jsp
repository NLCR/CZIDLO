<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>API</title>
    </head>
    <body>
        <h1>API pro CZIDLO verze <%=cz.nkp.urnnbn.core.Czidlo.VERSION%></h1>
        Aplikační programové rozhraní je popsáno na
        <a href="https://github.com/NLCR/CZIDLO/wiki/API">wiki projektu na Githubu</a>.<br>
        <p><a href="/api/v5/">API verze 5</a></p>
        <p><a href="/api/v4/">API verze 4 (deprecated)</a></p>
        <p><a href="/api/v3/">API verze 3 (deprecated)</a></p>
        <p><a href="/api/v2/">API verze 2 (deprecated)</a></p>
        <p>
        Pro ruční vyhledávání, vkládání či editaci záznamů a další administraci použijte <a href ="/web">webové rozhraní</a>.
        Pro hromadné sklízení záznamů použijte <a href ="/oaiPmhProvider/provider">OAI-PMH</a>,
        ke kterému je dostupné jednoduché <a href ="/oaiPmhProvider/web">webové rozhraní</a>.
        </p>
    </body>
</html>
