plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.reactnative"
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
    buildFeatures { viewBinding = true }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    
    // React Native Core
    api("com.facebook.react:react-android:0.76.9")
    
    // Hermes Engine (required for JS execution)
    api("com.facebook.react:hermes-android:0.76.9")
    
    // SoLoader for native library loading
    api("com.facebook.soloader:soloader:0.11.0")
    
    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // 依赖 router（获取 @Route 注解）
    implementation(project(":router"))
    
    // Startup 框架
    implementation(project(":startup"))
    kapt("com.mohanlv:init-annotator:0.0.6")
    compileOnly("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:router-annotator:0.0.6")
    
    // 依赖 base（BaseDialogFragment 等基础组件）
    implementation(project(":base"))
    
    testImplementation("junit:junit:4.13.2")
}



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


kapt {
    arguments {
        arg("initCollectorPackage", "com.mohanlv.reactnative")
        arg("initCollectorModuleName", "reactnative")
        arg("routerCollectorPackage", "com.mohanlv.reactnative")
        arg("routerCollectorModuleName", "reactnative")
    }
}

// 显式声明发布任务依赖 assembleRelease
tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.named("assembleRelease"))
}
