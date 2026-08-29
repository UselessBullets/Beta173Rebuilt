import groovy.json.JsonSlurper
import org.gradle.internal.os.OperatingSystem.*
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.io.path.fileSize


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
    dependsOn("copyNatives", "downloadAssets")
    mainClass.set("net.minecraft.client.Minecraft")
    classpath = sourceSets["main"].runtimeClasspath
//    args = listOf("-Dhttp.proxyHost=betacraft.ee", "-Dhttp.proxyPort=11705", "-Djava.util.Arrays.useLegacyMergeSort=true");

    systemProperty("java.library.path", layout.buildDirectory.dir("natives").get().asFile.absolutePath)
})

tasks.register("downloadAssets") {
    description = "Download's assets from Mojang's resource API"
    val workingDir = getWorkingDirectory("minecraft")
    downloadResourcesToDir("https://piston-meta.mojang.com/v1/packages/3d8e55480977e32acd9844e545177e69a52f594b/pre-1.6.json", File(workingDir, "resources"))

}

fun getWorkingDirectory(applicationName: String?): File {
    val userHome: String? = System.getProperty("user.home", ".")
    val workingDirectory: File = when (current()) {
        LINUX, SOLARIS -> File(userHome, ".$applicationName/")
        WINDOWS -> {
            val applicationData: String? = System.getenv("APPDATA")
            if (applicationData != null) File(applicationData, ".$applicationName/")
            else File(userHome, ".$applicationName/")
        }
        MAC_OS -> File(userHome, "Library/Application Support/$applicationName")
        else -> File(userHome, "$applicationName/")
    }
    if (!workingDirectory.exists() && !workingDirectory.mkdirs()) throw RuntimeException("The working directory could not be created: $workingDirectory")
    return workingDirectory
}

fun downloadResourcesToDir(manifestLocation: String, destination: File) {
    val assets: Map<*, *> = JsonSlurper().parse(URL(manifestLocation)) as Map<*, *>

    val objects = assets["objects"] as? Map<*, *>
    if (objects != null) {
        logger.lifecycle("Downloading Assets")
        val pool = Executors.newWorkStealingPool()
        val fileTotal = objects.size
        val fileCount = AtomicInteger(0);
        var sizeTotal = 0L
        val sizeCount = AtomicLong(0)

        for ((key, value) in objects) {
            if (key !is String) {
                logger.error("Asset Object Key '$key' is not a string!")
                continue
            }
            val obj = value as? Map<*, *>
            if (obj == null) {
                logger.error("Asset Object '$key' cannot be read!")
                continue
            }
            val hash = obj["hash"] as? String
            if (hash == null) {
                logger.error("Asset Object '$key' hash not found!")
                continue
            }
            val size = obj["size"] as? Number
            if (size != null) sizeTotal += size.toLong();


            pool.submit {
                downloadResource(key, hash, size, destination)
                fileCount.addAndGet(1)
                if (size != null) sizeCount.addAndGet(size.toLong())
            }
        }
        pool.shutdown()
        while (!pool.isTerminated) {
            val c = fileCount.get()
            val s = sizeCount.get()
            logger.lifecycle("Downloaded $c/$fileTotal files (%.3f / %.3f MB)".format((s / 1024.0 / 1024.0), sizeTotal / 1024.0 / 1024.0))
            Thread.sleep(1000L)
        }
        pool.shutdownNow()
        logger.lifecycle("Downloaded all $fileTotal assets")
    } else {
        logger.error("Failed to read Assets manifest!")
    }

}
fun downloadResource(key: String, hash: String, size: Number?, destination: File): Boolean {
    val assetFile = File(destination, key)
    if (assetFile.exists()) {
        // File the file exists and matches the expected size and hash already then skip downloading
        if (assetFile.isFile && (size == null || assetFile.toPath().fileSize() == size.toLong()) && sha1(assetFile.readBytes()) == hash) {
            logger.info("File $key up to date, skipping!")
            return false
        }
        // Otherwise delete out of date file
        assetFile.delete()
    }
    assetFile.parentFile.mkdirs()
    assetFile.createNewFile()
    val bytes = URL("https://resources.download.minecraft.net/${hash.substring(0, 2)}/$hash").openStream().readBytes()
    assetFile.writeBytes(bytes)
    val fHash = sha1(bytes)
    logger.info("Downloaded $key to file:///${assetFile.absolutePath}")
    if (size != null) {
        val fSize = assetFile.toPath().fileSize()
        if (fSize != size.toLong()) {
            logger.error("File $key at file:///${assetFile.absolutePath} was expected to be '$size' bytes but was actually '$fSize'!")
        }
    } else if (hash != fHash) {
        logger.error("File $key at file:///${assetFile.absolutePath} was expected to have sha1 hash of '$hash'  but was actually '$fHash'!")
    }
    return true
}

fun sha1(input: ByteArray): String {
    // Create a SHA-1 MessageDigest instance
    val digest = MessageDigest.getInstance("SHA-1")

    // Update the digest with the input
    digest.update(input)

    // Generate the hash and convert it to a hexadecimal string
    return digest.digest().joinToString("") { "%02x".format(it) }
}

tasks.withType(JavaCompile::class.java) {
    options.isIncremental = false
    options.encoding = "UTF-8"
    if (JavaVersion.current() != JavaVersion.VERSION_1_8) {
        options.compilerArgs.add("--release")
        options.compilerArgs.add("8")
    }
}

java {
    sourceCompatibility =  JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
