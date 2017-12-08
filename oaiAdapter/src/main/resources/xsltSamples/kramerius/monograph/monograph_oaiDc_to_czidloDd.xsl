<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:r="http://resolver.nkp.cz/v3/"
    exclude-result-prefixes="dc">

    <xsl:output method="xml" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"
        doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>

    <xsl:template match="/">
        <r:import xmlns:r="http://resolver.nkp.cz/v3/">
            <r:monograph>
                <xsl:call-template name="titleInfo"/>
                <xsl:call-template name="ccnb"/>
                <xsl:call-template name="isbn"/>
                <xsl:call-template name="otherId"/>
                <xsl:call-template name="documentType"/>
                <!--<xsl:call-template name="digitalBorn"/>-->
                <xsl:call-template name="primaryOriginator"/>
                <xsl:call-template name="otherOriginator"/>
                <xsl:call-template name="publication"/>
            </r:monograph>
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
            <xsl:call-template name="cutString">
                <xsl:with-param name="string" select="//dc:title"/>
                <xsl:with-param name="maxLength" select="50"/>
            </xsl:call-template>
        </r:title>
    </xsl:template>

    <!--zatim nic, z oai_dc nejde poznat podnazev-->
    <xsl:template name="subTitle"> </xsl:template>

    <xsl:template name="ccnb">
        <xsl:variable name="CCNB">
            <xsl:value-of select='//dc:identifier[starts-with(., "ccnb:")]'/>
        </xsl:variable>
        <xsl:if test="$CCNB != ''">
            <r:ccnb>
                <xsl:value-of select='substring-after($CCNB, "ccnb:")'/>
            </r:ccnb>
        </xsl:if>
    </xsl:template>

    <xsl:template name="isbn">
        <xsl:variable name="ISBN">
            <xsl:value-of select='//dc:identifier[starts-with(., "URN:ISBN:")]'/>
        </xsl:variable>
        <xsl:if test="$ISBN != ''">
            <r:isbn>
                <xsl:value-of select='substring-after($ISBN, "URN:ISBN:")'/>
            </r:isbn>
        </xsl:if>
    </xsl:template>

    <!--napr. <dc:identifier>oclc:228598236</dc:identifier>-->
    <xsl:template name="otherId">
        <xsl:variable name="OTHER_ID">
            <!--<xsl:value-of select='//dc:identifier[starts-with(., "URN:ISBN:")]'/>-->
            <xsl:value-of
                select='//dc:identifier[not(starts-with(., "URN:ISBN:")) and not(starts-with(., "isbn:")) and not(starts-with(., "ccnb:")) and not(starts-with(., "uuid:")) and not(starts-with(., "urnnbn:"))]'
            />
        </xsl:variable>
        <xsl:if test="$OTHER_ID != ''">
            <r:otherId>
                <xsl:value-of select="$OTHER_ID"/>
            </r:otherId>
        </xsl:if>
    </xsl:template>

    <!--v datech byva <dc:type>model:monograph</dc:type>, coz nema smysl-->
    <xsl:template name="documentType">
        <!--<xsl:if test="//dc:type">
            <r:documentType>
                <xsl:value-of select="//dc:type"/>
            </r:documentType>
        </xsl:if>-->
    </xsl:template>

    <xsl:template name="primaryOriginator">
        <xsl:if test="//dc:creator">
            <r:primaryOriginator type="AUTHOR">
                <xsl:value-of select="//dc:creator"/>
            </r:primaryOriginator>
        </xsl:if>
    </xsl:template>

    <xsl:template name="otherOriginator">
        <xsl:if test="//dc:contributor">
            <r:otherOriginator>
                <xsl:value-of select="//dc:contributor"/>
            </r:otherOriginator>
        </xsl:if>
    </xsl:template>


    <xsl:template name="publication">
        <r:publication>
            <xsl:call-template name="publisher"/>
            <xsl:call-template name="publicationPlace"/>
            <xsl:call-template name="publicationYear"/>
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

    <!--    zatim nic, z dat to zase neni videt-->
    <xsl:template name="publicationPlace"> </xsl:template>

    <xsl:template name="publicationYear">
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
            <!-- <r:archiverId>3</r:archiverId>-->

            <!-- URN:NBN -->
            <xsl:variable name="URN">
                <xsl:value-of select='//dc:identifier[starts-with(., "urnnbn:")]'/>
            </xsl:variable>

            <xsl:if test="$URN != ''">
                <r:urnNbn>
                    <r:value>
                        <xsl:value-of select='substring-after($URN, "urnnbn:")'/>
                    </r:value>
                </r:urnNbn>
            </xsl:if>

            <r:registrarScopeIdentifiers>
                <xsl:variable name="K4_pid">
                    <xsl:value-of select='//dc:identifier[starts-with(., "uuid:")]'/>
                </xsl:variable>
                <xsl:if test="$K4_pid">
                    <r:id type="K4_pid">
                        <xsl:value-of select="$K4_pid"/>
                    </r:id>
                </xsl:if>
            </r:registrarScopeIdentifiers>

            <r:financed>NDK</r:financed>

            <xsl:call-template name="technicalMetadata"/>

        </r:digitalDocument>
    </xsl:template>


    <xsl:template name="technicalMetadata">
        <!--<r:technicalMetadata/>-->
    </xsl:template>

    <xsl:template name="cutString">
        <xsl:param name="string"/>
        <xsl:param name="maxLength"/>
        <xsl:variable name="length" select="string-length($string)"/>
        <xsl:choose>
            <xsl:when test="$length > $maxLength">
                <xsl:value-of select="substring($string, 1, $maxLength)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>

