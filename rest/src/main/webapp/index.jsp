<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Resolver urn:nbn</title>
    </head>
    <body>
        <h1>Resolver urn:nbn - aplikační rozhraní</h1>
        Aplikační programové rozhraní je popsáno 
        <a href="http://code.google.com/p/urnnbn-resolver-v2/wiki/API">zde</a>. 
        Tato verze Resolveru podporuje API ve verzi 3 
        <a href="https://docs.google.com/spreadsheet/pub?key=0Ag5aMq4LaXVcdGxGVUFITE1lVUk5blkyZ2ZIc3RuT1E&gid=2">(specifikace API V3)</a>
        a kvůli zpětně kompatibilitě i verzi 2
        <a href="https://docs.google.com/spreadsheet/pub?key=0Ag5aMq4LaXVcdGxGVUFITE1lVUk5blkyZ2ZIc3RuT1E&gid=2">(specifikace API V2)</a>
        .
        Pro ruční vyhledávání, vkladání či editaci záznamů a další administraci použijte <a href ="/web">webové rozhraní</a>.
        <br>
        Pro hromadné sklízení záznamů použijte <a href ="/OaiPmhProvider/provider">rozhraní OAI-PMH</a>,
        které je dostupné také <a href ="/OaiPmhProvider/web">webové rozhraní</a>.
        <br>
        Některé jednoduché operace dostupné přímo přes toto API:
        <br>
        <a href ="/api/v3/registrars">Seznam registrátorů</a>
        <br>
        <a href ="/api/v3/digitalInstances">Digitální instance</a>
        <br>
    </body>
</html>
