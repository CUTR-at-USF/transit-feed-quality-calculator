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

import edu.usf.cutr.transitfeedqualitycalculator.util.FileUtil;
import edu.usf.cutr.transitfeeds.GetFeedsRequest;
import edu.usf.cutr.transitfeeds.GetFeedsResponse;
import edu.usf.cutr.transitfeeds.model.Feed;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A project that uses the gtfs-realtime-validator to assess the quality of a large number of transit feeds.
 */
public class TransitFeedQualityCalculator {

    private Set<Integer> mGtfsRtLocationIds = new HashSet<>();
    private int mNumGtfsRtFeeds = 0;

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

        // Loop through all GTFS-realtime feeds and download them
        GetFeedsResponse response = new GetFeedsRequest.Builder(apiKey)
                .setType("gtfsrealtime")
                .build()
                .call();
        int currentPage = response.getResults().getPage();
        int totalPages = response.getResults().getNumPages();
        System.out.println("total GTFS-realtime pages = " + totalPages);

        while (currentPage <= totalPages) {
            downloadGtfsRtFeeds(response.getResults().getFeeds());

            System.out.println("Downloaded GTFS-realtime page = " + currentPage);

            if (currentPage < totalPages) {
                response = new GetFeedsRequest.Builder(apiKey)
                        .setType("gtfsrealtime")
                        .setPage(currentPage + 1)
                        .build()
                        .call();
            }
            currentPage++;
        }

        // Loop through all GTFS feeds and download them
        response = new GetFeedsRequest.Builder(apiKey)
                .setType("gtfs")
                .build()
                .call();
        currentPage = response.getResults().getPage();
        totalPages = response.getResults().getNumPages();
        System.out.println("total GTFS pages = " + totalPages);

        while (currentPage <= totalPages) {
            downloadGtfsFeeds(response.getResults().getFeeds());

            System.out.println("Downloaded GTFS page = " + currentPage);

            if (currentPage < totalPages) {
                response = new GetFeedsRequest.Builder(apiKey)
                        .setType("gtfs")
                        .setPage(currentPage + 1)
                        .build()
                        .call();
            }
            currentPage++;
        }

        System.out.println("Location Ids for GTFS-rt feeds - " + mGtfsRtLocationIds.toString());
        System.out.println("Total number of GTFS-rt feeds - " + mNumGtfsRtFeeds);
        System.out.println("Total number of agencies with GTFS-rt feeds - " + mGtfsRtLocationIds.toString());

        // TODO - Walk mPath and feed GTFS and GTFS-realtime feeds into BatchProcessor (GTFS-realtime feed validator)
    }

    private void downloadGtfsRtFeeds(List<Feed> gtfsRtFeeds) {
        URL gtfsRtFeedUrl;

        for (Feed feed : gtfsRtFeeds) {
            // Read the GTFS-rt feed from the feed URL
            if (feed.getUrls() != null) {
                String urlString = Optional.ofNullable(feed.getUrls().getDownloadUrl()).orElse("");
                if (urlString.trim().isEmpty()) {
                    continue;
                }
                try {
                    gtfsRtFeedUrl = new URL(urlString);
                } catch (MalformedURLException e) {
                    System.err.println("Malformed Url '" + urlString + "' - " + e);
                    continue;
                }

                try {
                    writeGtfsRtToFile(gtfsRtFeedUrl, feed);

                    // Save the location ID of this GTFS-rt feed, so we know to download the GTFS for it later
                    mGtfsRtLocationIds.add(feed.getLocation().getId());
                    mNumGtfsRtFeeds++;
                } catch (IOException e) {
                    System.err.println("Error reading GTFS-realtime feed '" + urlString + "' - " + e);
                    continue;
                }
            }
        }
    }

    private void writeGtfsRtToFile(URL gtfsRtFeedUrl, Feed feed) throws IOException {
        String folderName = FileUtil.getFolderName(feed);
        String fileName = mPath.resolve(folderName) + File.separator + FileUtil.getGtfsRtFileName(feed);

        Files.createDirectories(mPath.resolve(folderName));
        FileUtil.writeUrlToFile(gtfsRtFeedUrl, fileName);
    }

    private void downloadGtfsFeeds(List<Feed> gtfsFeeds) {
        URL gtfsFeedUrl;

        for (Feed feed : gtfsFeeds) {
            if (!mGtfsRtLocationIds.contains(feed.getLocation().getId())) {
                // We didn't download a GTFS-realtime feed for this location - skip to next
                continue;
            }

            // Read the GTFS feed from the feed URL
            if (feed.getUrls() != null) {
                String urlString = Optional.ofNullable(feed.getUrls().getDownloadUrl()).orElse("");
                if (urlString.trim().isEmpty()) {
                    continue;
                }
                try {
                    gtfsFeedUrl = new URL(urlString);
                } catch (MalformedURLException e) {
                    System.err.println("Malformed Url '" + urlString + "' - " + e);
                    continue;
                }

                try {
                    writeGtfsToFile(gtfsFeedUrl, feed);
                } catch (IOException e) {
                    System.err.println("Error reading GTFS-realtime feed '" + urlString + "' - " + e);
                    continue;
                }
            }
        }
    }

    private void writeGtfsToFile(URL gtfsFeedUrl, Feed feed) throws IOException {
        String folderName = FileUtil.getFolderName(feed);
        String fileName = mPath.resolve(folderName) + File.separator + FileUtil.getGtfsFileName(feed);

        Files.createDirectories(mPath.resolve(folderName));
        FileUtil.writeUrlToFile(gtfsFeedUrl, fileName);
    }
}
