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
        <v2:registrars><xsl:apply-templates/></v2:registrars>
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
        </v2:registrar>
        
    </xsl:template>
         
    <xsl:template name="created">
        <xsl:if test="v3:created">
            <v2:created><xsl:value-of select="v3:created" /></v2:created>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="modified">
        <xsl:if test="v3:modified">
            <v2:modified><xsl:value-of select="v3:modified" /></v2:modified>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="name">
        <xsl:if test="v3:name">
            <v2:name><xsl:value-of select="v3:name" /></v2:name>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="description">
       <v2:description><xsl:value-of select="v3:description" /></v2:description>
    </xsl:template>

</xsl:stylesheet>
