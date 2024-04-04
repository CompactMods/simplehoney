tasks.create("mcVersion") {
    doFirst {
        val mc = libraries.versions.minecraft.get()
        println("version=$mc")
    }
}

tasks.create("neoVersion") {
    doFirst {
        val neo = libraries.versions.neoforge.get()
        println("version=$neo")
    }
}