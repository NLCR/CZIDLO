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
                exclude-result-prefixes="v2"
>
    <xsl:output method="xml" indent="no"/>
    
    <xsl:template match="/v2:digitalInstance"> 
        <v3:digitalInstance>
            <xsl:apply-templates/>
        </v3:digitalInstance>
    </xsl:template>
    
    <xsl:template match="v2:url">
        <v3:url>
            <xsl:apply-templates/>
        </v3:url>
    </xsl:template>
    
    <xsl:template match="v2:digitalLibraryId">
        <v3:digitalLibraryId>
            <xsl:apply-templates/>
        </v3:digitalLibraryId>
    </xsl:template>
    
    <xsl:template match="v2:format">
        <v3:format>
            <xsl:apply-templates/>
        </v3:format>
    </xsl:template>
    
    <xsl:template match="v2:accessibility">
        <v3:accessibility>
            <xsl:apply-templates/>
        </v3:accessibility>
    </xsl:template>
    
</xsl:stylesheet>
