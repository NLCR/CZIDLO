<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : getDigitalInstance.xsl
    Created on : 16. leden 2013, 2:13
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
        <xsl:for-each select="v3:digitalInstance">
            <xsl:call-template name="digitalInstance"/>
        </xsl:for-each>
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
            <xsl:for-each select="v3:digitalLibrary">
                <xsl:call-template name="digitalLibrary"/>
            </xsl:for-each>
            <xsl:for-each select="v3:digitalDocument">
                <xsl:call-template name="digitalDocument"/>
            </xsl:for-each>
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
    
    <xsl:template name="digitalLibrary">
        <v2:digitalLibrary>
            <xsl:call-template name="name"/>
            <xsl:call-template name="id">
                <xsl:with-param name="type">INTERNAL</xsl:with-param>
                <xsl:with-param name="value" select="@id"/>
            </xsl:call-template>
            <xsl:call-template name="description"/>
            <xsl:call-template name="url"/>
            <xsl:call-template name="created"/>
            <xsl:call-template name="modified"/>
            <xsl:for-each select="v3:registrar">
                <xsl:call-template name="registrar"/>
            </xsl:for-each>
        </v2:digitalLibrary>
    </xsl:template>
    
    <xsl:template name="name">
        <xsl:if test="v3:name">
            <v2:name>
                <xsl:value-of select="v3:name" />
            </v2:name>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="description">
        <xsl:if test="v3:description">
            <v2:description>
                <xsl:value-of select="v3:description" />
            </v2:description>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="modified">
        <xsl:if test="v3:modified">
            <v2:modified>
                <xsl:value-of select="v3:modified" />
            </v2:modified>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="registrar">
        <v2:registrar>
            <xsl:attribute name="code">
                <xsl:value-of select="@code"/>
            </xsl:attribute>
            <xsl:call-template name="created"/>
            <xsl:call-template name="modified"/>
            <xsl:call-template name="name"/>
            <xsl:call-template name="description"/>
        </v2:registrar>
    </xsl:template>
    
    
    
    <xsl:template name="digitalDocument">
        <v2:digitalDocument>
            <xsl:call-template name="created"/>
            <xsl:call-template name="modified"/>
            <xsl:call-template name="urnNbn"/>
            <xsl:call-template name="financed"/>
            <xsl:call-template name="contractNumber"/>
            <xsl:for-each select="v3:technicalMetadata">
                <xsl:call-template name="technicalMetadata"/>
            </xsl:for-each>
            <xsl:for-each select="v3:registrarScopeIdentifiers">
                <xsl:call-template name="registrarScopeIdentifiers"/>
            </xsl:for-each>
        </v2:digitalDocument>
    </xsl:template>
    
    <xsl:template name="urnNbn">
        <v2:urnNbn>
            <xsl:value-of select="v3:urnNbn/v3:value"/>
        </v2:urnNbn>
    </xsl:template>
    
    <xsl:template name="financed">
        <xsl:if test="v3:financed">
            <v2:financed>
                <xsl:value-of select="v3:financed" />
            </v2:financed>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="contractNumber">
        <xsl:if test="v3:contractNumber">
            <v2:contractNumber>
                <xsl:value-of select="v3:contractNumber" />
            </v2:contractNumber>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="technicalMetadata">
        <v2:technicalMetadata>
            <xsl:call-template name="format"/>
            <xsl:call-template name="extent"/>
            <xsl:call-template name="resolution"/>
            <xsl:call-template name="compression"/>
            <xsl:call-template name="color"/>
            <xsl:call-template name="iccProfile"/>
            <xsl:call-template name="pictureSize"/>
        </v2:technicalMetadata>
    </xsl:template>
    
    <xsl:template name="extent">
        <xsl:if test="v3:extent">
            <v2:extent>
                <xsl:value-of select="v3:extent" />
            </v2:extent>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="resolution">
        <xsl:if test="v3:resolution">
            <v2:resolution>
                <xsl:if test="v3:resolution/v3:horizontal">
                    <v2:horizontal>
                        <xsl:value-of select="v3:resolution/v3:horizontal" />
                    </v2:horizontal>
                </xsl:if>        
                <xsl:if test="v3:resolution/v3:vertical">
                    <v2:vertical>
                        <xsl:value-of select="v3:resolution/v3:vertical" />
                    </v2:vertical>
                </xsl:if>        
            </v2:resolution>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="compression">
        <xsl:if test="v3:compression">
            <v2:extent>
                <xsl:if test="v3:compression/@ratio">
                    <xsl:attribute name="ratio">
                        <xsl:value-of select="v3:compression/@ratio"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="v3:compression" />
            </v2:extent>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="color">
        <xsl:if test="v3:color">
            <v2:color>
                <xsl:if test="v3:color/v3:model">
                    <v2:model>
                        <xsl:value-of select="v3:color/v3:model" />
                    </v2:model>
                </xsl:if>        
                <xsl:if test="v3:color/v3:depth">
                    <v2:depth>
                        <xsl:value-of select="v3:color/v3:depth" />
                    </v2:depth>
                </xsl:if>        
            </v2:color>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="iccProfile">
        <xsl:if test="v3:iccProfile">
            <v2:iccProfile>
                <xsl:value-of select="v3:iccProfile" />
            </v2:iccProfile>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="pictureSize">
        <xsl:if test="v3:pictureSize">
            <v2:pictureSize>
                <xsl:if test="v3:pictureSize/v3:width">
                    <v2:width>
                        <xsl:value-of select="v3:pictureSize/v3:width" />
                    </v2:width>
                </xsl:if>        
                <xsl:if test="v3:pictureSize/v3:height">
                    <v2:height>
                        <xsl:value-of select="v3:pictureSize/v3:height" />
                    </v2:height>
                </xsl:if>        
            </v2:pictureSize>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="registrarScopeIdentifiers">
        <v2:registrarScopeIdentifiers>
            <xsl:for-each select="v3:id">
                <xsl:call-template name="id">
                    <xsl:with-param name="type" select="@type"/>
                    <xsl:with-param name="value" select="."/>
                </xsl:call-template>
            </xsl:for-each>
        </v2:registrarScopeIdentifiers>
    </xsl:template>
    
</xsl:stylesheet>

