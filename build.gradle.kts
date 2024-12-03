plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    id("io.ktor.plugin") version "3.0.1"
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
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
val kotlinVersion: String by project
val logbackVersion: String by project

dependencies {
    paperweight.paperDevBundle(minecraftVersion)

    // Kotlin
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("io.github.oshai:kotlin-logging:7.0.0")
    implementation("org.slf4j:slf4j-simple:2.0.16")

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

    // Ktor
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-client-websockets")
    implementation("io.ktor:ktor-client-cio-jvm:3.0.1")

    // Include
    includeInJar(kotlin("reflect"))
    includeInJar("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    includeInJar("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
    includeInJar("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    includeInJar("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serializationVersion")
    includeInJar("org.jetbrains.exposed:exposed-core:$exposedVersion")
    includeInJar("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    includeInJar("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    includeInJar("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    includeInJar("com.h2database:h2:$h2Version")
    includeInJar("io.github.oshai:kotlin-logging-jvm:7.0.0")
    includeInJar("io.github.oshai:kotlin-logging:7.0.0")
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(includeInJar.map { if (it.isDirectory) it else zipTree(it) })
}

kotlin {
    jvmToolchain(21)
}
