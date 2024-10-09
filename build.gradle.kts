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

val exposedVersion: String by project
dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    implementation("com.h2database:h2:2.2.224")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation(kotlin("reflect"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

kotlin {
    jvmToolchain(21)
}