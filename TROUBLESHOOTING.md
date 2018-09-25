### java.lang.OutOfMemoryError

*Symptom* - When running the application, whether in IntelliJ or in Command Line/Bash, I receive the error `Exception in thread "main" java.lang.OutOfMemoryError: Java heap space`

*Solution* - The underlying problem is probably related to the default config in [gtfs-realtime-validator](https://github.com/CUTR-at-USF/gtfs-realtime-validator/blob/master/gtfs-realtime-validator-lib/pom.xml#L129) that sets max heap space to 4GB. This may not be enough for exceptionally large feeds. If the machine has enough available memory, this can be solved by adding the option `-Xmx<N>G` to the command line. For example, `java -Xmx10G -jar target/transit-feed-quality-calculator-1.0.0-SNAPSHOT.jar -directory output -transitFeedsApiKey 123456` will increase heap space to 10GB

