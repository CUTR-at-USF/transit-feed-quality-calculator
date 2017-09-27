# transit-feed-quality-calculator [![Build Status](https://travis-ci.org/CUTR-at-USF/transit-feed-quality-calculator.svg?branch=master)](https://travis-ci.org/CUTR-at-USF/transit-feed-quality-calculator)
A project that uses the gtfs-realtime-validator to assess the quality of a large number of transit feeds.

### Requirements

You'll need [JDK 7 or higher](http://www.oracle.com/technetwork/java/javase/downloads/index.html).


### Setting up your environment

This project was created in [IntelliJ](https://www.jetbrains.com/idea/).  You can also compile it from the command line using [Maven](https://maven.apache.org/).

### Getting the code

To get started with this project, use a Git client to clone this repository to your local computer.  Then, in IntelliJ import the project as a Maven project.

### Dependencies

Managed via Maven:

* [**TransitFeeds.com Client Library**](https://github.com/CUTR-at-USF/transitfeeds-client-library) - For calling the TransitFeeds.com GetFeeds API
* [**GTFS-realtime Validator**](https://github.com/CUTR-at-USF/gtfs-realtime-validator) - For identifying warnings and errors in GTFS-relatime feeds

### Build the project

* IntelliJ - Clean and build the project
* Maven - `mvn install`