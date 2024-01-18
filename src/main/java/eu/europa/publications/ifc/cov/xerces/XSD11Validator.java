package eu.europa.publications.ifc.cov.xerces;

import java.io.File;
import java.io.IOException;
//import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
//import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.xerces.util.XMLCatalogResolver;
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
    /* "eu.europa.publications.ifc.cov.xerces.XSD11Validator".
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
     // This constructor is intentionally empty. Nothing special is needed here.
    }

    /**
     * Validate File using XML Schema 1.1 and OASIS catalog support.
     * <p>
     * Example of usage:
     * <p>
     * returnCode = xsd11Validator.validateFile("myPathToXMLSourceFile.xml",
     * "myPathToXMLSchemaFile.xml", "myPathToXMLCatalogFile.xml");
     *
     * @param xmlInput the optional XML source  
     * @param xsdInput the optional XML schema 
     * @param catalogFileName the optional OASIS catalog 
     * @return the integer return code 0: successful >0: failed with number of
     * errors
     *
     * @since 1.0
     * @author pcolot
     * @version 1.0
     */
    public int validateFile(final String xmlInput, final String xsdInput,
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
            if (ApplicationHandler.getErrorCount() > 0) {
                LOGGER.trace(MARKER, "Validate ended with " + ApplicationHandler.getErrorCount() + " error(s)");
            } else {
                LOGGER.trace(MARKER, "Validate ended without errors");
            }
            
            // 4. Nominal end
            LOGGER.trace(MARKER, "XML document successfully validated.");
        
        // 5. Alternate flows
        } catch (SAXException e) {
            LOGGER.error(MARKER, "XML document is invalid with "
                    + ApplicationHandler.getErrorCount() + " error(s).");
        } catch (IOException e) {
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
     * @return
     * @throws ApplicationHandler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private Validator getValidator(
            final String xsdInput,
            final String catalogFileName,
            StreamSource xmlInputStreamSource) 
                    throws ApplicationHandler, SAXNotRecognizedException, SAXNotSupportedException {

        // 1. Get the Validator
        LOGGER.trace(MARKER, "Get Validator on XML Stream" + xmlInputStreamSource.getPublicId());
        if (xsdInput != null) { LOGGER.trace(MARKER, "  using external xsd: " + xsdInput); }
        if (catalogFileName != null) { LOGGER.trace(MARKER, "  and catalog: " + catalogFileName); }

        // 2. Create a catalog resolver
        XMLCatalogResolver catalogResolver = getCatalogResolver(catalogFileName);
        
        // 3. Create a schema engine
        Schema schema = getSchema(xsdInput, xmlInputStreamSource, catalogResolver);
        
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
     * @return
     * @throws ApplicationHandler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static Schema getSchema(
            final String xsdInput,
            StreamSource xmlInputStreamSource,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler,
            SAXNotRecognizedException, SAXNotSupportedException {
        
        // 1. Get the Validator
        LOGGER.trace(MARKER, "Get Schema on XML Stream" + xmlInputStreamSource.getPublicId());
        if (xsdInput != null) { LOGGER.trace(MARKER, "  using external xsd: " + xsdInput); }
        LOGGER.trace(MARKER, "  and catalog: " + catalogResolver);
        
        // 2. Create a schema factory for XML 1.1
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/XML/XMLSchema/v1.1");
        schemaFactory.setFeature("http://apache.org/xml/features/validation/cta-full-xpath-checking", true);

        // 3. Get schema name
        URL xsdInputURL = getXsdURL(xsdInput, xmlInputStreamSource, catalogResolver);
        
        // 4. Get the schema
        LOGGER.trace(MARKER, "Creating schema from " + xsdInputURL);
        Schema schema = null;
        if (xsdInputURL != null) {
            try {
                schema = schemaFactory.newSchema(xsdInputURL);
            } catch (Exception e) {
                throw new ApplicationHandler(e);
            }
        } else {
            throw new ApplicationHandler("No schema detected");
        }
        
        return schema;
    }

    /** Get Xsd URL.
     * 
     * @param xsdInput
     * @param xmlInputStreamSource
     * @param catalogResolver
     * @return
     * @throws ApplicationHandler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static URL getXsdURL(
            final String xsdInput,
            StreamSource xmlInputStreamSource,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler,
            SAXNotRecognizedException, SAXNotSupportedException {
        
        // 1. Extract Xsd UrL
        LOGGER.trace(MARKER, "Get Xsd URL from XML Stream" + xmlInputStreamSource.getPublicId());
        if (xsdInput != null) { LOGGER.trace(MARKER, "  using external xsd: " + xsdInput); }
        LOGGER.trace(MARKER, "  and catalog: " + catalogResolver);

        // 2. Get the internal referred schema
        URL internalSchemaNameURL = getXsdInternalURL(xmlInputStreamSource, catalogResolver);
        LOGGER.trace(MARKER, "Extracted internal schema name URL: " + internalSchemaNameURL);
        
        if (xsdInput == null) {
         // 3. Without forced schema used the resolved internal one
            URL xsdInputURL = internalSchemaNameURL;
            
            return xsdInputURL;
        } else {
            // 4. Unless used the forced schema
            try {
                URL xsdInputURL = getURL(xsdInput);
                LOGGER.trace(MARKER, "XSD Schema Absolute Path: " + xsdInputURL);
                
                // check forced schema is equal to internal format unless warn
                if (!xsdInputURL.toURI().equals(internalSchemaNameURL.toURI())) {
                    LOGGER.warn(MARKER, "Internal schema name: "
                            + internalSchemaNameURL
                            + " differs from schema passed as argument: "
                            + xsdInputURL);
                }
                
                return xsdInputURL;
            } catch (Exception e) {
                throw new ApplicationHandler(e);
            }
        }
    }
    
    
    /** Get internal Xsd URL.
     * 
     * @param xsdInput
     * @param xmlInputStreamSource
     * @param catalogResolver
     * @return
     * @throws ApplicationHandler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static URL getXsdInternalURL(
            StreamSource xmlInputStreamSource,
            XMLCatalogResolver catalogResolver) throws ApplicationHandler {
        try {
            String xmlInput = xmlInputStreamSource.getPublicId();
            
            // 1. Extract Xsd UrL
            LOGGER.trace(MARKER, "Get internal Xsd URL from XML Stream" + xmlInput);
            LOGGER.trace(MARKER, "  and catalog: " + catalogResolver);
            
            //2. Get the XML root qualified namespace [TODO add DTD alternative]
            XMLStreamReader root = getRoot(xmlInputStreamSource);
            
            //3. Get the namespace
            LOGGER.trace(MARKER, "Get NamespaceURI : ");
            QName qname = root.getName();
            String namespaceURI = qname.getNamespaceURI();
            
            if (namespaceURI.isEmpty()) {
                // noschemalocation used
                LOGGER.trace(MARKER, "If no namespace is associated to root element" + " use noNamespaceSchemaLocation");
                namespaceURI = root.getAttributeValue( "http://www.w3.org/2001/XMLSchema-instance", "noNamespaceSchemaLocation");
            } else {
                //get schemalocations
                LOGGER.trace(MARKER, "If a namespace is associated to root element Get SchemaLocations");
                String schemaLocations = root.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation");
                
                if (!schemaLocations.isEmpty()) {
                    //schemaLocations present
                    String resolved = resolveLocation(schemaLocations, namespaceURI);
                    if (!resolved.isEmpty()) {
                        //schemaLocations used
                        namespaceURI = resolved;
                    } else {
                        // schemaLocations not used
                        // keep current namespaceURI
                    }
                } else {
                    // schemaLocations not present
                    // keep current namespaceURI
                }
            }
            
            // Release XMLStreamReader resources
            root.close();
            
            //4. Resolve namespaceURI against catalog Public, System, Relative to XMLInput
            String resolvedId = catalogResolver.resolvePublic(namespaceURI, null);
            if (resolvedId == null) {
                resolvedId = catalogResolver.resolveSystem(namespaceURI);
                if (resolvedId == null) {
                    resolvedId = resolveRelativePath(xmlInput, resolvedId);
                    if (resolvedId == null) {
                        resolvedId = namespaceURI;
                    }
                }
            }
            return new URL(namespaceURI);
        } catch ( Exception e) {
            throw new ApplicationHandler(e);
        }
    }
    
    
    /** Get XML input stream. 
     * 
     * @param xmlInput
     * @throws ApplicationHandler
     */
    private static StreamSource getXmlInputStream(final String xmlInput)
            throws ApplicationHandler {
        
        // 1. Get the XML Stream
        LOGGER.trace(MARKER, "Get Xml Input Stream from " + xmlInput);
        
        // 2. if a input is provided
        if (xmlInput != null) {
            try {
                // try to convert xmlInput as URL
                URL url = getURL(xmlInput);
                // create the stream
                return new StreamSource(url.toString());
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
     * @return
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
    
    /** Check catalog exist and get the parent directory of the catalog.
     * 
     * @param catalogFileName
     * @return
     * @throws ApplicationHandler
     */
    private static String getXMLCatalogRootDir(final String catalogFileName) 
            throws ApplicationHandler {
        
        // 1. Extract Xsd UrL Or Null
        LOGGER.trace(MARKER, "Get XML Catalog Root Dir from " + catalogFileName);
        
        try {
            String xmlCatalogRootDir = getBasePath(catalogFileName);
            LOGGER.trace(MARKER, "XML Catalog Root Dir: " + xmlCatalogRootDir);
            
            return xmlCatalogRootDir;
        } catch (Exception e) {
            throw new ApplicationHandler(e);
        }
    }
    
    /** Get Root Qualified Name.
     * 
     * @param xmlInputStreamSource
     * @return
     * @throws ApplicationHandler
     * @throws SAXNotRecognizedException
     * @throws SAXNotSupportedException
     */
    private static XMLStreamReader getRoot(
            StreamSource xmlInputStreamSource) throws ApplicationHandler {
        try {
            // 1. Extract Qualified Name from root element
            LOGGER.trace(MARKER, "Get the namespace from XML Stream" + xmlInputStreamSource.getPublicId());
            
            // 2. Create factory to handle XML input
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            
            // 3. Create XML Stream using factory
            XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(xmlInputStreamSource);
            
            // 4. XML Stream has content
            if (xmlStreamReader.hasNext()) {
        
                // 4. Get first XML content
                xmlStreamReader.next();
                
                // 5. XML is a element and is the root first and only processed element
                if (xmlStreamReader.isStartElement()) {
                    
                    // 8. Get Qualified Name associated to root element
                    LOGGER.trace(MARKER, "Get root element");
                    
                    return xmlStreamReader;
                } else {
                    throw new ApplicationHandler("XML input is not starting with an element");
                }
            }  else {
                throw new ApplicationHandler("XML input is not a XML content");
            }
        } catch (Exception e) {
            throw new ApplicationHandler(e);
        }
    }
    
    
    /** Get Url from system or URL resource.
     * 
     * @param input
     * @return
     * @throws ApplicationHandler
     */
    private static URL getURL(final String filePathOrURL)
            throws ApplicationHandler {
    
        // 1. Get the URL from system or http resource
        LOGGER.trace(MARKER, "Get input URL from " + filePathOrURL);
        
        // if any filePathOrURL is provided
        if (filePathOrURL != null) {
            try {
                if (filePathOrURL.startsWith("http")) {
                    // Extract the path as a URL
                    return new URL(filePathOrURL);
                } else {
                    // try to use input as a local file
    
                        // Create Stream source using filename
                        File file = new File(filePathOrURL);
                        String filename = file.getCanonicalPath() + "/" + file.getName();
                        return new URI(filename).toURL();
                }
            } catch (Exception e) {
                throw new ApplicationHandler(e);
            }
        } else {
            throw new ApplicationHandler("No input to be coverted to URL");
        }
    }
    
    /**
     * 
     * @param filePathOrURL
     * @return
     */
    public static String getBasePath(String filePathOrURL)
            throws ApplicationHandler {
        if (filePathOrURL.startsWith("http")) {
            // Extract the base path from a URL
            URI uri;
            try {
                uri = new URI(filePathOrURL);
            } catch (URISyntaxException e) {
                throw new ApplicationHandler("Invalid URL: " + filePathOrURL);
            }
            return uri.getScheme() + "://" + uri.getHost() + "/";
        } else {            
            // Extract the base path from a file path
            try {
                File file = new File(filePathOrURL);
                return file.getCanonicalPath() + "/";
            } catch (Exception e) {
                throw new ApplicationHandler("Invalid URL: " + filePathOrURL);
            }
        } 
    }
    
    public static String resolveRelativePath(String basePathOrURL, String relativePath) {
        if (basePathOrURL.startsWith("http")) {
            if (!relativePath.startsWith("/")) {
                basePathOrURL = basePathOrURL.substring(0, basePathOrURL.lastIndexOf("/") + 1);
            }
    
            URI baseURI;
            try {
                baseURI = new URI(basePathOrURL);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid base URL: " + basePathOrURL);
            }
    
            URI resolvedURI;
            try {
                resolvedURI = baseURI.resolve(relativePath);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid relative URL path: " + relativePath);
            }
    
            return resolvedURI.toString();
        } else {
            if (!relativePath.startsWith("/")) {
                basePathOrURL = new File(basePathOrURL).getAbsolutePath();
            }
    
            File resolved = new File(basePathOrURL, relativePath);
            return resolved.getAbsolutePath();
        } 
    }
    
    /** Resolve namespaceURI in the schemaLocations.
     * 
     * @param schemaLocations
     * @param namespaceURI
     * @return
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

//@TODO Manage referred catalog in sub-directory
//@TODO Add DTD system identifier tests in Test Suite 
//@TODO Add DTD public identifier tests in Test Suite 
//@TODO Add noNamespaceSchemaLocation tests in Test Suite
//@TODO Add http ref test
//@TODO use AKN independant example
//@TODO put on git
//@TODO package as independent tool (broken in maven)


////DTD
//// 6. Get DTD systemID
//DocumentBuilder documentBuilder = 
//Document document = documentBuilder.parse(xmlStreamReader);
//String systemId = document.getDoctype().getSystemId();
//
//LOGGER.trace(MARKER, "Location SystemId: " + systemId);
//if (systemId != null) {
//  resSystemId = systemId;
//  LOGGER.trace(MARKER, "Get DTD systemID: " + resSystemId);
//  resSystemURL = getAbsoluteUrlFromRelativeOrAbsolute(resSystemId, xmlInputStreamSourceDir);
//  break systemId;
//}
//
//// 7. Get DTD publicID
//String publicId = xmlStreamReader.getLocation().getPublicId();
//LOGGER.trace(MARKER, "Location PublicId: " + publicId);
//if (publicId != null) {
//  resPublicId = publicId;
//  LOGGER.trace(MARKER, "Get DTD publicID");
//  resSystemURL = getAbsoluteUrlFromRelativeOrAbsolute(resPublicId, xmlInputStreamSourceDir);
//  break publicId;
//}
