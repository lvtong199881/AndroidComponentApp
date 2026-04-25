pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.library") version "8.2.0"
        id("org.jetbrains.kotlin.android") version "2.2.10"
        id("com.facebook.react") version "0.76.9"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lvtong199881/AndroidComponentApp")
            credentials {
                username = "lvtong199881"
                password = System.getenv("GITHUB_TOKEN") ?: run {
                    val tokenFile = java.io.File(System.getProperty("user.home"), ".github_token")
                    if (tokenFile.exists()) tokenFile.readText().trim() else ""
                }
            }
        }
    }
}

// 组件模块（独立的 Gradle 项目）
// 基础组件（已发布到 Maven，本地不再编译）
// include(":startup")
// include(":router")
// include(":network")
// include(":logger")
include(":login")
include(":home")
include(":base")
include(":user")
include(":reactnative")
include(":websdk")
include(":shortvideo")

rootProject.name = "Components"
