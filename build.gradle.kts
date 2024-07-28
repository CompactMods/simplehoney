tasks.create("mcVersion") {
    doFirst {
        val mc = mojang.versions.minecraft.get()
        println("version=$mc")
    }
}

tasks.create("neoVersion") {
    doFirst {
        val neo = neoforged.versions.neoforge.get()
        println("version=$neo")
    }
}