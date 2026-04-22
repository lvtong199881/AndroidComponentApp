plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.router"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    
    // Startup 框架
    implementation(project(":startup"))
    kapt("com.mohanlv:init-annotator:0.0.6")
    
    // 路由注解处理器
    implementation("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:router-annotator:0.0.6")
    
    testImplementation("junit:junit:4.13.2")
}

// publishing 配置已在根项目统一管理



val target = project.findProperty("target")?.toString() ?: "local"
val tokenFile = System.getProperty("user.home") + "/.github_token"

if (target == "github") {
    (publishing as ExtensionAware).extensions.configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/lvtong199881/AndroidComponentApp")
                val token = File(tokenFile).takeIf { it.exists() }?.readText()?.trim() ?: ""
                credentials {
                    username = "lvtong199881"
                    password = token
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "router"
            version = System.getProperty("componentVersion", "1.0.0")
            artifact(file("build/outputs/aar/router-release.aar")) {
                extension = "aar"
            }
        }
    }
    
    repositories {
        maven {
            name = "LocalMaven"
            url = uri(System.getProperty("user.home") + "/.m2/repository/releases")
        }
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
    }
}

kapt {
    arguments {
        arg("initCollectorPackage", "com.mohanlv.router")
        arg("initCollectorModuleName", "router")
        arg("routerCollectorPackage", "com.mohanlv.router")
        arg("routerCollectorModuleName", "router")
    }
}

// 显式声明发布任务依赖 assembleRelease
tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.named("assembleRelease"))
}
