plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.home"
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
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation(project(":base"))
    implementation(project(":reactnative"))
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
            artifactId = "home"
            version = System.getProperty("componentVersion", "1.0.0")
            artifact(file("build/outputs/aar/home-release.aar")) {
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
