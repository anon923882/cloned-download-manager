pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

// See root settings.gradle.kts for why the Foojay resolver plugin is not
// applied. Composite builds inherit the toolchain configuration from the
// main project, so relying on the local JDK keeps behaviour consistent.

dependencyResolutionManagement{
    versionCatalogs {
        create("libs"){
            from(files("../../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "shared-code-between-gradle-and-app"
include("platform")
