import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.spotless)
    antlr
    id("utf8-workarounds")
}

group = "garden.ephemeral.calculator"
version = "1.0.0-SNAPSHOT"
description = "Simple calculator application built in Compose Desktop"

dependencies {
    antlr(libs.antlr4)

    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation(libs.komplex)
    implementation(libs.antlr4.runtime)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertk)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<AntlrTask> {
    arguments = arguments + listOf("-package", "garden.ephemeral.calculator.grammar")
    outputDirectory = file("$buildDir/generated-src/antlr/main/garden/ephemeral/calculator/grammar")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ANTLR plugin sets `compileJava` to depend on `generateGrammarSource`,
// but not `compileKotlin`, which causes a warning.
tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}

compose.desktop {
    application {
        mainClass = "garden.ephemeral.calculator.MainKt"
        jvmArgs("-Djpackage.app-version.unmangled=${project.version}")
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Calculator"
            packageVersion = project.version.toString().removeSuffix("-SNAPSHOT")
            description = project.description
            vendor = "Ephemeral Laboratories"
            copyright = "Copyright © 2022,2024 $vendor"

            windows {
                upgradeUuid = "3e9ee76f-453c-4819-9371-41745b72b8cc"
                menuGroup = packageName
                iconFile.set(file("src/installers/AppIcon.ico"))
            }
            macOS {
                iconFile.set(file("src/installers/AppIcon.icns"))
            }
            linux {
                iconFile.set(file("src/installers/AppIcon.png"))
            }
        }
    }
}

spotless {
    kotlin {
        ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
    }
}
