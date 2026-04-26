plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.mohanlv.startup"
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")

    testImplementation("junit:junit:4.13.2")
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
            artifactId = "startup"
            val moduleVersion = project.findProperty("$artifactId.version")?.toString() ?: "1.0.0"
            version = moduleVersion
            artifact(file("build/outputs/aar/startup-release.aar")) {
                extension = "aar"
            }
            pom {
                name.set("startup")
                description.set("Android Component: startup")
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
            url = uri("https://maven.pkg.github.com/lvtong199881/AndroidComponentApp")
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
