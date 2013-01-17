<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : reserveUrnNbn.xsl
    Created on : 15. leden 2013, 22:38
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
        <v2:urnNbnRerservation>
            <xsl:for-each select="v3:urnNbnReservation/v3:urnNbn">
                <v2:urnNbn>
                    <xsl:value-of select="."/>
                </v2:urnNbn>
            </xsl:for-each>    
        </v2:urnNbnRerservation>
    </xsl:template>
    
    <xsl:template name="urnNbn">
        <v2:urnNbn>
            <xsl:attribute name="created">
                <xsl:value-of select="@reserved"/>
            </xsl:attribute>
            <xsl:value-of select="."/>
        </v2:urnNbn>
    </xsl:template>
    
</xsl:stylesheet>

