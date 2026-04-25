plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.home"
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
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    // 源码依赖
    implementation(project(":reactnative"))
    implementation(project(":websdk"))
    implementation(project(":shortvideo"))

    // maven依赖
    implementation("com.mohanlv:base:1.1.1")
    implementation("com.mohanlv:network:1.1.1")
    implementation("com.mohanlv:router:1.0.10")
    // Retrofit + OkHttp (network:1.0.1 未暴露这些 transitive 依赖)
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.12.0")
    api("com.google.code.gson:gson:2.10.1")

    // 路由注解
    compileOnly("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:router-annotator:0.0.5")
    testImplementation("junit:junit:4.13.2")
}

kapt {
    arguments {
        arg("routerCollectorPackage", "com.mohanlv.home")
        arg("routerCollectorModuleName", "home")
    }
}

val tokenFile = System.getProperty("user.home") + "/.github_token"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "home"
            val moduleVersion = project.findProperty("home.version")?.toString() ?: "1.0.0"
            version = moduleVersion
            artifact("$buildDir/outputs/aar/home-release.aar") {
                extension = "aar"
            }
            pom {
                name.set("home")
                description.set("Android Component: home")
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

// 显式声明发布任务依赖 assembleRelease
tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.named("assembleRelease"))
}
