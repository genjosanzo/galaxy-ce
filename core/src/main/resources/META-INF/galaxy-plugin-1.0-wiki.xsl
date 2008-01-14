<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
               xmlns:g="http://www.mulesource.org/schema/galaxy-plugin">
    <xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="g:galaxy-plugin">
            h2. <xsl:value-of select="g:name"/>
            <xsl:text>
            </xsl:text>
            <xsl:value-of select="g:description"/>

            ||Properties||
            |Content Type|<xsl:value-of select="g:content-type"/>|
            |Namespace|<xsl:value-of select="g:namespace/g:prefix"/> (<xsl:value-of select="g:namespace/g:uri"/>)|

            h3. Indexes
            The following describes the indexes in which <xsl:value-of select="g:name"/> can be queried from Galaxy.

            ||Field Name||Display Name||Description||Type||
            <xsl:apply-templates select="g:indexes/g:index"/>

            h3. Policies
            Policies allow for design time or runtime rules to be applied to artifacts in the registry. The following can be applied to <xsl:value-of select="g:name"/>.

            ||Name||Description||
            <xsl:apply-templates select="g:policies/g:policy"/>
        </xsl:template>

        <xsl:template match="g:indexes/g:index">
            |<xsl:value-of select="g:field-name"/>|<xsl:value-of select="g:display-name"/>|<xsl:value-of select="g:description"/>|<xsl:value-of select="g:language"/>|
        </xsl:template>

        <xsl:template match="g:policies/g:policy">
            |<xsl:value-of select="g:name"/>|<xsl:value-of select="g:description"/>|
        </xsl:template>


</xsl:stylesheet>