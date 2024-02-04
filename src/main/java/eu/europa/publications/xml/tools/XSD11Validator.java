package eu.europa.publications.xml.tools;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.Authenticator;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.XMLConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.xerces.util.XMLCatalogResolver;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * XSD 1.1 validation main class.
 *
 * @since 1.0
 * @author pcolot
 * @version 1.0
 */
public final class XSD11Validator {
    
    /** The name of this LOGGER will be
    /* "eu.europa.publications.xml.tools.XSD11Validator".
    */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The name of this MARKER will be the current Class.
    */
    private static final Marker MARKER = MarkerManager.getMarker("CLASS");
    
    /** Create a error handler.
     */
    private static final ValidatorErrorHandler ERRORHANDLER = new ValidatorErrorHandler();
    
    /**
     * Class constructor.
     *
     */
    public XSD11Validator() {
        
        final String proxyHttp = System.getProperty("http.proxyHost");
        final String proxyHttps = System.getProperty("https.proxyHost");
        final String user = System.getProperty("http.proxyUser");
        final String password = System.getProperty("http.proxyPassword");
        
        
        if ((proxyHttp != null) || (proxyHttps != null)) {
            if ((user != null) && (password != null)) {
                LOGGER.trace(MARKER, "Setting up proxy configuration with authentification");
                // Proxy with required authentification
                Authenticator.setDefault(new ProxyAuthenticator(user, password));
            } else {
                LOGGER.trace(MARKER, "Setting up proxy configuration without authentification");
                // Proxy without required authentification
            }
        } else {
            // No proxy configured
        }
    }

    /**
     * Validate XML instance using XML Schema 1.1 and OASIS catalog support.
     * <p>
     * Example of usage:
     * <p>
     * returnCode = xsd11Validator.validateFile("myPathToXMLSourceFile.xml",
     * "myPathToXMLSchemaFile.xml", "myPathToXMLCatalogFile.xml");
     *
     * @param xmlInput the optional XML source  
     * @param xsdInput the optional XML schema 
     * @param catalogFileName the optional OASIS catalog 
     * @return The integer return code 0: successful >0: failed with number of
     * errors
     *
     * @since 1.0
     * @author pcolot
     * @version 1.0
     */
    public int validateXmlInstance(final String xmlInput, final String xsdInput,
            final String catalogFileName) {
        
        // 1. Logging using Log4j2
        LOGGER.info(MARKER,
                    "XSD 1.1 validation using catalog on File " + xmlInput
                        + " using schema " + xsdInput + " and catalog "
                        + catalogFileName);

        try {

            // 2. Create a Stream for the XML instance to be read
            StreamSource xmlInputStreamSource = getXmlInputStream(xmlInput);
            
            // 3. Get the validator from provide schema, catalog and xml input
            Validator validator = getValidator(xsdInput, catalogFileName, xmlInputStreamSource);
            
            // 3. Validate the Stream source
            validator.validate(xmlInputStreamSource);
            LOGGER.trace(MARKER, "Validate ended without errors");
        
        // 5. Alternate flows
        } catch (SAXException e) {
            LOGGER.error(MARKER, "XML document is invalid with "
                    + ApplicationHandler.getErrorCount() + " error(s).");
        } catch (Exception e) {
            LOGGER.error(MARKER, "XML document is invalid with "
                    + ApplicationHandler.getErrorCount() + " error(s).");
        } finally {
            // 20. Display end of validation
            LOGGER.trace(MARKER, "XML document validation finished.");
        }
        return ApplicationHandler.getErrorCount();
    }

    
    /**
     * Validate Schema using XML Schema 1.1 and OASIS catalog support.
     * <p>
     * Example of usage:
     * <p>
     * returnCode = xsd11Validator.validateSchema("myPathToXMLSchemaFile.xml", "myPathToXMLCatalogFile.xml");
     * 
     * @param xsdInput the optional XML schema 
     * @param catalogFileName the optional OASIS catalog 
     * @return The integer return code 0: successful >0: failed with number of
     * errors
     *
     * @since 1.0
     * @author pcolot
     * @version 1.0
     */
    public int validateSchema(final String xsdInput,
            final String catalogFileName) {
        
        // 1. Logging using Log4j2
        LOGGER.info(MARKER,
                    "XSD 1.1 validation using catalog on schema " + xsdInput + " and catalog "
                        + catalogFileName);

        try {
            
            // 1. Get the validator from provide schema, catalog and xml input
            getValidator(xsdInput, catalogFileName);
            LOGGER.trace(MARKER, "Validate ended without errors");
        
        // 5. Alternate flows
        } catch (SAXException e) {
            LOGGER.error(MARKER, "XML document is invalid with "
                    + ApplicationHandler.getErrorCount() + " error(s).");
        } catch (Exception e) {
            LOGGER.error(MARKER, "XML document is invalid with "
                    + ApplicationHandler.getErrorCount() + " error(s).");
        } finally {
            // 20. Display end of validation
            LOGGER.trace(MARKER, "XML document validation finished.");
        }
        return ApplicationHandler.getErrorCount();
    }    
    
    
    /** Get Validator engine.
     * 
     * @param xsdInput
     * @param catalogFileName
     * @param xmlInputStreamSource
     * @return The Validator
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private Validator getValidator(
            final String xsdInput,
            final String catalogFileName,
            StreamSource xmlInputStreamSource) 
                    throws ApplicationHandler, SAXNotRecognizedException, SAXNotSupportedException {

        return getValidatorEngine(xsdInput, catalogFileName, xmlInputStreamSource);
    }
 
    /** Get Validator engine.
     * 
     * @param xsdInput
     * @param catalogFileName
     * @param xmlInputStreamSource
     * @return The Validator
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private Validator getValidator(
            final String xsdInput,
            final String catalogFileName) 
                    throws ApplicationHandler, SAXNotRecognizedException, SAXNotSupportedException {
   
        return getValidatorEngine(xsdInput, catalogFileName, null);
    }
    
    /** Get Validator engine.
     * 
     * @param xsdInput
     * @param catalogFileName
     * @param xmlInputStreamSource
     * @return The Validator
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private Validator getValidatorEngine(
            final String xsdInput,
            final String catalogFileName,
            StreamSource xmlInputStreamSource) 
                    throws ApplicationHandler, SAXNotRecognizedException, SAXNotSupportedException {

        // 1. Get the Validator
        if (xmlInputStreamSource != null) { LOGGER.trace(MARKER, "Get Validator on XML Stream sysid: " +  xmlInputStreamSource.getSystemId() + ", publicId: " + xmlInputStreamSource.getPublicId()); }
        if (xsdInput != null) { LOGGER.trace(MARKER, "  using external xsd: " + xsdInput); }
        if (catalogFileName != null) { LOGGER.trace(MARKER, "  and catalog: " + catalogFileName); }

        // 2. Create a catalog resolver
        XMLCatalogResolver catalogResolver = getCatalogResolver(catalogFileName);
        
        // 3. Create a schema engine
        Schema schema = (xmlInputStreamSource != null) ? getSchema(xsdInput, xmlInputStreamSource, catalogResolver) : getSchema(xsdInput, catalogResolver);
        
        // 4. Create a validator based on the schema engine
        Validator validator = schema.newValidator();
        
        // 5. Set validator using the entity resolver as catalog resolver
        validator.setProperty("http://apache.org/xml/properties/internal/entity-resolver", catalogResolver);
        
        // 6. Attach an error handler to validator
        validator.setErrorHandler(ERRORHANDLER);
        
        return validator;
    }

    
    /** Get Schema engine.
     * 
     * @param xsdInput
     * @param xmlInputStreamSource
     * @param catalogResolver
     * @return The Schema
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static Schema getSchema(
            final String xsdInput,
            StreamSource xmlInputStreamSource,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler,
            SAXNotRecognizedException, SAXNotSupportedException {

        // 1. Get schema name
        URI xsdInputURI = getXsdURI(xsdInput, xmlInputStreamSource, catalogResolver);
        
        return getSchema(xsdInputURI, catalogResolver);
    }
    
    /** Get Schema engine.
     * 
     * @param xsdInput
     * @param catalogResolver
     * @return The Schema
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static Schema getSchema(
            final String xsdInput,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler,
            SAXNotRecognizedException, SAXNotSupportedException {

        // 1. Get schema name
        URI xsdInputURI = getURI(xsdInput);
        
        return getSchema(xsdInputURI, catalogResolver);
    } 
    
    /** Get Schema engine.
     * 
     * @param xsdInputURI
     * @param catalogResolver
     * @return The Schema
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException   */
    private static Schema getSchema(
            final URI xsdInputURI,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler,
            SAXNotRecognizedException, SAXNotSupportedException {
        
        // 1. Get the Validator
        if (xsdInputURI != null) { LOGGER.trace(MARKER, "  using external xsd: " + xsdInputURI.toString()); }
        LOGGER.trace(MARKER, "  and catalog: " + catalogResolver);
        
        // 2. Create a schema factory for XML 1.1
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
        
        //activating all xpath features supported (version 1, 2, and ...) 
        schemaFactory.setFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking", true);

        //restrict XML constants to avoid Deny Of Service attack
        schemaFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        
        //handle multiple files with same targetnamespace
        schemaFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);
        
        // associate the schema factory with the resource resolver, which is responsible for resolving the imported XSD's
        //schemaFactory.setResourceResolver(new ResourceResolver());

        // 4. Get the schema
        LOGGER.trace(MARKER, "Creating schema from " + xsdInputURI);
        Schema schema = null;
        if (xsdInputURI != null) {
            try {
                schema = schemaFactory.newSchema(xsdInputURI.toURL());
            } catch (Exception e) {
                ApplicationHandler.error(e);
            }
        } else {
            ApplicationHandler.error("No schema detected");
        }
        
        return schema;
    }

    /** Get Xsd URI.
     * 
     * @param xsdInput
     * @param xmlInputStreamSource
     * @param catalogResolver
     * @return The URI
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static URI getXsdURI(
            final String xsdInput,
            StreamSource xmlInputStreamSource,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler,
            SAXNotRecognizedException, SAXNotSupportedException {
        
        // 1. Extract Xsd URI
        LOGGER.trace(MARKER, "Get Xsd URI from XML Stream sysid: " +  xmlInputStreamSource.getSystemId() + ", publicId: " + xmlInputStreamSource.getPublicId());
        if (xsdInput != null) { LOGGER.trace(MARKER, "  using external xsd: " + xsdInput); }
        LOGGER.trace(MARKER, "  and catalog: " + catalogResolver);

        // 2. Get the internal referred schema
        URI internalSchemaNameURI = getXsdInternalURI(xmlInputStreamSource, catalogResolver);
        LOGGER.trace(MARKER, "Extracted internal schema name URI: " + internalSchemaNameURI);
        
        if (internalSchemaNameURI == null) {
            // Fall back to DTD
            internalSchemaNameURI = getDtdInternalURI(xmlInputStreamSource, catalogResolver);
            LOGGER.trace(MARKER, "Extracted internal DTD name URI: " + internalSchemaNameURI);
        }
        
        if (xsdInput == null) {
         // 3. Without forced schema used the resolved internal one
            if (internalSchemaNameURI != null) {
            URI xsdInputURI = internalSchemaNameURI;
            
            return xsdInputURI;
            } else {
                throw new ApplicationHandler("Neither a nonamespace nor a namespace is attached to the root nor a default no namespace location is provided");
            }
        } else {
            // 4. Unless used the forced schema
            try {
                URI xsdInputURI = getURI(xsdInput);
                LOGGER.trace(MARKER, "XSD Schema Absolute Path: " + xsdInputURI);
                
                // check forced schema is equal to internal format unless warn
                if ((internalSchemaNameURI != null) && (!xsdInputURI.equals(internalSchemaNameURI))) {
                    LOGGER.warn(MARKER, "Internal schema name: "
                            + internalSchemaNameURI
                            + " differs from schema passed as argument: "
                            + xsdInputURI);
                }
                
                return xsdInputURI;
            } catch ( ApplicationHandler a) {
                throw a;
            } catch (Exception e) {
                throw new ApplicationHandler(e);
            }
        }
    }
    
    
    /** Get the schema URI defined inside the XML instance.
     * 
     * @param xsdInput
     * @param xmlInputStreamSource
     * @param catalogResolver
     * @return The URI
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static URI getXsdInternalURI(
            StreamSource xmlInputStreamSource,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler {
        try {            
            // 1. Extract Xsd URI
            LOGGER.trace(MARKER, "Get internal Xsd URI from XML Stream sysid: " +  xmlInputStreamSource.getSystemId() + ", publicId: " + xmlInputStreamSource.getPublicId());
            LOGGER.trace(MARKER, "  and catalog: " + catalogResolver);
            
            //2. Get the XML root qualified namespace 
            XMLStreamReader root = getRoot(xmlInputStreamSource);
            
            //3. Get the namespace
            QName qname = root.getName();
            String namespaceURI = qname.getNamespaceURI();

            LOGGER.trace(MARKER, "Get root name : " + qname .toString());
            
            if (namespaceURI.isEmpty()) {
                // noschemalocation used
                LOGGER.trace(MARKER, "No namespace is associated to root element, use noNamespaceSchemaLocation");
                
                namespaceURI = root.getAttributeValue( "http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation");
                
                if (namespaceURI != null) {
                    LOGGER.trace(MARKER, "noNamespaceSchemaLocation is " + namespaceURI);
                } else {
                    // namespace is null if #defaultNameSpace has to be used
                }
                
            } else {
                //get schemalocations
                LOGGER.trace(MARKER, "The following namespace is associated to the root element: " + namespaceURI);
                
                String schemaLocations = root.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
                
                if (schemaLocations != null) {
                    //schemaLocations present
                    LOGGER.trace(MARKER, "The schemalocations associated to root element is: " + schemaLocations);
                    
                    String resolved = resolveLocation(schemaLocations, namespaceURI);
                    if (!resolved.isEmpty()) {
                        //schemaLocations used
                        LOGGER.trace(MARKER, "The URI " + namespaceURI + " is resolved by schemalocations as: " + resolved);
                        
                        namespaceURI = resolved;
                    } else {
                        // schemaLocations not used
                        // keep current namespaceURI
                        LOGGER.trace(MARKER, "The URI " + namespaceURI + " is not resolved by schemalocations and stay unchanged");
                    }
                } else {
                    // schemaLocations not present
                    // keep current namespaceURI
                    LOGGER.trace(MARKER, "No schemalocations is associated to root element the URI " + namespaceURI + " stay unchanged");
                }
            }
            
            // Release XMLStreamReader resources
            root.close();
            
            // if any namespace is defined try to resolved it
            if (namespaceURI != null) {
                //4. Resolve namespaceURI against catalog Public, System, Relative to XMLInput
                LOGGER.trace(MARKER, "Resolve URI " + namespaceURI + " against catalog Public, System, Relative to XMLInput");
                
                String resolvedId = catalogResolver.resolvePublic(namespaceURI, null);
                if (resolvedId == null) {
                    LOGGER.trace(MARKER, "Failed Resolving of URI " + namespaceURI + " against Public catalog");
                    
                    resolvedId = catalogResolver.resolveSystem(namespaceURI);
                    if (resolvedId == null) {
                        LOGGER.trace(MARKER, "Failed Resolving of URI " + namespaceURI + " against System catalog");
                        
                        resolvedId = resolveRelativePath(xmlInputStreamSource.getSystemId(), namespaceURI);
                        if (resolvedId == null) {
                            LOGGER.trace(MARKER, "Failed Resolving of URI " + namespaceURI + " relative to XMLInput");
                            resolvedId = namespaceURI;
                        } else {
                            LOGGER.trace(MARKER, "Successful Resolving of URI " + namespaceURI + " relative to XMLInput as " + resolvedId);                
                        }
                    } else {
                        LOGGER.trace(MARKER, "Successful Resolving of URI " + namespaceURI + " against System catalog as " + resolvedId);                
                    }
                } else {
                    LOGGER.trace(MARKER, "Successful Resolving of URI " + namespaceURI + " against Public catalog as " + resolvedId);                
                }
                return getURI(resolvedId);                
            } else {
                return null;
            }
        } catch ( ApplicationHandler a) {
            throw a;
        } catch ( Exception e) {
            throw new ApplicationHandler(e);
        }
    }
    
    
    /** Get the DTD URI defined inside the XML instance.
     * 
     * @param xsdInput
     * @param xmlInputStreamSource
     * @param catalogResolver
     * @return The URI
     * @throws ApplicationHandler The application exception handler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static URI getDtdInternalURI(
            StreamSource xmlInputStreamSource,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler {
        try {            
            // 1. Extract DTD URI
            LOGGER.trace(MARKER, "Get internal DTD URI from XML Stream sysid: " +  xmlInputStreamSource.getSystemId() + ", publicId: " + xmlInputStreamSource.getPublicId());
            LOGGER.trace(MARKER, "  and catalog: " + catalogResolver);
            
            final InputStream inputStream = new URL(xmlInputStreamSource.getSystemId()).openStream();
            final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            
            //documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            // If you can't completely disable DTDs, then at least do the following:
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            // JDK7+ - http://xml.org/sax/features/external-general-entities
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            // JDK7+ - http://xml.org/sax/features/external-parameter-entities
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            // Disable external DTDs as well
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);
            
            documentBuilderFactory.setNamespaceAware(false);
            documentBuilderFactory.setValidating(false);
            
            final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            //documentBuilder.setEntityResolver(catalogResolver);
            Document document = documentBuilder.parse(inputStream);
            String namespaceURI;
            if (document.getDoctype() != null) {
                String systemId = document.getDoctype().getSystemId();
                String publicId = document.getDoctype().getPublicId();
                
                namespaceURI = (systemId != null) ? systemId : publicId;
            } else {
                namespaceURI = null;
            }
            inputStream.close();
            

            LOGGER.trace(MARKER, "Get namespaceURI : " + namespaceURI);

            // if any namespace is defined try to resolved it
            if (namespaceURI != null) {
                //4. Resolve namespaceURI against catalog Public, System, Relative to XMLInput
                LOGGER.trace(MARKER, "Resolve URI " + namespaceURI + " against catalog Public, System, Relative to XMLInput");
                
                String resolvedId = catalogResolver.resolvePublic(namespaceURI, null);
                if (resolvedId == null) {
                    LOGGER.trace(MARKER, "Failed Resolving of URI " + namespaceURI + " against Public catalog");
                    
                    resolvedId = catalogResolver.resolveSystem(namespaceURI);
                    if (resolvedId == null) {
                        LOGGER.trace(MARKER, "Failed Resolving of URI " + namespaceURI + " against System catalog");
                        
                        resolvedId = resolveRelativePath(xmlInputStreamSource.getSystemId(), namespaceURI);
                        if (resolvedId == null) {
                            LOGGER.trace(MARKER, "Failed Resolving of URI " + namespaceURI + " relative to XMLInput");
                            resolvedId = namespaceURI;
                        } else {
                            LOGGER.trace(MARKER, "Successful Resolving of URI " + namespaceURI + " relative to XMLInput as " + resolvedId);                
                        }
                    } else {
                        LOGGER.trace(MARKER, "Successful Resolving of URI " + namespaceURI + " against System catalog as " + resolvedId);                
                    }
                } else {
                    LOGGER.trace(MARKER, "Successful Resolving of URI " + namespaceURI + " against Public catalog as " + resolvedId);                
                }
                return getURI(resolvedId);                
            } else {
                return null;
            }
        } catch ( ApplicationHandler a) {
            throw a;
        } catch ( Exception e) {
            throw new ApplicationHandler(e);
        }
    }
    
    /** Get XML input stream. 
     * 
     * @param xmlInput The URI of the XML input 
     * @throws ApplicationHandler The application exception handler
     * @return The Stream Source
     */
    public static StreamSource getXmlInputStream(final String xmlInput)
            throws ApplicationHandler {
        
        // 1. Get the XML Stream
        LOGGER.trace(MARKER, "Get Xml Input Stream from " + xmlInput);
        
        // 2. if a input is provided
        if (xmlInput != null) {
            try {
                // try to convert xmlInput as URI
                URI uri = getURI(xmlInput);
                // create the stream
                return new StreamSource(uri.toString());
            } catch ( ApplicationHandler a) {
                throw a;
            } catch (Exception e) {
                throw new ApplicationHandler(e);
            } 
        } else {
            throw new ApplicationHandler("No source detected");
        }
    }
    
    
    /** Get Catalog resolver engine.
     * 
     * @param catalogFileName
     * @param catalogResolver
     * @return The Catalog Resolver
     */
    private static XMLCatalogResolver getCatalogResolver(final String catalogFileName) {
        XMLCatalogResolver catalogResolver = new XMLCatalogResolver();
        
        // 1. Get the XML Catalog Resolver
        LOGGER.trace(MARKER, "Get XML Catalog Resolver from " + catalogFileName);
        
        // if any XMLCatalogResolver is provided
        if (catalogFileName != null) {
            catalogResolver.setPreferPublic(true);
            catalogResolver.setCatalogList(new String[] {catalogFileName});
        }
        
        return catalogResolver;
    }
    
    
    /** Get Root Qualified Name.
     * 
     * @param xmlInputStreamSource
     * @return The XML Stream Reader on the root element
     * @throws ApplicationHandler The application exception handler
     */
    private static XMLStreamReader getRoot(
            StreamSource xmlInputStreamSource) throws ApplicationHandler {
        try {
            // 1. Extract Qualified Name from root element
            LOGGER.trace(MARKER, "Get the namespace from XML Stream sysid: " +  xmlInputStreamSource.getSystemId() + ", publicId: " + xmlInputStreamSource.getPublicId());
            
            // 2. Create factory to handle XML input
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            
            // 3. Create XML Stream using factory
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlInputStreamSource);
            
            // 4. XML Stream has content
            while (xmlStreamReader.hasNext()) {
        
                // 4. Get first XML content
                xmlStreamReader.next();
                
                // 5. XML is a element and is the root first and only processed element
                if (xmlStreamReader.isStartElement()) {
                    
                    // 8. Get Qualified Name associated to root element
                    LOGGER.trace(MARKER, "Get root element");
                    
                    return xmlStreamReader;
                } 
            }  
            throw new ApplicationHandler("XML input is not a XML content");
            
        } catch ( ApplicationHandler a) {
            throw a;
        } catch (Exception e) {
            throw new ApplicationHandler(e);
        }
    }


    
    /** Resolve the relative Path using the base path
     * 
     * @param basePathOrURI The base path of the file or of the URI
     * @param relativePath The relative path of the file or of the URI
     * @return The resolved path
     * @throws ApplicationHandler The application exception handler 
     */
    public static String resolveRelativePath(String basePathOrURI, String relativePath) 
            throws ApplicationHandler {
        
        File file = new File(relativePath);
        if (file.isAbsolute()) {
            return getURI(relativePath).toString();
        } else {
            if ( basePathOrURI.startsWith("urn:") ) {
                
                URI baseURI;
                try {
                    baseURI = new URI(basePathOrURI);
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid base URI: " + basePathOrURI);
                }
        
                return baseURI.toString();
            } else if ( basePathOrURI.startsWith("http:") || basePathOrURI.startsWith("https:") || basePathOrURI.startsWith("file:") ) {
                basePathOrURI = basePathOrURI.substring(0, basePathOrURI.lastIndexOf("/") + 1);
                
                URI baseURI;
                try {
                    baseURI = new URI(basePathOrURI);
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid base URI: " + basePathOrURI);
                }
        
                URI resolvedURI;
                try {
                    resolvedURI = baseURI.resolve(relativePath);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid relative URI path: " + relativePath);
                }
        
                return resolvedURI.toString();
            } else {
                basePathOrURI = new File(basePathOrURI).getAbsolutePath();

                File resolved = new File(basePathOrURI, relativePath);
                return resolved.getAbsolutePath();
            }            
        }

    }

        
    /** Get the base path of a File or of an URI
     * 
     * @param filePathOrURI Filename or URI to extract the path
     * @return The path
     * @throws ApplicationHandler The application exception handler
     */
    public static String getBasePath(String filePathOrURI)
            throws ApplicationHandler {
        if (filePathOrURI.startsWith("http:") || filePathOrURI.startsWith("https:") || filePathOrURI.startsWith("file:") || filePathOrURI.startsWith("urn:")) {
            // Extract the base path from a URI
            URI uri;
            try {
                uri = new URI(filePathOrURI);
            } catch (URISyntaxException e) {
                throw new ApplicationHandler("Invalid URI: " + filePathOrURI);
            }
            return uri.getScheme() + "://" + uri.getHost() + "/" + uri.getPath().substring(0, uri.getPath().lastIndexOf("/") + 1);
        } else {            
            // Extract the base path from a file path
            try {
                File file = new File(filePathOrURI);
                return file.getCanonicalPath() + "/";
            } catch (Exception e) {
                throw new ApplicationHandler("Invalid URI: " + filePathOrURI);
            }
        } 
    }

    
    /** Get URI from system or URI resource.
     * 
     * @param filePathOrURI Filename or URI to extract the URI
     * @return The URI
     * @throws ApplicationHandler The application exception handler
     */
    private static URI getURI(final String filePathOrURI)
            throws ApplicationHandler {
    
        // 1. Get the URI from system or http resource
        LOGGER.trace(MARKER, "Get input URI from " + filePathOrURI);
        
        // if any filePathOrURI is provided
        if (filePathOrURI != null) {
            try {
                if (filePathOrURI.startsWith("http:") || filePathOrURI.startsWith("https:") || filePathOrURI.startsWith("file:") || filePathOrURI.startsWith("urn:")) {
                    // Extract the path as a URI
                    return new URI(filePathOrURI);
                } else {
                    // try to use input as a local file
    
                    // Create Stream source using filename
                    File file = new File(filePathOrURI);
                    URI fileURI = file.getCanonicalFile().toURI();
                    return fileURI;
                }
            } catch (Exception e) {
                throw new ApplicationHandler(e);
            }
        } else {
            throw new ApplicationHandler("No input to be converted to URI");
        }
    }

    
    /** Resolve namespaceURI in the schemaLocations.
     * 
     * @param schemaLocations The schema locations
     * @param namespaceURI The namespace to be resolved
     * @return The resolved namespaceURI
     */
    public static String resolveLocation(final String schemaLocations, final String namespaceURI) {
        String res = "";
        
        boolean found = false;
        int i = 0;
        for (String str : schemaLocations.split(" ")) {
            if ( found ) {
                res = str;
                return str;
            }
    
            if (i % 2 == 0) {
                found = str.equals(namespaceURI);
            } 
    
            i++;
        }
        return res;
    }

}

//Develop

//TODO add selector for version 1.0 or 1.1 (1.1 not fully backward compatble)
//TODO add default catalog xml.xsd, ...

//TODO support DTD by reorganization of code

// Develop testsuite to:
//TODO ... Add use XSD1.1 test suite example
//TODO ... Add use XSD1.1 test extra-suite example


// Use different catalogs
//TODO ... replicate test for catalog null, empty normal, public, system and referred
//TODO ... Manage referred catalog in sub-directory
//TODO ... Add public/system and asbolute/relative catalog resolutions for schema
//TODO Add test with absolute references for xml input, xml schema, xml catalog

//TODO Add DTD system identifier tests in Test Suite 
//TODO Add DTD public identifier tests in Test Suite

// +++Extend dev to support:
//TODO ....Refactorize to check schema synthax
//TODO ....Refactorize to check catalog

// +++QC
//TODO Refactorize to reduce God Class (split in classes)





// Documentation
//When and How to use versioning

// Improvements for cat backward compatibility:
//TODO Refactorize to support DTD


//myDocumentBuilder.setEntityResolver(new EntityResolver() {
//    public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId)
//           throws SAXException, java.io.IOException
//    {
//      if (publicId.equals("--myDTDpublicID--"))
//        // this deactivates the open office DTD
//        return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
//      else return null;
//    }
//});


