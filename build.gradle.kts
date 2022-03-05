import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
    antlr
    id("utf8-workarounds")
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
            packageName = "Calculator"
            packageVersion = "1.0.0"
        }
    }
}