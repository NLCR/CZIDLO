<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>API V2</title>
    </head>
    <body>
        <h1>API verze 2 pro CZIDLO verze <%=cz.nkp.urnnbn.core.Czidlo.VERSION%></h1>
        Verze 2 je zastaralá a bude v některé následující verzi CZIDLO odstraněna. Doporučuje se přechod na <a href="/api/v3/">API v3</a>.  
        
        <p>
        Popis API V2: <a href="https://github.com/NLCR/CZIDLO/wiki/API#verze-2">Github</a><br/>
        Příklady operací: <a href="https://github.com/NLCR/CZIDLO/wiki/API-v2---p%C5%99%C3%ADklady">Github</a><br/>
        Specifikace API V2: <a href="https://docs.google.com/spreadsheet/pub?key=0Ag5aMq4LaXVcdGxGVUFITE1lVUk5blkyZ2ZIc3RuT1E&gid=2">Google spreadsheet</a><br/>
        </p>
        
        <p>
        <h2>XML schémata pro validaci vstupů/výstupů operací API</h2>
        Vstupy operace <i>Registrace digitálního dokumentu</i>: <a href ="/api/v2/digDocRegistration.xsd">/api/v2/digDocRegistration.xsd</a><br/>
        Vstupy operace <i>Import digitální instance</i>: <a href ="/api/v2/digInstImport.xsd">/api/v2/digInstImport.xsd</a><br/>
        </p>
        
        <p>
        <h2>Ukázky operací dostupných přes API</h2>
        Seznam registrátorů: <a href ="/api/v2/registrars">/api/v2/registrars</a><br/>
        Digitální instance: <a href ="/api/v2/digitalInstances">/api/v2/digitalInstances</a><br/>
        </p>
        
    </body>
</html>
