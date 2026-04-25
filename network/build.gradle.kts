plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.network"
    compileSdk = 35

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
    api("androidx.core:core-ktx:1.12.0")
    
    // Startup 框架
    api(project(":startup"))
    api("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:init-annotator:0.0.6")
    
    // 日志模块
    api(project(":logger"))
    
    // Retrofit + OkHttp
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.squareup.okhttp3:logging-interceptor:4.12.0")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    api("com.google.code.gson:gson:2.10.1")
    testImplementation("junit:junit:4.13.2")
}

// 发布配置
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "network"
            val moduleVersion = project.findProperty("network.version")?.toString() ?: "1.0.0"
version = moduleVersion
            artifact(file("build/outputs/aar/network-release.aar")) {
                extension = "aar"
            }
            
            pom {
                name.set("network")
                description.set("Android Component: network")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
            }
            pom.withXml {
                val deps = asNode().appendNode("dependencies")
                listOf(
                    "startup" to project.findProperty("startup.version"),
                    "logger" to project.findProperty("logger.version")
                ).forEach { (artifact, version) ->
                    deps.appendNode("dependency").apply {
                        appendNode("groupId", "com.mohanlv")
                        appendNode("artifactId", artifact)
                        appendNode("version", version)
                        appendNode("scope", "compile")
                    }
                }
                // External compile dependencies (from mavenCentral/Google)
                listOf(
                    "com.squareup.retrofit2:retrofit:2.9.0",
                    "com.squareup.retrofit2:converter-gson:2.9.0",
                    "com.squareup.okhttp3:okhttp:4.12.0",
                    "com.squareup.okhttp3:logging-interceptor:4.12.0",
                    "com.google.code.gson:gson:2.10.1",
                    "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
                ).forEach { coord ->
                    val parts = coord.split(":")
                    deps.appendNode("dependency").apply {
                        appendNode("groupId", parts[0])
                        appendNode("artifactId", parts[1])
                        appendNode("version", parts[2])
                        appendNode("scope", "compile")
                    }
                }
            }
        }
    }
}

kapt {
    arguments {
        arg("initCollectorPackage", "com.mohanlv.network")
        arg("initCollectorModuleName", "network")
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.named("assembleRelease"))
}