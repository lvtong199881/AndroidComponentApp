// publish.gradle.kts - 发布配置
// 在根项目应用此配置

subprojects {
    apply(plugin = "maven-publish")

    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            configure<PublishingExtension> {
                repositories {
                    maven {
                        name = "GitHubPackages"
                        url = uri("https://maven.pkg.github.com/lvtong199881/AndroidComponentApp")
                        val tokenFile = System.getProperty("user.home") + "/.github_token"
                        val token = File(tokenFile).takeIf { it.exists() }?.readText()?.trim() ?: ""
                        credentials {
                            username = "lvtong199881"
                            password = token
                        }
                    }
                    maven {
                        name = "LocalMaven"
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