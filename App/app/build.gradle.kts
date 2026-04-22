// 组件依赖配置说明：
// - component.sourceDependency=true  → 源码依赖（可调试）
// - component.sourceDependency=false → Maven 依赖
// 配置文件：gradle.properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mohanlv.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mohanlv.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

// 组件依赖配置
val componentSourceDependency = providers.gradleProperty("component.sourceDependency").get().toBoolean()
val componentVersion = providers.gradleProperty("component.version").get()
val componentGroupId = providers.gradleProperty("component.groupId").get()

dependencies {
    // AndroidX
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    // ========== 组件依赖（自动切换源码/Maven 模式） ==========
    implementation("$componentGroupId:base:$componentVersion")
    implementation("$componentGroupId:router:$componentVersion")
    implementation("$componentGroupId:network:$componentVersion")
    implementation("$componentGroupId:login:$componentVersion")
    implementation("$componentGroupId:home:$componentVersion")
    implementation("$componentGroupId:reactnative:$componentVersion")
    implementation("$componentGroupId:logger:$componentVersion")
    implementation("$componentGroupId:user:$componentVersion")
    implementation("$componentGroupId:websdk:$componentVersion")
    // =======================================================
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    
    // React Native SoLoader (for native library loading)
    implementation("com.facebook.soloader:soloader:0.11.0")
    
    // React Native Core (for ReactInstanceManager)
    implementation("com.facebook.react:react-android:0.76.9")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
