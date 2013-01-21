<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : getDigitalDocument.xsl
    Created on : 21. leden 2013, 5:09
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
        <v2:digitalDocument>
            <xsl:for-each select="v3:digitalDocument">
                <xsl:call-template name="digitalDocument"/>
            </xsl:for-each>
            
            <xsl:for-each select="v3:digitalDocument/v3:intelectualEntity">
                <xsl:call-template name="intelectualEntity"/>
            </xsl:for-each>
        
            <xsl:for-each select="v3:digitalDocument/v3:technicalMetadata">
                <xsl:call-template name="technicalMetadata"/>
            </xsl:for-each>
            
            <xsl:for-each select="v3:digitalDocument/v3:registrar">
                <xsl:call-template name="registrar"/>
            </xsl:for-each>
       
            <xsl:for-each select="v3:digitalDocument/v3:registrarScopeIdentifiers">
                <xsl:call-template name="registrarScopeIdentifiers"/>
            </xsl:for-each>
        
            <xsl:for-each select="v3:digitalDocument/v3:archiver">
                <xsl:call-template name="archiver"/>
            </xsl:for-each>
        
            <xsl:for-each select="v3:digitalDocument/v3:digitalInstances">
                <xsl:call-template name="digitalInstances"/>
            </xsl:for-each>
            
        </v2:digitalDocument>
    </xsl:template>
    
    <xsl:template name="digitalDocument">
        <xsl:call-template name="created"/>
        <xsl:call-template name="modified"/>
 
        <v2:urnNbn>
            <xsl:value-of select="v3:urnNbn/v3:value" />
        </v2:urnNbn>
        
        <xsl:if test="v3:financed">
            <v2:financed>
                <xsl:value-of select="v3:financed" />
            </v2:financed>
        </xsl:if>
        
        <xsl:if test="v3:contractNumber">
            <v2:contractNumber>
                <xsl:value-of select="v3:contractNumber" />
            </v2:contractNumber>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="intelectualEntity">
        <v2:intelectualEntity>
            <xsl:attribute name="type">
                <xsl:value-of select="@type"/>
            </xsl:attribute>
            
            <xsl:for-each select="v3:titleInfo">
                <xsl:call-template name="titleInfo"/>
            </xsl:for-each>
            
            <xsl:call-template name="ieIdentifiers"/>
            
            <xsl:if test="v3:documentType">
                <v2:documentType>
                    <xsl:value-of select="v3:documentType"/>
                </v2:documentType>
            </xsl:if>
            
            <xsl:if test="v3:digitalBorn">
                <v2:digitalBorn>
                    <xsl:value-of select="v3:digitalBorn"/>
                </v2:digitalBorn>
            </xsl:if>
            
            <xsl:if test="v3:primaryOriginator">
                <v2:primaryOriginator>
                    <xsl:attribute name="type">
                        <xsl:value-of select="v3:primaryOriginator/@type"/>
                    </xsl:attribute>
                    <xsl:value-of select="v3:primaryOriginator"/>
                </v2:primaryOriginator>
            </xsl:if>
            
            <xsl:if test="v3:otherOriginator">
                <v2:otherOriginator>
                    <xsl:value-of select="v3:otherOriginator"/>
                </v2:otherOriginator>
            </xsl:if>
            
            <xsl:if test="v3:publication">
                <v2:publication>
                    <xsl:if test="v3:publication/v3:publisher">
                        <v2:publisher>
                            <xsl:value-of select="v3:publication/v3:publisher"/>
                        </v2:publisher>
                    </xsl:if>
                    <xsl:if test="v3:publication/v3:place">
                        <v2:place>
                            <xsl:value-of select="v3:publication/v3:place"/>
                        </v2:place>
                    </xsl:if>
                    <xsl:if test="v3:publication/v3:year">
                        <v2:year>
                            <xsl:value-of select="v3:publication/v3:year"/>
                        </v2:year>
                    </xsl:if>
                </v2:publication>
            </xsl:if>
            
        </v2:intelectualEntity>
    </xsl:template>
    
    <xsl:template name="ieIdentifiers">
        <xsl:if test="v3:ccnb">
            <v2:ccnb>
                <xsl:value-of select="v3:ccnb"/>
            </v2:ccnb>
        </xsl:if>
        
        <xsl:if test="v3:isbn">
            <v2:isbn>
                <xsl:value-of select="v3:isbn"/>
            </v2:isbn>
        </xsl:if>
        
        <xsl:if test="v3:issn">
            <v2:issn>
                <xsl:value-of select="v3:issn"/>
            </v2:issn>
        </xsl:if>
        
        <xsl:if test="v3:otherId">
            <v2:otherId>
                <xsl:value-of select="v3:otherId"/>
            </v2:otherId>
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
    
    <xsl:template name="format">
        <xsl:if test="v3:format">
            <v2:format>
                <xsl:value-of select="v3:format" />
            </v2:format>
        </xsl:if>
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
    
    <xsl:template name="created">
        <xsl:if test="v3:created">
            <v2:created>
                <xsl:value-of select="v3:created" />
            </v2:created>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="modified">
        <xsl:if test="v3:modified">
            <v2:modified>
                <xsl:value-of select="v3:modified" />
            </v2:modified>
        </xsl:if>
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
    
    <xsl:template name="archiver">
        <v2:archiver>
            <xsl:if test="@id">
                <v2:id>
                    <xsl:attribute name="type">INTERNAL</xsl:attribute>
                    <xsl:value-of select="@id"/>
                </v2:id>
            </xsl:if>
            <xsl:call-template name="created"/>
            <xsl:call-template name="modified"/>
            <xsl:call-template name="name"/>
            <xsl:call-template name="description"/>
        </v2:archiver>
    </xsl:template>
    
    <xsl:template name="digitalInstances">
        <v2:digitalInstances>
            <xsl:if test="@count">
                <xsl:attribute name="count">
                    <xsl:value-of select="@count"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:for-each select="v3:digitalInstance">
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
    
    <xsl:template name="deactivated">
        <xsl:if test="v3:deactivated">
            <v2:modified>
                <xsl:value-of select="v3:deactivated" />
            </v2:modified>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="url">
        <xsl:if test="v3:url">
            <v2:url>
                <xsl:value-of select="v3:url" />
            </v2:url>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="titleInfo">
        <v2:titleInfo>
            <xsl:if test="v3:title">
                <v2:title>
                    <xsl:value-of select="v3:title"/>
                </v2:title>
            </xsl:if>
            
            <xsl:if test="v3:monographTitle">
                <v2:monographTitle>
                    <xsl:value-of select="v3:monographTitle"/>
                </v2:monographTitle>
            </xsl:if>
            
            <xsl:if test="v3:periodicalTitle">
                <v2:periodicalTitle>
                    <xsl:value-of select="v3:periodicalTitle"/>
                </v2:periodicalTitle>
            </xsl:if>
            
            <xsl:if test="v3:volumeTitle">
                <v2:volumeTitle>
                    <xsl:value-of select="v3:volumeTitle"/>
                </v2:volumeTitle>
            </xsl:if>
            
            <xsl:if test="v3:issueTitle">
                <v2:issueTitle>
                    <xsl:value-of select="v3:issueTitle"/>
                </v2:issueTitle>
            </xsl:if>
            
            <xsl:if test="v3:subTitle">
                <v2:subTitle>
                    <xsl:value-of select="v3:subTitle"/>
                </v2:subTitle>
            </xsl:if>
        </v2:titleInfo>
    </xsl:template>
    
    <xsl:template name="accessibility">
        <xsl:if test="v3:accessibility">
            <v2:accessibility>
                <xsl:value-of select="v3:accessibility" />
            </v2:accessibility>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>