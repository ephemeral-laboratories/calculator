
import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.spotless)
    idea
    alias(libs.plugins.antlr.kotlin)
    id("utf8-workarounds")
}

group = "garden.ephemeral.calculator"
version = "1.0.0-SNAPSHOT"
description = "Simple calculator application built in Compose Desktop"

val generatedAntlrDir: Provider<Directory> = layout.buildDirectory.dir("generated/antlr")

kotlin {
    jvmToolchain(17)
    jvm()
    sourceSets {
        commonMain {
            kotlin {
                srcDir(generatedAntlrDir)
            }
            dependencies {
                implementation(compose.components.resources)
            }
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.antlr.kotlin.runtime)
            implementation(libs.icu4j)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.uiTestJUnit4)
            implementation(libs.kotest.runner.junit5)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.datatest)
            implementation(libs.kotest.property)
            runtimeOnly(libs.junit.platform.launcher)
            runtimeOnly(libs.junit.vintage.engine)
        }
    }
}

val generateKotlinGrammarSource by tasks.registering(AntlrKotlinTask::class) {
    // XXX: Suspicious. This seems to be working around some problem with the antlr-kotlin plugin.
    dependsOn("cleanGenerateKotlinGrammarSource")

    source = fileTree(layout.projectDirectory.dir("src/commonMain/antlr")) {
        include("**/*.g4")
    }

    packageName = "garden.ephemeral.calculator.grammar"

    arguments = listOf("-visitor")

    outputDirectory = generatedAntlrDir.get().dir(packageName!!.replace(".", "/")).asFile
}

tasks.withType<KotlinCompile> {
    dependsOn(generateKotlinGrammarSource)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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
