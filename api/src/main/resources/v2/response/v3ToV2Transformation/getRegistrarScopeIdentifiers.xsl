<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : getRegistrarScopeIdentifiers.xsl
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
        <v2:registrarScopeIdentifiers>
            <xsl:for-each select="v3:registrarScopeIdentifiers/v3:id">
                <xsl:call-template name="id"/>
            </xsl:for-each>     
        </v2:registrarScopeIdentifiers>
    </xsl:template>
    
    <xsl:template name="id">
        <v2:id>
            <xsl:attribute name="type">
                <xsl:value-of select="@type"/>
            </xsl:attribute>
            <xsl:value-of select="." />
        </v2:id>
    </xsl:template>
</xsl:stylesheet>
