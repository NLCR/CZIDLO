<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>API V5</title>
    </head>
    <body>
        <h1>API verze 5 pro CZIDLO verze <%=cz.nkp.urnnbn.core.Czidlo.VERSION%></h1>
        
        <p>
        Popis API V5: <a href="https://github.com/NLCR/CZIDLO/wiki/API#verze-5">Github</a><br/>
        Příklady operací: <a href="https://github.com/NLCR/CZIDLO/wiki/API-v5---p%C5%99%C3%ADklady">Github</a><br/>
        Specifikace API V5: <a href="https://docs.google.com/spreadsheets/d/1JBWhkdek0AOT-uo3QC7GApkI6yP6GrvMLJkjAbVW1LM/edit?usp=sharing">Google spreadsheet</a><br/>
        </p>
                
        <p>
        <h2>XML schémata pro validaci vstupů/výstupů operací API</h2>
        Výstupy všech operací: <a href ="/api/v5/response.xsd">/api/v5/response.xsd</a><br/>
        Vstupy operace <i>Registrace digitálního dokumentu</i>: <a href ="/api/v5/digDocRegistration.xsd">/api/v5/digDocRegistration.xsd</a><br/>
        Vstupy operace <i>Import digitální instance</i>: <a href ="/api/v5/digInstImport.xsd">/api/v5/digInstImport.xsd</a><br/>
        </p>
        
        <p>
        <h2>Ukázky operací dostupných přes API</h2>
        Seznam registrátorů: <a href ="/api/v5/registrars">/api/v5/registrars</a><br/>
        Digitální instance: <a href ="/api/v5/digitalInstances">/api/v5/digitalInstances</a><br/>
        </p>
        
    </body>
</html>
