pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
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
