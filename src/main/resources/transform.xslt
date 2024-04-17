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
                    <!-- insert only if no other element at the same level diff:delete exists -->
                    <xsl:variable name="otherDel" select="//*[name() = current()/name() and ./.. = current()/..]/@diff:delete"/>
                    <xsl:if test="not($otherDel)">
                        <xsl:call-template name="generateXPathElement"/>
                    </xsl:if>
                </xsl:for-each>
            </missingNodes>
            <superfluousNodes>
                <xsl:for-each select="//*[@diff:delete='true']">
                    <!-- insert only if no other element at the same level with diff:insert exists -->
                    <xsl:variable name="otherIns" select="//*[name() = current()/name() and ./.. = current()/..]/@diff:insert"/>
                    <xsl:if test="not($otherIns)">
                        <xsl:call-template name="generateXPathElement"/>
                    </xsl:if>
                </xsl:for-each>
            </superfluousNodes>
            <incorrectTextValues>
                <xsl:for-each select="//diff:*[local-name() = 'ins' or local-name() = 'del']/..">
                    <xsl:variable name="currNode" select="current()"/>
                    <xsl:variable name="currIns" select="$currNode/diff:ins"/>
                    <xsl:variable name="currDel" select="$currNode/diff:del"/>

                    <xsl:choose>
                        <!-- insert only if not both ins and del with same content exist, then it would be just at another order -->
                        <xsl:when test="$currIns and $currDel">
                            <xsl:variable name="otherDel" select="//*[name() = $currNode/name() and ./.. = $currNode/..]/diff:del[text() = $currIns/text()]"/>
                            <xsl:variable name="otherIns" select="//*[name() = $currNode/name() and ./.. = $currNode/..]/diff:ins[text() = $currDel/text()]"/>
                            <xsl:variable name="otherDelSameLevel"
                                          select="//*[name() = $currNode/name() and count(./preceding-sibling::*) = count($currNode/preceding-sibling::*)]/diff:del[text() = $currIns/text()]"/>
                            <xsl:variable name="otherInsSameLevel"
                                          select="//*[name() = $currNode/name() and count(./preceding-sibling::*) = count($currNode/preceding-sibling::*)]/diff:ins[text() = $currDel/text()]"/>
                            <xsl:if test="not($otherDelSameLevel and $otherInsSameLevel) and not($otherDel and $otherIns)">
                                <xsl:call-template name="generateXPathElementText"/>
                            </xsl:if>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- Insert only if parent does not have attribute diff:insert -->
                            <xsl:variable name="parentIns" select="$currNode/ancestor-or-self::*[@diff:insert='true']"/>
                            <xsl:if test="not($parentIns)">
                                <xsl:call-template name="generateXPathElementText"/>
                            </xsl:if>
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
                        <xsl:variable name="currDelAttr" select="$currNode/@del:*[local-name() = $attName]"/>
                        <xsl:variable name="isNodeInsert" select="$currNode/@diff:insert"/>

                        <!-- insert only if not also "del" exists (then it would be incorrectAttributeValue) -->
                        <!-- insert only if current node is not inserted (then it would be displacedNode) -->
                        <xsl:if test="not($currDelAttr) and not($isNodeInsert)">
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
                        <xsl:variable name="currInsAttr" select="$currNode/@ins:*[local-name() = $attName]"/>
                        <xsl:variable name="isNodeDelete" select="$currNode/@diff:delete"/>

                        <!-- insert only if not also "ins" exists (then it would be incorrectAttributeValue) -->
                        <!-- insert only if current node is not deleted (then it would be displacedNode) -->
                        <xsl:if test="not($currInsAttr) and not($isNodeDelete)">
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
                        <!-- insert only if "ins" also exists, otherwise it is missing or superfluous -->
                        <xsl:if test="$currNode/@ins:*[local-name() = $attName]">
                            <!-- find other node with ins and del values switched (new value is equal to this delete value and vice versa) -->
                            <xsl:variable name="other"
                                          select="//*[name() = $currNode/name() and ./.. = $currNode/..][@del:*[local-name() = $attName] = $currNode/@*[local-name() = $attName] and @*[local-name() = $attName] = $currNode/@del:*[local-name() = $attName]]"/>

                            <!-- insert only if the values are not equal, otherwise only misplaced node -->
                            <xsl:if test="not($other) or deep-equal($currNode, $other)">
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
