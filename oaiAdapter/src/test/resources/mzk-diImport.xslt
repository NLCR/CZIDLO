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


    <!-- K4 MZK (resolver-test) -->
    <xsl:variable name="digitalLibraryId">50</xsl:variable>
    <!-- K4 MZK (resolver-dev) -->
    <!--<xsl:variable name="digitalLibraryId">42</xsl:variable>-->
    <!-- K4 MZK (resolver prod?) -->
    <!--<xsl:variable name="digitalLibraryId">49</xsl:variable>-->

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
            <!--http://www.digitalniknihovna.cz/mzk/uuid/uuid:71a80bc3-9361-4d5e-8207-179aba225342-->
            <!--http://kramerius.mzk.cz/search/handle/uuid:71a80bc3-9361-4d5e-8207-179aba225342-->
            <!--<xsl:value-of
                    select="concat('http://kramerius.mzk.cz/search/handle/uuid:',//dr:uuid)"/>-->
            <xsl:value-of
                    select="concat('http://www.digitalniknihovna.cz/mzk/uuid/uuid:',//dr:uuid)"/>

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
