import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.spotless)
    idea
    antlr
    id("utf8-workarounds")
}

group = "garden.ephemeral.calculator"
version = "1.0.0-SNAPSHOT"
description = "Simple calculator application built in Compose Desktop"

dependencies {
    antlr(libs.antlr4)

    implementation(compose.components.resources)
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    implementation(libs.antlr4.runtime)

    testImplementation(compose.desktop.uiTestJUnit4)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.framework.datatest)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.vintage.engine)
}

tasks.withType<AntlrTask> {
    arguments = arguments + listOf("-package", "garden.ephemeral.calculator.grammar")
}
tasks.generateGrammarSource {
    outputDirectory = layout.buildDirectory.dir("generated-src/antlr/main/garden/ephemeral/calculator/grammar").get().asFile
}
tasks.generateTestGrammarSource {
    outputDirectory = layout.buildDirectory.dir("generated-src/antlr/test/garden/ephemeral/calculator/grammar").get().asFile
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ANTLR plugin sets `compileJava` to depend on `generateGrammarSource`,
// but not `compileKotlin`, which causes a warning.
tasks.compileKotlin {
    dependsOn(tasks.generateGrammarSource)
}
tasks.compileTestKotlin {
    dependsOn(tasks.generateTestGrammarSource)
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
        targetExclude("build/generated/**")
        ktlint(libs.versions.ktlint.get())
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
    }
}

idea {
    module {
        isDownloadSources = true
    }
}
