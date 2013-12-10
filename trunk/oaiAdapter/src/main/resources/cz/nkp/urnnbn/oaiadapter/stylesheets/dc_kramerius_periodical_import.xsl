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
            <r:periodical> 
                <xsl:call-template name="titleInfo"/>
                <xsl:call-template name="issn"/>
                <xsl:call-template name="documentType"/>                
                <xsl:call-template name="creator"/>   
                <xsl:call-template name="contributor"/>   
                <xsl:call-template name="publication"/>                 
            </r:periodical>
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
        <xsl:if test="//dc:type">
            <r:documentType>
                <xsl:value-of select="//dc:type"/>
            </r:documentType>        
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template name="title">                       
        <r:title>
            <xsl:call-template name="cutString">
                <xsl:with-param name="string" select="//dc:title"/>
                <xsl:with-param name="maxLength" select="50"/>
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
    
    
    <xsl:template name="issn">  
        <xsl:variable name="issn">
            <xsl:value-of select='//dc:identifier[starts-with(.,"issn:")]'/>
        </xsl:variable>            
        <xsl:if test='$issn and substring($issn,1,9) != "issn:issn" and string-length($issn) = 14'>
            <r:issn>
                <xsl:value-of select="substring($issn, 6)"/>
            </r:issn>
        </xsl:if>
    </xsl:template>        
    

    <xsl:template name="contributor">                       
        <xsl:if test="//dc:contributor">
            <r:otherOriginator>
                <xsl:value-of select="//dc:contributor"/>
            </r:otherOriginator>
        </xsl:if>
    </xsl:template>    
    
    
    
    <xsl:template name="publication">        
        <r:publication>
            <xsl:call-template name="publisher"/>    
            <xsl:call-template name="year"/>                    
        </r:publication>  
    </xsl:template>        

    
    <xsl:template name="publisher">                       
        <xsl:if test="//dc:publisher">
            <r:publisher>
                <xsl:call-template name="cutString">
                    <xsl:with-param name="string" select="//dc:publisher"/>
                    <xsl:with-param name="maxLength" select="50"/>
                </xsl:call-template>		                                
            </r:publisher>
        </xsl:if>
    </xsl:template>    
    
    <xsl:template name="year">   
        <xsl:variable name="year">
            <xsl:value-of select="//dc:date"/>
        </xsl:variable>            
        <xsl:if test="$year and floor($year) = $year">
            <r:year>                
                <xsl:value-of select="$year"/>
            </r:year>
        </xsl:if>        
    </xsl:template>    
    
    
    
    
    <xsl:template name="digitalDocument">           
        <r:digitalDocument>
            <r:registrarScopeIdentifiers>
                <xsl:variable name="K4_pid">
                    <xsl:value-of select='//dc:identifier[starts-with(.,"uuid")]'/>
                </xsl:variable>            
                <xsl:if test='$K4_pid'>
                    <r:id type="K4_pid">
                        <xsl:value-of select="$K4_pid"/>
                    </r:id>
                </xsl:if>
            </r:registrarScopeIdentifiers>            
            <r:technicalMetadata/>
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
