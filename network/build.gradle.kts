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
    api("com.mohanlv:startup:1.0.10")
    kapt("com.mohanlv:init-annotator:0.0.6")
    
    // 日志模块
    api("com.mohanlv:logger:1.0.10")
    
    // Retrofit + OkHttp
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.squareup.okhttp3:logging-interceptor:4.12.0")
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