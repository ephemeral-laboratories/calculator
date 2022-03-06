import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
    antlr
    id("utf8-workarounds")
    id("com.diffplug.spotless") version "6.3.0"
}

group = "garden.ephemeral.calculator"
version = "1.0.0-SNAPSHOT"
description = "Simple calculator application built in Compose Desktop"

dependencies {
    val komplexVersion: String by project.extra
    val antlrVersion: String by project.extra
    val junitVersion: String by project.extra
    val assertkVersion: String by project.extra

    antlr("org.antlr:antlr4:$antlrVersion")

    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation("garden.ephemeral.math:komplex:$komplexVersion")
    implementation("org.antlr:antlr4-runtime:$antlrVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertkVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ANTLR plugin sets `compileJava` to depend on `generateGrammarSource`,
// but not `compileKotlin`, which causes a warning.
tasks.named("compileKotlin") {
    dependsOn("generateGrammarSource")
}

compose.desktop {
    application {
        mainClass = "garden.ephemeral.calculator.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = project.name.capitalizeAsciiOnly()
            packageVersion = "1.0.0"
            description = project.description
            vendor = "Ephemeral Laboratories"
            copyright = "Copyright © 2022 $vendor"

            windows {
                upgradeUuid = "3e9ee76f-453c-4819-9371-41745b72b8cc"
                menuGroup = packageName
                perUserInstall = true
                iconFile.set(file("???"))
            }
        }
    }
}

spotless {
    kotlin {
        ktlint("0.44.0").userData(mapOf("disabled_rules" to "no-wildcard-imports"))
    }
    kotlinGradle {
        ktlint("0.44.0")
    }
}
