import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    java
}

group = "dev.kosztadani.examples.stream"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val junitVersion = "5.8.1"
    testImplementation(group="org.junit.jupiter", name="junit-jupiter-api", version=junitVersion)
    testRuntimeOnly(group="org.junit.jupiter", name="junit-jupiter-engine", version=junitVersion)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(PASSED, SKIPPED, FAILED)
    }
}
