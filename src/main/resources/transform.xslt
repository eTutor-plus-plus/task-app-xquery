<?xml version="1.0"?>
<xsl:stylesheet version="3.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:diff="https://www.pageseeder.org/diffx"
                xmlns:ins="https://www.pageseeder.org/diffx/insert"
                xmlns:del="https://www.pageseeder.org/diffx/delete">

    <xsl:output indent="yes" method="xml" encoding="utf-8" omit-xml-declaration="yes"/>

    <xsl:template match="/xquery-result">
        <xquery-analysis>
            <!-- Nodes -->
            <missingNodes>
                <xsl:for-each select="//*[@diff:insert='true']">
                    <xsl:call-template name="generateXPathElement"/>
                </xsl:for-each>
            </missingNodes>
            <superfluousNodes>
                <xsl:for-each select="//*[@diff:delete='true']">
                    <xsl:call-template name="generateXPathElement"/>
                </xsl:for-each>
            </superfluousNodes>
            <incorrectTextValues>
                <xsl:for-each select="//diff:*[local-name() = 'ins' or local-name() = 'del']/..">
                    <xsl:variable name="currNode" select="current()"/>
                    <xsl:variable name="currIns" select="$currNode/diff:ins"/>
                    <xsl:variable name="currDel" select="$currNode/diff:del"/>

                    <!-- insert only if the same in the opposite direction does not exist, because then it is just different order and order is checked somewhere else -->
                    <xsl:choose>
                        <xsl:when test="$currIns and $currDel">
                            <xsl:variable name="otherDel" select="//*[name() = $currNode/name() and parent = $currNode/parent]/diff:del[text() = $currIns/text()]"/>
                            <xsl:variable name="otherIns" select="//*[name() = $currNode/name() and parent = $currNode/parent]/diff:ins[text() = $currDel/text()]"/>
                            <xsl:if test="not($otherDel and $otherIns)">
                                <xsl:call-template name="generateXPathElementText"/>
                            </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:call-template name="generateXPathElementText"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </incorrectTextValues>

            <!-- Attributes -->
            <missingAttributes>
                <xsl:for-each select="//*[@ins:*='true']">
                    <xsl:variable name="currNode" select="current()"/>
                    <xsl:for-each select="@ins:*">
                        <xsl:variable name="attName" select="local-name(.)"/>
                        <xsl:if test="not($currNode/@del:*[local-name() = $attName])">
                            <!-- insert only if not also "del" exists (then incorrect value) -->
                            <xsl:call-template name="generateXPathAttributeInsert">
                                <xsl:with-param name="currNode" select="$currNode"/>
                                <xsl:with-param name="attName" select="$attName"/>
                            </xsl:call-template>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:for-each>
            </missingAttributes>
            <superfluousAttributes>
                <xsl:for-each select="//*[@del:*]">
                    <xsl:variable name="currNode" select="current()"/>
                    <xsl:for-each select="@del:*">
                        <xsl:variable name="attName" select="local-name(.)"/>
                        <xsl:if test="not($currNode/@ins:*[local-name() = $attName])">
                            <!-- insert only if not also "ins" exists (then incorrect value) -->
                            <xsl:call-template name="generateXPathAttributeDelete">
                                <xsl:with-param name="currNode" select="$currNode"/>
                                <xsl:with-param name="attName" select="$attName"/>
                            </xsl:call-template>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:for-each>
            </superfluousAttributes>
            <incorrectAttributeValues>
                <xsl:for-each select="//*[@del:*]">
                    <xsl:variable name="currNode" select="current()"/>
                    <xsl:for-each select="@del:*">
                        <xsl:variable name="attName" select="local-name(.)"/>
                        <xsl:if test="$currNode/@ins:*[local-name() = $attName]"> <!-- insert only if "ins" also exists -->
                            <xsl:variable name="otherDel" select="//*[name() = $currNode/name() and parent = $currNode/parent]/@del:*[local-name() = $attName]"/>
                            <xsl:variable name="otherIns" select="//*[name() = $currNode/name() and parent = $currNode/parent]/@ins:*[local-name() = $attName]"/>

                            <!-- insert only if the same in the opposite direction does not exist, because then it is just different order and order is checked somewhere else -->
                            <xsl:if test="not($otherDel = $currNode/@*[name() = $attName] and $otherIns)">
                                <xsl:call-template name="generateXPathAttributeValue">
                                    <xsl:with-param name="currNode" select="$currNode"/>
                                    <xsl:with-param name="attName" select="$attName"/>
                                </xsl:call-template>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:for-each>
            </incorrectAttributeValues>
        </xquery-analysis>
    </xsl:template>

    <!-- XPath -->
    <xsl:template name="generateFullXPath">
        <xsl:param name="currNode" select="current()"/>

        <xsl:for-each select="$currNode/ancestor::*">
            <xsl:if test="name() != 'xquery-result'">
                <xsl:text>/</xsl:text>
                <xsl:value-of select="name()"/>
                <xsl:text>[</xsl:text>
                <xsl:value-of select="count(preceding-sibling::*) + 1"/>
                <xsl:text>]</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>/</xsl:text>
        <xsl:value-of select="$currNode/name()"/>
        <xsl:text>[</xsl:text>
        <xsl:value-of select="count($currNode/preceding-sibling::*) + 1"/>
        <xsl:text>]</xsl:text>
    </xsl:template>
    <xsl:template name="generateXPathElement">
        <xsl:param name="currNode" select="current()"/>

        <entry>
            <path>
                <xsl:for-each select="$currNode/ancestor::*">
                    <xsl:if test="name() != 'xquery-result'">
                        <xsl:text>/</xsl:text>
                        <xsl:value-of select="name()"/>
                        <xsl:text>[</xsl:text>
                        <xsl:value-of select="count(preceding-sibling::*) + 1"/>
                        <xsl:text>]</xsl:text>
                    </xsl:if>
                </xsl:for-each>
            </path>
            <name>
                <xsl:value-of select="$currNode/name()"/>
            </name>
        </entry>
    </xsl:template>
    <xsl:template name="generateXPathElementText">
        <xsl:param name="currNode" select="current()"/>

        <entry>
            <path>
                <xsl:call-template name="generateFullXPath">
                    <xsl:with-param name="currNode" select="$currNode"/>
                </xsl:call-template>
            </path>
            <expectedValue>
                <xsl:choose>
                    <xsl:when test="$currNode/diff:ins">
                        <xsl:value-of select="$currNode/diff:ins"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text/>
                    </xsl:otherwise>
                </xsl:choose>
            </expectedValue>
        </entry>
    </xsl:template>
    <xsl:template name="generateXPathAttributeInsert">
        <xsl:param name="currNode" select="current()"/>
        <xsl:param name="attName" select="local-name(.)"/>

        <entry>
            <path>
                <xsl:call-template name="generateFullXPath">
                    <xsl:with-param name="currNode" select="$currNode"/>
                </xsl:call-template>
            </path>
            <name>
                <xsl:value-of select="$attName"/>
            </name>
            <value>
                <xsl:value-of select="$currNode/@*[name() = $attName]"/>
            </value>
        </entry>
    </xsl:template>
    <xsl:template name="generateXPathAttributeDelete">
        <xsl:param name="currNode" select="current()"/>
        <xsl:param name="attName" select="local-name(.)"/>

        <entry>
            <path>
                <xsl:call-template name="generateFullXPath">
                    <xsl:with-param name="currNode" select="$currNode"/>
                </xsl:call-template>
            </path>
            <name>
                <xsl:value-of select="$attName"/>
            </name>
            <value>
                <xsl:value-of select="$currNode/@*[local-name() = $attName]"/>
            </value>
        </entry>
    </xsl:template>
    <xsl:template name="generateXPathAttributeValue">
        <xsl:param name="currNode" select="current()"/>
        <xsl:param name="attName" select="local-name(.)"/>

        <entry>
            <path>
                <xsl:call-template name="generateFullXPath">
                    <xsl:with-param name="currNode" select="$currNode"/>
                </xsl:call-template>
            </path>
            <name>
                <xsl:value-of select="$attName"/>
            </name>
            <expectedValue>
                <xsl:value-of select="$currNode/@*[name() = $attName]"/>
            </expectedValue>
        </entry>
    </xsl:template>
</xsl:stylesheet>
