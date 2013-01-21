<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : getDigitalInstances.xsl
    Created on : 16. leden 2013, 1:08
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
        <v2:digitalInstances>
            <xsl:if test="v3:digitalInstances/@count">
                <xsl:attribute name="count">
                    <xsl:value-of select="v3:digitalInstances/@count"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:for-each select="v3:digitalInstances/v3:digitalInstance">
                <xsl:call-template name="digitalInstance"/>
            </xsl:for-each>
        </v2:digitalInstances>
    </xsl:template>
    
    <xsl:template name="digitalInstance">
        <v2:digitalInstance>
            <xsl:attribute name="active">
                <xsl:value-of select="@active"/>
            </xsl:attribute>
            <xsl:call-template name="id">
                <xsl:with-param name="type">INTERNAL</xsl:with-param>
                <xsl:with-param name="value" select="@id"/>
            </xsl:call-template>
            <xsl:call-template name="created"/>
            <xsl:call-template name="deactivated"/>
            <xsl:call-template name="url"/>
            <xsl:call-template name="format"/>
            <xsl:call-template name="accessibility"/>
        </v2:digitalInstance>
    </xsl:template>
    
    <xsl:template name="url">
        <xsl:if test="v3:url">
            <v2:url>
                <xsl:value-of select="v3:url" />
            </v2:url>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="format">
        <xsl:if test="v3:format">
            <v2:format>
                <xsl:value-of select="v3:format" />
            </v2:format>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="accessibility">
        <xsl:if test="v3:accessibility">
            <v2:accessibility>
                <xsl:value-of select="v3:accessibility" />
            </v2:accessibility>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="created">
        <xsl:if test="v3:created">
            <v2:created>
                <xsl:value-of select="v3:created" />
            </v2:created>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="deactivated">
        <xsl:if test="v3:deactivated">
            <v2:modified>
                <xsl:value-of select="v3:deactivated" />
            </v2:modified>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="id">
        <xsl:param name="type"/>
        <xsl:param name="value"/>
        <v2:id>
            <xsl:attribute name="type">
                <xsl:value-of select="$type"/>
            </xsl:attribute>
            <xsl:value-of select="$value" />
        </v2:id>
    </xsl:template>
</xsl:stylesheet>
