// Top-level build file for Components
plugins {
    id("com.android.library") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}

// 生成路由表的 task
tasks.register<Exec>("generateRouteTable") {
    group = "router"
    description = "Generate RouteTable.kt by scanning @Route annotations"
    
    workingDir = rootProject.projectDir
    commandLine("python3", "generate_routes.py")
    
    doFirst {
        println("Generating RouteTable...")
    }
}

// 为 library 模块配置 Maven 发布
subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            plugins.apply("maven-publish")
            
            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("release") {
                        groupId = "com.mohanlv.component"
                        artifactId = project.name
                        version = "1.0.0"
                        
                        afterEvaluate {
                            from(components["release"])
                        }
                    }
                }
                
                repositories {
                    mavenLocal()
                }
            }
        }
        
        // router 模块需要在编译前生成路由表
        if (project.name == "router") {
            tasks.matching { it.name.startsWith("compile") }.configureEach {
                dependsOn(":generateRouteTable")
            }
        }
    }
}
