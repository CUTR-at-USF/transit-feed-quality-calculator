/*
 * Copyright (C) 2017 University of South Florida (sjbarbeau@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usf.cutr.transitfeedqualitycalculator;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Main {
    private final static String DIRECTORY = "directory";
    private final static String TRANSIT_FEEDS_API_KEY = "transitfeedsapikey";
    private final static String CSV_PATH_AND_FILE = "csv";
    private final static String FORCE_GTFS_DOWNLOAD = "forcegtfsdownload";
    private final static String ERRORS_TO_IGNORE = "errorstoignore";
    private final static String WARNINGS_TO_IGNORE = "warningstoignore";

    /**
     * Downloads, validates, and analyzes all GTFS-realtime feeds from TransitFeeds.com and outputs to the provided directory
     *
     * @param args see README "Command line options" for supported arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException, ParseException {
        // Parse command line parameters
        Options options = setupCommandLineOptions();

        String directoryName = getDirectoryFromArgs(options, args);
        String transitFeedsApiKey = getTransitFeedsApiKeyFromArgs(options, args);
        String csvFile = getCsvPathAndFileFromArgs(options, args);
        String forceGtfsDownload = getForceGtfsDownloadFromArgs(options, args);
        String errorsToIgnore = getErrorsToIgnoreFromArgs(options, args);
        String warningsToIgnore = getWarningsToIgnoreFromArgs(options, args);

        if (directoryName == null) {
            System.err.println("You must provide a directory such as `-directory output`");
            return;
        }

        TransitFeedQualityCalculator calculator = new TransitFeedQualityCalculator(Paths.get(directoryName));
        if (transitFeedsApiKey != null) {
            calculator.setTransitFeedsApiKey(transitFeedsApiKey);
        }
        if (csvFile != null) {
            calculator.setCsvDownloaderFile(csvFile);
        }
        if (forceGtfsDownload != null && (forceGtfsDownload.contains("false") || forceGtfsDownload.contains("no"))) {
            calculator.setForceDownloadGtfs(false);
        }
        if (errorsToIgnore != null) {
            calculator.setErrorsToIgnore(errorsToIgnore);
        }
        if (warningsToIgnore != null) {
            calculator.setWarningsToIgnore(warningsToIgnore);
        }
        calculator.calculate();
    }

    /**
     * Sets up the command-line options that this application supports
     */
    private static Options setupCommandLineOptions() {
        Options options = new Options();
        Option downloadDirectory = Option.builder(DIRECTORY)
                .hasArg()
                .desc("The path to the directory to which the feeds should be downloaded and the analysis output written")
                .build();
        Option transitFeedsApiKey = Option.builder(TRANSIT_FEEDS_API_KEY)
                .hasArg()
                .desc("The API key that should be used to retrieve feed URLs from TransitFeeeds.com")
                .build();
        Option csvPath = Option.builder(CSV_PATH_AND_FILE)
                .hasArg()
                .desc("The path and file name of the CSV file that contains feeds to be downloaded")
                .build();
        Option forceGtfsDownload = Option.builder(FORCE_GTFS_DOWNLOAD)
                .hasArg()
                .desc("True if the GTFS zip file should be downloaded even if it already exists on disk, false if it should not")
                .build();
        Option errorsToIgnore = Option.builder(ERRORS_TO_IGNORE)
                .hasArg()
                .desc("A comma-deliminted list of errors to ignore when analyzing errors and producing Excel output, like `E017,E018`")
                .build();
        Option warningsToIgnore = Option.builder(WARNINGS_TO_IGNORE)
                .hasArg()
                .desc("A comma-deliminted list of warnings to ignore when analyzing warnings and producing Excel output, like `W007,W008`")
                .build();

        options.addOption(downloadDirectory);
        options.addOption(transitFeedsApiKey);
        options.addOption(csvPath);
        options.addOption(forceGtfsDownload);
        options.addOption(errorsToIgnore);
        options.addOption(warningsToIgnore);
        return options;
    }

    private static String getDirectoryFromArgs(Options options, String[] args) throws ParseException {
        String directory = null;
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(DIRECTORY)) {
            directory = cmd.getOptionValue(DIRECTORY);
        }
        return directory;
    }

    private static String getTransitFeedsApiKeyFromArgs(Options options, String[] args) throws ParseException {
        String apiKey = null;
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(TRANSIT_FEEDS_API_KEY)) {
            apiKey = cmd.getOptionValue(TRANSIT_FEEDS_API_KEY);
        }
        return apiKey;
    }

    private static String getCsvPathAndFileFromArgs(Options options, String[] args) throws ParseException {
        String pathAndFile = null;
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(CSV_PATH_AND_FILE)) {
            pathAndFile = cmd.getOptionValue(CSV_PATH_AND_FILE);
        }
        return pathAndFile;
    }

    private static String getForceGtfsDownloadFromArgs(Options options, String[] args) throws ParseException {
        String force = null;
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(FORCE_GTFS_DOWNLOAD)) {
            force = cmd.getOptionValue(FORCE_GTFS_DOWNLOAD);
        }
        return force;
    }

    private static String getErrorsToIgnoreFromArgs(Options options, String[] args) throws ParseException {
        String errors = null;
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(ERRORS_TO_IGNORE)) {
            errors = cmd.getOptionValue(ERRORS_TO_IGNORE);
        }
        return errors;
    }

    private static String getWarningsToIgnoreFromArgs(Options options, String[] args) throws ParseException {
        String warnings = null;
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);
        if (cmd.hasOption(WARNINGS_TO_IGNORE)) {
            warnings = cmd.getOptionValue(WARNINGS_TO_IGNORE);
        }
        return warnings;
    }
}
