plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.lombok)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    //mavenLocal() // Only use that for testing / no commits

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://jitpack.io") // DecentHolograms
    }

    maven {
        url = uri("https://repo.dachstein.cloud/repository/btegermany")
    }

    maven {
        url = uri("https://mvn.wesjd.net/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/releases/") // PlaceholderAPI
    }

    maven {
        url = uri("https://repo.fancyinnovations.com/releases") // FancyHolograms
    }
}

dependencies {
    api(libs.co.aikar.acf.paper)
    api(libs.dev.triumphteam.triumph.gui.paper)
    api(libs.co.aikar.idb.core)
    api(libs.co.aikar.idb.bukkit)
    api(libs.com.zaxxer.hikaricp) {
        exclude(mapOf("group" to "org.slf4j", "module" to "slf4j-api"))
    }
    api(libs.fr.mrmicky.fastboard)
    api(libs.net.wesjd.anvilgui)
    api(libs.org.json.json)
    compileOnly(libs.io.papermc.paper.paper.api)
    compileOnly(libs.com.github.plan.player.analytics.plan)
    compileOnly(libs.dev.nachwahl.cosmetics)
    compileOnly(libs.com.github.decentsoftware.eu.decentholograms)
    compileOnly(libs.de.oliver.fancyholograms)
    compileOnly(libs.me.clip.placeholderapi) {
        exclude(mapOf("group" to "net.kyori", "module" to "adventure-platform-bukkit"))
        exclude(mapOf("group" to "org.bstats", "module" to "bstats-bukkit"))
    }
    compileOnly(libs.net.luckperms.api)
}

group = "dev.nachwahl"
version = "1.0.2-SNAPSHOT"
description = "Lobby"
java.sourceCompatibility = JavaVersion.VERSION_25

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier = ""

    relocationPrefix = "dev.nachwahl.lobby.shaded"
    enableAutoRelocation = true
}

tasks.assemble {
    dependsOn(tasks.shadowJar) // Ensure that the shadowJar task runs before the build task
}

tasks.jar {
    archiveClassifier = "UNSHADED"
    enabled = false // Disable the default jar task since we are using shadowJar
}
