<?xml version="1.0" encoding="UTF-8"?>

<!--
Copyright (C) 2012 Martin Řehánek

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-->

<!--
    Document   : digInstImportV2ToV3.xsl
    Created on : 3. leden 2013, 1:31
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
    
    <xsl:template match="/"> 
        <v2:registrars>
            <xsl:apply-templates/>
        </v2:registrars>
    </xsl:template>
    
    <xsl:template match="v3:registrar">
        <v2:registrar>
            <xsl:attribute name="code">
                <xsl:value-of select="@code"/>
            </xsl:attribute>
            <xsl:call-template name="created"/>
            <xsl:call-template name="modified"/>
            <xsl:call-template name="name"/>
            <xsl:call-template name="description"/>
            <xsl:if test="v3:digitalLibraries">
                <xsl:call-template name="digitalLibraries">
                    <xsl:with-param name="registrar" select="."/>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="v3:catalogs">
                <xsl:call-template name="catalogs">
                    <xsl:with-param name="registrar" select="."/>
                </xsl:call-template>
            </xsl:if>
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
        <v2:description>
            <xsl:value-of select="v3:description" />
        </v2:description>
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
    
    <xsl:template name="urlPrefix">
        <xsl:param name="root"/>
        <xsl:if test="$root/v3:urlPrefix">
            <v2:urlPrefix>
                <xsl:value-of select="$root/v3:urlPrefix" />
            </v2:urlPrefix>
        </xsl:if>
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

</xsl:stylesheet>
