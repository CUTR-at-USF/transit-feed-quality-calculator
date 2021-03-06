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

import javax.net.ssl.SSLHandshakeException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class FileUtil {

    private static final String GTFS_RT_FILE_EXTENSION = ".pb";
    public static final String GTFS_FILE_NAME = "gtfs.zip";

    // From https://stackoverflow.com/a/26420820/937715
    final static int[] ILLEGAL_CHARS = {34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47};
    static {
        Arrays.sort(ILLEGAL_CHARS);
    }

    /**
     * Returns a new String that is the same as the input String except that all characters that cannot occur in Windows or Unix file names have been removed
     *
     * From https://stackoverflow.com/a/26420820/937715
     *
     * @param input String to examine
     * @return a new String that is the same as the input but without any characters that cannot occur in Windows or Unix file names
     */
    public static String removeIllegalFileCharacters(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder output = new StringBuilder();
        int length = input.codePointCount(0, input.length());
        for (int i = 0; i < length; i++) {
            int c = input.codePointAt(i);
            if (Arrays.binarySearch(ILLEGAL_CHARS, c) < 0) {
                output.appendCodePoint(c);
            }
        }
        return output.toString();
    }

    /**
     * Writes the information from the provided URL to the provided fileName
     *
     * @param url      URL to retrieve
     * @param fileName name of file to write the information to
     * @return true if the file was downloaded, false if it was not
     */
    public static boolean writeUrlToFile(URL url, String fileName) throws IOException {
        URLConnection urlConnection = url.openConnection();
        // Set user agent (required by some servers, otherwise you get a HTTP 403 - #30)
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        // Check for HTTP 301 redirect
        String redirect = urlConnection.getHeaderField("Location");
        if (redirect != null) {
            System.out.println("Redirecting to " + redirect);
            urlConnection = new URL(redirect).openConnection();
        }

        // Opens input stream from the HTTP(S) connection
        InputStream in;
        try {
            in = urlConnection.getInputStream();
        } catch (SSLHandshakeException sslEx) {
            System.err.println("SSL handshake failed for " + url.toString() + ".  Try installing the JCE Extension or adding `-Djsse.enableSNIExtension=false` when running the application - see https://github.com/CUTR-at-USF/transit-feed-quality-calculator#running-the-application: " + sslEx);
            return false;
        }

        byte[] gtfsRtProtobuf = IOUtils.toByteArray(in);
        ByteBuffer buffer = ByteBuffer.wrap(gtfsRtProtobuf);

        FileOutputStream fos = new FileOutputStream(fileName);
        FileChannel fileChannel = fos.getChannel();
        fileChannel.write(buffer);
        fileChannel.close();
        fos.close();
        return true;
    }

    /**
     * Returns the folder name that should be used to store GTFS, GTFS-realtime, and validation files for the provided TransitFeeds.com feed
     *
     * @param feed feed from TransitFeeds.com
     * @return the folder name that should be used to store GTFS, GTFS-realtime, and validation files for the provided TransitFeeds.com feed
     */
    public static String getFolderName(Feed feed) {
        return getFolderName(Integer.toString(feed.getLocation().getId()), feed.getLocation().getTitleWithRegion());
    }

    /**
     * Returns the folder name that should be used to store GTFS, GTFS-realtime, and validation files for the provided feed ID and region
     *
     * @param id     id for this feed
     * @param region region for this feed
     * @return the folder name that should be used to store GTFS, GTFS-realtime, and validation files for the provided feed ID and region
     */
    public static String getFolderName(String id, String region) {
        if (id == null) {
            return removeIllegalFileCharacters(region);
        } else {
            return id + "-" + removeIllegalFileCharacters(region);
        }
    }

    /**
     * Returns the file name to use for a GTFS-realtime feed.
     *
     * This method will replace any path separators ("/", "\") with the character "-" to avoid creating invalid
     * file names.  The process first converts all path separators to the system separator, and then replaces
     * occurrences of the system separator with "-".
     *
     * @param feedTitle GTFS-realtime feed title that is being saved to file
     * @return the file name to use for a GTFS-realtime file
     */
    public static String getGtfsRtFileName(String feedTitle) {
        return removeIllegalFileCharacters(feedTitle) + "-" + System.currentTimeMillis() + GTFS_RT_FILE_EXTENSION;
    }

    /**
     * Returns the file name to use for a GTFS feed
     *
     * @return the file name to use for a GTFS file
     */
    public static String getGtfsFileName() {
        return GTFS_FILE_NAME;
    }
}
