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

    val lwjglVer = "2.9.3"
    implementation("org.lwjgl.lwjgl:lwjgl:$lwjglVer")
    implementation("org.lwjgl.lwjgl:lwjgl_util:$lwjglVer")
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${lwjglVer}")
}

task("copyNatives", Copy::class) {
    configurations.runtimeClasspath.get()
        .filter { it.extension == "jar" && it.name.contains("platform") }
        .forEach { from(zipTree(it).filter { file -> !file.name.contains("META-INF") && !file.name.contains(".MF") }).into(layout.buildDirectory.dir("natives").get()) }
}

task("runServer", JavaExec::class) {
    val mpDir = File(workingDir, "mp")
    mpDir.mkdir()
    workingDir = mpDir
    mainClass.set("net.minecraft.server.MinecraftServer")
    classpath = sourceSets["main"].runtimeClasspath
}

task("runClient", JavaExec::class) {
    dependsOn(tasks["copyNatives"])
    mainClass.set("net.minecraft.client.Minecraft")
    classpath = sourceSets["main"].runtimeClasspath

    systemProperty("java.library.path", layout.buildDirectory.dir("natives").get().asFile.absolutePath)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
