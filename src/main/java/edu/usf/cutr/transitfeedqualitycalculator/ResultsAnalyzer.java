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

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usf.cutr.gtfsrtvalidator.api.model.OccurrenceModel;
import edu.usf.cutr.gtfsrtvalidator.batch.BatchProcessor;
import edu.usf.cutr.gtfsrtvalidator.helper.ErrorListHelperModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ResultsAnalyzer {

    private Path mPath;

    /**
     * Analyzes the results files of the GTFS validator for all subfolders of the provided path when analyzeResults() is called
     *
     * @param path path which contains subfolders that each has results from the GTFS-realtime validator
     */
    public ResultsAnalyzer(Path path) throws IOException {
        mPath = path;
    }

    /**
     * Analyzes the results files of the GTFS validator for all subfolders of the path provided in the constructor
     *
     * @throws IOException if reading from or writing to the path provided in the constructor fails
     */
    public void analyzeResults() throws IOException {
        // Get all the subfolders of mPath
        List<Path> subFolders = Files.walk(mPath, 1)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
        // First entry will be the main mPath folder itself, so remove that, and we will have only subdirectories
        subFolders.remove(0);

        ObjectMapper mapper = new ObjectMapper();
        ErrorListHelperModel[] allErrorLists;

        // For each feed subdirectory, analyze the results files
        for (Path path : subFolders) {
            List<Path> resultsFiles = Files.walk(path)
                    .filter(p -> p.toFile().isFile() && p.toString().endsWith(BatchProcessor.RESULTS_FILE_EXTENSION))
                    .collect(Collectors.toList());
            for (Path file : resultsFiles) {
                allErrorLists = mapper.readValue(file.toFile(), ErrorListHelperModel[].class);
                System.out.println("-------------------------");
                System.out.println("Validation results for - " + file);

                // All rules, including a list of error occurrences for each rule
                for (ErrorListHelperModel rule : allErrorLists) {
                    // All occurrences for a single rule
                    for (OccurrenceModel error : rule.getOccurrenceList()) {
                        System.out.println(error.getPrefix() + " " + rule.getErrorMessage().getValidationRule().getOccurrenceSuffix());
                    }
                }
            }
        }
    }
}
