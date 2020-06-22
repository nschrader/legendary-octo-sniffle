# Requirements
* Java 14
* `./gradlew` will download Gradle and all other dependencies

# Build

There are several possibilities to build this project, depending on how you want to distribute and execute the application later.

* `./gradlew shadowDistZip` to generate `build/distributions/legendary-octo-sniffle-shadow.zip`. To run the application extract its contents somewhere and execute the `bin/legendary-octo-sniffle-shadow` shell script.
* `./gradlew installShadowDist` to do all of the above at once and execute the `build/install/legendary-octo-sniffle-shadow/bin/legendary-octo-sniffle-shadow` shell script.
* `./gradlew run --args="$YOUR_ARGS"` for an immediate execution.
* `./gradlew test` for testing.

You could also `./gradlew shadowJar` and then execute `java -jar legendary-octo-sniffle-all.jar` in `build/libs`, but there are some JVM arguments missing, leading to a warning message. Apart from that it's a legitimate alternative.
