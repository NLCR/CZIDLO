<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : getRegistrarScopeIdentifierValue.xsl
    Created on : 16. leden 2013, 1:00
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
        <v2:id>
            <xsl:if test="v3:id/@type">
                <xsl:attribute name="type">
                    <xsl:value-of select="v3:id/@type"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="v3:id"/>
        </v2:id>
    </xsl:template>
    
</xsl:stylesheet>
