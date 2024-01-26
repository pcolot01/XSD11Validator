package eu.europa.publications.xml.tools;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Proxy Authenticator.
 *
 * @since 1.0
 * @author pcolot
 * @version 1.0
 */
public class ProxyAuthenticator extends Authenticator {
    
    /** The name of this LOGGER will be
    /* "eu.europa.publications.xml.tools.XSD11Validator".
    */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The name of this MARKER will be the current Class.
    */
    private static final Marker MARKER = MarkerManager.getMarker("CLASS");
    
    /** The identification of the credential to be used for proxy authentification
     * 
     */
    private String user, password;
    
    /** Proxy Authenticator based on credential based on username and password.
     * 
     * @param user The user
     * @param password The associated password
     */
    public ProxyAuthenticator(String user, String password) {
        
        LOGGER.trace(MARKER, "ProxyAuthenticator(String user, String password)");

        this.user = user;
        this.password = password;      
    }
    
    /** Get Password Authentication on request by Proxy or Server
     *  
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        String requestingHost = getRequestingHost();
        RequestorType getRequestorType = getRequestorType();

        LOGGER.trace(MARKER, "Get Password Authentication: by " + requestingHost + " as " + getRequestorType);

        // Apply the stored proxy authorization
        LOGGER.trace(MARKER, "Setting getPasswordAuthentication() request recieved from->" + requestingHost + " using user: " + user);
        return new PasswordAuthentication(user, password.toCharArray());
    }

}


