<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:marc="http://www.loc.gov/MARC21/slim"
                xmlns:r="http://resolver.nkp.cz/v2/"
                exclude-result-prefixes="marc">

    <xsl:output method="xml"
            encoding="UTF-8"
            indent="yes"
            omit-xml-declaration="yes"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            />
            
            
            
    <xsl:template match="/">
        <r:import xmlns:r="http://resolver.nkp.cz/v2/">
            
            <r:otherEntity> 
                <xsl:call-template name="titleInfo"/>
            </r:otherEntity>
           
        </r:import>
    </xsl:template>
    

    
    
    <xsl:template name="titleInfo">        
        <r:titleInfo>
            <xsl:call-template name="title"/>    
            <xsl:call-template name="subTitle"/>                    
        </r:titleInfo>  
    </xsl:template>    
    
    <xsl:template name="subTitle">        
        <xsl:if test="//marc:datafield[@tag='245']/marc:subfield[@code='b']">
            <r:subTitle>
                <xsl:value-of select="//marc:datafield[@tag='245']/marc:subfield[@code='b']"/>
            </r:subTitle>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="title">                       
        <r:title>
            <xsl:value-of select="//marc:datafield[@tag='245']/marc:subfield[@code='a']"/>
        </r:title>
    </xsl:template>
    
    
    
    <xsl:template name="author">
        <div class="title">
            <h2>

                <xsl:choose>
                    <xsl:when test="//marc:datafield[@tag='100']">Autor:
                        <xsl:value-of select="//marc:datafield[@tag='100']/marc:subfield[@code='a']"/>
                        <xsl:text> </xsl:text>
                        <xsl:value-of select="//marc:datafield[@tag='100']/marc:subfield[@code='d']"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>Autor neznámý</xsl:text>

                    </xsl:otherwise>
                </xsl:choose>
            </h2>
        </div>
    </xsl:template>

</xsl:stylesheet>
