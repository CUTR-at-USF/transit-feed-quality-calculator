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

    /**
     * Downloads, validates, and analyzes all GTFS-realtime feeds from TransitFeeds.com and outputs to the provided directory
     *
     * @param args
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException, ParseException {
        // Parse command line parameters
        Options options = setupCommandLineOptions();

        String directoryName = getDirectoryFromArgs(options, args);
        String transitFeedsApiKey = getTransitFeedsApiKeyFromArgs(options, args);
        String csvFile = getCsvPathAndFileFromArgs(options, args);

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

        options.addOption(downloadDirectory);
        options.addOption(transitFeedsApiKey);
        options.addOption(csvPath);
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
}
