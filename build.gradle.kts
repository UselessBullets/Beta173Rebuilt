import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.register

plugins {
    id("java")
    id("com.gradleup.shadow") version ("8.3.5")
    id("org.cyclonedx.bom") version ("2.3.1")
}

group = "net.minecraft"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://libraries.minecraft.net/")
}

dependencies {
    implementation("com.paulscode:soundsystem:20120107") // needs to be included in client jar
    implementation("com.paulscode:librarylwjglopenal:20100824") // needs to be included in client jar
    implementation("com.paulscode:codecwav:20101023") // needs to be included in client jar
//    implementation("com.paulscode:codecjorbis:20101023") // Is copied into project since b173 uses a very slightly modified version to faciliate CodecMus
    implementation("net.sourceforge.argo:argo:3.4") // needs to be included in client jar, newer than what would've been in b173, but the older version on maven don't work properly, think b173 used 2.10 or 2.11 but thats not on maven

    val lwjglVer = "2.9.4-nightly-20150209"
    implementation("org.lwjgl.lwjgl:lwjgl:$lwjglVer")
    implementation("org.lwjgl.lwjgl:lwjgl_util:$lwjglVer")
    runtimeOnly("org.lwjgl.lwjgl:lwjgl-platform:${lwjglVer}")

    runtimeOnly("net.java.jinput:jinput-platform:2.0.5")
    implementation("net.java.jinput:jinput:2.0.5")
    implementation("net.java.jutils:jutils:1.0.0")
}

tasks.register("copyNatives", Copy::class.java, {
    configurations.runtimeClasspath.get()
        .filter { it.extension == "jar" && it.name.contains("platform") }
        .forEach {
            from(zipTree(it).filter { file -> !file.name.contains("META-INF") && !file.name.contains(".MF") }).into(
                layout.buildDirectory.dir("natives").get()
            )
        }
})

tasks.register("runServer", JavaExec::class.java, {
    val mpDir = File(workingDir, "mp")
    mpDir.mkdir()
    workingDir = mpDir
    mainClass.set("net.minecraft.server.MinecraftServer")
    classpath = sourceSets["main"].runtimeClasspath
})

tasks.register("runClient", JavaExec::class.java, {
    dependsOn(tasks["copyNatives"])
    mainClass.set("net.minecraft.client.Minecraft")
    classpath = sourceSets["main"].runtimeClasspath
//    args = listOf("-Dhttp.proxyHost=betacraft.ee", "-Dhttp.proxyPort=11705", "-Djava.util.Arrays.useLegacyMergeSort=true");

    systemProperty("java.library.path", layout.buildDirectory.dir("natives").get().asFile.absolutePath)
})

tasks.withType(JavaCompile::class.java) {
    options.isIncremental = false
    options.encoding = "UTF-8"
    if (JavaVersion.current() != JavaVersion.VERSION_1_8) {
        options.compilerArgs.add("--release")
        options.compilerArgs.add("8")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
