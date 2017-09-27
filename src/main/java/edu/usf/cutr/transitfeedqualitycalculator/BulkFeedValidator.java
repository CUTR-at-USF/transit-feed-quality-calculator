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

import edu.usf.cutr.gtfsrtvalidator.batch.BatchProcessor;
import edu.usf.cutr.transitfeedqualitycalculator.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class BulkFeedValidator {

    private Path mPath;

    /**
     * Validates the GTFS/GTFS-realtime feeds in all subfolders of the provided path when .validateFeeds() is called
     *
     * @param path path which contains subfolders that each has a GTFS file named FileUtil.GTFS_FILE_NAME and contains
     *             GTFS-realtime protocol buffer files
     */
    public BulkFeedValidator(Path path) throws IOException {
        mPath = path;
    }

    /**
     * Validates the GTFS/GTFS-realtime feeds in all subfolders of the provided path and generates output in that same folder
     *
     * @throws IOException if reading from or writing to the path provided in the constructor fails
     */
    public void validateFeeds() throws IOException {
        List<Path> subFolders = Files.walk(mPath, 1)
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
        // First entry will be the main mPath folder itself, so remove that, and we will have only subdirectories
        subFolders.remove(0);

        // For each feed subdirectory, run the batch processor, with GTFS_FILE_NAME as GTFS file and on any protobuf files in the folder
        for (Path path : subFolders) {
            BatchProcessor processor = new BatchProcessor.Builder(path + File.separator + FileUtil.GTFS_FILE_NAME, path.toString())
                    .setPlainTextExtension("txt")
                    .build();
            try {
                processor.processFeeds();
            } catch (Exception e) {
                System.err.println("Error validating feed - " + e);
            }
        }
    }
}
