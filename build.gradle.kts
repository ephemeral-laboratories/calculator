import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.strumenta.antlrkotlin.gradle.AntlrKotlinTask
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.spotless)
    idea
    alias(libs.plugins.antlr.kotlin)
    id("utf8-workarounds")
    alias(libs.plugins.sentry)
}

group = "garden.ephemeral.calculator"
version = "1.0.0-SNAPSHOT"
description = "Simple calculator application built in Compose Desktop"

val generatedAntlrDir: Provider<Directory> = layout.buildDirectory.dir("generated/antlr")

buildkonfig {
    packageName = "garden.ephemeral.calculator"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "CopyrightYears", "2024")
        buildConfigField(FieldSpec.Type.STRING, "OrganisationName", "Ephemeral Laboratories")
        buildConfigField(FieldSpec.Type.STRING, "ApplicationName", "Calculator")
        buildConfigField(FieldSpec.Type.STRING, "Version", version.toString())
        buildConfigField(
            FieldSpec.Type.STRING,
            "SentryDSN",
            "https://5ca64c177663f7bc15fd6943d2357c50@o4508522554130432.ingest.de.sentry.io/4508522556948560",
        )
    }
}

kotlin {
    jvmToolchain(21)
    jvm()
    sourceSets {
        commonMain {
            kotlin {
                srcDir(generatedAntlrDir)
            }
            dependencies {
                implementation(compose.components.resources)
                implementation(libs.kt.math)
                implementation(libs.sentry)
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
        jvmTarget = JvmTarget.JVM_21
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
                iconFile = file("src/installers/AppIcon.ico")
            }
            macOS {
                iconFile = file("src/installers/AppIcon.icns")
            }
            linux {
                iconFile = file("src/installers/AppIcon.png")
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

sentry {
    // Uploads source to Sentry (project is open source so that is OK)
    includeSourceContext = true

    org = "ephemeral-laboratories"
    projectName = "calculator"
    authToken = provider {
        System.getenv("SENTRY_AUTH_TOKEN")
            ?: loadProperties("secrets.properties").getProperty("SENTRY_AUTH_TOKEN")
    }
}
