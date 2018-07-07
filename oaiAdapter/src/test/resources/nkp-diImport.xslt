<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:czi="http://resolver.nkp.cz/v5/"
                xmlns:mods="http://www.loc.gov/mods/v3"
                xmlns:dr="http://registrdigitalizace.cz/schemas/drkramerius/v4"
                xmlns:dc="http://purl.org/dc/elements/1.1/"
                exclude-result-prefixes="mods dr dc">

    <xsl:output method="xml"
                encoding="UTF-8"
                indent="yes"
                omit-xml-declaration="yes"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
    />

    <!-- K4 NKP (resolver-test2) -->
    <xsl:variable name="digitalLibraryId">41</xsl:variable>
    <!--<xsl:variable name="digitalLibraryId">48</xsl:variable>-->


    <xsl:template match="/">
        <czi:digitalInstance>
            <xsl:call-template name="url"/>
            <xsl:call-template name="digitalLibraryId"/>
            <!--<xsl:call-template name="format"/>-->
            <!--<xsl:call-template name="accessibility"/>-->
        </czi:digitalInstance>
    </xsl:template>

    <xsl:template name="url">
        <czi:url>
            <xsl:value-of
                    select="concat('http://kramerius4.nkp.cz/search/handle/uuid:',//dr:uuid)"/>
        </czi:url>
    </xsl:template>

    <xsl:template name="digitalLibraryId">
        <czi:digitalLibraryId>
            <xsl:value-of select="$digitalLibraryId"/>
        </czi:digitalLibraryId>
    </xsl:template>

    <xsl:template name="format">
        <czi:format>html</czi:format>
    </xsl:template>

    <xsl:template name="accessibility">
        <czi:accessibility>veřejné</czi:accessibility>
    </xsl:template>

</xsl:stylesheet>
