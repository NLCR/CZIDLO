<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:mods="http://www.loc.gov/mods/v3"
                xmlns:czi="http://resolver.nkp.cz/v5/"
                xmlns:dr="http://registrdigitalizace.cz/schemas/drkramerius/v4"
                exclude-result-prefixes="mods dr">

    <xsl:output method="xml"
                encoding="UTF-8"
                indent="yes"
                omit-xml-declaration="yes"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
    />

    <xsl:variable name="documentType">článek</xsl:variable>
    <xsl:variable name="useArchiverId" select="false()"/>
    <xsl:variable name="archiverId">123</xsl:variable>

    <xsl:template match="/">
        <czi:import>
            <czi:analytical>
                <xsl:call-template name="titleInfo"/>
                <xsl:call-template name="otherId"/>
                <xsl:call-template name="documentType"/>
                <xsl:call-template name="primaryOriginator"/>
                <xsl:call-template name="otherOriginator"/>
                <xsl:call-template name="sourceDocument"/>
            </czi:analytical>
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

    <xsl:template name="otherId">
        <xsl:variable name="otherIdWithType"
                      select="//mods:mods/mods:identifier[@type]"/>
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


    <xsl:template name="sourceDocument">
        <czi:sourceDocument>
            <xsl:variable name="host" select="//mods:mods/mods:relatedItem[@type='host']"/>
            <xsl:if test="$host">
                <!--title info-->
                <czi:titleInfo>
                    <!--title-->
                    <xsl:variable name="host_title" select="$host/mods:titleInfo/mods:title"/>
                    <xsl:if test="$host_title">
                        <czi:title>
                            <xsl:value-of select="$host_title"/>
                        </czi:title>
                    </xsl:if>
                    <!--volume title-->
                    <xsl:variable name="host_volumeNumber"
                                  select="$host/mods:part[@type='article']/mods:detail[@type='volume']/mods:number"/>
                    <xsl:if test="$host_volumeNumber">
                        <czi:volumeTitle>
                            <xsl:value-of select="$host_volumeNumber"/>
                        </czi:volumeTitle>
                    </xsl:if>
                    <!--issue title-->
                    <xsl:variable name="host_issueNumber"
                                  select="$host/mods:part[@type='article']/mods:detail[@type='issue']/mods:number"/>
                    <xsl:if test="$host_issueNumber">
                        <czi:issueTitle>
                            <xsl:value-of select="$host_issueNumber"/>
                        </czi:issueTitle>
                    </xsl:if>
                </czi:titleInfo>

                <!--ccnb-->
                <xsl:variable name="host_ccnb" select="$host/mods:identifier[@type='ccnb']"/>
                <xsl:if test="$host_ccnb">
                    <czi:issn>
                        <xsl:value-of select="$host_ccnb"/>
                    </czi:issn>
                </xsl:if>

                <!--isbn-->
                <xsl:variable name="host_isbn" select="$host/mods:identifier[@type='isbn']"/>
                <xsl:if test="$host_isbn">
                    <czi:issn>
                        <xsl:value-of select="$host_isbn"/>
                    </czi:issn>
                </xsl:if>

                <!--issn-->
                <xsl:variable name="host_issn" select="$host/mods:identifier[@type='issn']"/>
                <xsl:if test="$host_issn">
                    <czi:issn>
                        <xsl:value-of select="$host_issn"/>
                    </czi:issn>
                </xsl:if>

                <!--other identifier-->
                <xsl:variable name="host_otherIdWithType"
                              select="$host/mods:identifier[@type and @type!='urnnbn' and @type!='isbn' and @type!='ccnb']"/>

                <xsl:variable name="host_otherIdWithoutType"
                              select="$host/mods:identifier[not(@type)]"/>
                <xsl:choose>
                    <xsl:when test="$host_otherIdWithType">
                        <czi:otherId>
                            <xsl:value-of select="concat($host_otherIdWithType/@type,':',$host_otherIdWithType)"/>
                        </czi:otherId>
                    </xsl:when>
                    <xsl:when test="$host_otherIdWithoutType">
                        <czi:otherId>
                            <xsl:value-of select="$host_otherIdWithoutType"/>
                        </czi:otherId>
                    </xsl:when>
                </xsl:choose>

            </xsl:if>
        </czi:sourceDocument>
    </xsl:template>

</xsl:stylesheet>
