plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.login"
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
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.mohanlv:base:1.2.28")
    implementation("com.mohanlv:network:1.2.15")
    implementation("com.mohanlv:router:1.2.10")
    implementation("com.mohanlv:startup:1.2.29")
    implementation("com.mohanlv:logger:1.2.26")
    api("com.google.code.gson:gson:2.10.1")
    testImplementation("junit:junit:4.13.2")

    kapt("com.mohanlv:init-annotator:1.0.1")
    // 路由注解
    compileOnly("com.mohanlv:router-annotation:1.0.1")
    kapt("com.mohanlv:router-annotator:1.0.1")
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
            artifactId = "login"
            val moduleVersion = project.findProperty("$artifactId.version")?.toString() ?: "1.0.0"
            version = moduleVersion
            artifact(file("build/outputs/aar/login-release.aar")) {
                extension = "aar"
            }
            pom {
                name.set("login")
                description.set("Android Component: login")
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
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/lvtong199881/PackagesMaven")
            credentials {
                username = "lvtong199881"
                password = System.getenv("GITHUB_TOKEN") ?: run {
                    val tokenFile = File(System.getProperty("user.home"), ".github_token")
                    if (tokenFile.exists()) tokenFile.readText().trim() else ""
                }
            }
        }
    }
}

kapt {
    arguments {
        arg("initCollectorPackage", "com.mohanlv.login")
        arg("initCollectorModuleName", "login")
        arg("routerCollectorPackage", "com.mohanlv.login")
        arg("routerCollectorModuleName", "login")
    }
}