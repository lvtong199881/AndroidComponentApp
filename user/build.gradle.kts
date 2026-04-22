plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.user"
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
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation(project(":base"))
    implementation(project(":login"))
    
    // 路由注解
    compileOnly("com.mohanlv:router-annotation:0.0.6")
    kapt("com.mohanlv:router-annotator:0.0.6")
    testImplementation("junit:junit:4.13.2")
}

kapt {
    arguments {
        arg("routerCollectorPackage", "com.mohanlv.user")
        arg("routerCollectorModuleName", "user")
    }
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.mohanlv"
            artifactId = "user"
            version = System.getProperty("componentVersion", "1.0.0")
            artifact(file("build/outputs/aar/user-release.aar")) {
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

// 显式声明发布任务依赖 assembleRelease
tasks.withType<PublishToMavenRepository>().configureEach {
    dependsOn(tasks.named("assembleRelease"))
}
