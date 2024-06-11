package ch.sbb.rssched;

import ch.sbb.rssched.client.RsschedMatsimClient;
import ch.sbb.rssched.client.RsschedMatsimRequestGenerator;
import ch.sbb.rssched.client.config.RsschedRequestConfig;
import ch.sbb.rssched.client.config.RsschedRequestConfigReader;
import ch.sbb.rssched.client.dto.response.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * Read the request configuration from an Excel file and optionally send the request to the solver using the
 * RsschedMatsimClient.
 * <p>
 * <b>Usage:</b>
 * <pre>
 * {@code ./rssched-matsim-client <config_file> -h / --host <host> -p / --port <port> -d / --dry-run}
 * </pre>
 * <p>
 * If {@code -d / --dry-run} is present, the request is not sent to the solver.
 * <p>
 * <b>Required:</b>
 * <ul>
 *   <li>{@code config_file}: Path to the Excel configuration file</li>
 * </ul>
 * <p>
 * <b>Optional:</b>
 * <ul>
 *   <li>{@code -h / --host}: Scheduler base URL (default: "localhost")</li>
 *   <li>{@code -p / --port}: Scheduler port (default: 3000)</li>
 *   <li>{@code -d / --dry-run}: If present, do not send the request to the solver (default: false)</li>
 * </ul>
 * <p>
 * <b>Example with maven:</b>
 * <pre>
 * {@code mvn exec:java -Dexec.args="path_to_file.xlsx -h localhost -p 3000 -d"}
 * </pre>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Application {

    // TODO: Export also RSSched configuration excel to output folder

    // TODO: Use instance ID for file paths.

    public static final String APP_CMD_SYNTAX = "rssched-matsim-client <config_file>";
    public static final String DEFAULT_HOST = "http://localhost";
    public static final String DEFAULT_PORT = "3000";

    public static void main(String[] args) {
        try {
            runApplication(args);
        } catch (IOException | RuntimeException e) {
            System.err.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(APP_CMD_SYNTAX, initOptions());
            System.exit(1);
        }
    }

    static void runApplication(String[] args) throws IOException {
        Options options = initOptions();

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }

        String[] remainingArgs = cmd.getArgs();
        if (remainingArgs.length < 1) {
            throw new RuntimeException("Missing required argument: config_file");
        }

        String requestConfigXlsx = remainingArgs[0];
        String schedulerBaseUrl = cmd.getOptionValue("host", DEFAULT_HOST);
        int schedulerPort = Integer.parseInt(cmd.getOptionValue("port", DEFAULT_PORT));
        boolean sendToSolver = !cmd.hasOption("dry-run");

        RsschedRequestConfig config = new RsschedRequestConfigReader().readExcelFile(requestConfigXlsx);

        if (sendToSolver) {
            // create request and send to solver
            RsschedMatsimClient client = new RsschedMatsimClient(schedulerBaseUrl, schedulerPort);
            Response response = client.process(config);
            System.out.println(response.getInfo());
        } else {
            // only create request and export as JSON
            new RsschedMatsimRequestGenerator().process(config);
        }
    }

    private static Options initOptions() {
        Options options = new Options();

        Option hostOption = new Option("h", "host", true, "Scheduler base URL");
        hostOption.setRequired(false);
        options.addOption(hostOption);

        Option portOption = new Option("p", "port", true, "Scheduler port");
        portOption.setRequired(false);
        options.addOption(portOption);

        Option dryRunOption = new Option("d", "dry-run", false, "Dry run (do not send the request to the solver)");
        dryRunOption.setRequired(false);
        options.addOption(dryRunOption);

        return options;
    }
}
