pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    plugins {
        id("com.android.library") version "8.2.0"
        id("org.jetbrains.kotlin.android") version "2.2.10"
        id("com.google.devtools.ksp") version "2.0.0-1.0.24"
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

rootProject.name = "Components"
