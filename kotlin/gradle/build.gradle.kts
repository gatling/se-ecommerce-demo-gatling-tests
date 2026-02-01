plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.allopen") version "2.3.0"
    id("com.diffplug.spotless") version "8.2.0"
    // The following line allows to load io.gatling.gradle plugin and directly apply it
    id("io.gatling.gradle") version "3.14.9.8"
}

tasks.withType(JavaCompile::class) {
  options.release.set(21)
}

kotlin {
  compilerOptions {
    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
  }
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
    gatlingImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.0")
}

spotless {
    kotlin {
        ktfmt()
    }
}
