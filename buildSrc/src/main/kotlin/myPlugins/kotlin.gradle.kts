package myPlugins

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}
repositories {
    mavenCentral()
    google()
    maven("https://jitpack.io")
}

fun getOptIns(): Set<String> = setOf(
    "androidx.compose.animation.ExperimentalAnimationApi",
    "androidx.compose.foundation.ExperimentalFoundationApi",
    "androidx.compose.ui.ExperimentalComposeUiApi",
)

fun getFeatures(): Set<String> = setOf(
    "context-parameters",
)

kotlin {
    jvmToolchain(21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)

        val optIns = getOptIns().map { "-Xopt-in=$it" }
        val features = getFeatures().map { "-X$it" }
        freeCompilerArgs.set(optIns + features)
    }
}
