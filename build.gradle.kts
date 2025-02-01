import java.util.*

plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.spotbugs)
    alias(libs.plugins.spotless)
    alias(libs.plugins.nexus.publish)
}

val workdir = "src/workdir"

val platforms = listOf(
    // listOf(platform folder name, jar classifier)
    listOf("linux-x86", "linux32"), // linux i686
    listOf("linux-amd64", "linux64"), // linux amd64
    listOf("linux-aarch64", "linux"), // linux aarch64
    listOf("macosx-x86", "mac"),    // mac amd64
    listOf("windows-x86", "win32")  // windows 32bit
)

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.commons.beanutils.commons.beanutils)
    implementation(libs.commons.logging.commons.logging)
    implementation(libs.com.jgoodies.jgoodies.common)
    implementation(libs.com.jgoodies.forms)
    implementation(libs.com.formdev.flatlaf)
    implementation(libs.com.thoughtworks.xstream.xstream)
    implementation(libs.org.apache.ant.ant)
    implementation(libs.org.jetbrains.annotations)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

// Create a custom JAR task that excludes a specific package
val coreJar by tasks.registering(Jar::class) {
    archiveClassifier.set("core")
    from(sourceSets.main.get().output) {
        exclude("net/sf/launch4j/form/**")
    }
    from(rootProject.layout.projectDirectory.file("LICENSE.txt"))
    group = "build"
}

tasks.jar {
    archiveClassifier.set("")
    from(rootProject.layout.projectDirectory.file("LICENSE.txt"))
}

fun platformJarTaskName(ident: String) : String {
    return "jar${ident.split("-").map { it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} .joinToString("") }"
}

val platformJar = tasks.register("platformJar") {
    group = "build"
    description = "Package all platform-specific binaries into JARs"
}
tasks.getByPath("assemble").dependsOn(platformJar)

platforms.forEach { platform ->
    tasks.register<Jar>(platformJarTaskName(platform[0])) {
        group = "Packaging"
        description = "Package binaries for ${platform[0]}"
        archiveClassifier.set("workdir-${platform[1]}")
        into("launch4j-workdir-${platform[0]}/") {
            from(layout.projectDirectory.dir("${workdir}/platforms/${platform[0]}"))
            from(layout.projectDirectory.dir("${workdir}/common"))
        }
    }
    tasks.getByPath("platformJar").dependsOn(tasks.named(platformJarTaskName(platform[0])))
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        // artifacts
        artifact(coreJar.get()) {
            classifier = "core"
        }
        platforms.forEach { platform ->
            artifact(tasks.named(platformJarTaskName(platform[0]))) {
                classifier = "workdir-${platform[1]}"
            }
        }

        pom {
            name.set("launch4j")
            description.set("launch4j")
            url.set("https://codeberg.org/miurahr/launch4j")
            licenses {
                license {
                    name.set("The 3-Clause BSD License")
                    url.set("https://codeberg.org/miurahr/launch4j/src/branch/master/LICENSE.txt")
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set("grzegok")
                    name.set("Grzegorz Kowal")
                    email.set("grzegok@users.sourceforge.net")
                }
                developer {
                    id.set("miurahr")
                    name.set("Hiroshi Miura")
                    email.set("miurahr@linux.com")
                }
            }
            scm {
                connection.set("scm:git:git://codeberg.org/miurahr/launch4j.git")
                developerConnection.set("scm:git:git://codeberg.org/miurahr/launch4j.git")
                url.set("https://codeberg.org/miurahr/launch4j")
            }
        }
    }
}

val signKey = listOf("signingKey", "signing.keyId", "signing.gnupg.keyName").find { project.hasProperty(it) }
tasks.withType<Sign> {
    onlyIf { signKey != null && !rootProject.version.toString().endsWith("-SNAPSHOT") }
}

signing {
    when (signKey) {
        "signingKey" -> {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        "signing.keyId" -> { /* do nothing */
        }
        "signing.gnupg.keyName" -> {
            useGpgCmd()
        }
    }
    sign(publishing.publications["maven"])
}
val ossrhUsername: String? by project
val ossrhPassword: String? by project

nexusPublishing.repositories {
    sonatype {
        stagingProfileId = "15818299f2c2bb"
        nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
        snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        if (ossrhUsername != null && ossrhPassword != null) {
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        } else {
            username.set(System.getenv("SONATYPE_USER"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
}

spotbugs {
    reportLevel = com.github.spotbugs.snom.Confidence.valueOf("HIGH")
}

spotless {
    java {
        palantirJavaFormat()
        importOrder()
        removeUnusedImports()
        formatAnnotations()
    }
}
