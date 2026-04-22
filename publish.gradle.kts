// publish.gradle.kts - 发布配置
// 在根项目应用此配置

subprojects {
    apply(plugin = "maven-publish")

    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("maven") {
                        groupId = "com.mohanlv"
                        artifactId = project.name
                        version = findVersion()
                        from(components["release"])

                        pom {
                            name.set(project.name)
                            description.set("Android Component: ${project.name}")
                            licenses {
                                license {
                                    name.set("MIT")
                                    url.set("https://opensource.org/licenses/MIT")
                                }
                            }
                        }
                    }
                }

                repositories {
                    maven {
                        val repoUrl = if (version.toString().endsWith("SNAPSHOT")) {
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

fun findVersion(): String {
    val versionFile = file("../version.gradle.kts")
    if (versionFile.exists()) {
        val content = versionFile.readText()
        val match = Regex("""set\("componentVersion",\s*"([^"]+)"""").find(content)
        if (match != null) {
            return match.groupValues[1]
        }
    }
    return "1.0.0"
}
