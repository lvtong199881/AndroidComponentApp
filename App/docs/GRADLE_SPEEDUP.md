# Gradle 下载加速指南

## 当前配置状态 ✅

### 1. Gradle Wrapper 镜像 (已配置)
文件：`gradle/wrapper/gradle-wrapper.properties`
```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip
```
使用腾讯云镜像下载 Gradle 发行版。

### 2. 插件仓库镜像 (已配置)
文件：`settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
```

## 额外加速方案

### 方案 1: 配置全局 Gradle 镜像 (推荐)

创建或编辑 `~/.gradle/gradle.properties`：

```bash
mkdir -p ~/.gradle
cat >> ~/.gradle/gradle.properties << 'EOF'
# 阿里云 Maven 镜像
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.configureondemand=true

# 增加 Gradle 堆大小
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
EOF
```

### 方案 2: 配置项目级 gradle.properties

在 `gradle.properties` 中添加：

```properties
# 启用缓存和并行构建
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true

# 增加 JVM 内存
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8

# 阿里云 Maven 镜像（备选）
# maven { url 'https://maven.aliyun.com/repository/google' }
# maven { url 'https://maven.aliyun.com/repository/public' }
```

### 方案 3: 使用华为云镜像 (备选)

如果阿里云慢，可以替换为华为云：

**settings.gradle.kts**
```kotlin
pluginManagement {
    repositories {
        maven { url = uri("https://repo.huaweicloud.com/repository/maven/") }
        maven { url = uri("https://mirrors.huaweicloud.com/repository/maven/") }
        google()
        mavenCentral()
    }
}
```

**build.gradle.kts (项目级)**
```kotlin
allprojects {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        google()
        mavenCentral()
    }
}
```

## 常用优化命令

```bash
# 清理并重新构建
gradle clean build --refresh-dependencies

# 离线模式构建 (依赖已下载时)
gradle build --offline

# 查看依赖树
gradle dependencies

# 只构建特定模块
gradle :Components:home:build

# 禁用守护进程 (调试用)
gradle build --no-daemon
```

## 检查 Gradle 缓存

```bash
# 查看 Gradle 缓存位置
echo $GRADLE_USER_HOME  # 默认：~/.gradle

# 查看缓存大小
du -sh ~/.gradle/caches

# 清理旧缓存 (谨慎使用)
gradle clean
rm -rf ~/.gradle/caches/build-cache-*
```

## 网络诊断

```bash
# 测试阿里云镜像速度
curl -I https://maven.aliyun.com/repository/google/

# 测试腾讯云镜像速度
curl -I https://mirrors.cloud.tencent.com/

# 测试 Gradle 官方源速度
curl -I https://services.gradle.org/
```

---
🌙 莫寒慕 · 2026-04-01
