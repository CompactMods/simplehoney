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
}

base {
    archivesName.set(modId)
    group = "dev.compactmods.simplehoney"
    version = envVersion
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

sourceSets.main {
    resources {
        srcDir("src/generated/resources")
        srcDir("build/generated/sources/modMetadata")
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

var datagen = sourceSets.register("datagen") {
    java.srcDir("src/datagen/java")
}

var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {

    var replaceProperties: Map<String, Any> = mapOf(
        "minecraft_version" to mojang.versions.minecraft.get(),
        "neo_version" to neoforged.versions.neoforge.get(),
        "minecraft_version_range" to mojang.versions.minecraftRange.get(),
        "neo_version_range" to neoforged.versions.neoforgeRange.get(),
        "mod_id" to modId,
        "mod_name" to prop("mod_name"),
        "mod_license" to prop("mod_license"),
        "mod_version" to envVersion,
        "mod_authors" to prop("mod_authors"),
        "mod_description" to prop("mod_description")
    )

    expand(replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

neoForge {
    ideSyncTask(generateModMetadata)
    addModdingDependenciesTo(datagen.get())

    mods.create(modId) {
        this.sourceSet(sourceSets.main.get())
        this.sourceSet(datagen.get())
    }

    runs {
        register("client") {
            client()

            programArguments.addAll("--width", "1920")
            programArguments.addAll("--height", "1080")
        }

        register("server") {
            server()
            programArgument("--nogui")
        }

        // This run config launches GameTestServer and runs all registered gametests, then exits.
        // By default, the server will crash when no gametests are provided.
        // The gametest system is also enabled by default for other run configs under the /test command.
        register("gameTestServer") {
            type = "gameTestServer"
        }

        register("data") {
            clientData()

            // example of overriding the workingDirectory set in configureEach above, uncomment if you want to use it
            // gameDirectory = project.file(""run-data"")

            this.sourceSet = datagen.get()

            // Specify the modid for data generation, where to output the resulting resource, and where to look for existing resources.
            programArguments.addAll("--mod", modId)
            programArguments.addAll("--all")
            programArguments.addAll("--output", file("src/generated/resources").absolutePath)
            programArguments.addAll("--existing", file("src/main/resources").absolutePath)
        }

        configureEach {
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
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
    dependencies {
        "datagenImplementation"(sourceSets.main.get().output)
    }
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

tasks.named<ProcessResources>("processResources") {
    dependsOn(generateModMetadata)
}

fun prop(name: String): String {
    if (project.properties.containsKey(name))
        return project.property(name) as String;

    return "";
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

tasks.register("mcVersion") {
    doFirst {
        val mc = mojang.versions.minecraft.get()
        println("version=$mc")
    }
}

tasks.register("neoVersion") {
    doFirst {
        val neo = neoforged.versions.neoforge.get()
        println("version=$neo")
    }
}