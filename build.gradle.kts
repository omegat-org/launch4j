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
    "linux32", // linux i686
    "linux64", // linux amd64
    "linux",   // linux aarch64
    "macosx-x86",  // mac amd64
    "win32"  // windows 32bit
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

platforms.forEach { platform ->
    val platformJarTaskName = "jar${platform.replace("-", "")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
    tasks.register<Jar>(platformJarTaskName) {
        group = "Packaging"
        description = "Package binaries for $platform"
        if (platform.startsWith("mac")) {
            // for backward compatibility
            archiveClassifier.set("workdir-mac")
        } else {
            archiveClassifier.set("workdir-${platform}")
        }
        into("launch4j-workdir-${platform}/") {
            from(layout.projectDirectory.dir("${workdir}/platforms/${platform}"))
            from(layout.projectDirectory.dir("${workdir}/common"))
        }
    }
}

tasks.register("platformJar") {
    group = "build"
    description = "Package all platform-specific binaries into JARs"
    dependsOn(platforms.map { "jar${it.replace("-", "")
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}" })
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        // artifacts
        artifact(coreJar.get()) {
            classifier = "core"
        }
        platforms.forEach { platform ->
            artifact(tasks.named("jar${platform.replace("-", "")
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}")) {
                classifier = "workdir-${platform}"
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
