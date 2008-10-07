<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:g="http://www.mulesource.org/schema/galaxy-plugins">
    <xsl:output method="text" version="1.0" encoding="UTF-8" indent="no" omit-xml-declaration="yes" xml:space="default"/>

    <xsl:template match="g:galaxy">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="g:artifact-type">
h1. <xsl:value-of select="g:name"/> Plugin

<xsl:value-of select="g:description"/>

h3. Properties associated with this plugin

||Property||Value||
|Content Type|<xsl:value-of select="g:content-type"/>|
|Namespace|<xsl:value-of select="g:namespace/g:local-name"/> (<xsl:value-of select="g:namespace/g:uri"/>)|


h3. Indexes
The following describes the indexes in which <xsl:value-of select="g:name"/> can be queried from Galaxy.
        <xsl:apply-templates select="g:indexes"/>

h3. Policies
Policies allow for design time or runtime rules to be applied to artifacts in the registry. The following can be applied to <xsl:value-of select="g:name"/>.
        <xsl:apply-templates select="g:policies"/>
    </xsl:template>

    <xsl:template match="g:indexes">
        <xsl:apply-templates select="g:notes"/>
||Display Name||Type||<xsl:apply-templates select="g:index"/></xsl:template>

    <xsl:template match="g:policies">
        <xsl:apply-templates select="g:notes"/>
||Name||Description||<xsl:apply-templates select="g:policy"/>
    </xsl:template>

<xsl:template match="g:index">
|<xsl:value-of select="g:description"/>|<xsl:value-of select="indexer"/>|</xsl:template>

<xsl:template match="g:policy">
|<xsl:value-of select="g:name"/>|<xsl:value-of select="g:description"/>|</xsl:template>

    <xsl:template match="g:notes">
<xsl:value-of select="."/>
    </xsl:template>
</xsl:stylesheet>