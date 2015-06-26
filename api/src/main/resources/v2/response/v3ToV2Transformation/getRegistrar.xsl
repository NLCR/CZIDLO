<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : getRegistrar.xsl
    Created on : 15. leden 2013, 21:10
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
        <v2:registrar>
            <xsl:attribute name="code">
                <xsl:value-of select="v3:registrar/@code"/>
            </xsl:attribute>
            <xsl:call-template name="created">
                <xsl:with-param name="root" select="v3:registrar"/>
            </xsl:call-template>
            <xsl:call-template name="modified">
                <xsl:with-param name="root" select="v3:registrar"/>
            </xsl:call-template>
            <xsl:call-template name="name">
                <xsl:with-param name="root" select="v3:registrar"/>
            </xsl:call-template>
            <xsl:call-template name="description">
                <xsl:with-param name="root" select="v3:registrar"/>
            </xsl:call-template>
            <xsl:if test="v3:registrar/v3:digitalLibraries">
                <xsl:call-template name="digitalLibraries">
                    <xsl:with-param name="registrar" select="v3:registrar"/>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="v3:registrar/v3:catalogs">
                <xsl:call-template name="catalogs">
                    <xsl:with-param name="registrar" select="v3:registrar"/>
                </xsl:call-template>
            </xsl:if>
        </v2:registrar>
    </xsl:template>
         
    <xsl:template name="created">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:created">
            <v2:created>
                <xsl:value-of select="$root/v3:created" />
            </v2:created>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="modified">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:modified">
            <v2:modified>
                <xsl:value-of select="$root/v3:modified" />
            </v2:modified>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="name">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:name">
            <v2:name>
                <xsl:value-of select="$root/v3:name" />
            </v2:name>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="urlPrefix">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:urlPrefix">
            <v2:urlPrefix>
                <xsl:value-of select="$root/v3:urlPrefix" />
            </v2:urlPrefix>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="description">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:description">
            <v2:description>
                <xsl:value-of select="$root/v3:description" />
            </v2:description>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="digitalLibraries">
        <xsl:param name="registrar"/>
        <v2:digitalLibraries>
            <xsl:for-each select="$registrar/v3:digitalLibraries/v3:digitalLibrary">
                <xsl:call-template name="digitalLibrary"/>
            </xsl:for-each>
        </v2:digitalLibraries>
    </xsl:template>
    
    <xsl:template name="digitalLibrary">
        <v2:digitalLibrary>
            <xsl:call-template name="name">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>    
            <xsl:call-template name="id">
                <xsl:with-param name="type">INTERNAL</xsl:with-param>
                <xsl:with-param name="value" select="./@id"/>
            </xsl:call-template>    
            <xsl:call-template name="description">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>    
            <xsl:call-template name="url">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>
            <xsl:call-template name="created">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>
            <xsl:call-template name="modified">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>
        </v2:digitalLibrary>
    </xsl:template>
    
    <xsl:template name="url">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:url">
            <v2:url>
                <xsl:value-of select="$root/v3:url" />
            </v2:url>
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
    
    <xsl:template name="catalogs">
        <xsl:param name="registrar"/>
        <v2:catalogs>
            <xsl:for-each select="$registrar/v3:catalogs/v3:catalog">
                <xsl:call-template name="catalog"/>
            </xsl:for-each>
        </v2:catalogs>
    </xsl:template>
    
    <xsl:template name="catalog">
        <v2:catalog>
            <xsl:call-template name="created">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>
            <xsl:call-template name="modified">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>
            <xsl:call-template name="name">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>    
            <xsl:call-template name="description">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>    
            <xsl:call-template name="urlPrefix">
                <xsl:with-param name="root" select="."/>
            </xsl:call-template>
        </v2:catalog>
    </xsl:template>

</xsl:stylesheet>
