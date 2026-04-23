// publish.gradle.kts - 发布配置
// 子模块自行配置发布，根项目提供通用配置

subprojects {
    apply(plugin = "maven-publish")

    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            configure<PublishingExtension> {
                repositories {
                    maven {
                        val repoUrl = if (project.version.toString().endsWith("SNAPSHOT")) {
                            "${System.getProperty("user.home")}/.m2/repository/snapshots"
                        } else {
                            "${System.getProperty("user.home")}/.m2/repository/releases"
                        }
                        url = uri(repoUrl)
                    }
                }
            }
        }
    }
}