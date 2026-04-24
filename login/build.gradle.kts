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
    implementation(project(":base"))
    implementation(project(":network"))
    implementation(project(":router"))
    testImplementation("junit:junit:4.13.2")

    kapt("com.mohanlv:init-annotator:0.0.6")
    compileOnly("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:router-annotator:0.0.6")
}


// publishing 配置已在根项目统一管理



val target = project.findProperty("target")?.toString() ?: "local"
val tokenFile = System.getProperty("user.home") + "/.github_token"


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "login"
            val moduleVersion = project.findProperty("login.version")?.toString() ?: "1.0.0"
version = moduleVersion
            artifact(file("build/outputs/aar/login-release.aar")) {
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

kapt {
    arguments {
        arg("initCollectorPackage", "com.mohanlv.login")
        arg("initCollectorModuleName", "login")
        arg("routerCollectorPackage", "com.mohanlv.login")
        arg("routerCollectorModuleName", "login")
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
            artifactId = "login"
            version = System.getProperty("componentVersion", "1.0.0")
            artifact("$buildDir/outputs/aar/login-release.aar") {
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
}
