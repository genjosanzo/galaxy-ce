<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:g="http://www.mulesource.org/schema/galaxy-plugin">
    <xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="g:galaxy-plugins">
        <xsl:apply-templates/>
        <p/>
    </xsl:template>

    <xsl:template match="g:galaxy-plugin">
        <h1>
            <xsl:value-of select="g:name"/> Plugin
        </h1>

        <xsl:value-of select="g:description"/>

        <h3>Properties associated with this plugin</h3>

        <table class="confluenceTable">
            <tbody>
            <tr>
                <th class="confluenceTh">Property</th>
                <th class="confluenceTh">Value</th>
            </tr>
            <tr>
                <td class="confluenceTd">Content Type</td>
                <td class="confluenceTd">
                    <xsl:value-of select="g:content-type"/>
                </td>
            </tr>
            <tr>
                <td class="confluenceTd">Namespace</td>
                <td class="confluenceTd">
                    <xsl:value-of select="g:namespace/g:prefix"/>
                    (<xsl:value-of select="g:namespace/g:uri"/>)
                </td>
            </tr>
            </tbody>
        </table>

        <h3>Indexes</h3>
        <p>The following describes the indexes in which <xsl:value-of select="g:name"/> can be queried from Galaxy.</p>

        <xsl:apply-templates select="g:indexes"/>

        <h3>Policies</h3>
        <p>Policies allow for design time or runtime rules to be applied to artifacts in the registry. The following can be
        applied to <xsl:value-of select="g:name"/>.</p>

        <xsl:apply-templates select="g:policies"/>
    </xsl:template>

    <xsl:template match="g:indexes">
        <table class="confluenceTable">
            <tbody>
                <tr>
                    <th class="confluenceTh">Field Name</th>
                    <th class="confluenceTh">Display Name</th>
                    <th class="confluenceTh">Description</th>
                    <th class="confluenceTh">Type</th>
                </tr>
                <xsl:apply-templates select="g:index"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="g:policies">
        <table class="confluenceTable">
            <tbody>
                <tr>
                    <th class="confluenceTh">Name</th>
                    <th class="confluenceTh">Description</th>
                </tr>
                <xsl:apply-templates select="g:policy"/>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="g:index">
        <tr>
            <td class="confluenceTd">
                <xsl:value-of select="g:field-name"/>
            </td>
            <td class="confluenceTd">
                <xsl:value-of select="g:display-name"/>
            </td>
            <td class="confluenceTd">
                <xsl:value-of select="g:description"/>
            </td>
            <td class="confluenceTd">
                <xsl:value-of select="g:language"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="g:policy">
        <tr>
            <td class="confluenceTd">
                <xsl:value-of select="g:name"/>
            </td>
            <td class="confluenceTd">
                <xsl:value-of select="g:description"/>
            </td>
        </tr>
    </xsl:template>


</xsl:stylesheet>