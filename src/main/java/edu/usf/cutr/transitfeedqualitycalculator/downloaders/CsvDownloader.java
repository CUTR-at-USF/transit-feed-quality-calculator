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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import edu.usf.cutr.transitfeedqualitycalculator.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;

/**
 * Downloads feeds using GTFS and GTFS-realtime URLs retrived from a CSV file, which may include API keys
 */
public class CsvDownloader extends BaseDownloader {

    private File mCsvFile;
    private int mNumGtfsRtFeeds = 0;
    private HashSet<String> mGtfsUrls = new HashSet<>();

    /**
     * Downloads GTFS and GTFS-realtime files using URLs from the provided csvFile to the provided Path when downloadFeeds() is called
     *
     * @param path    path in which to write the output files
     * @param csvFile CSV file containing the Feed ID, GTFS URL, and GTFS-realtime URL
     */
    public CsvDownloader(Path path, File csvFile) throws IOException {
        super(path);
        mCsvFile = csvFile;
    }

    @Override
    public void downloadFeeds() throws IOException {
        mGtfsUrls.clear();
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(CsvFeed.class).withHeader();
        MappingIterator<CsvFeed> it = mapper.readerFor(CsvFeed.class).with(schema)
                .readValues(mCsvFile);
        System.out.println("Downloading feeds using CSV file entries...");
        while (it.hasNextValue()) {
            CsvFeed feed = it.nextValue();
            URL gtfsRtFeedUrl;
            URL gtfsFeedUrl;

            if (feed.getGtfsUrl().trim().isEmpty() || feed.getGtfsRtUrl().trim().isEmpty()) {
                continue;
            }
            // Download GTFS-realtime feed
            try {
                gtfsRtFeedUrl = new URL(feed.getGtfsRtUrl());
                writeFeedToFile(gtfsRtFeedUrl,
                        FileUtil.getFolderName(null, feed.getRegionId()),
                        FileUtil.getGtfsRtFileName(feed.getTitle()));
                mNumGtfsRtFeeds++;
            } catch (MalformedURLException e) {
                System.err.println("Malformed Url '" + feed.getGtfsRtUrl() + "' - " + e);
                continue;
            } catch (IOException e) {
                System.err.println("Error downloading GTFS-realtime feed '" + feed.getGtfsRtUrl() + "' - " + e);
                continue;
            }
            // Download GTFS feed
            try {
                if (mGtfsUrls.contains(feed.getGtfsUrl())) {
                    // We've already downloaded this GTFS data for another GTFS-rt URL record in the CSV file - skip to next record
                    System.out.println("Already downloaded " + feed.getGtfsUrl() + " from another GTFS-rt feed in the CSV, skipping download");
                    System.out.println("Downloaded GTFS-realtime for " + feed.getTitle() + " (GTFS already downloaded from " + feed.getGtfsUrl() + " for another CSV record)");
                    continue;
                }

                gtfsFeedUrl = new URL(feed.getGtfsUrl());
                writeFeedToFile(gtfsFeedUrl,
                        FileUtil.getFolderName(null, feed.getRegionId()),
                        FileUtil.getGtfsFileName());
                mGtfsUrls.add(feed.getGtfsUrl());
            } catch (MalformedURLException e) {
                System.err.println("Malformed Url '" + feed.getGtfsUrl() + "' - " + e);
                continue;
            } catch (IOException e) {
                System.err.println("Error downloading GTFS feed '" + feed.getGtfsUrl() + "' - " + e);
                continue;
            }
            System.out.println("Downloaded GTFS and GTFS-realtime for " + feed.getTitle());
        }

        System.out.println("CSVDownloader - Total number of GTFS-rt feeds downloaded - " + mNumGtfsRtFeeds);
    }

    /**
     * A model class used to parse the feed information from a CSV file
     */
    @JsonPropertyOrder({"region_id", "title", "gtfs_url", "gtfs_rt_url"})
    static class CsvFeed {

        String regionId;
        String title;
        String gtfsUrl;
        String gtfsRtUrl;

        public CsvFeed() {
        }

        @JsonProperty("region_id")
        public String getRegionId() {
            return regionId;
        }

        public void setRegionId(String regionId) {
            this.regionId = regionId;
        }

        @JsonProperty("title")
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @JsonProperty("gtfs_url")
        public String getGtfsUrl() {
            return gtfsUrl;
        }

        public void setGtfsUrl(String gtfsUrl) {
            this.gtfsUrl = gtfsUrl;
        }

        @JsonProperty("gtfs_rt_url")
        public String getGtfsRtUrl() {
            return gtfsRtUrl;
        }

        public void setGtfsRtUrl(String gtfsRtUrl) {
            this.gtfsRtUrl = gtfsRtUrl;
        }

        @Override
        public String toString() {
            return "Feed{" +
                    "regionId='" + regionId + '\'' +
                    ", title='" + title + '\'' +
                    ", gtfsUrl='" + gtfsUrl + '\'' +
                    ", gtfsRtUrl='" + gtfsRtUrl + '\'' +
                    '}';
        }
    }
}
