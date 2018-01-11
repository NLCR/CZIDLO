<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:r="http://resolver.nkp.cz/v5/"
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

                <!--DIGITAL DOCUMENT-->

                <!--id-->
                <field name="dd.id">
                    <!--<xsl:value-of select='@id'/>-->
                    <xsl:value-of select='r:urnNbn/r:value'/>
                </field>

                <!--registrarScopeIds - only value is indexed-->
                <xsl:for-each select="r:registrarScopeIdentifiers/r:id">
                    <field name="dd.registrarScopeId">
                        <xsl:value-of select='.'/>
                    </field>
                </xsl:for-each>

                <xsl:if test="r:financed">
                    <field name="dd.financed">
                        <xsl:value-of select='r:financed'/>
                    </field>
                </xsl:if>

                <xsl:if test="r:contractNumber">
                    <field name="dd.contractNumber">
                        <xsl:value-of select='r:contractNumber'/>
                    </field>
                </xsl:if>

                <!--INTELECTUAL ENTITY-->

                <xsl:variable name="ieType" select="r:intelectualEntity/@type"/>

                <!--ie.type-->
                <!--<field name="ie.type">
                    <xsl:value-of select='$ieType'/>
                </field>-->

                <!--ie.docType-->
                <!--<xsl:if test="r:intelectualEntity/r:documentType">
                    <field name="ie.docType">
                        <xsl:value-of select='r:intelectualEntity/r:documentType'/>
                    </field>
                </xsl:if>-->

                <!--title-->
                <!--<xsl:call-template name="title">
                    <xsl:with-param name="ieType" select="$ieType"/>
                </xsl:call-template>-->

                <!--title info-->
                <xsl:variable name="titleInfo" select="r:intelectualEntity/r:titleInfo"/>
                <xsl:if test="$titleInfo/r:title">
                    <field name="ie.title">
                        <xsl:value-of select='$titleInfo/r:title'/>
                    </field>
                </xsl:if>
                <xsl:if test="$titleInfo/r:subTitle">
                    <field name="ie.subTitle">
                        <xsl:value-of select='$titleInfo/r:subTitle'/>
                    </field>
                </xsl:if>
                <xsl:if test="$titleInfo/r:monographTitle">
                    <field name="ie.monographTitle">
                        <xsl:value-of select='$titleInfo/r:monographTitle'/>
                    </field>
                </xsl:if>
                <xsl:if test="$titleInfo/r:periodicalTitle">
                    <field name="ie.periodicalTitle">
                        <xsl:value-of select='$titleInfo/r:periodicalTitle'/>
                    </field>
                </xsl:if>
                <xsl:if test="$titleInfo/r:volumeTitle">
                    <field name="ie.volumeTitle">
                        <xsl:value-of select='$titleInfo/r:volumeTitle'/>
                    </field>
                </xsl:if>
                <xsl:if test="$titleInfo/r:issueTitle">
                    <field name="ie.issueTitle">
                        <xsl:value-of select='$titleInfo/r:issueTitle'/>
                    </field>
                </xsl:if>

                <!--identifiers-->
                <xsl:if test="r:intelectualEntity/r:ccnb">
                    <field name="ie.ccnb">
                        <xsl:value-of select='r:intelectualEntity/r:ccnb'/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:isbn">
                    <field name="ie.isbn">
                        <xsl:value-of select='r:intelectualEntity/r:isbn'/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:issn">
                    <field name="ie.issn">
                        <xsl:value-of select='r:intelectualEntity/r:issn'/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:otherId">
                    <field name="ie.otherId">
                        <xsl:value-of select='r:intelectualEntity/r:otherId'/>
                    </field>
                </xsl:if>

                <!--originators-->
                <xsl:if test="r:intelectualEntity/r:primaryOriginator[@type='AUTHOR']">
                    <field name="ie.primaryOriginator.author">
                        <xsl:value-of select="r:intelectualEntity/r:primaryOriginator[@type='AUTHOR']"/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:primaryOriginator[@type='EVENT']">
                    <field name="ie.primaryOriginator.event">
                        <xsl:value-of select="r:intelectualEntity/r:primaryOriginator[@type='EVENT']"/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:primaryOriginator[@type='CORPORATION']">
                    <field name="ie.primaryOriginator.corporation">
                        <xsl:value-of select="r:intelectualEntity/r:primaryOriginator[@type='CORPORATION']"/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:otherOriginator">
                    <field name="ie.otherOriginator">
                        <xsl:value-of select="r:intelectualEntity/r:otherOriginator"/>
                    </field>
                </xsl:if>

                <!--publication-->
                <xsl:if test="r:intelectualEntity/r:publication/r:publisher">
                    <field name="ie.publication.publisher">
                        <xsl:value-of select="r:intelectualEntity/r:publication/r:publisher"/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:publication/r:place">
                    <field name="ie.publication.place">
                        <xsl:value-of select="r:intelectualEntity/r:publication/r:place"/>
                    </field>
                </xsl:if>
                <xsl:if test="r:intelectualEntity/r:publication/r:year">
                    <field name="ie.publication.year">
                        <xsl:value-of select="r:intelectualEntity/r:publication/r:year"/>
                    </field>
                </xsl:if>

                <!--source document-->
                <xsl:apply-templates select="r:intelectualEntity/r:sourceDocument"/>

            </doc>
        </add>
    </xsl:template>


    <xsl:template match="r:sourceDocument">

        <!--title info-->
        <xsl:variable name="titleInfo" select="r:titleInfo"/>
        <xsl:if test="$titleInfo/r:title">
            <field name="ie.srcDoc.title">
                <xsl:value-of select='$titleInfo/r:title'/>
            </field>
        </xsl:if>
        <xsl:if test="$titleInfo/r:subTitle">
            <field name="ie.srcDoc.subTitle">
                <xsl:value-of select='$titleInfo/r:subTitle'/>
            </field>
        </xsl:if>
        <xsl:if test="$titleInfo/r:monographTitle">
            <field name="ie.srcDoc.monographTitle">
                <xsl:value-of select='$titleInfo/r:monographTitle'/>
            </field>
        </xsl:if>
        <xsl:if test="$titleInfo/r:periodicalTitle">
            <field name="ie.srcDoc.periodicalTitle">
                <xsl:value-of select='$titleInfo/r:periodicalTitle'/>
            </field>
        </xsl:if>
        <xsl:if test="$titleInfo/r:volumeTitle">
            <field name="ie.srcDoc.volumeTitle">
                <xsl:value-of select='$titleInfo/r:volumeTitle'/>
            </field>
        </xsl:if>
        <xsl:if test="$titleInfo/r:issueTitle">
            <field name="ie.srcDoc.issueTitle">
                <xsl:value-of select='$titleInfo/r:issueTitle'/>
            </field>
        </xsl:if>

        <!--identifiers-->
        <xsl:if test="r:ccnb">
            <field name="ie.srcDoc.ccnb">
                <xsl:value-of select='r:ccnb'/>
            </field>
        </xsl:if>
        <xsl:if test="r:isbn">
            <field name="ie.srcDoc.isbn">
                <xsl:value-of select='r:isbn'/>
            </field>
        </xsl:if>
        <xsl:if test="r:issn">
            <field name="ie.srcDoc.issn">
                <xsl:value-of select='r:issn'/>
            </field>
        </xsl:if>
        <xsl:if test="r:otherId">
            <field name="ie.srcDoc.otherId">
                <xsl:value-of select='r:otherId'/>
            </field>
        </xsl:if>

        <!--publication-->
        <xsl:if test="r:publication/r:publisher">
            <field name="ie.srcDoc.publication.publisher">
                <xsl:value-of select="r:publication/r:publisher"/>
            </field>
        </xsl:if>
        <xsl:if test="r:publication/r:place">
            <field name="ie.srcDoc.publication.place">
                <xsl:value-of select="r:publication/r:place"/>
            </field>
        </xsl:if>
        <xsl:if test="r:publication/r:year">
            <field name="ie.srcDoc.publication.year">
                <xsl:value-of select="r:publication/r:year"/>
            </field>
        </xsl:if>

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
