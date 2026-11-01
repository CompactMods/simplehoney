dependencyResolutionManagement {
    addVersionCatalog(this, "neoforged")
    addVersionCatalog(this, "mojang")
}

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("1.0.0")
}

fun addVersionCatalog(dependencyResolutionManagement: DependencyResolutionManagement, name: String) {
    dependencyResolutionManagement.versionCatalogs.create(name) {
        from(files("./gradle/$name.versions.toml"))
    }
}
