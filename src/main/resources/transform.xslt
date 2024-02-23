<?xml version="1.0"?>
<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/xquery-result">
        <xquery-analysis>
            <missingNodes></missingNodes>
            <superfluousNodes></superfluousNodes>
            <displacedNodes></displacedNodes>
            <missingAttributes></missingAttributes>
            <superfluousAttributes></superfluousAttributes>
            <incorrectAttributeValues></incorrectAttributeValues>

            <html>
                <body>
                    <h2>Mietstatistik</h2>
                    <table border="1">
                        <tr bgcolor="#9acd32">
                            <th>Anzahl</th>
                            <th>Summe</th>
                            <th>Quadratmeterpreis</th>
                        </tr>
                        <tr>
                            <td>
                                <xsl:apply-templates select="mietstatistik/anzahl"/>
                            </td>
                            <td>
                                <xsl:value-of select="mietstatistik/sum-preis"/>
                            </td>
                            <td>
                                <xsl:value-of select="mietstatistik/qm-preis"/>
                            </td>
                        </tr>
                    </table>
                </body>
            </html>
        </xquery-analysis>
    </xsl:template>

</xsl:stylesheet>
