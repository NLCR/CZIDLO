<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:r="http://resolver.nkp.cz/v4/"
                exclude-result-prefixes="r">

    <xsl:output method="xml"
                encoding="UTF-8"
                indent="yes"
                omit-xml-declaration="yes"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
    />

    <xsl:template match="r:digitalDocument">
        <add>
            <doc>
                <xsl:variable name="ieType" select="r:intelectualEntity/@type"/>

                <!--id-->
                <field name="id">
                    <xsl:value-of select='@id'/>
                </field>

                <!--ie.type-->
                <field name="ie.type">
                    <xsl:value-of select='$ieType'/>
                </field>

                <!--ie.docType-->
                <xsl:if test="r:intelectualEntity/r:documentType">
                    <field name="ie.docType">
                        <xsl:value-of select='r:intelectualEntity/r:documentType'/>
                    </field>
                </xsl:if>

                <!--SEARCHABLE FIELDS-->

                <!--title-->
                <xsl:call-template name="title">
                    <xsl:with-param name="ieType" select="$ieType"/>
                </xsl:call-template>

                <!--isbn-->
                <xsl:if test="r:intelectualEntity/r:isbn">
                    <field name="isbn">
                        <xsl:value-of select='r:intelectualEntity/r:isbn'/>
                    </field>
                </xsl:if>

                <!--issn-->
                <xsl:if test="r:intelectualEntity/r:issn">
                    <field name="issn">
                        <xsl:value-of select='r:intelectualEntity/r:issn'/>
                    </field>
                </xsl:if>

                <!--ccnb-->
                <xsl:if test="r:intelectualEntity/r:ccnb">
                    <field name="ccnb">
                        <xsl:value-of select='r:intelectualEntity/r:ccnb'/>
                    </field>
                </xsl:if>

                <!--otherId-->
                <xsl:if test="r:intelectualEntity/r:otherId">
                    <field name="otherId">
                        <xsl:value-of select='r:intelectualEntity/r:otherId'/>
                    </field>
                </xsl:if>

                <!--TODO: originator, publication-->

                <!--<xsl:choose>
                    <xsl:when test="//mods:mods/mods:name[@type='personal']/mods:namePart[not(@type)]">
                        <xsl:value-of select="//mods:mods/mods:name[@type='personal']/mods:namePart[not(@type)]"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of
                                select="concat(//mods:mods/mods:name[@type='personal']/mods:namePart[@type='family'],', ', //mods:mods/mods:name[@type='personal']/mods:namePart[@type='given'])"/>
                    </xsl:otherwise>
                </xsl:choose>-->


            </doc>
        </add>
    </xsl:template>

    <xsl:template name="title">
        <xsl:param name="ieType"/>
        <xsl:variable name="titleInfo" select="r:intelectualEntity/r:titleInfo"/>
        <xsl:choose>
            <xsl:when test="$ieType='MONOGRAPH'">
                <xsl:choose>
                    <xsl:when test="$titleInfo/r:subTitle">
                        <field name="title">
                            <xsl:value-of select="concat($titleInfo/r:title,': ', $titleInfo/r:subTitle)"/>
                        </field>
                    </xsl:when>
                    <xsl:otherwise>
                        <field name="title">
                            <xsl:value-of select="$titleInfo/r:title"/>
                        </field>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>

            <xsl:when test="$ieType='MONOGRAPH_VOLUME'">
                <field name="title">
                    <xsl:value-of select="concat($titleInfo/r:monographTitle,': ', $titleInfo/r:volumeTitle)"/>
                </field>
            </xsl:when>

            <xsl:when test="$ieType='PERIODICAL'">
                <xsl:choose>
                    <xsl:when test="$titleInfo/r:subTitle">
                        <field name="title">
                            <xsl:value-of select="concat($titleInfo/r:title,': ', $titleInfo/r:subTitle)"/>
                        </field>
                    </xsl:when>
                    <xsl:otherwise>
                        <field name="title">
                            <xsl:value-of select="$titleInfo/r:title"/>
                        </field>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>

            <xsl:when test="$ieType='PERIODICAL_VOLUME'">
                <field name="title">
                    <xsl:value-of select="concat($titleInfo/r:periodicalTitle,': ', $titleInfo/r:volumeTitle)"/>
                </field>
            </xsl:when>

            <xsl:when test="$ieType='PERIODICAL_ISSUE'">
                <xsl:choose>
                    <xsl:when
                            test="$titleInfo/r:volumeTitle">
                        <field name="title">
                            <xsl:value-of
                                    select="concat($titleInfo/r:periodicalTitle,': ', $titleInfo/r:volumeTitle,': ', $titleInfo/r:issueTitle)"/>
                        </field>
                    </xsl:when>
                    <xsl:otherwise>
                        <field name="title">
                            <xsl:value-of
                                    select="concat($titleInfo/r:periodicalTitle,': ', $titleInfo/r:issueTitle)"/>
                        </field>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>

            <xsl:when test="$ieType='THESIS'">
                <xsl:choose>
                    <xsl:when test="$titleInfo/r:subTitle">
                        <field name="title">
                            <xsl:value-of select="concat($titleInfo/r:title,': ', $titleInfo/r:subTitle)"/>
                        </field>
                    </xsl:when>
                    <xsl:otherwise>
                        <field name="title">
                            <xsl:value-of select="$titleInfo/r:title"/>
                        </field>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>

            <xsl:when test="$ieType='ANALYTICAL'">
                <xsl:choose>
                    <xsl:when test="$titleInfo/r:subTitle">
                        <field name="title">
                            <xsl:value-of select="concat($titleInfo/r:title,': ', $titleInfo/r:subTitle)"/>
                        </field>
                    </xsl:when>
                    <xsl:otherwise>
                        <field name="title">
                            <xsl:value-of select="$titleInfo/r:title"/>
                        </field>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>

            <xsl:when test="$ieType='OTHER'">
                <xsl:choose>
                    <xsl:when test="$titleInfo/r:subTitle">
                        <field name="title">
                            <xsl:value-of select="concat($titleInfo/r:title,': ', $titleInfo/r:subTitle)"/>
                        </field>
                    </xsl:when>
                    <xsl:otherwise>
                        <field name="title">
                            <xsl:value-of select="$titleInfo/r:title"/>
                        </field>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
