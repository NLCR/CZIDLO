<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:r="http://resolver.nkp.cz/v3/"
                exclude-result-prefixes="dc">                    

    <xsl:output method="xml"
            encoding="UTF-8"
            indent="yes"
            omit-xml-declaration="yes"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            />                        
            
    <xsl:template match="/">
        <r:import xmlns:r="http://resolver.nkp.cz/v3/">            
            <r:analytical> 
                <xsl:call-template name="titleInfo"/>
                <xsl:call-template name="documentType"/>   
                <xsl:call-template name="creator"/>   
                <xsl:call-template name="sourceDocument"/>
            </r:analytical>
            <xsl:call-template name="digitalDocument"/>
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
        <r:documentType>článek</r:documentType>        
    </xsl:template>
    
    
    <xsl:template name="title">                       
        <r:title>
            <xsl:call-template name="cutString">
                <xsl:with-param name="string" select="//dc:title"/>
                <xsl:with-param name="maxLength" select="100"/>
            </xsl:call-template>	
        </r:title>
    </xsl:template>

    <xsl:template name="creator">                       
        <xsl:if test="//dc:creator">
            <r:primaryOriginator type="AUTHOR">
                <xsl:value-of select="//dc:creator"/>
            </r:primaryOriginator>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="sourceDocument">
        <r:sourceDocument>
            <r:titleInfo>
                <r:title>Duha</r:title>
            </r:titleInfo>
            <r:issn>ISSN:1804-4255</r:issn>
            <r:publication>
                <r:publisher>Moravská zemská knihovna</r:publisher>
                <r:place>Brno</r:place>
            </r:publication>
        </r:sourceDocument>    
    </xsl:template>
    
    <xsl:template name="digitalDocument">           
        <r:digitalDocument>
            <!-- URN:NBN -->
            <xsl:variable name="URN">
                <xsl:value-of select='//dc:identifier[starts-with(., "urn:nbn:")]'/>
            </xsl:variable>
            
            <xsl:if test="$URN != ''">
                <r:urnNbn>
                    <r:value>
                        <xsl:value-of select='$URN'/>
                    </r:value>
                </r:urnNbn>
            </xsl:if>
            
            <r:technicalMetadata>
                <r:format>html</r:format>
            </r:technicalMetadata>
        </r:digitalDocument>
    </xsl:template>        

    
    
    <xsl:template name="cutString">
        <xsl:param name="string"/>
        <xsl:param name="maxLength"/>          
        <xsl:variable name="length" select="string-length($string)"/>
        <xsl:choose>
            <xsl:when test="$length &gt; $maxLength">
                <xsl:value-of select="substring($string, 1, $maxLength)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$string"/>
            </xsl:otherwise>
        </xsl:choose>                                
    </xsl:template>    
     
</xsl:stylesheet>
