<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:xlink="http://www.w3.org/1999/xlink" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:ts="http://www.w3.org/XML/2004/xml-schema-test-suite/" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:local="http://local"
    xsi:schemaLocation="http://www.w3.org/XML/2004/xml-schema-test-suite/ ../resources/eu/europa/publications/xml/common/xsts.xsd" 
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:output indent="no" encoding="UTF-8" method="text"/>
    
    <xsl:param name="generationDirectory" select="replace(local:basePath(base-uri(document(''))), '/src/.*$', '/target/generated-test-sources/java/')"/>
    <xsl:param name="relativePath" select="replace(local:basePath(base-uri()), '^.*/testSuites/', '')"/>
    <xsl:param name="modulePath" select="'eu/europa/publications/xml/'"/>
    <xsl:param name="moduleName" select="'eu.europa.publications.xml'"/>
    <xsl:param name="filename" select="local:filename(base-uri())"/>
    
    <xsl:variable name="dataDir" select="'./src/test/resources/eu/europa/publications/xml/testSuites/'"/>

    <xsl:template match="/">
        <xsl:param name="generationDirectory" select="$generationDirectory"/>
        <xsl:param name="relativePath" select="$relativePath" as="xs:string"/>
        <xsl:param name="modulePath" select="$modulePath" as="xs:string"/>
        <xsl:param name="moduleName" select="$moduleName" as="xs:string"/>
        <xsl:param name="filename" select="$filename" as="xs:string?"/>
        <DBG>
	        Scan document: <xsl:value-of select="base-uri()"/>
	           generationDirectory: <xsl:value-of select="$generationDirectory"/>
	           relativePath: <xsl:value-of select="$relativePath"/>
	           modulePath: <xsl:value-of select="$modulePath"/>
	           moduleName: <xsl:value-of select="$moduleName"/>
	           fileName: <xsl:value-of select="$filename"/>
	        
	        Generate package-info.java: 
	<!--        <xsl:call-template name="local:generateFile_package-info.java">
	            <xsl:with-param name="generationDirectory" select="$generationDirectory"/>
	            <xsl:with-param name="modulePath" select="$modulePath"/>
	            <xsl:with-param name="moduleName" select="$moduleName"/>
	            <xsl:with-param name="filename" select="$filename"/>
	        </xsl:call-template>-->
	 
	        <xsl:apply-templates>
	            <xsl:with-param name="generationDirectory" select="$generationDirectory"/>
	            <xsl:with-param name="relativePath" select="$relativePath"/>
	            <xsl:with-param name="modulePath" select="$modulePath"/>
	            <xsl:with-param name="moduleName" select="$moduleName"/>
	            <xsl:with-param name="filename" select="$filename"/>
	        </xsl:apply-templates>
        </DBG>
    </xsl:template>
    
    <xsl:template match="text()"/>
    
    <xsl:template match="ts:testSuite">
        <xsl:param name="generationDirectory" as="xs:string"/>
        <xsl:param name="relativePath" as="xs:string"/>
        <xsl:param name="modulePath" as="xs:string"/>
        <xsl:param name="moduleName" as="xs:string"/>
        <xsl:param name="filename" as="xs:string?"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="generationDirectory" select="$generationDirectory"/>
            <xsl:with-param name="relativePath" select="$relativePath"/>
            <xsl:with-param name="modulePath" select="$modulePath"/>
            <xsl:with-param name="moduleName" select="$moduleName"/>
            <xsl:with-param name="filename" select="$filename"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="ts:testSetRef[@xlink:href]">
        <xsl:param name="generationDirectory" as="xs:string"/>
        <xsl:param name="relativePath" as="xs:string"/>
        <xsl:param name="modulePath" as="xs:string"/>
        <xsl:param name="moduleName" as="xs:string"/>
        <xsl:param name="filename" as="xs:string?"/>
        
        <xsl:variable name="basePath" select="local:basePath(@xlink:href)"/>
        <xsl:variable name="filename" select="local:filename(@xlink:href)"/>
        
        <xsl:variable name="baseName" select="replace(replace(replace($basePath, '/$', ''), '/', '.'), '^(.)', '.$1')"/>

        <xsl:apply-templates select="document(@xlink:href, /)">
            <xsl:with-param name="generationDirectory" select="$generationDirectory"/>
            <xsl:with-param name="relativePath" select="concat($relativePath, $basePath)"/>
            <xsl:with-param name="modulePath" select="concat($modulePath, $basePath)"/>
            <xsl:with-param name="moduleName" select="concat($moduleName, $baseName)"/>
            <xsl:with-param name="filename" select="$filename"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="ts:testSet">
        <xsl:param name="generationDirectory" as="xs:string"/>
        <xsl:param name="relativePath" as="xs:string"/>
        <xsl:param name="modulePath" as="xs:string"/>
        <xsl:param name="moduleName" as="xs:string"/>
        <xsl:param name="filename" as="xs:string?"/>
        
        <xsl:variable name="basePath" select="concat(@name, '/')"/>
        <xsl:variable name="filename" select="@name"/>
        
        <xsl:variable name="baseName" select="replace(replace(replace($basePath, '/$', ''), '/', '.'), '^(.)', '.$1')"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="generationDirectory" select="$generationDirectory"/>
            <xsl:with-param name="relativePath" select="$relativePath"/>
            <xsl:with-param name="modulePath" select="$modulePath"/>
            <xsl:with-param name="moduleName" select="$moduleName"/>

<!--            <xsl:with-param name="relativePath" select="concat($relativePath, $basePath)"/>
            <xsl:with-param name="modulePath" select="concat($modulePath, $basePath)"/>
            <xsl:with-param name="moduleName" select="concat($moduleName, $baseName)"/>-->
            <xsl:with-param name="filename" select="$filename"/>
        </xsl:apply-templates>

    </xsl:template>

    <xsl:template match="ts:testGroup">
        <xsl:param name="generationDirectory" as="xs:string"/>
        <xsl:param name="relativePath" as="xs:string"/>
        <xsl:param name="modulePath" as="xs:string"/>
        <xsl:param name="moduleName" as="xs:string"/>
        <xsl:param name="filename" as="xs:string?"/>

        <xsl:call-template name="local:generateFile_xxxTests.java">
            <xsl:with-param name="generationDirectory" select="$generationDirectory"/>
            <xsl:with-param name="relativePath" select="$relativePath"/>
            <xsl:with-param name="modulePath" select="$modulePath"/>
            <xsl:with-param name="moduleName" select="$moduleName"/>
            <xsl:with-param name="filename" select="$filename"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="local:generateFile_package-info.java">
        <xsl:param name="generationDirectory" as="xs:string"/>
        <xsl:param name="modulePath" as="xs:string"/>
        <xsl:param name="moduleName" as="xs:string"/>
        <xsl:param name="filename" as="xs:string"/>
        
        <xsl:variable name="fullPath" select="concat($generationDirectory, $modulePath,'package-info.java')" as="xs:string"/>
        <xsl:result-document indent="no" encoding="UTF-8" method="text" href="{$fullPath}">
            <xsl:text>/**
 * This test modulePath is about validation of XML content
 * using XSD 1.1 validator and catalog resolver.
 * &lt;p&gt;
 * This modulePath combine xerce2-j validator and XML catalog resolver
 * &lt;/p&gt;
 *
 * @since 1.0
 * @author pcolot
 * @version 1.0
 */
package </xsl:text><xsl:value-of select="$moduleName"/><xsl:text>;</xsl:text>
        </xsl:result-document> 
        
    </xsl:template>

    <xsl:template name="local:generateFile_xxxTests.java">
        <xsl:param name="generationDirectory" as="xs:string"/>
        <xsl:param name="relativePath" as="xs:string"/>
        <xsl:param name="modulePath" as="xs:string"/>
        <xsl:param name="moduleName" as="xs:string"/>
        <xsl:param name="filename" as="xs:string?"/>
        
        <xsl:result-document indent="no" encoding="UTF-8" method="text" href="{concat($generationDirectory, $modulePath, @name, 'Tests.java')}">
			
            <xsl:text>package </xsl:text><xsl:value-of select="$moduleName"/><xsl:text>;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import eu.europa.publications.xml.tools.ApplicationHandler;
import eu.europa.publications.xml.tools.XSD11Validator;

/**
 * </xsl:text><xsl:value-of select="@description"/><xsl:text>.
 *
 * @since 1.0
 * @author pcolot
 * @version 1.0
 */
class </xsl:text><xsl:value-of select="replace(concat(@name, 'Tests'), '-|\.', '_')"/><xsl:text> {

    /** The name of this Logger will be "</xsl:text><xsl:value-of select="concat($moduleName, '.', @name, 'Tests')"/><xsl:text>".
     */
    private static final Logger logger = LogManager.getLogger();
    private static final Marker marker = MarkerManager.getMarker("CLASS");
    
    /** Root directory for all test resources
     */
    final String dataDir = "</xsl:text><xsl:value-of select="$dataDir"/><xsl:text>";

    /** Setup executed before each test.
     * 
     */
    @BeforeEach
    public void setup() {
        ApplicationHandler.resetErrorCount();
    }
    
</xsl:text> 
        <xsl:text>     // Group: </xsl:text><xsl:value-of select="@name"/><xsl:text>
     //    </xsl:text><xsl:value-of select="@description"/><xsl:text>

</xsl:text>
        
        <xsl:apply-templates select="ts:instanceTest">
            <xsl:with-param name="generationDirectory" select="$generationDirectory"/>
            <xsl:with-param name="relativePath" select="$relativePath"/>            
            <xsl:with-param name="modulePath" select="$modulePath"/>
            <xsl:with-param name="moduleName" select="$moduleName"/>
            <xsl:with-param name="filename" select="$filename"/>
            <xsl:with-param name="schema" select="ts:schemaTest[1]/ts:schemaDocument[1]/@xlink:href"/>
            <xsl:with-param name="catalog" select="ts:catalogTest[1]/ts:catalogDocument[1]/@xlink:href"/>
            <xsl:with-param name="testName" select="@name"/>
        </xsl:apply-templates>
        <xsl:text>
}
</xsl:text>               
         </xsl:result-document> 
    </xsl:template>


    <xsl:template match="ts:instanceTest">
        <xsl:param name="generationDirectory" as="xs:string"/>
        <xsl:param name="relativePath" as="xs:string"/>
        <xsl:param name="modulePath" as="xs:string"/>
        <xsl:param name="moduleName" as="xs:string"/>
        <xsl:param name="filename" as="xs:string?"/>
        <xsl:param name="schema" as="xs:string?"/>
        <xsl:param name="catalog" as="xs:string?"/>
        <xsl:param name="testName" as="xs:string?"/>
        
        <xsl:text>    /** </xsl:text><xsl:value-of select="replace(string-join(ts:annotation/ts:documentation//text(),''), '\n', ' ')"/><xsl:text>
     * 
     */
    @Test
	void </xsl:text><xsl:value-of select="replace(concat($testName, '__', @name), '-|\.', '_')"/><xsl:text>()
	{
	    final String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

		XSD11Validator xsd11Validator = new XSD11Validator();
		int returnValue = xsd11Validator.validateFile(</xsl:text><xsl:value-of select="local:stringOrNull($relativePath, ts:instanceDocument/@xlink:href)"/><xsl:text>, </xsl:text><xsl:value-of select="local:stringOrNull($relativePath, $schema)"/><xsl:text>, </xsl:text><xsl:value-of select="local:stringOrNull($relativePath, $catalog)"/><xsl:text>);
</xsl:text>
            <xsl:choose>
                <xsl:when test="not(empty(ts:expected/@withCode))">
                    <xsl:text>
		int expectedValue = </xsl:text><xsl:value-of select="ts:expected/@withCode"/><xsl:text>;
		boolean hasCode = (returnValue == expectedValue);
		if (hasCode) logger.info(marker, "Test: " + methodName + " returned: " + returnValue + " As expected");
		else logger.error(marker, "Test: " + methodName + " returned unexpected value: " + returnValue + " in place of expected value: " + expectedValue) ;
		assert hasCode;
</xsl:text>
                </xsl:when>
            </xsl:choose>
        <xsl:text>

		boolean asExpected = </xsl:text>
            <xsl:choose>
                <xsl:when test="ts:expected[last()]/@validity eq 'valid'">
                    <xsl:text>(returnValue == 0)</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>(returnValue != 0)</xsl:text>                    
                </xsl:otherwise>
            </xsl:choose>
        <xsl:text>;
		if (asExpected) logger.info(marker, "Test: " + methodName + " returned status: </xsl:text><xsl:value-of select="ts:expected/@validity"/><xsl:text> As expected");
		else logger.error(marker, "Test: " + methodName + " returned unexpected status: </xsl:text>
            <xsl:choose>
                <xsl:when test="ts:expected[last()]/@validity eq 'valid'">
                    <xsl:text>invalid</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>valid</xsl:text>                    
                </xsl:otherwise>
            </xsl:choose>
        <xsl:text>" ) ;
		assert asExpected;
	}</xsl:text>
    </xsl:template>


    <xsl:function name="local:basePath">
        <xsl:param name="path" as="xs:string"/>
        
        <xsl:variable name="dashes" select="index-of(string-to-codepoints($path), string-to-codepoints('/'))"/>
        <xsl:choose>
            <xsl:when test="not(empty($dashes))">
                <xsl:value-of select="substring($path, 1, xs:integer($dashes[last()]))"/>                
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="local:filename">
        <xsl:param name="path" as="xs:string"/>

        <xsl:variable name="dashes" select="index-of(string-to-codepoints($path), string-to-codepoints('/'))"/>
        <xsl:choose>
            <xsl:when test="not(empty($dashes))">
                <xsl:value-of select="substring($path, xs:integer($dashes[last()]) + 1, string-length($path))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$path"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="local:stringOrNull">
		<xsl:param name="relativePath" as="xs:string"/>
        <xsl:param name="path" as="xs:string?"/>

        <xsl:choose>
            <xsl:when test="empty($path) or ($path eq 'null()')">
                <xsl:text>null</xsl:text>
            </xsl:when>
            <xsl:when test="matches($path,'^(file:)?\./')">
                <xsl:text>"</xsl:text><xsl:value-of select="concat($dataDir, $relativePath, replace($path, '^(file:)?\./', ''))"/><xsl:text>"</xsl:text>
            </xsl:when>
            <xsl:when test="matches($path,'^(file:)?\.\./')">
                <xsl:text>"</xsl:text><xsl:value-of select="concat($dataDir, local:basePath(replace($relativePath, '/$', '')), replace($path, '^(file:)?\.\./', ''))"/><xsl:text>"</xsl:text>
            </xsl:when>
            <xsl:when test="matches($path,'^(file:)?/')">
                <xsl:text>"</xsl:text><xsl:value-of select="$path"/><xsl:text>"</xsl:text>
            </xsl:when>
            <xsl:when test="matches($path,'^(https?:)?/')">
                <xsl:text>"</xsl:text><xsl:value-of select="$path"/><xsl:text>"</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>"</xsl:text><xsl:value-of select="concat($dataDir, $relativePath, $path)"/><xsl:text>"</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>    
</xsl:stylesheet>
