plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.reactnative"
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
    implementation("com.mohanlv:router:1.0.10")
    implementation("com.mohanlv:startup:1.0.10")
    compileOnly("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:router-annotator:0.0.6")
    implementation("com.mohanlv:base:0.0.5")
    
    testImplementation("junit:junit:4.13.2")
}

val tokenFile = System.getProperty("user.home") + "/.github_token"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "reactnative"
            val moduleVersion = project.findProperty("reactnative.version")?.toString() ?: "1.0.0"
            version = moduleVersion
            artifact("$buildDir/outputs/aar/reactnative-release.aar") {
                extension = "aar"
            }
            pom {
                name.set("reactnative")
                description.set("Android Component: reactnative")
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
            name = "LocalMaven"
            url = uri(System.getProperty("user.home") + "/.m2/repository/releases")
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lvtong199881/AndroidComponentApp")
            credentials {
                username = "lvtong199881"
                password = File(tokenFile).takeIf { it.exists() }?.readText()?.trim() ?: ""
            }
        }
    }
}

kapt {
    arguments {
        arg("routerCollectorPackage", "com.mohanlv.reactnative")
        arg("routerCollectorModuleName", "reactnative")
    }
}
