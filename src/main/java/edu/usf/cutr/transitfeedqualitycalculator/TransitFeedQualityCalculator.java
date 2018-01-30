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

import edu.usf.cutr.transitfeedqualitycalculator.downloaders.CsvDownloader;
import edu.usf.cutr.transitfeedqualitycalculator.downloaders.TransitFeedsDownloader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

/**
 * A project that uses the gtfs-realtime-validator to assess the quality of a large number of transit feeds.
 */
public class TransitFeedQualityCalculator {

    private Path mPath;
    private String mTransitFeedsApiKey = null;
    private String mCsvDownloaderFile = null;
    private boolean mDownloadFeeds = true;
    private boolean mForceDownloadGtfs = true;
    private boolean mValidateFeeds = true;
    private String mErrorsToIgnore = "E017"; // Comma separated string of errors to ignore
    private String mWarningsToIgnore = ""; // Comma separated string of warnings to ignore

    /**
     * Creates the feed quality calculated with the path to write the output files
     *
     * @param path path in which to write the output files
     */
    public TransitFeedQualityCalculator(Path path) throws IOException {
        mPath = path;
        Files.createDirectories(path);
    }

    /**
     * Sets the API key to be used for retrieving feed URLs from the TransitFeeds.com API
     *
     * @param apiKey the API key to be used for retrieving feed URLs from the TransitFeeds.com API
     */
    public void setTransitFeedsApiKey(String apiKey) {
        mTransitFeedsApiKey = apiKey;
    }

    /**
     * Sets if the calculator should download feeds (default), or if it should just process the feeds already on disk
     *
     * @param downloadFeeds true to download feeds again, or false if feeds should not be downloaded
     */
    public void setDownloadFeeds(boolean downloadFeeds) {
        mDownloadFeeds = downloadFeeds;
    }

    /**
     * Set to true if the GTFS file should be downloaded again even if it already exists on disk for each feed, or false if the file should not be downloaded if it already exists.  This parameter is ignored if `setDownloadFeeds()` is set to false.
     * @param forceDownloadGtfs true if the GTFS file should be downloaded again even if it already exists on disk for each feed, or false if the file should not be downloaded if it already exists
     */
    public void setForceDownloadGtfs(boolean forceDownloadGtfs) {
        mForceDownloadGtfs = forceDownloadGtfs;
    }

    /**
     * Sets if the validator should validate feeds (default), or if it should just analyze the feeds that have already been validated on disk
     *
     * @param validateFeeds true to validate the feeds, or false if feeds should not be validated
     */
    public void setValidateFeeds(boolean validateFeeds) {
        mValidateFeeds = validateFeeds;
    }

    /**
     * The path to a CSV file containing region_id,title,gtfs_url,gtfs_rt_url for feeds to download, if feeds should be downloaded from a CSV file
     *
     * @param csvDownloaderFile path to a CSV file containing region_id,title,gtfs_url,gtfs_rt_url for feeds to download
     */
    public void setCsvDownloaderFile(String csvDownloaderFile) {
        mCsvDownloaderFile = csvDownloaderFile;
    }

    /**
     * Run the feed quality calculations
     */
    public void calculate() throws IOException, NoSuchAlgorithmException, NoSuchFieldException, IllegalAccessException {
        if (mCsvDownloaderFile == null && mTransitFeedsApiKey == null) {
            System.out.println("No TransitFeeds.com API key or CSV file provided - no feeds will be downloaded");
        } else {
            if (mDownloadFeeds) {
                if (mCsvDownloaderFile != null) {
                    CsvDownloader csvDownloader = new CsvDownloader(mPath, new File(mCsvDownloaderFile));
                    csvDownloader.downloadFeeds(mForceDownloadGtfs);
                }

                if (mTransitFeedsApiKey != null) {
                    TransitFeedsDownloader transitFeedsDownloader = new TransitFeedsDownloader(mPath, mTransitFeedsApiKey);
                    transitFeedsDownloader.downloadFeeds(mForceDownloadGtfs);
                }
            }
        }

        if (mValidateFeeds) {
            BulkFeedValidator validator = new BulkFeedValidator(mPath);
            validator.validateFeeds();
        }

        ResultsAnalyzer analyzer = new ResultsAnalyzer(mPath, mErrorsToIgnore, mWarningsToIgnore);
        analyzer.analyzeResults();
    }
}
