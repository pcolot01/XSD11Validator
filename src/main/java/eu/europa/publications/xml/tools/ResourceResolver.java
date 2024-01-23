/**
 * 
 */
package eu.europa.publications.xml.tools;

import java.io.InputStream;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * 
 */
public class ResourceResolver implements LSResourceResolver {

    /** the resource resolver implementation.
     * 
     * @param type
     * @param namespaceURI
     * @param publicId
     * @param systemId
     * @param baseURI
     * @return
     */
    public LSInput resolveResource(String type, String namespaceURI,
            String publicId, String systemId, String baseURI) {
        
        System.out.print("resolveResource: \n");
        System.out.print("   type: " + type + "\n");
        System.out.print("   namespaceURI: " + namespaceURI + "\n");
        System.out.print("   publicId: " + publicId + "\n");
        System.out.print("   systemId: " + systemId + "\n");
        System.out.print("   baseURI: " + baseURI + "\n");
         // note: in this sample, the XSD's are expected to be in the root of the classpath
        Input input = new Input(publicId, systemId, null);
        input.setBaseURI(baseURI);
        return input;

    }

}
