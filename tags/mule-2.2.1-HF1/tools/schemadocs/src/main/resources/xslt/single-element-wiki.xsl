<xsl:stylesheet
        version="2.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        >

    <!-- $Id$ -->

    <!-- this generates html documentation for a particular, named element
         using the information within a single schema.

         a lot of information is available and comments can be found in
         many related areas of the schema; this tries to give a fairly
         comprehensive summary without too much duplication.

         a lot of things could be improved.  obvious targets include:
         - cross-linking
           (now supported via explicit pages in links.xml)
         - using multiple schema
           (current work-around is to use normalized schema - see normalize.sh)
           (normalized schema are also now generated by maven - see schemadocs profile)

         some features commented-out to give a "friendlier" interface.
    -->

    <!-- this is the element we will generate documentation for -->
    <xsl:param name="elementName"/>

    <xsl:output method="text"/>
    <xsl:include href="http://www.mulesource.org/xslt/mule/schemadoc/2.2/schemadoc-core-wiki.xsl"/>
    <!--Use this if testing locally -->
    <!--<xsl:include href="schemadoc-core-wiki.xsl"/>-->

    <xsl:template match="/">
        <xsl:apply-templates select="//xsd:element[@name=$elementName]" mode="single-element"/>
    </xsl:template>


</xsl:stylesheet>
