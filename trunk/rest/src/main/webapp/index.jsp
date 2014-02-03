<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>CZIDLO: API</title>
    </head>
    <body>
        <h1>CZIDLO: aplikační rozhraní</h1>
        Aplikační programové rozhraní je popsáno na
        <a href="http://code.google.com/p/urnnbn-resolver-v2/wiki/API">wiki projektu na Google Code</a>. 
        Tato verze CZIDLO (4.2.1) podporuje API ve verzi 3 
        <a href="https://docs.google.com/spreadsheet/pub?key=0Ag5aMq4LaXVcdGxGVUFITE1lVUk5blkyZ2ZIc3RuT1E&gid=2">(specifikace API V3)</a>
        a kvůli zpětně kompatibilitě i verzi 2
        <a href="https://docs.google.com/spreadsheet/pub?key=0Ag5aMq4LaXVcdGxGVUFITE1lVUk5blkyZ2ZIc3RuT1E&gid=2">(specifikace API V2)</a>
        .
        Pro ruční vyhledávání, vkládání či editaci záznamů a další administraci použijte <a href ="/web">webové rozhraní</a>.
        <br>
        Pro hromadné sklízení záznamů použijte <a href ="/oaiPmhProvider/provider">OAI-PMH</a>,
        ke kterému je dostupné jednoduché <a href ="/oaiPmhProvider/web">webové rozhraní</a>.
        <br>
        Ukázky operací dostupných přes API:
        <br>
        <a href ="/api/v3/registrars">Seznam registrátorů</a>
        <br>
        <a href ="/api/v3/digitalInstances">Digitální instance</a>
        <br>
    </body>
</html>
