/**
 *
 */
package eu.europa.publications.ifc.cov.xerces;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Helper Class to manage warnings and errors; and to count errors occurrences.
 *
 * @author pcolot
 *
 */

final class ValidatorErrorHandler implements ErrorHandler {

    /** Default error handler.
     *
     */
    ValidatorErrorHandler() {
        super();
    }

    /** Handle Warning.
     *
     * @param exception The exception at the origin of the warning
     * @throws SAXException SaxException at the origin of the issue delegated to the application handler
     */
    @Override
    public void warning(final SAXParseException exception)
            throws SAXException {
        ApplicationHandler.warn(exception);
    }

    /** Handle Error.
     *
     * @param exception The exception at the origin of the error
     * @throws SAXException SaxException at the origin of the issue delegated to the application handler
     */
    @Override
    public void error(final SAXParseException exception)
            throws SAXException {
        ApplicationHandler.error(exception);
    }

    /** Handle Fatal Error.
     * fatal error
     * @param exception The exception at the origin of the warning
     * @throws SAXException SaxException at the origin of the issue delegated to the application handler
     */
    @Override
    public void fatalError(final SAXParseException exception)
            throws SAXException {
        ApplicationHandler.fatal(exception);
    }
}