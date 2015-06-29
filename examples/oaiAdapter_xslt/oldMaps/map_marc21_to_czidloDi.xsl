<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:marc="http://www.loc.gov/MARC21/slim"
                xmlns:r="http://resolver.nkp.cz/v3/"
                exclude-result-prefixes="marc">

    <xsl:output method="xml"
            encoding="UTF-8"
            indent="yes"
            omit-xml-declaration="yes"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
            />


    <xsl:template match="/">                
        <r:digitalInstance xmlns:r="http://resolver.nkp.cz/v3/">        
            <xsl:call-template name="url"/>
            <xsl:call-template name="digitalLibraryId"/>                
            <xsl:call-template name="format"/>   
            <xsl:call-template name="accessibility"/>    
        </r:digitalInstance>        
    </xsl:template>
        
    
    <xsl:template name="url">           
        <xsl:variable name = "url" >
            <xsl:value-of select="//marc:datafield[@tag='856']/marc:subfield[@code='u']"/>
        </xsl:variable>   
        <xsl:if test="substring($url,1,25) = 'http://imageserver.mzk.cz'">
            <r:url>
                <xsl:value-of select="$url"/>    
            </r:url>
        </xsl:if>                
    </xsl:template>    

    <xsl:template name="digitalLibraryId">                       
        <r:digitalLibraryId>44</r:digitalLibraryId>      
    </xsl:template>    

    <xsl:template name="format">                              
            <r:format>JPEG 2000</r:format>
    </xsl:template>       

    <xsl:template name="accessibility">                       
            <r:accessibility>volně přístupné</r:accessibility>
    </xsl:template>       

     
</xsl:stylesheet>

