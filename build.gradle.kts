plugins {
    id("java")
}

group = "net.minecraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    implementation("com.paulscode:librarylwjglopenal:20100824") // needs to be included in client jar
    implementation("com.paulscode:codecwav:20101023") // needs to be included in client jar
//    implementation("com.paulscode:codecjorbis:20101023") // needs to be included in client jar

    val lwjglVer = "2.9.4-nightly-20150209"
    implementation("org.lwjgl.lwjgl:lwjgl:$lwjglVer")
    implementation("org.lwjgl.lwjgl:lwjgl_util:$lwjglVer")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
