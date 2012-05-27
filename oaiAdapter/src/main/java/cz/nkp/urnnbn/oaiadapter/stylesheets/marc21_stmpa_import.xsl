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
                <xsl:call-template name="documentType"/>     
                <xsl:call-template name="digitalBorn"/>     
                <xsl:call-template name="creator"/>   
                <xsl:call-template name="contributor"/>   
                <xsl:call-template name="publication"/>                 
            </r:otherEntity>
            <xsl:call-template name="digitalDocument"/>
        </r:import>
    </xsl:template>
    


    <xsl:template name="titleInfo">        
        <r:titleInfo>
            <xsl:call-template name="title"/>    
            <xsl:call-template name="subTitle"/>                    
        </r:titleInfo>  
    </xsl:template>    


    <xsl:template name="title">                       
        <r:title>
            <xsl:value-of select="//marc:datafield[@tag='245']/marc:subfield[@code='a']"/>
        </r:title>
    </xsl:template>

    
    
    <xsl:template name="subTitle">        
        <xsl:if test="//marc:datafield[@tag='245']/marc:subfield[@code='b']">
            <r:subTitle>
                <xsl:value-of select="//marc:datafield[@tag='245']/marc:subfield[@code='b']"/>
            </r:subTitle>
        </xsl:if>
    </xsl:template>


    <xsl:template name="documentType">
        <xsl:choose>
            <xsl:when test="substring(//marc:controlfield[@tag='007']/text(),1,2)='aj'">
                <r:documentType>kniha</r:documentType> 
            </xsl:when>
            <xsl:otherwise>
                <r:documentType>grafika</r:documentType> 
            </xsl:otherwise>
        </xsl:choose>                               
    </xsl:template>
    

    <xsl:template name="digitalBorn">                       
        <r:digitalBorn>false</r:digitalBorn>
    </xsl:template>    
    

    

    <xsl:template name="creator">                       
        <xsl:if test="//marc:datafield[@tag='100']/marc:subfield[@code='a']">
            <r:primaryOriginator type="AUTHOR">
                <xsl:value-of select="//marc:datafield[@tag='100']/marc:subfield[@code='a']"/>
            </r:primaryOriginator>
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
            <xsl:call-template name="place"/>      
            <xsl:call-template name="year"/>                  
        </r:publication>  
    </xsl:template>        

    
    <xsl:template name="publisher">                       
        <xsl:if test="//marc:datafield[@tag='260']/marc:subfield[@code='b']">
            <r:publisher>
                <xsl:value-of select="//marc:datafield[@tag='260']/marc:subfield[@code='b']"/>
            </r:publisher>
        </xsl:if>
    </xsl:template>    


    <xsl:template name="place">                       
        <xsl:if test="//marc:datafield[@tag='260']/marc:subfield[@code='a']">
            <r:place>
                <xsl:value-of select="//marc:datafield[@tag='260']/marc:subfield[@code='a']"/>
            </r:place>
        </xsl:if>
    </xsl:template>    
    
    
    
    <xsl:template name="year">                       
        <xsl:if test="//marc:datafield[@tag='260']/marc:subfield[@code='c']">
            <r:year>
                <xsl:value-of select="//marc:datafield[@tag='260']/marc:subfield[@code='c']"/>
            </r:year>
        </xsl:if>
    </xsl:template>    
    
       
    
    <xsl:template name="digitalDocument">           
        <r:digitalDocument>
            <r:registrarScopeIdentifiers>
                <r:id type="mapy_mzk_cz_id">
                    <xsl:value-of select="//dc:identifier"/>
                </r:id>
                <r:id type="mapy_mzk_cz_id">
                    <xsl:value-of select="//dc:identifier"/>
                </r:id>
            </r:registrarScopeIdentifiers>
            <r:technicalMetadata/>
        </r:digitalDocument>
    </xsl:template>    

    

    </xsl:stylesheet>
