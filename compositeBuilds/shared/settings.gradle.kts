pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement{
    versionCatalogs {
        create("libs"){
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "shared-code-between-gradle-and-app"
include("platform")
