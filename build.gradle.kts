plugins {
    id("java")
}

group = "net.minecraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net")
}

dependencies {
    val lwjglVer = "2.9.4-nightly-20150209"
    implementation("org.lwjgl:lwjgl:$lwjglVer")
}
