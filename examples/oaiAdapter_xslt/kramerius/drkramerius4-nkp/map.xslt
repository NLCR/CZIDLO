<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mods="http://www.loc.gov/mods/v3"
                xmlns:czi="http://resolver.nkp.cz/v3/"
                xmlns:dr="http://registrdigitalizace.cz/schemas/drkramerius/v4"
                exclude-result-prefixes="mods dr">

    <xsl:output method="xml"
                encoding="UTF-8"
                indent="yes"
                omit-xml-declaration="yes"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
    />

    <xsl:variable name="documentType">mapa</xsl:variable>
    <xsl:variable name="useArchiverId" select="false()"/>
    <xsl:variable name="archiverId">123</xsl:variable>

    <xsl:template match="/">
        <czi:import>
            <czi:monograph>
                <xsl:call-template name="titleInfo"/>
                <xsl:call-template name="ccnb"/>
                <xsl:call-template name="isbn"/>
                <xsl:call-template name="otherId"/>
                <xsl:call-template name="documentType"/>
                <xsl:call-template name="primaryOriginator"/>
                <xsl:call-template name="otherOriginator"/>
                <xsl:call-template name="publication"/>
            </czi:monograph>
            <xsl:call-template name="digitalDocument"/>
        </czi:import>
    </xsl:template>

    <xsl:template name="titleInfo">
        <czi:titleInfo>
            <xsl:call-template name="title"/>
            <xsl:call-template name="subTitle"/>
        </czi:titleInfo>
    </xsl:template>

    <xsl:template name="title">
        <czi:title>
            <xsl:call-template name="cutString">
                <xsl:with-param name="string" select="//mods:mods/mods:titleInfo/mods:title"/>
                <xsl:with-param name="maxLength" select="100"/>
            </xsl:call-template>
        </czi:title>
    </xsl:template>

    <xsl:template name="subTitle">
        <xsl:variable name="subTitle" select="//mods:mods/mods:titleInfo/mods:subTitle"/>
        <xsl:if test="$subTitle and $subTitle != ''">
            <czi:subTitle>
                <xsl:call-template name="cutString">
                    <xsl:with-param name="string" select="$subTitle"/>
                    <xsl:with-param name="maxLength" select="100"/>
                </xsl:call-template>
            </czi:subTitle>
        </xsl:if>
    </xsl:template>

    <xsl:template name="ccnb">
        <xsl:variable name="ccnb" select="//mods:mods/mods:identifier[@type='ccnb']"/>
        <xsl:if test="$ccnb">
            <czi:ccnb>
                <xsl:value-of select="$ccnb"/>
            </czi:ccnb>
        </xsl:if>
    </xsl:template>

    <xsl:template name="isbn">
        <xsl:variable name="isbn" select="//mods:mods/mods:identifier[@type='isbn']"/>
        <xsl:if test="$isbn">
            <czi:isbn>
                <xsl:value-of select="$isbn"/>
            </czi:isbn>
        </xsl:if>
    </xsl:template>

    <xsl:template name="otherId">
        <xsl:variable name="otherIdWithType"
                      select="//mods:mods/mods:identifier[@type and @type!='urnnbn' and @type!='isbn' and @type!='ccnb']"/>
        <xsl:variable name="otherIdWithoutType"
                      select="//mods:mods/mods:identifier[not(@type)]"/>
        <xsl:choose>
            <xsl:when test="$otherIdWithType">
                <czi:otherId>
                    <xsl:value-of select="concat($otherIdWithType/@type,':',$otherIdWithType)"/>
                </czi:otherId>
            </xsl:when>
            <xsl:when test="$otherIdWithoutType">
                <czi:otherId>
                    <xsl:value-of select="$otherIdWithoutType"/>
                </czi:otherId>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="documentType">
        <czi:documentType>
            <xsl:value-of select="$documentType"/>
        </czi:documentType>
    </xsl:template>

    <xsl:template name="primaryOriginator">
        <xsl:choose>
            <xsl:when test="//mods:mods/mods:name[@type='personal']">
                <czi:primaryOriginator type="AUTHOR">
                    <xsl:choose>
                        <xsl:when test="//mods:mods/mods:name[@type='personal']/mods:namePart[not(@type)]">
                            <xsl:value-of select="//mods:mods/mods:name[@type='personal']/mods:namePart[not(@type)]"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of
                                    select="concat(//mods:mods/mods:name[@type='personal']/mods:namePart[@type='family'],', ', //mods:mods/mods:name[@type='personal']/mods:namePart[@type='given'])"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </czi:primaryOriginator>
            </xsl:when>
            <xsl:when test="//mods:mods/mods:name[@type='corporate']">
                <czi:primaryOriginator type="CORPORATION">
                    <xsl:value-of select="//mods:mods/mods:name[@type='corporate']"/>
                </czi:primaryOriginator>
            </xsl:when>
            <xsl:when test="//mods:mods/mods:name[@type='conference']">
                <czi:primaryOriginator type="EVENT">
                    <xsl:value-of select="//mods:mods/mods:name[@type='conference']"/>
                </czi:primaryOriginator>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="otherOriginator">
        <xsl:if test="count(//mods:mods/mods:name) &gt; 1">
            <czi:otherOriginator>
                <xsl:choose>
                    <xsl:when test="//mods:mods/mods:name[2]/mods:namePart[not(@type)]">
                        <xsl:value-of select="//mods:mods/mods:name[2]/mods:namePart[not(@type)]"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of
                                select="concat(//mods:mods/mods:name[2]/mods:namePart[@type='family'],', ', //mods:mods/mods:name[2]/mods:namePart[@type='given'])"/>
                    </xsl:otherwise>
                </xsl:choose>
            </czi:otherOriginator>
        </xsl:if>
    </xsl:template>

    <xsl:template name="publication">
        <xsl:if test="//mods:mods/mods:originInfo">
            <czi:publication>
                <!--publisher-->
                <xsl:variable name="publisher" select="//mods:mods/mods:originInfo/mods:publisher"/>
                <xsl:if test="$publisher">
                    <czi:publisher>
                        <xsl:value-of select="$publisher"/>
                    </czi:publisher>
                </xsl:if>
                <!--publication place-->
                <xsl:variable name="place"
                              select="//mods:mods/mods:originInfo/mods:place/mods:placeTerm[@type='text']"/>
                <xsl:if test="$place">
                    <czi:place>
                        <xsl:value-of select="$place"/>
                    </czi:place>
                </xsl:if>
                <!--publication year-->
                <xsl:variable name="year" select="//mods:mods/mods:originInfo/mods:dateIssued"/>
                <xsl:if test="$year">
                    <czi:year>
                        <xsl:value-of select="$year"/>
                    </czi:year>
                </xsl:if>
            </czi:publication>
        </xsl:if>
    </xsl:template>

    <xsl:template name="digitalDocument">
        <czi:digitalDocument>
            <!--archiver id-->
            <xsl:if test="$useArchiverId">
                <czi:archiverId>
                    <xsl:value-of select="$archiverId"/>
                </czi:archiverId>
            </xsl:if>
            <!--urn:nbn-->
            <xsl:variable name="urnnbn" select="//mods:mods/mods:identifier[@type='urnnbn']"/>
            <xsl:if test="$urnnbn">
                <czi:urnNbn>
                    <czi:value>
                        <xsl:value-of select="$urnnbn"/>
                    </czi:value>
                </czi:urnNbn>
            </xsl:if>
            <!--registrar-scope ids-->
            <czi:registrarScopeIdentifiers>
                <czi:id type="uuid">
                    <xsl:value-of select="//dr:record/dr:uuid"/>
                </czi:id>
            </czi:registrarScopeIdentifiers>
            <!--financed-->
            <!--contract number-->
            <czi:technicalMetadata>
                <!--format-->
                <!--extent-->
                <!--resolution-->
                <!--compression-->
                <!--color-->
                <!--iccProfile-->
                <!--pictureSize-->
            </czi:technicalMetadata>
        </czi:digitalDocument>
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
