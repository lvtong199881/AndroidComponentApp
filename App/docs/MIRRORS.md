# 🇨🇳 国内镜像加速配置

## 已配置的镜像源

### 1. Gradle 插件仓库（`settings.gradle.kts`）
```kotlin
maven { url = uri("https://maven.aliyun.com/repository/google") }
maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
maven { url = uri("https://maven.aliyun.com/repository/public") }
```

### 2. Maven 依赖仓库（`dependencies-config.gradle.kts`）
```kotlin
maven { url = uri("https://maven.aliyun.com/repository/google") }
maven { url = uri("https://maven.aliyun.com/repository/public") }
```

### 3. Gradle 发行版（`gradle-wrapper.properties`）
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
```

---

## 🚀 如果下载还是慢

### 方式 1：手动下载 Gradle

1. **从阿里云下载**
   ```bash
   wget https://mirrors.aliyun.com/macports/distfiles/gradle/gradle-8.2-bin.zip
   ```

2. **解压到 Gradle 缓存**
   ```bash
   mkdir -p ~/.gradle/wrapper/dists/gradle-8.2-bin
   unzip gradle-8.2-bin.zip -d ~/.gradle/wrapper/dists/gradle-8.2-bin/
   ```

3. **重新 Sync**

### 方式 2：配置全局 Gradle 镜像

**编辑 `~/.gradle/gradle.properties`：**
```properties
# 阿里云 Maven 镜像
org.gradle.gradleDistributionUrl=https\://mirrors.aliyun.com/macports/distfiles/gradle/gradle-8.2-bin.zip

# Kotlin 编译使用国内镜像
kotlin.daemon.jvmargs=-Xmx2048m
```

### 方式 3：使用 Gradle 配置镜像（推荐）

**编辑 `~/.gradle/init.gradle.kts`：**
```kotlin
allprojects {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}
```

---

## 📋 阿里云镜像地址大全

| 仓库类型 | 镜像地址 |
|---------|---------|
| **Google** | `https://maven.aliyun.com/repository/google` |
| **公共库** | `https://maven.aliyun.com/repository/public` |
| **Gradle 插件** | `https://maven.aliyun.com/repository/gradle-plugin` |
| **Central** | `https://maven.aliyun.com/repository/central` |
| **Gradle 发行版** | `https://mirrors.aliyun.com/macports/distfiles/gradle/` |

---

## ✅ 验证配置

```bash
cd App
./gradlew build --refresh-dependencies
```

查看输出，应该能看到从阿里云下载：
```
Downloading from: https://maven.aliyun.com/repository/google/...
```

---

## 🔧 故障排除

### Q: 还是下载失败？
**A:** 尝试清理缓存：
```bash
./gradlew clean
rm -rf ~/.gradle/caches/
# 重新 Sync
```

### Q: Android Studio 不生效？
**A:** 重启 Android Studio + Invalidate Caches：
```
File → Invalidate Caches → Invalidate and Restart
```

### Q: 某些库阿里云没有？
**A:** 配置多个镜像源，会自动 fallback：
```kotlin
repositories {
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    google()  // 官方 Google（备用）
    mavenCentral()  // 官方 Central（备用）
}
```

---

**配置完成后，Sync Gradle 应该快很多！** 🚀
