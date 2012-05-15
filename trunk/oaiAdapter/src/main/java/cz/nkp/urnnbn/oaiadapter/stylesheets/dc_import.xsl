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
        <r:import xmlns:r="http://resolver.nkp.cz/v2/">            
            <r:monograph> 
                <xsl:call-template name="titleInfo"/>
                <xsl:call-template name="documentType"/>                
                <xsl:call-template name="creator"/>   
                <xsl:call-template name="publication"/>    
            </r:monograph>
        </r:import>
    </xsl:template>
    

    
    
    <xsl:template name="titleInfo">        
        <r:titleInfo>
            <xsl:call-template name="title"/>    
            <xsl:call-template name="subTitle"/>                    
        </r:titleInfo>  
    </xsl:template>    

    
    
    
    <xsl:template name="subTitle">        
    </xsl:template>

    <xsl:template name="documentType">        
        <r:documentType>kniha</r:documentType>        
    </xsl:template>
    
    
    <xsl:template name="title">                       
        <r:title>
            <xsl:value-of select="//dc:title"/>
        </r:title>
    </xsl:template>

    <xsl:template name="creator">                       
        <xsl:if test="//dc:creator">
            <r:primaryOriginator type="AUTHOR">
                <xsl:value-of select="//dc:creator"/>
            </r:primaryOriginator>
        </xsl:if>
    </xsl:template>
    
    
    
    <xsl:template name="publication">        
        <r:titleInfo>
            <xsl:call-template name="publisher"/>    
            <xsl:call-template name="year"/>                    
        </r:titleInfo>  
    </xsl:template>        

    
    <xsl:template name="publisher">                       
        <xsl:if test="//dc:publisher">
            <r:publisher>
                <xsl:value-of select="//dc:publisher"/>
            </r:publisher>
        </xsl:if>
    </xsl:template>    
    
    <xsl:template name="year">                       
        <xsl:if test="//dc:date">
            <r:publisher>
                <xsl:value-of select="//dc:date"/>
            </r:publisher>
        </xsl:if>
    </xsl:template>    
    
     
</xsl:stylesheet>
