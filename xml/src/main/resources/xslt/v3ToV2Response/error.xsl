<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : error.xsl
    Created on : 18. leden 2013, 0:34
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
        <v2:error>
            <xsl:call-template name="code">
                <xsl:with-param name="root" select="v3:error"/>
            </xsl:call-template>
            <xsl:call-template name="message">
                <xsl:with-param name="root" select="v3:error"/>
            </xsl:call-template>
        </v2:error>
    </xsl:template>
         
    <xsl:template name="code">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:code">
            <v2:code>
                <xsl:value-of select="$root/v3:code" />
            </v2:code>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="message">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:message">
            <v2:message>
                <xsl:value-of select="$root/v3:message" />
            </v2:message>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
