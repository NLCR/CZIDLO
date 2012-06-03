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
        <xsl:variable name="title">
            <xsl:value-of select="//marc:datafield[@tag='245']/marc:subfield[@code='a']"/>
        </xsl:variable>
        <r:title>
            <xsl:call-template name="adjustString">
                <xsl:with-param name="string" select="$title"/>
                <xsl:with-param name="maxLength" select="100"/>
            </xsl:call-template>
        </r:title>
    </xsl:template>
   
    
    <xsl:template name="subTitle">        
        <xsl:variable name="sub_title">
            <xsl:value-of select="//marc:datafield[@tag='245']/marc:subfield[@code='b']"/>
        </xsl:variable>
        <xsl:if test="$sub_title">
            <r:subTitle>
                <xsl:call-template name="adjustString">
                    <xsl:with-param name="string" select="$sub_title"/>
                    <xsl:with-param name="maxLength" select="200"/>
                </xsl:call-template>
            </r:subTitle>
        </xsl:if>        
    </xsl:template>


    <xsl:template name="documentType">
        <xsl:choose>
            <xsl:when test="substring(//marc:controlfield[@tag='007']/text(),1,2)='aj'">
                <r:documentType>mapa</r:documentType> 
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
        <xsl:variable name="creator">
            <xsl:value-of select="//marc:datafield[@tag='100']/marc:subfield[@code='a']"/>
        </xsl:variable>
        <xsl:if test="$creator">
            <r:primaryOriginator type="AUTHOR">          
                <xsl:call-template name="adjustString">
                    <xsl:with-param name="string" select="$creator"/>                    
                </xsl:call-template>
            </r:primaryOriginator>
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
        <xsl:variable name="publisher">
            <xsl:value-of select="//marc:datafield[@tag='260']/marc:subfield[@code='b']"/>
        </xsl:variable>
        <xsl:if test="$publisher">
            <r:publisher>                
                <xsl:call-template name="adjustString">
                    <xsl:with-param name="string" select="$publisher"/>
                    <xsl:with-param name="maxLength" select="50"/>
                </xsl:call-template>
            </r:publisher>
        </xsl:if>        
    </xsl:template>




    <xsl:template name="place">        
        <xsl:variable name="place">
            <xsl:value-of select="//marc:datafield[@tag='260']/marc:subfield[@code='a']"/>
        </xsl:variable>
        <xsl:if test="$place">
            <r:place>
                <xsl:call-template name="adjustString">
                    <xsl:with-param name="string" select="$place"/>
                    <xsl:with-param name="maxLength" select="50"/>
                </xsl:call-template>
            </r:place>
        </xsl:if>
    </xsl:template>    
    
    
    
    <xsl:template name="year">      
        <xsl:if test="//marc:datafield[@tag='260']/marc:subfield[@code='c']">
            <xsl:variable name="year">
                <xsl:value-of select="//marc:datafield[@tag='260']/marc:subfield[@code='c']"/>
            </xsl:variable>
            <xsl:if test="floor($year) = $year">
                <r:year>                
                    <xsl:value-of select="//marc:datafield[@tag='260']/marc:subfield[@code='c']"/>
                </r:year>
            </xsl:if>
        </xsl:if>
    </xsl:template>    
           
    
    <xsl:template name="digitalDocument">           
        <r:digitalDocument>
            <r:registrarScopeIdentifiers>
                <xsl:variable name="sysno">
                    <xsl:value-of select="//marc:controlfield[@tag='001']"/>
                </xsl:variable>
                <r:id type="aleph_id">
                    <xsl:value-of select="concat('mzk03:',$sysno)"/>
                </r:id>

                 <!--<xsl:variable name="sig">
                    <xsl:value-of select="//marc:datafield[@tag='910']/marc:subfield[@code='b']"/>
                </xsl:variable>                                    
                <xsl:if test="$sig">
                    <r:id type="signatura">
                        <xsl:value-of select="$sig"/>
                    </r:id>
                </xsl:if>                  
                
               <xsl:variable name="sig_old">
                    <xsl:value-of select="//marc:datafield[@tag='Z30']/marc:subfield[@code='9']"/>
                </xsl:variable>                                    
                <xsl:if test="$sig_old">
                    <r:id type="signatura_puvodni">
                        <xsl:value-of select="translate($sig_old, '/', '_')"/>
                    </r:id>
                </xsl:if>  -->
                
                
                
                <xsl:variable name="link">
                    <xsl:value-of select="//marc:datafield[@tag='856']/marc:subfield[@code='u']"/>
                </xsl:variable>                
                <xsl:if test="substring($link,1,25) = 'http://imageserver.mzk.cz'">
                    <r:id type="mapy_mzk_cz_id">
                        <xsl:value-of select="translate(substring($link,27), '/', '_')"/>
                    </r:id>                
                </xsl:if>                
                

            </r:registrarScopeIdentifiers>
            <r:technicalMetadata>
                <r:format>JPEG 2000</r:format>
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
    
    <xsl:template name="adjustString">
        <xsl:param name="string"/>
        <xsl:param name="maxLength"/>  
        <xsl:param name="punctuation">            
            <xsl:text>:,;/[] </xsl:text>
        </xsl:param>
        <xsl:variable name="length" select="string-length($string)"/>
        <xsl:choose>
            <xsl:when test="$length=0"/>
            <xsl:when test="contains($punctuation, substring($string,$length,1))">
                <!--<a><xsl:value-of select="substring($string,1,$length - 1)"/></a>-->
                <xsl:call-template name="adjustString">
                    <xsl:with-param name="string" select="substring($string,1,$length - 1)"/>
                    <xsl:with-param name="maxLength" select="$maxLength"/>
                    <xsl:with-param name="punctuation" select="$punctuation"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="contains($punctuation, substring($string,1,1))">
                <!--<a><xsl:value-of select="substring($string,2,$length - 1)"/></a>-->
                <xsl:call-template name="adjustString">
                    <xsl:with-param name="string" select="substring($string,2,$length - 1)"/>
                    <xsl:with-param name="maxLength" select="$maxLength"/>
                    <xsl:with-param name="punctuation" select="$punctuation"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="not($string)"/>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$maxLength">
                        <xsl:call-template name="cutString">
                            <xsl:with-param name="string" select="$string"/>
                            <xsl:with-param name="maxLength" select="$maxLength"/>
                        </xsl:call-template>				                        
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$string"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    
    
    

</xsl:stylesheet>
