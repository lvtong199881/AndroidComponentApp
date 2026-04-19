plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    // maven-publish 已在根项目配置，无需重复应用
}

android {
    namespace = "com.mohanlv.base"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.databinding:viewbinding:9.1.0")
    implementation("io.coil-kt:coil:2.5.0")
    
    // 核心基础 SDK（通过 api() 传递依赖）
    api(project(":router"))
    api(project(":network"))
    
    testImplementation("junit:junit:4.13.2")
}


// publishing 配置已在根项目统一管理
