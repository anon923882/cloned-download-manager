import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins{
    kotlin("jvm")
}
repositories{
    mavenCentral()
}

kotlin {
    jvmToolchain(21)

    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

version=1
group="ir.amirab.util"
