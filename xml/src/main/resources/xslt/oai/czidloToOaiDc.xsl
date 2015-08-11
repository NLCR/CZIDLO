<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : czidloToOaiDc.xsl
    Created on : 22. červenec 2012, 19:22
    Author     : Martin Řehánek
    Description:
        Template for transforming digital document record in internal format of URN:NBN Resolver into oai_dc.
-->
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:resolver="http://resolver.nkp.cz/v3/"
                xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/" 
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                exclude-result-prefixes="resolver"
>
    <xsl:output encoding="UTF-8" method="xml" indent="yes" />
    <!--remove content of elements containing only white spaces-->
    <xsl:strip-space elements="*" />
    
    <xsl:template match="//resolver:digitalDocument">
        <oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <!--<oai_dc:dc>-->
            <xsl:variable name="entityType" select="//resolver:intelectualEntity/@type"/>
            <xsl:choose>
                <xsl:when test="$entityType='MONOGRAPH'">
                    <xsl:call-template name="monograph"/>
                </xsl:when>
                <xsl:when test="$entityType='MONOGRAPH_VOLUME'">
                    <xsl:call-template name="monographVolume"/>
                </xsl:when>
                <xsl:when test="$entityType='PERIODICAL'">
                    <xsl:call-template name="periodical"/>
                </xsl:when>
                <xsl:when test="$entityType='PERIODICAL_VOLUME'">
                    <xsl:call-template name="periodicalVolume"/>
                </xsl:when>
                <xsl:when test="$entityType='PERIODICAL_ISSUE'">
                    <xsl:call-template name="periodicalIssue"/>
                </xsl:when>
                <xsl:when test="$entityType='THESIS'">
                    <xsl:call-template name="thesis"/>
                </xsl:when>
                <xsl:when test="$entityType='ANALYTICAL'">
                    <xsl:call-template name="analytical"/>
                </xsl:when>
                <xsl:when test="$entityType='OTHER'">
                    <xsl:call-template name="otherEntity"/>
                </xsl:when>
            </xsl:choose>
        </oai_dc:dc>
    </xsl:template>
    
    <xsl:template name="monograph">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:title"/><xsl:for-each select="//resolver:intelectualEntity/resolver:titleInfo/resolver:subTitle"> (<xsl:value-of select="."/>)</xsl:for-each></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="monographVolume">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:monographTitle"/>, <xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:volumeTitle"/></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="periodical">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:title"/><xsl:for-each select="//resolver:intelectualEntity/resolver:titleInfo/resolver:subTitle"> (<xsl:value-of select="."/>)</xsl:for-each></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="periodicalVolume">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:periodicalTitle"/>, <xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:volumeTitle"/></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="periodicalIssue">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:periodicalTitle"/><xsl:for-each select="//resolver:intelectualEntity/resolver:titleInfo/resolver:volumeTitle">, <xsl:value-of select="."/></xsl:for-each>, <xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:issueTitle"/></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="thesis">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:title"/><xsl:for-each select="//resolver:intelectualEntity/resolver:titleInfo/resolver:subTitle"> (<xsl:value-of select="."/>)</xsl:for-each></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="analytical">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:title"/><xsl:for-each select="//resolver:intelectualEntity/resolver:titleInfo/resolver:subTitle"> (<xsl:value-of select="."/>)</xsl:for-each></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="otherEntity">
        <dc:title><xsl:value-of select="//resolver:intelectualEntity/resolver:titleInfo/resolver:title"/><xsl:for-each select="//resolver:intelectualEntity/resolver:titleInfo/resolver:subTitle"> (<xsl:value-of select="."/>)</xsl:for-each></dc:title>
        <xsl:call-template name="entityData"/>
        <xsl:call-template name="technical"/>
    </xsl:template> 
    
    <xsl:template name="entityData">
        <xsl:for-each select="//resolver:intelectualEntity">
            <xsl:call-template name="isbn"/>
            <xsl:call-template name="ccnb"/>
            <xsl:call-template name="otherId"/>
            <xsl:call-template name="documentType"/>
            <xsl:call-template name="primaryOriginator"/>
            <xsl:call-template name="otherOriginator"/>
            <xsl:call-template name="publication"/>
            <xsl:call-template name="sourceDoc"/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="isbn">
        <xsl:for-each select="resolver:isbn">
            <dc:identifier>isbn:<xsl:value-of select="."/></dc:identifier>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="ccnb">
        <xsl:for-each select="resolver:ccnb">
            <dc:identifier>ccnb:<xsl:value-of select="."/></dc:identifier>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="otherId">
        <xsl:for-each select="resolver:otherId">
            <dc:identifier><xsl:value-of select="."/></dc:identifier>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="documentType">
        <xsl:for-each select="resolver:documentType">
            <dc:type><xsl:value-of select="."/></dc:type>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="primaryOriginator">
        <xsl:for-each select="resolver:primaryOriginator">
            <xsl:if test="./@type='AUTHOR'">
                <dc:creator><xsl:value-of select="."/></dc:creator>
            </xsl:if>
            <xsl:if test="./@type='CORPORATION'">
                <dc:creator><xsl:value-of select="."/></dc:creator>
            </xsl:if>
            <!--no dc:creator for ./@type='EVENT'-->
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="otherOriginator">
        <xsl:for-each select="resolver:otherOriginator">
            <dc:contributor><xsl:value-of select="."/></dc:contributor>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="publication">
        <xsl:for-each select="resolver:publication">
            <xsl:choose>
                <xsl:when test="resolver:publisher">
                    <xsl:choose>
                        <xsl:when test="resolver:place">
                            <dc:publisher><xsl:value-of select="resolver:place"/>: <xsl:value-of select="resolver:publisher"/></dc:publisher>                    
                        </xsl:when>
                        <xsl:otherwise>
                            <dc:publisher><xsl:value-of select="resolver:publisher"/></dc:publisher>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="resolver:place">
                            <dc:publisher><xsl:value-of select="resolver:place"/></dc:publisher>
                        </xsl:when>
                        <xsl:otherwise>
                            <!--no place no publisher-->
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="resolver:year">
                <dc:date><xsl:value-of select="resolver:year"/></dc:date>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="sourceDoc">
        <xsl:for-each select="resolver:sourceDocument">
            <xsl:call-template name="sourceDocTitleInfo"/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="sourceDocTitleInfo">
        <xsl:for-each select="resolver:titleInfo">
            <xsl:choose>
                <xsl:when test="resolver:title">
                    <xsl:choose>
                        <xsl:when test="resolver:volumeTitle">
                            <xsl:choose>
                                <xsl:when test="resolver:issueTitle">
                                    <dc:source><xsl:value-of select="resolver:title"/>, <xsl:value-of select="resolver:volumeTitle"/>, <xsl:value-of select="resolver:issueTitle"/> <xsl:call-template name="subtitle"/></dc:source>
                                </xsl:when>
                                <xsl:otherwise>
                                    <dc:source><xsl:value-of select="resolver:title"/>, <xsl:value-of select="resolver:volumeTitle"/> <xsl:call-template name="subtitle"/></dc:source>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:choose>
                                <xsl:when test="resolver:issueTitle">
                                    <dc:source><xsl:value-of select="resolver:title"/>, <xsl:value-of select="resolver:issueTitle"/> <xsl:call-template name="subtitle"/></dc:source>
                                </xsl:when>
                                <xsl:otherwise>
                                    <dc:source><xsl:value-of select="resolver:title"/> <xsl:call-template name="subtitle"/></dc:source>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="resolver:volumeTitle">
                            <xsl:choose>
                                <xsl:when test="resolver:issueTitle">
                                    <dc:source><xsl:value-of select="resolver:volumeTitle"/>, <xsl:value-of select="resolver:issueTitle"/> <xsl:call-template name="subtitle"/></dc:source>
                                </xsl:when>
                                <xsl:otherwise>
                                    <dc:source><xsl:value-of select="resolver:volumeTitle"/> <xsl:call-template name="subtitle"/></dc:source>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:choose>
                                <xsl:when test="resolver:issueTitle">
                                    <dc:source><xsl:value-of select="resolver:issueTitle"/> <xsl:call-template name="subtitle"/></dc:source>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!--empty titleInfo-->
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="subtitle">
        <xsl:if test="resolver:subtitle">(<xsl:value-of select="resolver:subitle"/>)</xsl:if>
    </xsl:template>
    
    <xsl:template name="technical">
        <xsl:for-each select="resolver:technicalMetadata">
            <xsl:call-template name="format"/>
            <xsl:call-template name="extent"/>
            <xsl:call-template name="resolution"/>
            <xsl:call-template name="pictureSize"/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="format">
        <xsl:for-each select="resolver:format">
            <dc:format><xsl:value-of select="."/></dc:format>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="extent">
        <xsl:for-each select="resolver:extent">
            <dc:coverage><xsl:value-of select="."/></dc:coverage>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="resolution">
        <xsl:for-each select="resolver:resolution">
            <dc:description><xsl:value-of select="resolver:horizontal"/>x<xsl:value-of select="resolver:vertical"/></dc:description>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="pictureSize">
        <xsl:for-each select="resolver:pictureSize">
            <dc:description><xsl:value-of select="resolver:width"/>x<xsl:value-of select="resolver:height"/> px</dc:description>
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>
