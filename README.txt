
Console:
========
Start "Perl (command line) [V5]"

Git Setup:
==========

git config --global http.proxy http://someUserName:somePassword@proxy-t2-bx.welcome.ec.europa.eu:8012



Java Setup:
===========
Start "Perl (command line) [V5]"

set JAVA_HOME=C:\ProgramData\AppV\A454648B-9095-4AC1-99F6-CBF5BBDF051A\2D2550AA-8F33-4BDF-ADF3-F4C30397D7BC\Root\plugins\org.eclipse.justj.openjdk.hotspot.jre.full.win32.x86_64_17.0.4.v20220903-1038\jre

set PATH=%PATH%;%JAVA_HOME%\bin

JAVA_FLAGS=-Dhttp.proxyUser=someUserName -Dhttp.proxyPassword=somePassword -Dhttps.proxyHost=proxy-t2-bx.welcome.ec.europa.eu -Dhttps.proxyPort=8012 -Dhttp.proxyHost=proxy-t2-bx.welcome.ec.europa.eu -Dhttp.proxyPort=8012 -Dhttp.nonProxyHosts="localhost|127.0.0.1"


java %JAVA_FLAGS% -version



Maven Setup:
============
@Java Setup

set MVN_HOME=D:\apache-maven-3.9.3

set "MAVEN_OPTS="

set PATH=%PATH%;%MVN_HOME%\bin

REM User Settings: C:\Users\COLOTPI\.m2\settings.xml
REM replace ... with current password
REM  <settings 
REM  	xmlns="http://maven.apache.org/SETTINGS/1.0.0" 
REM  	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
REM   	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
REM   <!-- <localRepository/> -->
REM   <!-- <interactiveMode/> -->
REM   <!-- <offline/> -->
REM   <!-- <pluginGroups/> -->
REM   <!-- <servers/> OxygenXml maven repository -->
REM   <!-- <mirrors/> -->
REM   <proxies>
REM     <proxy>
REM       <id>httpproxy</id>
REM       <active>true</active>
REM       <protocol>http</protocol>
REM       <host>proxy-t2-bx.welcome.ec.europa.eu</host>
REM       <port>8012</port>
REM       <username>colotpi</username>
REM       <password>...</password>
REM       <nonProxyHosts>localhost,127.0.0.1</nonProxyHosts>
REM     </proxy>
REM     <proxy>
REM       <id>myhttpsproxy</id>
REM       <active>true</active>
REM       <protocol>https</protocol>
REM       <host>proxy-t2-bx.welcome.ec.europa.eu</host>
REM       <port>8012</port>
REM       <username>colotpi</username>
REM       <password>...</password>
REM       <nonProxyHosts>localhost,127.0.0.1</nonProxyHosts>
REM     </proxy>
REM   </proxies>
REM   <profiles>
REM     <profile>
REM       <repositories>
REM         <repository>
REM           <id>oxygenxml</id>
REM           <name>Oxygen XML</name>
REM           <releases>
REM             <enabled>true</enabled>
REM             <updatePolicy>always</updatePolicy>
REM             <checksumPolicy>warn</checksumPolicy>
REM           </releases>
REM           <snapshots>
REM             <enabled>true</enabled>
REM             <updatePolicy>never</updatePolicy>
REM             <checksumPolicy>fail</checksumPolicy>
REM           </snapshots>
REM           <url>https://www.oxygenxml.com/maven</url>
REM           <layout>default</layout>
REM         </repository>
REM         <repository>
REM           <id>central</id>
REM           <url>https://repo.maven.apache.org/maven2</url>
REM           <releases>
REM             <enabled>true</enabled>
REM           </releases>
REM         </repository>
REM       </repositories>
REM       <pluginRepositories>
REM         <pluginRepository>
REM           <id>central</id>
REM           <url>https://repo.maven.apache.org/maven2</url>
REM           <releases>
REM             <enabled>true</enabled>
REM           </releases>
REM         </pluginRepository>
REM       </pluginRepositories>
REM     </profile>
REM   </profiles>
REM   <!-- <activeProfiles/> -->
REM </settings>

mvn -v

REM example of project generation: mvn org.apache.maven.plugins:maven-archetype-plugin:2.4:generate -DarchetypeGroupId=com.oxygenxml.samples -DarchetypeArtifactId=oxygen-sdk-samples-archetype -DarchetypeVersion=25.1.0.2 -DgroupId=myGroup -DartifactId=mySample -Dversion=1.0-SNAPSHOT -DarchetypeRepository=https://www.oxygenxml.com/maven/

Xerces-Validator
================

cd /D D:\OP\_DEV\XSD11Validator

mvn clean compile test site install

Usage:
======
cd /D D:\OP\_DEV\XSD11Validator

D:\OP\_DEV\XSD11Validator>java -jar target\XSD11Validator-2.0-SNAPSHOT-shaded.jar
17:09:30.487 [main] ERROR eu.europa.publications.ifc.cov.xerces.Main - Missing required option: i
usage: XSD11Validator
 -c,--catalog <arg>   OASIS root catalog file path
 -i,--input <arg>     XML input file path
 -s,--schema <arg>    XML schema file path



D:\OP\_DEV\XSD11Validator>java -jar target\XSD11Validator-2.0-SNAPSHOT-shaded.jar -i .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xml -s .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xsd -c .\src\test\resources\eu\europa\publications\ifc\cov\xerces\catalog.xml
1. ←[34m11:33:03 eu.europa.publications.ifc.cov.xerces.XSD11Validator:83 XSD 1.1 validation using catalog on File .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xml using schema .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xsd and catalog .\src\main\resources\eu\europa\publications\ifc\cov\xerces\catalog.xml
←[m2. ←[34m11:33:03 eu.europa.publications.ifc.cov.xerces.Main:110 return Code: 0
←[m

D:\OP\_DEV\XSD11Validator>java  -Dlog4j.configurationFile=./src/test/resources/eu/europa/publications/ifc/cov/xerces/log4j2.xml 
-jar target\XSD11Validator-2.0-SNAPSHOT-shaded.jar -i .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xml -s .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xsd -c .\src\test\resources\eu\europa\publications\ifc\cov\xerces\catalog.xml
1. ←[34m11:34:14 eu.europa.publications.ifc.cov.xerces.XSD11Validator:83 XSD 1.1 validation using catalog on File .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xml using schema .\src\test\resources\eu\europa\publications\ifc\cov\xerces\xerces-test.xsd and catalog .\src\test\resources\eu\europa\publications\ifc\cov\xerces\catalog.xml
←[m2.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:292 XML Catalog Root Dir: D:\OP\_DEV\XSD11Validator\.\src\test\resources\eu\europa\publications\ifc\cov\xerces
3.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:404 Get XML schema namespace associated to root element
4.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:407 NamespaceURI : http://ifc.europa.eu/common_vocabulary
5.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:539 getAbsoluteUriFromRelativeorAbsolute: HTTP: http://ifc.europa.eu/common_vocabulary from http://ifc.europa.eu/common_vocabulary based on: file:///D:/OP/_DEV/XSD11Validator/src/test/resources/eu/europa/publications/ifc/cov/xerces/
6.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:428 If a namespace is associated to root element Get SchemaLocations
7.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:433 SchemaLocations Resolution of resPublicId: http://ifc.europa.eu/common_vocabulary using : http://ifc.europa.eu/common_vocabulary ../../../ifc/cov/xerces/xerces-test.xsd
8.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:444 schemaLocationSystemId: ../../../ifc/cov/xerces/xerces-test.xsd
9.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:446 getAbsoluteUriFromRelativeorAbsolute resPublicId: ../../../ifc/cov/xerces/xerces-test.xsd using : xmlInputStreamSource.getSystemId())
10.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:549 getAbsoluteUriFromRelativeorAbsolute: Relative URI: file:/D:/OP/_DEV/XSD11Validator/src/test/resources/eu/europa/publications/ifc/cov/xerces/xerces-test.xsd from ../../../ifc/cov/xerces/xerces-test.xsd based on: file:///D:/OP/_DEV/XSD11Validator/src/test/resources/eu/europa/publications/ifc/cov/xerces/
11.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:480 Catalog Resolution of resSystemId: ../../../ifc/cov/xerces/xerces-test.xsd using : catalogResolver
12.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:483 Resolve resSystemId using XMLCatalogResolver: null
13.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:500 Clean room pattern
14.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:180 Extracted schema name URL: file:/D:/OP/_DEV/XSD11Validator/src/test/resources/eu/europa/publications/ifc/cov/xerces/xerces-test.xsd
15.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:189 XSD Schema Absolute Path: file:/D:/OP/_DEV/XSD11Validator/./src/test/resources/eu/europa/publications/ifc/cov/xerces/xerces-test.xsd
16.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:192 Internal schema name: file:/D:/OP/_DEV/XSD11Validator/src/test/resources/eu/europa/publications/ifc/cov/xerces/xerces-test.xsd differs from schema passed as argument: file:/D:/OP/_DEV/XSD11Validator/./src/test/resources/eu/europa/publications/ifc/cov/xerces/xerces-test.xsd
17.         11:34:14     eu.europa.publications.ifc.cov.xerces.XSD11Validator:200 Creating schema from file:/D:/OP/_DEV/XSD11Validator/./src/test/resources/eu/europa/publications/ifc/cov/xerces/xerces-test.xsd
18.         11:34:15     eu.europa.publications.ifc.cov.xerces.XSD11Validator:100 Validate ended without errors
19.         11:34:15     eu.europa.publications.ifc.cov.xerces.XSD11Validator:104 XML document successfully validated.
20.         11:34:15     eu.europa.publications.ifc.cov.xerces.XSD11Validator:118 XML document validation finished.
21. ←[34m11:34:15 eu.europa.publications.ifc.cov.xerces.Main:110 return Code: 0



TODO after changes:
===================
- Review documentation
- Add new test to cover changes


TODO:
=====

CodeQualityReview for QC Review
  Review codestyle rules to comply with my usage
  Improve code by removing coding imperfections


Manage DTD systemId publicId
Tests
 noNamespaceSchemaLocation tests
 defaultnamespace tests
 Test with different kind of catalog
 Test with http resources
 
 ++ Test with official suite

