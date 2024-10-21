plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "nl.xandermarc"
version = "0.0.1"

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
}

val includeInJar: Configuration by configurations.creating
val minecraftVersion: String by project
val exposedVersion: String by project
val serializationVersion: String by project
val coroutinesVersion: String by project
val h2Version: String by project

dependencies {
    paperweight.paperDevBundle(minecraftVersion)

    // Kotlin
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")

    // Coroutines
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // Serialization
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("com.h2database:h2:$h2Version")

    // Include
    includeInJar(kotlin("reflect"))
    includeInJar("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    includeInJar("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    includeInJar("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    includeInJar("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.WARN
    from(includeInJar.map { if (it.isDirectory) it else zipTree(it) })
}

kotlin {
    jvmToolchain(21)
}
