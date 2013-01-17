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
    Document   : digDocRegistrationV2ToV3.xsl
    Created on : 3. leden 2013, 0:05
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

    <xsl:template match="/v2:import">
        <v3:import>
            <xsl:apply-templates/>
        </v3:import>
    </xsl:template>
    
    <xsl:template match="v2:monograph">
        <v3:monograph>
            <xsl:apply-templates/>
        </v3:monograph>
    </xsl:template>
    
    <xsl:template match="v2:monographVolume">
        <v3:monographVolume>
            <xsl:apply-templates/>
        </v3:monographVolume>
    </xsl:template>
    
    <xsl:template match="v2:periodical">
        <v3:periodical>
            <xsl:apply-templates/>
        </v3:periodical>
    </xsl:template>
    
    <xsl:template match="v2:periodicalVolume">
        <v3:periodicalVolume>
            <xsl:apply-templates/>
        </v3:periodicalVolume>
    </xsl:template>
    
    <xsl:template match="v2:periodicalIssue">
        <v3:periodicalIssue>
            <xsl:apply-templates/>
        </v3:periodicalIssue>
    </xsl:template>
    
    <xsl:template match="v2:analytical">
        <v3:analytical>
            <xsl:apply-templates/>
        </v3:analytical>
    </xsl:template>
    
    <xsl:template match="v2:thesis">
        <v3:thesis>
            <xsl:apply-templates/>
        </v3:thesis>
    </xsl:template>
    
    <xsl:template match="v2:otherEntity">
        <v3:otherEntity>
            <xsl:apply-templates/>
        </v3:otherEntity>
    </xsl:template>
    
    <xsl:template match="v2:titleInfo">
        <v3:titleInfo>
            <xsl:apply-templates/>
        </v3:titleInfo>
    </xsl:template>
    
    <xsl:template match="v2:title">
        <v3:title>
            <xsl:apply-templates/>
        </v3:title>
    </xsl:template>
    
    <xsl:template match="v2:subTitle">
        <v3:subTitle>
            <xsl:apply-templates/>
        </v3:subTitle>
    </xsl:template>
    
    <xsl:template match="v2:monographTitle">
        <v3:monographTitle>
            <xsl:apply-templates/>
        </v3:monographTitle>
    </xsl:template>
    
    <xsl:template match="v2:volumeTitle">
        <v3:volumeTitle>
            <xsl:apply-templates/>
        </v3:volumeTitle>
    </xsl:template>
    
    <xsl:template match="v2:periodicalTitle">
        <v3:periodicalTitle>
            <xsl:apply-templates/>
        </v3:periodicalTitle>
    </xsl:template>
    
    <xsl:template match="v2:issueTitle">
        <v3:issueTitle>
            <xsl:apply-templates/>
        </v3:issueTitle>
    </xsl:template>
    
    <xsl:template match="v2:ccnb">
        <v3:ccnb>
            <xsl:apply-templates/>
        </v3:ccnb>
    </xsl:template>
    
    <xsl:template match="v2:isbn">
        <v3:isbn>
            <xsl:apply-templates/>
        </v3:isbn>
    </xsl:template>
    
    <xsl:template match="v2:issn">
        <v3:issn>
            <xsl:apply-templates/>
        </v3:issn>
    </xsl:template>
    
    <xsl:template match="v2:otherId">
        <v3:otherId>
            <xsl:apply-templates/>
        </v3:otherId>
    </xsl:template>
    
    <xsl:template match="v2:documentType">
        <v3:documentType>
            <xsl:apply-templates/>
        </v3:documentType>
    </xsl:template>
    
    <xsl:template match="v2:digitalBorn">
        <v3:digitalBorn>
            <xsl:apply-templates/>
        </v3:digitalBorn>
    </xsl:template>
    
    <xsl:template match="v2:primaryOriginator">
        <xsl:choose>
            <xsl:when test="@type">
                <v3:primaryOriginator>
                    <xsl:attribute name="type">
                        <xsl:value-of select="@type"/>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </v3:primaryOriginator>    
            </xsl:when> 
            <xsl:otherwise>
                <v3:primaryOriginator>
                    <xsl:apply-templates/>
                </v3:primaryOriginator>    
            </xsl:otherwise>   
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="v2:otherOriginator">
        <v3:otherOriginator>
            <xsl:apply-templates/>
        </v3:otherOriginator>
    </xsl:template>
    
    <xsl:template match="v2:sourceDocument">
        <v3:sourceDocument>
            <xsl:apply-templates/>
        </v3:sourceDocument>
    </xsl:template>
    
    <xsl:template match="v2:publication">
        <v3:publication>
            <xsl:apply-templates/>
        </v3:publication>
    </xsl:template>
    
    <xsl:template match="v2:publisher">
        <v3:publisher>
            <xsl:apply-templates/>
        </v3:publisher>
    </xsl:template>
    
    <xsl:template match="v2:place">
        <v3:place>
            <xsl:apply-templates/>
        </v3:place>
    </xsl:template>
    
    <xsl:template match="v2:year">
        <v3:year>
            <xsl:apply-templates/>
        </v3:year>
    </xsl:template>
    
    <xsl:template match="v2:digitalDocument">
        <v3:digitalDocument>
            <xsl:apply-templates/>
        </v3:digitalDocument>
    </xsl:template>
        
    <xsl:template match="v2:archiverId">
        <v3:archiverId>
            <xsl:apply-templates/>
        </v3:archiverId>
    </xsl:template>
    
    <xsl:template match="v2:urnNbn">
        <v3:urnNbn>
            <v3:value>
              <xsl:apply-templates/>
            </v3:value>    
        </v3:urnNbn>
    </xsl:template>
    
    <xsl:template match="v2:registrarScopeIdentifiers">
        <v3:registrarScopeIdentifiers>
            <xsl:apply-templates/>
        </v3:registrarScopeIdentifiers>
    </xsl:template>
    
    <xsl:template match="v2:id">
        <xsl:choose>
            <xsl:when test="@type">
                <v3:id>
                    <xsl:attribute name="type">
                        <xsl:value-of select="@type"/>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </v3:id>    
            </xsl:when> 
            <xsl:otherwise>
                <v3:id>
                    <xsl:apply-templates/>
                </v3:id>    
            </xsl:otherwise>   
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="v2:financed">
        <v3:financed>
            <xsl:apply-templates/>
        </v3:financed>
    </xsl:template>
    
    <xsl:template match="v2:contractNumber">
        <v3:contractNumber>
            <xsl:apply-templates/>
        </v3:contractNumber>
    </xsl:template>
    
    <xsl:template match="v2:technicalMetadata">
        <v3:technicalMetadata>
            <xsl:apply-templates/>
        </v3:technicalMetadata>
    </xsl:template>
    
    <xsl:template match="v2:format">
        <xsl:choose>
            <xsl:when test="@version">
                <v3:format>
                    <xsl:attribute name="version">
                        <xsl:value-of select="@version"/>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </v3:format>    
            </xsl:when> 
            <xsl:otherwise>
                <v3:format>
                    <xsl:apply-templates/>
                </v3:format>    
            </xsl:otherwise>   
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="v2:extent">
        <v3:extent>
            <xsl:apply-templates/>
        </v3:extent>
    </xsl:template>
    
    <xsl:template match="v2:resolution">
        <v3:resolution>
            <xsl:apply-templates/>
        </v3:resolution>
    </xsl:template>
    
    <xsl:template match="v2:horizontal">
        <v3:horizontal>
            <xsl:apply-templates/>
        </v3:horizontal>
    </xsl:template>
    
    <xsl:template match="v2:vertical">
        <v3:vertical>
            <xsl:apply-templates/>
        </v3:vertical>
    </xsl:template>
    
    <xsl:template match="v2:compression">
        <xsl:choose>
            <xsl:when test="@ratio">
                <v3:compression>
                    <xsl:attribute name="ratio">
                        <xsl:value-of select="@ratio"/>
                    </xsl:attribute>
                    <xsl:apply-templates/>
                </v3:compression>    
            </xsl:when> 
            <xsl:otherwise>
                <v3:compression>
                    <xsl:apply-templates/>
                </v3:compression>    
            </xsl:otherwise>   
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="v2:color">
        <v3:color>
            <xsl:apply-templates/>
        </v3:color>
    </xsl:template>
    
    <xsl:template match="v2:model">
        <v3:model>
            <xsl:apply-templates/>
        </v3:model>
    </xsl:template>
    
    <xsl:template match="v2:depth">
        <v3:depth>
            <xsl:apply-templates/>
        </v3:depth>
    </xsl:template>
    
    <xsl:template match="v2:iccProfile">
        <v3:iccProfile>
            <xsl:apply-templates/>
        </v3:iccProfile>
    </xsl:template>
    
    <xsl:template match="v2:pictureSize">
        <v3:pictureSize>
            <xsl:apply-templates/>
        </v3:pictureSize>
    </xsl:template>
    
    <xsl:template match="v2:width">
        <v3:width>
            <xsl:apply-templates/>
        </v3:width>
    </xsl:template>
    
    <xsl:template match="v2:height">
        <v3:height>
            <xsl:apply-templates/>
        </v3:height>
    </xsl:template>
    
    <xsl:template match="v2:degreeAwardingInstitution">
        <v3:degreeAwardingInstitution>
            <xsl:apply-templates/>
        </v3:degreeAwardingInstitution>
    </xsl:template>
    
</xsl:stylesheet>
