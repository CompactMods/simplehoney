dependencyResolutionManagement {

    versionCatalogs.create("neoforged") {
        version("mdg", "1.0.14")
        version("neoforge", "21.0.143")
        version("neogradle", "7.0.157")

        version("neoforgeRange") {
            require("[21.0,22)")
            prefer("21.0.143")
        }

        plugin("moddev", "net.neoforged.gradle.userdev")
            .versionRef("neogradle")

//        plugin("moddev", "net.neoforged.moddev")
//            .versionRef("mdg")

        library("neoforged", "net.neoforged", "neoforge")
            .versionRef("neoforge")

        library("testframework", "net.neoforged", "testframework")
            .versionRef("neoforge")
    }

    versionCatalogs.create("mojang") {
        version("minecraft", "1.21.0")
        version("minecraftRange") {
            this.require("[1.21, 1.21.1)")
            this.prefer("1.21")
        }
    }
}

pluginManagement {
    plugins {
        id("idea")
        id("eclipse")
        id("maven-publish")
        id("java-library")
    }

    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()

        // maven("https://maven.architectury.dev/")

        maven("https://maven.parchmentmc.org") {
            name = "ParchmentMC"
        }

        maven("https://maven.neoforged.net/releases") {
            name = "NeoForged"
        }

        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "Sponge Snapshots"
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

include("neoforge")

