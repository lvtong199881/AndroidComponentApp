plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.websdk"
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

    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.webkit:webkit:1.9.0")
    implementation(project(":base"))
    
    // 路由注解
    compileOnly("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:router-annotator:0.0.6")
}

kapt {
    arguments {
        arg("routerCollectorPackage", "com.mohanlv.websdk")
        arg("routerCollectorModuleName", "websdk")
    }
}


// 显式声明发布任务依赖 assembleRelease
tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.named("assembleRelease"))
}

// 发布配置
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "websdk"
            val moduleVersion = project.findProperty("websdk.version")?.toString() ?: "1.0.0"
version = moduleVersion
            artifact(file("build/outputs/aar/websdk-release.aar")) {
                extension = "aar"
            }
            pom {
                name.set("websdk")
                description.set("Android Component: websdk")
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
