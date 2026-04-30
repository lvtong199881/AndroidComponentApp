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
                        url = uri("https://maven.pkg.github.com/lvtong199881/PackagesMaven")
                        credentials {
                            username = "lvtong199881"
                            password = System.getenv("GITHUB_TOKEN") ?: ""
                        }
                    }
                }
            }
        }
    }
}