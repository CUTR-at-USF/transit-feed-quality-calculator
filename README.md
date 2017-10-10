# transit-feed-quality-calculator [![Build Status](https://travis-ci.org/CUTR-at-USF/transit-feed-quality-calculator.svg?branch=master)](https://travis-ci.org/CUTR-at-USF/transit-feed-quality-calculator)
A project that uses the [gtfs-realtime-validator](https://github.com/CUTR-at-USF/gtfs-realtime-validator) to assess the quality of a large number of transit feeds.

When it runs, it:
1. Fetches the URLs for all known GTFS-realtime feeds and corresponding GTFS data from the [TransitFeeds.com GetFeeds API](http://transitfeeds.com/api/swagger/#!/default/getFeeds) and downloads them each to a subdirectory
1. Runs the [gtfs-realtime-validator Batch Processor](https://github.com/CUTR-at-USF/gtfs-realtime-validator#batch-processing) on each of the subdirectories
1. Produces summary statistics and graphs (*In progress - see [Issue #2](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/issues/2) and [WIP Pull Request #3](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/pull/3))*) 

### Requirements

You'll need [JDK 7 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

### Setting up your environment

This project was created in [IntelliJ](https://www.jetbrains.com/idea/).  You can also compile it from the command line using [Maven](https://maven.apache.org/).

### Getting the code

To get started with this project, use a Git client to clone this repository to your local computer.  Then, in IntelliJ import the project as a Maven project.

### Dependencies

Managed via Maven:

* [**TransitFeeds.com Client Library**](https://github.com/CUTR-at-USF/transitfeeds-client-library) - For calling the [TransitFeeds.com GetFeeds API](http://transitfeeds.com/api/swagger/#!/default/getFeeds)
* [**GTFS-realtime Validator**](https://github.com/CUTR-at-USF/gtfs-realtime-validator) - For identifying warnings and errors in GTFS-relatime feeds

### Build the project

* IntelliJ - Clean and build the project
* Maven - `mvn install`

### Run the project

Run the [Main.main()](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/Main.java) method.

Here's what it looks like:

~~~
String transitFeedsApiKey = "YOUR_API_KEY_HERE";
String directoryName = "feeds"; // New directory where feed subdirectories will be created
TransitFeedQualityCalculator calculator = new TransitFeedQualityCalculator(Paths.get(directoryName), transitFeedsApiKey);
calculator.calculate();
~~~

This demonstrates the usage of the [`TransitFeedQualityCalculator`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/TransitFeedQualityCalculator.java), which performs the 3 steps outlined above:
1. **Download** - Via [`TransitFeedsDownloader`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/TransitFeedsDownloader.java)
1. **Validate** - Via [`BulkFeedValidator`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/BulkFeedValidator.java)
1. **Analyze** - Via [`ResultsAnalyzer`](https://github.com/CUTR-at-USF/transit-feed-quality-calculator/blob/master/src/main/java/edu/usf/cutr/transitfeedqualitycalculator/ResultsAnalyzer.java)

### Sample output

You'll see a lot of folders within the "feeds" directory, one for each transit agency:

![image](https://user-images.githubusercontent.com/928045/31410882-d16ea5b4-addd-11e7-9c9e-89b9d724a200.png)

If you look in one of those folders, you'll see the following:

![image](https://user-images.githubusercontent.com/928045/31410887-d40186c0-addd-11e7-9d69-117e97049792.png)

This contains the GTFS and GTFS-realtime source files downloaded from the agency:
1. gtfs-zip - The GTFS data that was downloaded from the agency URL (HART, in this case) provided by TransitFeeds.com API
1. HART Trip Updates-xxxx.pb - The TripUpdates binary Protocol Buffer file that was downloaded from the agency URL (HART, in this case) provided by TransitFeeds.com API, with the UTC time in milliseconds appended
1. HART Vehicle Positions-xxxx.pb - The VehiclePositions binary Protocol Buffer file that was downloaded from the agency URL (HART, in this case) provided by TransitFeeds.com API, with the UTC time in milliseconds appended

...as well as plain text versions of the GTFS-realtime files generated by the gtfs-realtime-validator:
1. HART Trip Updates-xxxx.pb.txt - The plain text version of the above TripUpdates binary
1. HART Vehicle Positions-xxxx.pb.txt - The plain text version of the above VehiclePositions binary

...and the validation results for each GTFS-realtime file (see [gtfs-realtime-validator Batch Processor](https://github.com/CUTR-at-USF/gtfs-realtime-validator#batch-processing) output examples for details):
1. HART Trip Updates-xxxx.results.json - The validation results for the above TripUpdates binary 
1. HART Vehicle Positions-xxxx.results.json - The validation results for the above VehiclePositions binary