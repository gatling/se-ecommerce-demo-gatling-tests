plugins {
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.allopen") version "2.1.21"
    id("com.diffplug.spotless") version "7.0.4"
    // The following line allows to load io.gatling.gradle plugin and directly apply it
    id("io.gatling.gradle") version "3.14.3"
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
    gatlingImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
}

spotless {
    kotlin {
        ktfmt()
    }
}
