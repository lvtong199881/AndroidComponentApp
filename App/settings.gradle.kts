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

rootProject.name = "AndroidComponentApp"

// 宿主应用
include(":app")

// 应用组件
include(":startup")
include(":router")
include(":network")
include(":base")
include(":login")
include(":home")
include(":reactnative")
include(":user")

// 应用组件依赖配置（自动切换源码/Maven 模式）
apply(from = "dependencies-config.gradle.kts")
