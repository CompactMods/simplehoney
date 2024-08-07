@file:Suppress("SpellCheckingInspection")

import java.text.SimpleDateFormat
import java.util.*

var envVersion: String = System.getenv("VERSION") ?: "9.9.9"
if (envVersion.startsWith("v"))
    envVersion = envVersion.trimStart('v')

val modId: String = property("mod_id") as String
val isRelease: Boolean = (System.getenv("RELEASE") ?: "false").equals("true", true)

plugins {
    id("idea")
    id("eclipse")
    id("maven-publish")
    id("java-library")
    // alias(neoforged.plugins.moddev)
    id("net.neoforged.gradle.userdev") version ("7.0.119")
}

base {
    archivesName.set(modId)
    group = "dev.compactmods.simplehoney"
    version = envVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

sourceSets.main {
    java {
        srcDir("src/main/java")
    }

    resources {
        srcDir("src/main/resources")
        srcDir("src/generated/resources")
    }
}

sourceSets.test {
    java {
        srcDir("src/test/java")
    }

    resources {
        srcDir("src/test/resources")
    }
}

minecraft {
    modIdentifier.set(modId)
    // accessTransformers.file(project.file("src/main/resources/META-INF/accesstransformer.cfg"))
}

runs {
    // applies to all the run configs below
    configureEach {
        // Recommended logging data for a userdev environment
        systemProperty("forge.logging.markers", "") // 'SCAN,REGISTRIES,REGISTRYDUMP'

        // Recommended logging level for the console
        systemProperty("forge.logging.console.level", "debug")

        if (!System.getenv().containsKey("CI")) {
            // JetBrains Runtime Hotswap
            // jvmArgument("-XX:+AllowEnhancedClassRedefinition")
        }

        modSource(sourceSets.main.get())
    }

    create("client") {
        // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
        systemProperty("forge.enabledGameTestNamespaces", modId)

        programArguments("--username", "Nano")
        programArguments("--width", "1920")
        programArguments("--height", "1080")
    }

    create("server") {
        systemProperty("forge.enabledGameTestNamespaces", modId)
        environmentVariables("TEST_RESOURCES", project.file("src/test/resources").path)
        modSource(project.sourceSets.test.get())
    }

    create("data") {
        dataGenerator(true)

        programArguments("--mod", modId)
        programArguments("--all")
        programArguments("--output", file("src/generated/resources").absolutePath)
        programArguments("--existing", file("src/main/resources").absolutePath)
    }

    create("gameTestServer") {
        systemProperty("forge.enabledGameTestNamespaces", modId)
        environmentVariable("TEST_RESOURCES", file("src/test/resources").path)
        modSource(project.sourceSets.test.get())
    }
}

repositories {
    mavenLocal()

    maven("https://maven.blamejared.com/") {
        // location of the maven that hosts JEI files since January 2023
        name = "Jared's maven"
    }

    maven("https://www.cursemaven.com") {
        content {
            includeGroup("curse.maven")
        }
    }

    maven("https://modmaven.dev") {
        // location of a maven mirror for JEI files, as a fallback
        name = "ModMaven"
    }
}

dependencies {
    // implementation(neoforged.neoforge)
    implementation("net.neoforged", "neoforge", "21.0.143")
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    val gitVersion = providers.exec {
        commandLine("git", "rev-parse", "HEAD")
    }.standardOutput.asText.get()

    this.exclude("dev/compactmods/simplehoney/datagen/**")

    manifest {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
        attributes(
            mapOf(
                "Specification-Title" to "Simple Honey",
                "Specification-Vendor" to "CompactMods",
                "Specification-Version" to "2",
                "Implementation-Title" to "Simple Honey",
                "Implementation-Version" to envVersion,
                "Implementation-Vendor" to "CompactMods",
                "Implementation-Timestamp" to now,
                "Minecraft-Version" to mojang.versions.minecraft.get(),
                "NeoForge-Version" to neoforged.versions.neoforge.get(),
                "Main-Commit" to gitVersion
            )
        )
    }
}

fun prop(name: String): String {
    if (project.properties.containsKey(name))
        return project.property(name) as String;

    return "";
}

tasks.withType<ProcessResources>().configureEach {
    var replaceProperties: Map<String, Any> = mapOf(
        "minecraft_version" to mojang.versions.minecraft.get(),
        "neo_version" to neoforged.versions.neoforge.get(),
        "minecraft_version_range" to prop("minecraft_version_range"),
        "neo_version_range" to prop("neo_version_range"),
        "loader_version_range" to prop("loader_version_range"),
        "mod_id" to modId,
        "mod_name" to prop("mod_name"),
        "mod_license" to prop("mod_license"),
        "mod_version" to envVersion,
        "mod_authors" to prop("mod_authors"),
        "mod_description" to prop("mod_description")
    )

    inputs.properties(replaceProperties)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

val PACKAGES_URL = System.getenv("GH_PKG_URL") ?: "https://maven.pkg.github.com/compactmods/simplehoney"
publishing {
    publications.register<MavenPublication>("simplehoney") {
        artifactId = "$modId-neoforge"
        from(components.getByName("java"))
    }

    repositories {
        // GitHub Packages
        maven(PACKAGES_URL) {
            name = "GitHubPackages"
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}