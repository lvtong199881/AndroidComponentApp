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
    }
}

// 组件模块（独立的 Gradle 项目）
include(":router")
include(":network")
include(":login")
include(":home")
include(":base")
include(":user")
include(":reactnative")

rootProject.name = "Components"
