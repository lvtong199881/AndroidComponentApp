plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    // maven-publish 已在根项目配置，无需重复应用
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
    implementation(project(":base"))
    implementation(project(":network"))
    implementation(project(":router"))
    implementation(project(":user"))
    testImplementation("junit:junit:4.13.2")
}

// publishing 配置已在根项目统一管理
