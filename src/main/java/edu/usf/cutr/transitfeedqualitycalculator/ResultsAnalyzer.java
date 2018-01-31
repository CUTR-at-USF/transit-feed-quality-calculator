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
import edu.usf.cutr.gtfsrtvalidator.lib.batch.BatchProcessor;
import edu.usf.cutr.gtfsrtvalidator.lib.model.OccurrenceModel;
import edu.usf.cutr.gtfsrtvalidator.lib.model.helper.ErrorListHelperModel;
import edu.usf.cutr.transitfeedqualitycalculator.model.Agency;
import edu.usf.cutr.transitfeedqualitycalculator.model.AnalysisOutput;
import edu.usf.cutr.transitfeedqualitycalculator.model.Feed;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ResultsAnalyzer {

    private Path mPath;
    private List<String> mErrorsToIgnore;
    private List<String> mWarningsToIgnore;
    /**
     * Analyzes the results files of the GTFS validator for all subfolders of the provided path when analyzeResults() is called
     *
     * @param path path which contains subfolders that each has results from the GTFS-realtime validator
     * @param errorsToIgnore a comma-delimited list of error IDs for errors that the results analyzer should ignore when computing results
     * @param warningsToIgnore a comma-delimited list of error IDs for warnings that the results analyzer should ignore when computing results
     */
    public ResultsAnalyzer(Path path, String errorsToIgnore, String warningsToIgnore) throws IOException {
        mPath = path;
        mErrorsToIgnore = new ArrayList<>();
        Collections.addAll(mErrorsToIgnore, errorsToIgnore.replace(" ", "").split(","));
        mWarningsToIgnore = new ArrayList<>();
        Collections.addAll(mWarningsToIgnore, warningsToIgnore.replace(" ", "").split(","));
    }

    /**
     * Analyzes the results files of the GTFS validator for all subfolders of the path provided in the constructor
     *
     * @return the results of the analysis
     * @throws IOException if reading from or writing to the path provided in the constructor fails
     */
    public AnalysisOutput analyzeResults() throws IOException {
        // Get all the subfolders of mPath
        List<Path> subFolders = Files.walk(mPath, 1)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
        // First entry will be the main mPath folder itself, so remove that, and we will have only subdirectories
        subFolders.remove(0);
        ObjectMapper mapper = new ObjectMapper();
        ErrorListHelperModel[] allErrorLists;
        AnalysisOutput output = new AnalysisOutput();
        List<Agency> agencies = new ArrayList<>();
        Map<String, List<Feed>> errorMap = new HashMap<>();
        Map<String, List<Feed>> warningMap = new HashMap<>();
        output.setAgencies(agencies);
        output.setErrorMap(errorMap);
        output.setWarningMap(warningMap);
        // For each feed subdirectory, analyze the results files
        for (Path path : subFolders) {
            List<Path> resultsFiles = Files.walk(path)
                    .filter(p -> p.toFile().isFile() && p.toString().endsWith(BatchProcessor.RESULTS_FILE_EXTENSION))
                    .collect(Collectors.toList());
            if (!resultsFiles.isEmpty()) {
                int id = 0;
                Agency agency = new Agency();
                agencies.add(agency);
                List<Feed> agencyFeeds = new ArrayList<>();
                String agencyFullName = path.getFileName().toString();
                agency.setId(Integer.parseInt(agencyFullName.split("-")[0].trim()));
                agency.setLocation(agencyFullName.split("-")[1].trim());
                agency.setFeedList(agencyFeeds);
                for (Path file : resultsFiles) {
                    Feed agencyFeed = new Feed();
                    agencyFeed.setId(++id);
                    agencyFeed.setName(file.getFileName().toString().split("-")[0].trim());
                    List<ErrorListHelperModel> errorList = new ArrayList<>();
                    List<ErrorListHelperModel> warningList = new ArrayList<>();
                    String errors = "", warnings = "";
                    agencyFeed.setErrorList(errorList);
                    agencyFeed.setWarningList(warningList);
                    agencyFeeds.add(agencyFeed);
                    allErrorLists = mapper.readValue(file.toFile(), ErrorListHelperModel[].class);
                    System.out.println("-------------------------");
                    System.out.println("Validation results for - " + file);
                    System.out.println(allErrorLists.length + " types of errors/warnings were detected");
                    // All rules, including a list of error occurrences for each rule
                    for (ErrorListHelperModel rule : allErrorLists) {
                        if (rule.getErrorMessage().getValidationRule().getSeverity().equals("ERROR") &&
                                !mErrorsToIgnore.contains(rule.getErrorMessage().getValidationRule().getErrorId())) {
                            List<Feed> eList;
                            String errorText = " error";
                            errorList.add(rule);
                            errors = errors + " " + rule.getErrorMessage().getValidationRule().getErrorId();
                            if (errorMap.containsKey(rule.getErrorMessage().getValidationRule().getErrorId())) {
                                eList = errorMap.get(rule.getErrorMessage().getValidationRule().getErrorId());
                                eList.add(agencyFeed);
                                errorMap.replace(rule.getErrorMessage().getValidationRule().getErrorId(), eList);
                            } else {
                                eList = new ArrayList<>();
                                eList.add(agencyFeed);
                                errorMap.put(rule.getErrorMessage().getValidationRule().getErrorId(), eList);
                            }
                            System.out.println(rule.getOccurrenceList().size() + errorText + " occurrence(s) for Rule "
                                    + rule.getErrorMessage().getValidationRule().getErrorId() + " - " + rule.getErrorMessage().getValidationRule().getTitle() + ":");
                            // All occurrences for a single rule
                            for (OccurrenceModel error : rule.getOccurrenceList()) {
                                System.out.println(error.getPrefix() + " " + rule.getErrorMessage().getValidationRule().getOccurrenceSuffix());
                            }
                        } else if (rule.getErrorMessage().getValidationRule().getSeverity().equals("WARNING") &&
                                !mWarningsToIgnore.contains(rule.getErrorMessage().getValidationRule().getErrorId())) {
                            List<Feed> wList;
                            String errorText = " warning";
                            warningList.add(rule);
                            warnings = warnings + " " + rule.getErrorMessage().getValidationRule().getErrorId();
                            if (warningMap.containsKey(rule.getErrorMessage().getValidationRule().getErrorId())) {
                                wList = warningMap.get(rule.getErrorMessage().getValidationRule().getErrorId());
                                wList.add(agencyFeed);
                                warningMap.replace(rule.getErrorMessage().getValidationRule().getErrorId(), wList);
                            } else {
                                wList = new ArrayList<>();
                                wList.add(agencyFeed);
                                warningMap.put(rule.getErrorMessage().getValidationRule().getErrorId(), wList);
                            }
                            System.out.println(rule.getOccurrenceList().size() + errorText + " occurrence(s) for Rule "
                                    + rule.getErrorMessage().getValidationRule().getErrorId() + " - " + rule.getErrorMessage().getValidationRule().getTitle() + ":");
                            // All occurrences for a single rule
                            for (OccurrenceModel error : rule.getOccurrenceList()) {
                                System.out.println(error.getPrefix() + " " + rule.getErrorMessage().getValidationRule().getOccurrenceSuffix());
                            }
                        }
                    }
                    agencyFeed.setErrors(errors);
                    agencyFeed.setWarnings(warnings);
                }
            }
        }
        return output;
    }
}
