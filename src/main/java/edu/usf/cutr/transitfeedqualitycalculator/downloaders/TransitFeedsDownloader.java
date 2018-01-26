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
package edu.usf.cutr.transitfeedqualitycalculator.downloaders;

import edu.usf.cutr.transitfeedqualitycalculator.util.FileUtil;
import edu.usf.cutr.transitfeeds.GetFeedsRequest;
import edu.usf.cutr.transitfeeds.GetFeedsResponse;
import edu.usf.cutr.transitfeeds.model.Feed;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Downloads GTFS and GTFS-realtime files using URLs from TransitFeeds.com
 */
public class TransitFeedsDownloader extends BaseDownloader {

    private Set<Integer> mGtfsRtLocationIds = new HashSet<>();
    private int mNumGtfsRtFeeds = 0;

    private String mApiKey;

    /**
     * Downloads GTFS and GTFS-realtime files using URLs from TransitFeeds.com to the provided Path when downloadFeeds() is called
     *
     * @param path   path in which to write the output files
     * @param apiKey API key to use with TransitFeeds.com API
     */
    public TransitFeedsDownloader(Path path, String apiKey) throws IOException {
        super(path);
        mApiKey = apiKey;
    }

    /**
     * Downloads feeds from TransitFeeds.com.  This method is synchronous and will return when all the GTFS-realtime
     * and their corresponding GTFS feeds have been downloaded
     *
     * @throws IOException if TransitFeeds.com API can't be reached, or if writing to the path provided in the constructor fails
     */
    public void downloadFeeds() throws IOException {
        System.out.println("Downloading feeds using TransitFeeds.com API...");
        // Loop through all GTFS-realtime feeds and download them
        GetFeedsResponse response = new GetFeedsRequest.Builder(mApiKey)
                .setType("gtfsrealtime")
                .build()
                .call();
        int currentPage = response.getResults().getPage();
        int totalPages = response.getResults().getNumPages();
        System.out.println("Total GTFS-realtime pages = " + totalPages);

        while (currentPage <= totalPages) {
            downloadGtfsRtFeeds(response.getResults().getFeeds());
            System.out.println("Downloaded GTFS-realtime page = " + currentPage);

            if (currentPage < totalPages) {
                response = new GetFeedsRequest.Builder(mApiKey)
                        .setType("gtfsrealtime")
                        .setPage(currentPage + 1)
                        .build()
                        .call();
            }
            currentPage++;
        }

        // Loop through all GTFS feeds and download the ones that we have a corresponding GTFS-realtime feed for (based on Location ID from TransitFeeds.com)
        response = new GetFeedsRequest.Builder(mApiKey)
                .setType("gtfs")
                .build()
                .call();
        currentPage = response.getResults().getPage();
        totalPages = response.getResults().getNumPages();
        System.out.println("Total GTFS pages = " + totalPages);

        while (currentPage <= totalPages) {
            downloadGtfsFeeds(response.getResults().getFeeds());
            System.out.println("Downloaded GTFS page = " + currentPage);

            if (currentPage < totalPages) {
                response = new GetFeedsRequest.Builder(mApiKey)
                        .setType("gtfs")
                        .setPage(currentPage + 1)
                        .build()
                        .call();
            }
            currentPage++;
        }

        System.out.println("TransitFeeds.com - Location Ids for GTFS-rt feeds - " + mGtfsRtLocationIds.toString());
        System.out.println("TransitFeeds.com - Total number of GTFS-rt feeds downloaded - " + mNumGtfsRtFeeds);
        System.out.println("TransitFeeds.com -Total number of agencies with GTFS-rt feeds - " + mGtfsRtLocationIds.size());
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
                    writeFeedToFile(gtfsRtFeedUrl, FileUtil.getFolderName(feed), FileUtil.getGtfsRtFileName(feed.getTitle()), true);

                    // Save the location ID of this GTFS-rt feed, so we know to download the GTFS for it later
                    mGtfsRtLocationIds.add(feed.getLocation().getId());
                    mNumGtfsRtFeeds++;
                } catch (IOException e) {
                    System.err.println("Error downloading GTFS-realtime feed '" + urlString + "' - " + e);
                    continue;
                }
            }
        }
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
                    writeFeedToFile(gtfsFeedUrl, FileUtil.getFolderName(feed), FileUtil.getGtfsFileName(), false);
                } catch (IOException e) {
                    System.err.println("Error downloading GTFS feed '" + urlString + "' - " + e);
                    continue;
                }
            }
        }
    }
}
