plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.allopen") version "2.2.10"
    id("com.diffplug.spotless") version "7.2.1"
    // The following line allows to load io.gatling.gradle plugin and directly apply it
    id("io.gatling.gradle") version "3.14.3.5"
}

group = "example"

gatling {
    enterprise.closureOf<Any> {
        // Enterprise Cloud (https://cloud.gatling.io/) configuration reference: https://docs.gatling.io/reference/integrations/build-tools/gradle-plugin/
    }
}

repositories {
    mavenCentral()
}

dependencies {
    gatlingImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
}

spotless {
    kotlin {
        ktfmt()
    }
}
