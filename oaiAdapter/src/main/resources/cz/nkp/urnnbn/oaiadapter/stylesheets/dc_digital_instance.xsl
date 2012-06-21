<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:r="http://resolver.nkp.cz/v2/"
                exclude-result-prefixes="dc">                    

    <xsl:output method="xml"
            encoding="UTF-8"
            indent="yes"
            omit-xml-declaration="yes"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            />
            
            
            
    <xsl:template match="/">                
        <r:digitalInstance xmlns:r="http://resolver.nkp.cz/v2/">        
            <xsl:call-template name="url"/>
            <xsl:call-template name="digitalLibraryId"/>                
            <xsl:call-template name="format"/>   
            <xsl:call-template name="accessibility"/>    
        </r:digitalInstance>        
    </xsl:template>
        
    
    <xsl:template name="url">           
        <xsl:variable name = "pid" >
            <xsl:value-of select="//dc:identifier"/>
        </xsl:variable> 
        <r:url>
            <xsl:value-of select = "concat('http://kramerius.mzk.cz/search/handle/',$pid)" /> 
        </r:url>
       <!-- <xsl:if test="//dc:source">
            <r:url>
                <xsl:value-of select="//dc:source"/>
            
        </xsl:if> -->
    </xsl:template>    

    <xsl:template name="digitalLibraryId">                       
        <r:digitalLibraryId>41</r:digitalLibraryId>      
    </xsl:template>    

    <xsl:template name="format">                       
        <xsl:if test="//dc:format">
            <r:format>
                <xsl:value-of select="//dc:format"/>
            </r:format>
        </xsl:if>
    </xsl:template>       

    <xsl:template name="accessibility">                       
        <xsl:if test="//dc:rights">
            <r:accessibility>
                <xsl:value-of select="//dc:rights"/>
            </r:accessibility>
        </xsl:if>
    </xsl:template>       
     
     
     
</xsl:stylesheet>
