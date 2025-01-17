plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    id("com.gradle.develocity") version "3.19"
}
develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}

rootProject.name = "launch4j"
include(
    "demo:ConsoleApp",
    "demo:SimpleApp",
    "demo:ModuleApp",
    "demo:ExitCodeApp",
)