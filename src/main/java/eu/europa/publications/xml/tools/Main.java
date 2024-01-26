package eu.europa.publications.xml.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/** Main.
 *
 * @author pcolot
 *
 */
public final class Main {

    /** The name of this LOGGER will be
    /* "eu.europa.publications.xml.tools.XSD11Validator".
    */
    private static final Logger LOGGER = LogManager.getLogger();

    /** The name of this MARKER will be the current Class.
    */
    private static final Marker MARKER = MarkerManager.getMarker("CLASS");

    /**
     * Utility class forbidding by declaration any direct instantiation and
     * any indirect instantiation by systematically throwing exception.
     */
    private Main() {
        throw new UnsupportedOperationException();
    }

    /**
     * Return a system exist's execution code describing to good or failed
     * execution of the XSD 1.1 validation. The args is the arguments' list
     * issued from the command line.
     * <p>
     * usage: XSD11Validator
     * <p>
     * -c, --catalog &lt;arg&gt; OASIS root catalog file path
     * <p>
     * -i, --input &lt;arg&gt; XML input file path
     * <p>
     * -s, --schema &lt;arg&gt; XML schema file path
     * <p>
     * return code: =0 for success
     * <p>
     * &lt;0 for XSD 1.1 validation runtime failure
     * <p>
     * &gt;0 for number of errors encountered during the XSD 1.1 validation
     * <p>
     * Example of usage:
     * <p>
     * java
     * -Dlog4j.configurationFile=.../log4j2.xml
     * -jar ...\maven-xerces-jar-with-dependencies.jar
     * -i .\...\xerces-test.xml
     * -s .\...\xerces-test.xsd
     * -c .\...\catalog.xml
     * <p>
     * return Code: 0
     *
     * see ../../../README.md for additional informations
     *
     * @param args the list of command line's arguments
     *
     *
     * @since 1.0
     * @author pcolot
     * @version 1.0
     */
    public static void main(final String[] args) {
        int returnCode = 1;
        final Options options = new Options();
        final HelpFormatter formatter = new HelpFormatter();
        
        try {
            final Option input = new Option("i", "input", true, "XML input file path");
            input.setRequired(true);
            options.addOption(input);

            final Option schema = new Option("s", "schema", true, "XML schema file path");
            schema.setRequired(false);
            options.addOption(schema);

            final Option catalog = new Option("c", "catalog", true, "OASIS root catalog file path");
            catalog.setRequired(false);
            options.addOption(catalog);

            final CommandLineParser parser = new DefaultParser();

            final CommandLine cmd = parser.parse(options, args);

            final String inputFilePath = cmd.getOptionValue("input");
            final String schemaFilePath = cmd.getOptionValue("schema");
            final String catalogFilePath = cmd.getOptionValue("catalog");
            final XSD11Validator xsd11Validator = new XSD11Validator();

            returnCode = xsd11Validator.validateFile(inputFilePath, schemaFilePath, catalogFilePath);
        } catch (ParseException e) {
            LOGGER.error(MARKER, e.getMessage());
            formatter.printHelp("XSD11Validator", options);
            System.exit(1);
        }
        LOGGER.info(MARKER, "return Code: " + returnCode);
        System.exit(returnCode);
    }
}
