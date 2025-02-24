@file:Suppress("SpellCheckingInspection")

import org.slf4j.event.Level
import java.text.SimpleDateFormat
import java.util.*

val modId: String = "simplehoney"

var envVersion: String = System.getenv("VERSION") ?: "9.9.9"
if (envVersion.startsWith("v"))
    envVersion = envVersion.trimStart('v')

plugins {
    java
    id("idea")
    id("eclipse")
    id("maven-publish")
    alias(neoforged.plugins.moddev)
}

base {
    archivesName.set("simplehoney")
    group = "dev.compactmods.simplehoney"
    version = envVersion
}

java {
    toolchain.vendor.set(JvmVendorSpec.JETBRAINS)
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

neoForge {
    version = neoforged.versions.neoforge.get()

    val mainMod = this.mods.create(modId) {
        modSourceSets.add(sourceSets.main)

        if(System.getenv().containsKey("CI")) {
            modSourceSets.add(sourceSets.test)
        }
    }

    unitTest {
        enable()
        testedMod = mods.named(modId)
    }

    parchment {
        enabled = true
        mappingsVersion = libs.versions.parchment
        minecraftVersion = libs.versions.parchmentMC
    }

    runs {
        // applies to all the run configs below
        configureEach {
            logLevel.set(Level.DEBUG)
            sourceSet = project.sourceSets.main

            // JetBrains Runtime Hotswap
            if (!System.getenv().containsKey("CI")) {
                jvmArgument("-XX:+AllowEnhancedClassRedefinition")
            }

            systemProperties.put("terminal.ansi", "true")
        }

        create("client") {
            client()
            gameDirectory.set(file("runs/client"))

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("forge.enabledGameTestNamespaces", modId)

            programArguments.addAll("--username", "Nano")
            programArguments.addAll("--width", "1920")
            programArguments.addAll("--height", "1080")
        }

        create("client2") {
            client()
            gameDirectory.set(file("runs/client"))

            // Comma-separated list of namespaces to load gametests from. Empty = all namespaces.
            systemProperty("forge.enabledGameTestNamespaces", modId)

            programArguments.addAll("--username", "Nano2")
            programArguments.addAll("--width", "1920")
            programArguments.addAll("--height", "1080")
        }

        this.create("data") {
            this.clientData()

            this.gameDirectory.set(file("runs/data"))

            programArguments.addAll("--mod", modId)
            programArguments.addAll("--all")
            programArguments.addAll("--output", file("src/generated/resources").absolutePath)
            programArguments.addAll("--existing", file("src/main/resources").absolutePath)
        }

        create("server") {
            server()
            gameDirectory.set(file("runs/server"))
            programArgument("nogui")
        }
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