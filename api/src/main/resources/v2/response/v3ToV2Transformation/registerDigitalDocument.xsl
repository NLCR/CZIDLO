<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : registerDigitalDocument.xsl
    Created on : 18. leden 2013, 0:22
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
        <v2:urnNbn>
            <xsl:call-template name="datestamps">
                <xsl:with-param name="root" select="v3:urnNbn"/>
            </xsl:call-template>
            <xsl:call-template name="status">
                <xsl:with-param name="root" select="v3:urnNbn"/>
            </xsl:call-template>
            <xsl:call-template name="registrarCode">
                <xsl:with-param name="root" select="v3:urnNbn"/>
            </xsl:call-template>
            <!--            <xsl:call-template name="documentCode">
                <xsl:with-param name="root" select="v3:urnNbn"/>
            </xsl:call-template>-->
            <xsl:call-template name="value">
                <xsl:with-param name="root" select="v3:urnNbn"/>
            </xsl:call-template>
            <xsl:call-template name="digitalDocumentId">
                <xsl:with-param name="root" select="v3:urnNbn"/>
            </xsl:call-template>
        </v2:urnNbn>
    </xsl:template>
         
    <xsl:template name="datestamps">
        <xsl:param name="root"/>
        <xsl:choose>
            <xsl:when test="$root/v3:status = 'ACTIVE'">
                <xsl:choose>
                    <xsl:when test="$root/v3:reserved">
                        <v2:created>
                            <xsl:value-of select="$root/v3:reserved" />
                        </v2:created>
                        <v2:modified>
                            <xsl:value-of select="$root/v3:registered" />
                        </v2:modified>
                    </xsl:when>
                    <xsl:otherwise>
                        <v2:created>
                            <xsl:value-of select="$root/v3:registered" />
                        </v2:created>     
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$root/v3:status = 'DEACTIVATED'">
                <xsl:choose>
                    <xsl:when test="$root/v3:reserved">
                        <v2:created>
                            <xsl:value-of select="$root/v3:reserved" />
                        </v2:created>
                    </xsl:when>
                    <xsl:otherwise>
                        <v2:created>
                            <xsl:value-of select="$root/v3:registered" />
                        </v2:created>     
                    </xsl:otherwise>
                </xsl:choose>
                <v2:modified>
                    <xsl:value-of select="$root/v3:deactivated" />
                </v2:modified>
            </xsl:when>
            <xsl:when test="$root/v3:status = 'RESERVED'">
                <v2:created>
                    <xsl:value-of select="$root/v3:reserved" />
                </v2:created>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="status">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:status">
            <v2:status>
                <xsl:value-of select="$root/v3:status" />
            </v2:status>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="registrarCode">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:registrarCode">
            <v2:registrarCode>
                <xsl:value-of select="$root/v3:registrarCode" />
            </v2:registrarCode>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="documentCode">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:documentCode">
            <v2:documentCode>
                <xsl:value-of select="$root/v3:documentCode" />
            </v2:documentCode>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="value">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:value">
            <v2:value>
                <xsl:value-of select="$root/v3:value" />
            </v2:value>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="digitalDocumentId">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:digitalDocumentId">
            <v2:digitalDocumentId>
                <xsl:value-of select="$root/v3:digitalDocumentId" />
            </v2:digitalDocumentId>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
