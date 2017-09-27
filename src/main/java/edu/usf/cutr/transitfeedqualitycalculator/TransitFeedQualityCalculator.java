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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A project that uses the gtfs-realtime-validator to assess the quality of a large number of transit feeds.
 */
public class TransitFeedQualityCalculator {

    private Path mPath;

    /**
     * Creates the feed quality calculated with the path to write the output files
     *
     * @param mPath path in which to write the output files
     */
    public TransitFeedQualityCalculator(Path mPath) throws IOException {
        this.mPath = mPath;
        Files.createDirectories(mPath);
    }

    /**
     * Run the feed quality calculations
     */
    public void calculate() throws IOException {
        String apiKey = "76edc18d-54d4-4132-9f53-e8e25be976e7";
        FeedDownloader downloader = new FeedDownloader(mPath, apiKey);
        downloader.downloadFeeds();

        // TODO - Walk mPath and feed GTFS and GTFS-realtime feeds into BatchProcessor (GTFS-realtime feed validator)
    }
}
