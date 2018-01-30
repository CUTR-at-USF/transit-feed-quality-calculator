# transit-feed-quality-calculator [![Build Status](https://travis-ci.org/CUTR-at-USF/transit-feed-quality-calculator.svg?branch=master)](https://travis-ci.org/CUTR-at-USF/transit-feed-quality-calculator)
A project that uses the [gtfs-realtime-validator](https://github.com/CUTR-at-USF/gtfs-realtime-validator) to assess the quality of a large number of transit feeds.

This tool:
1. Fetches the URLs for GTFS-realtime feeds and corresponding GTFS data from either the [TransitFeeds.com GetFeeds API](http://transitfeeds.com/api/swagger/#!/default/getFeeds) or a specified `.csv` file, and downloads them from each agency's server into a subdirectory
1. Runs the [gtfs-realtime-validator Batch Processor](https://github.com/CUTR-at-USF/gtfs-realtime-validator/tree/master/gtfs-realtime-validator-lib#batch-processing) on each of the subdirectories
1. Produces summary statistics and graphs, such as:

![image](https://user-images.githubusercontent.com/928045/32026095-cb7b3c10-b9b0-11e7-9725-def9f867f9ca.png)

## Running the application

You'll need [JDK 7 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

This project was created in [IntelliJ](https://www.jetbrains.com/idea/).  You can also compile it from the command line using [Maven](https://maven.apache.org/).

If you're downloading GTFS or GTFS-rt from secure HTTPS URLs, you may need to install the [Java Cryptography Extension (JCE)](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html).  You will need to replace the `US_export_policy.jar` and `local_policy.jar` files in your JVM `/security` directory, such as `C:\Program Files\Java\jdk1.8.0_73\jre\lib\security`, with the JAR files in the JCE Extension download.  Alternately, you can add `-Djsse.enableSNIExtension=false` to the command line when running the application. 

To download feeds, you'll also need a [TransitFeeds.com API](http://transitfeeds.com/api/) key or a `.csv` file that includes feed information (see below).

### Command line 

1. `mvn package`
1. `java -Djsse.enableSNIExtension=false -jar target/transit-feed-quality-calculator-1.0.0-SNAPSHOT.jar -directory output -transitfeedsapikey 1234567689 -csv feeds.csv`

Note that to download feeds, you'll need to provide an API key for TransitFeeds.com or a `.csv` file that includes feed information.

See the below [command-line options](README.md#command-line-options) section for a description.

### IntelliJ

Run the [Main.main()](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/Main.java) method, and provide the [command-line options](README.md#command-line-options) via the ["Run configurations->Program arguments" feature](https://www.jetbrains.com/help/idea/run-debug-configuration-application.html).

#### Command line options

* `-directory "output"` - **Required** - The directory to which feeds will be downloaded (in this case `output`), and to which validation and analysis files will be output
* `-transitfeedsapikey YOUR_API_KEY` - *(Optional)* - Your [TransitFeeds.com API](http://transitfeeds.com/api/) key (in this case, `YOUR_API_KEY`)
* `-csv "feeds.csv"` - *(Optional)* - A CSV file holding feed information (in this case, `feeds.csv` - you can name it whatever you want)
* `-forcegtfsdownload false` - *(Optional)* - If `false`, if there is already a GTFS file on disk for a feed it will not download a new GTFS file.  If `true` or if the command-line option is omitted, then a new GTFS file will always be downloaded and overwrite any current GTFS file for each feed.

If you want to download feeds, either `-transitfeedsapikey` or `-csv` parameters must be provided.  If these are missing, this tool will proceed to validate and analyze the feeds currently in `-directory` without downloading any new files.

The `feeds.csv` file should be formatted as follows:

~~~
region_id,title,gtfs_url,gtfs_rt_url
"10000-Portland, OR, USA","TriMet Trip Update",https://developer.trimet.org/schedule/gtfs.zip,http://developer.trimet.org/ws/V1/TripUpdate&appID=225D5601E7729B9ED863DCA39
"10000-Portland, OR, USA","TriMet Alerts",https://developer.trimet.org/schedule/gtfs.zip,http://developer.trimet.org/ws/V1/FeedSpecAlerts&appID=225D5601E7729B9ED863DCA39
"20000-Oakland, CA, USA","AC Transit Trip Update",http://www.actransit.org/wp-content/uploads/GTFSWinter17B.zip,http://api.actransit.org/transit/gtfsrt/tripupdates?token=9A6257A021F944E7BE0AD32702DF23CE
~~~

Tips:
* `region_id` should follow the format of `10000-Portland, OR, USA` - a `-` should separate the ID from the region name.  The `region_id` field will be the name of the subdirectory under `-directory` in which feed files will be saved.  We recommend prefixing it with a large integer value following the region pattern of TransitFeeds.com, to avoid collisions with downloads from TransitFeeds.com.
* If you have more than one GTFS-rt feed (e.g., VehiclePositions and TripUpdates), use the same `region_id` for each.  This way the GTFS data will only get downloaded once for that feed, and both GTFS-rt feeds will be downloaded to the same directory.
* The `title` field will be the file name of the downloaded protocol buffer file
* `gtfs_url` and `gtfs_url_url` can contain API keys if needed (e.g., `http://developer.trimet.org/ws/V1/TripUpdate&appID=1234567890`)
* Be sure to surrounding any fields that contains spaces with `"`

## Sample output

You'll see a lot of folders within the `output` directory, one for each transit agency:

![image](https://user-images.githubusercontent.com/928045/31410882-d16ea5b4-addd-11e7-9c9e-89b9d724a200.png)

If you look in one of those folders, you'll see the following:

![image](https://user-images.githubusercontent.com/928045/31410887-d40186c0-addd-11e7-9d69-117e97049792.png)

This contains the GTFS and GTFS-realtime source files downloaded from the agency:
1. **gtfs-zip** - The GTFS data that was downloaded from the agency URL (HART, in this case) provided by TransitFeeds.com API
1. **HART Trip Updates-xxxx.pb** - The TripUpdates binary Protocol Buffer file that was downloaded from the agency URL (HART, in this case) provided by TransitFeeds.com API, with the UTC time in milliseconds appended
1. **HART Vehicle Positions-xxxx.pb** - The VehiclePositions binary Protocol Buffer file that was downloaded from the agency URL (HART, in this case) provided by TransitFeeds.com API, with the UTC time in milliseconds appended

...as well as plain text versions of the GTFS-realtime files generated by the gtfs-realtime-validator:
1. **HART Trip Updates-xxxx.pb.txt** - The plain text version of the above TripUpdates binary
1. **HART Vehicle Positions-xxxx.pb.txt** - The plain text version of the above VehiclePositions binary

...and the validation results for each GTFS-realtime file (see [gtfs-realtime-validator Batch Processor](https://github.com/CUTR-at-USF/gtfs-realtime-validator/tree/master/gtfs-realtime-validator-lib#batch-processing) output examples for details):
1. **HART Trip Updates-xxxx.results.json** - The validation results for the above TripUpdates binary 
1. **HART Vehicle Positions-xxxx.results.json** - The validation results for the above VehiclePositions binary

An Excel spreadsheet file `graphs.xlsx` will be generated in the root folder of the project that contains graphs that summarize all of the analyzed GTFS-realtime feeds - for example:

![image](https://user-images.githubusercontent.com/928045/32021084-52ef90bc-b9a0-11e7-91db-387c3f1f2f50.png)

## Implementation details

Take a look at the [Main.main()](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/Main.java) method.

Here's a simplified version of what it looks like:

~~~
String directoryName = "your-directory";
String transitFeedsApiKey = "YOUR_TRANSIT_FEEDS.COM_API_HERE";
String csvFile = "feed-file.csv";

TransitFeedQualityCalculator calculator = new TransitFeedQualityCalculator(Paths.get(directoryName));
if (transitFeedsApiKey != null) {
    calculator.setTransitFeedsApiKey(transitFeedsApiKey);
}
if (csvFile != null) {
    calculator.setCsvDownloaderFile(csvFile);
}
calculator.calculate();
~~~

This demonstrates the usage of the [`TransitFeedQualityCalculator`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/TransitFeedQualityCalculator.java), which performs the 3 steps outlined above:
1. **Download** - Via [`TransitFeedsDownloader`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/downloaders/TransitFeedsDownloader.java) and [`CsvDownloader`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/downloaders/CsvDownloader.java)
1. **Validate** - Via [`BulkFeedValidator`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/BulkFeedValidator.java)
1. **Analyze** - Via [`ResultsAnalyzer`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/ResultsAnalyzer.java)

## Dependencies

Managed via Maven:

* [**TransitFeeds.com Client Library**](https://github.com/CUTR-at-USF/transitfeeds-client-library) - For calling the [TransitFeeds.com GetFeeds API](http://transitfeeds.com/api/swagger/#!/default/getFeeds)
* [**GTFS-realtime Validator**](https://github.com/CUTR-at-USF/gtfs-realtime-validator) - For identifying warnings and errors in GTFS-relatime feeds