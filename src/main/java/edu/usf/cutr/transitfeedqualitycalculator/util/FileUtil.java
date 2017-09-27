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
package edu.usf.cutr.transitfeedqualitycalculator.util;

import edu.usf.cutr.transitfeeds.model.Feed;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtil {

    private static final String GTFS_RT_FILE_EXTENSION = ".pb";
    public static final String GTFS_FILE_NAME = "gtfs.zip";

    /**
     * Writes the information from the provided URL to the provided fileName
     *
     * @param url      URL to retrieve
     * @param fileName name of file to write the information to
     */
    public static void writeUrlToFile(URL url, String fileName) throws IOException {
        InputStream in = url.openStream();
        byte[] gtfsRtProtobuf = IOUtils.toByteArray(in);
        ByteBuffer buffer = ByteBuffer.wrap(gtfsRtProtobuf);

        FileOutputStream fos = new FileOutputStream(fileName);
        FileChannel fileChannel = fos.getChannel();
        fileChannel.write(buffer);
        fileChannel.close();
        fos.close();
    }

    /**
     * Returns the folder name that should be used to store GTFS, GTFS-realtime, and validation files for the provided TransitFeeds.com feed
     *
     * @param feed
     * @return
     */
    public static String getFolderName(Feed feed) {
        return feed.getLocation().getId() + "-" + feed.getLocation().getTitleWithRegion();
    }

    /**
     * Returns the file name to use for a GTFS-realtime feed
     *
     * @param feed GTFS-realtime feed that is being saved to file
     * @return the file name to use for a GTFS-realtime file
     */
    public static String getGtfsRtFileName(Feed feed) {
        return feed.getTitle() + "-" + System.currentTimeMillis() + GTFS_RT_FILE_EXTENSION;
    }

    /**
     * Returns the file name to use for a GTFS feed
     *
     * @param feed GTFS feed that is being saved to file
     * @return the file name to use for a GTFS file
     */
    public static String getGtfsFileName(Feed feed) {
        return GTFS_FILE_NAME;
    }
}
