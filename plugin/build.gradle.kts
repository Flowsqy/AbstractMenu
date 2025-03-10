plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://libraries.minecraft.net/")
    }
}

dependencies {
    implementation(libs.jetbrains.annotations)
    implementation(libs.spigot.api)
    implementation(libs.mojang.authlib)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

project.base.archivesName.set(rootProject.name)
group = "fr.flowsqy.abstractmenu"
version = "2.0.3"

tasks.processResources {
    expand(Pair("version", version))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()
            from(components["java"])
        }
    }
}

