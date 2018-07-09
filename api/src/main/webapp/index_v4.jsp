<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>API V4</title>
    </head>
    <body>
        <h1>API verze 4 pro CZIDLO verze <%=cz.nkp.urnnbn.core.Czidlo.VERSION%></h1>
        Verze 4 je zastaralá a bude v některé následující verzi CZIDLO odstraněna. Doporučuje se přechod na <a href="/api/v5/">API verze 5</a>.
        
        <p>
        Popis API V4: <a href="https://github.com/NLCR/CZIDLO/wiki/API#verze-4">Github</a><br/>
        Příklady operací: <a href="https://github.com/NLCR/CZIDLO/wiki/API-v4---p%C5%99%C3%ADklady">Github</a><br/>
        Specifikace API V4: <a href="https://docs.google.com/spreadsheets/d/1QT1dLjsjZrXzqdv-TqD18UTrCmzpQ24-ilv7-X1RbvY/edit?usp=sharing">Google spreadsheet</a><br/>
        </p>
                
        <p>
        <h2>XML schémata pro validaci vstupů/výstupů operací API</h2>
        Výstupy všech operací: <a href ="/api/v4/response.xsd">/api/v4/response.xsd</a><br/>
        Vstupy operace <i>Registrace digitálního dokumentu</i>: <a href ="/api/v4/digDocRegistration.xsd">/api/v4/digDocRegistration.xsd</a><br/>
        Vstupy operace <i>Import digitální instance</i>: <a href ="/api/v4/digInstImport.xsd">/api/v4/digInstImport.xsd</a><br/>
        </p>
        
        <p>
        <h2>Ukázky operací dostupných přes API</h2>
        Seznam registrátorů: <a href ="/api/v4/registrars">/api/v4/registrars</a><br/>
        Digitální instance: <a href ="/api/v4/digitalInstances">/api/v4/digitalInstances</a><br/>
        </p>
        
    </body>
</html>
