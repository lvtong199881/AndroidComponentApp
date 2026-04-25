plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.base"
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
    
    buildFeatures {
        viewBinding = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.databinding:viewbinding:9.1.0")
    implementation("io.coil-kt:coil:2.5.0")
    
    // Startup 框架
    api("com.mohanlv:startup:1.0.10")
    kapt("com.mohanlv:init-annotator:0.0.6")
    
    // 日志模块
    api("com.mohanlv:logger:1.0.10")
    
    // 核心基础 SDK（通过 api() 传递依赖，供业务模块使用）
    api("com.mohanlv:router:1.0.10")
    api("com.mohanlv:network:1.0.1")
    
    testImplementation("junit:junit:4.13.2")
}

// 发布配置
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "base"
            val moduleVersion = project.findProperty("base.version")?.toString() ?: "1.0.0"
            version = moduleVersion
            artifact(file("build/outputs/aar/base-release.aar")) {
                extension = "aar"
            }
            
            pom {
                name.set("base")
                description.set("Android Component: base")
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
        arg("initCollectorPackage", "com.mohanlv.base")
        arg("initCollectorModuleName", "base")
    }
}

tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.named("assembleRelease"))
}
