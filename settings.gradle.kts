pluginManagement {
    val mavenUser: String by settings
    val mavenPassword: String by settings
    repositories {
        maven(url = "https://nexus.azuraglobal.vn/repository/maven-releases/") {
            credentials {
                username = mavenUser
                password = mavenPassword
            }
        }
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    val mavenUser: String by settings
    val mavenPassword: String by settings
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://artifact.bytedance.com/repository/pangle/")
        maven(url = "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        maven(url = "https://nexus.azuraglobal.vn/repository/maven-releases/") {
            credentials {
                username = mavenUser
                password = mavenPassword
            }
        }
    }
}

rootProject.name = "Base Compose"
include(":app")
 