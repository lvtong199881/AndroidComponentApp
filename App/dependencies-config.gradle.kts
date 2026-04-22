// 组件依赖配置脚本
// 根据 gradle.properties 中的 component.sourceDependency 自动切换依赖模式

// 使用 orNull 和 elvis 运算符提供默认值，避免 MissingValueException
val componentSourceDependency = providers.gradleProperty("component.sourceDependency").orNull?.toBoolean() ?: true  // 默认使用源码依赖
val componentVersion = providers.gradleProperty("component.version").orNull ?: "1.0.0"  // 默认版本
val componentGroupId = providers.gradleProperty("component.groupId").orNull ?: "com.mohanlv.component"  // 默认 groupId

println("========================================")
println("📦 Component Dependency Mode: ${if (componentSourceDependency) "SOURCE (Debug)" else "MAVEN (Release)"}")
println("   Version: $componentVersion")
println("   GroupId: $componentGroupId")
println("========================================")

if (componentSourceDependency) {
    // ========== 源码依赖模式（开发调试） ==========
    includeBuild("../Components") {
        dependencySubstitution {
            substitute(module("$componentGroupId:base")).using(project(":base"))
            substitute(module("$componentGroupId:router")).using(project(":router"))
            substitute(module("$componentGroupId:network")).using(project(":network"))
            substitute(module("$componentGroupId:login")).using(project(":login"))
            substitute(module("$componentGroupId:home")).using(project(":home"))
            substitute(module("$componentGroupId:user")).using(project(":user"))
            substitute(module("$componentGroupId:reactnative")).using(project(":reactnative"))
            substitute(module("$componentGroupId:logger")).using(project(":logger"))
        }
    }
    println("✅ Using SOURCE dependency (Components/ folder)")
} else {
    // ========== Maven 依赖模式（发布/CI） ==========
    dependencyResolutionManagement {
        repositories {
            mavenLocal()
            // 阿里云 Maven 镜像
            maven { url = uri("https://maven.aliyun.com/repository/google") }
            maven { url = uri("https://maven.aliyun.com/repository/public") }
            mavenCentral()
        }
    }
    println("✅ Using MAVEN dependency ($componentGroupId:x:$componentVersion)")
}
