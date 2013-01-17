<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : getDigitalDocuments.xsl
    Created on : 15. leden 2013, 23:00
    Author     : martin
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="1.0"
                xmlns:v2="http://resolver.nkp.cz/v2/"
                xmlns:v3="http://resolver.nkp.cz/v3/"
                exclude-result-prefixes="v3"
>
    <xsl:output method="xml" indent="no"/>
    
    <xsl:template match="/v3:response">
        <v2:digitalDocuments>
            <v2:count><xsl:value-of select="/v3:response/v3:digitalDocuments/@count"/></v2:count>
        </v2:digitalDocuments>
    </xsl:template>

</xsl:stylesheet>
