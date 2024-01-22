/**
 *
 */
package eu.europa.publications.xml.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/** Application handler for counting and logging warning, error, fatal.
 *
 * @author pcolot
 *
 */
public class ApplicationHandler extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** The name of this LOGGER will be
    /* "eu.europa.publications.xml.tools.ApplicationHandler".
    */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The name of this MARKER will be the current Class.
    */
    private static final Marker MARKER = MarkerManager.getMarker("CLASS");

    /** Set instance error counter to default 0.
     *
     */
    private static int errorCount;

    /*
     * Class constructors.
     *
     */
    /** Default class constructor.
     *
     */
    public ApplicationHandler() {
        super();
        this.printStackTrace();
        incrementErrorCount();
    }

    /** Class constructor handling a message string.
     *
     * @param message The provided message
     */
    public ApplicationHandler(final String message) {
        super(message);
        this.printStackTrace();
        incrementErrorCount();
        LOGGER.error(MARKER, message);
    }

    /** Class constructor handling an exception cause.
     *
     * @param cause The provided exception cause
     */
    public ApplicationHandler(final Throwable cause) {
        super(cause);
        incrementErrorCount();
        cause.printStackTrace();
        LOGGER.error(MARKER, cause.getMessage());
    }

    /** Class constructor handling a message and an exception cause.
     *
     * @param message The provided message
     * @param cause The provided exception cause
     */
    public ApplicationHandler(final String message, final Throwable cause) {
        super(message, cause);
        incrementErrorCount();
        cause.printStackTrace();
        LOGGER.error(MARKER, message);
        LOGGER.error(MARKER, cause.getMessage());
    }

    /**Class constructor handling a message, an exception cause and a writeable stack trace.
     *
     * @param message The provided message
     * @param cause The provided exception cause
     * @param enableSuppression The provided flag
     * @param writableStackTrace The provided writeable stack trace flag
     */
    public ApplicationHandler(final String message, final Throwable cause,
            final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        incrementErrorCount();
        cause.printStackTrace();
        LOGGER.error(MARKER, message);
        LOGGER.error(MARKER, cause.getMessage());
    }

    /** Setter to access error count.
     *
     */
    public static void resetErrorCount() {
        errorCount = 0;
    }

    /** Getter to access error count.
     *
     * @return The number of error message accounted
     */
    public static int getErrorCount() {
        return errorCount;
    }

    /** Setter to access error count.
     *
     */
    private static void incrementErrorCount() {
        errorCount++;
    }

    /** Warn about an exception.
     *
     * @param exception The provided exception
     */
    public static void warn(final Exception exception) {
        LOGGER.warn(MARKER, exception.getMessage());
    }

    /**Error about an exception.
     *
     * @param exception The provided exception
     */
    public static void error(final Exception exception) {
        incrementErrorCount();
        LOGGER.error(MARKER, exception.getMessage());
    }

    /** Fatal about an exception.
     *
     * @param exception The provided exception
     */
    public static void fatal(final Exception exception) {
        incrementErrorCount();
        LOGGER.fatal(MARKER, exception.getMessage());
    }

}
