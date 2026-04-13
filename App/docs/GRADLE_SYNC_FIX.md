# Gradle Sync 下载源码问题修复

## 问题
Android Studio Sync 时尝试下载 `gradle-8.2-src.zip`（源码包），但配置中只有二进制包镜像。

## 原因
Android Studio 默认会下载 Gradle 源码包用于：
- 代码导航
- 调试 Gradle 内部逻辑
- 查看源码注释

## 解决方案

### 方案 1: 添加源码包镜像 ✅ (已配置)

编辑 `gradle/wrapper/gradle-wrapper.properties`：

```properties
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip
distributionSrcUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-src.zip
```

### 方案 2: 禁用源码下载 (推荐 - 不需要源码时)

**Android Studio 设置:**
1. `Preferences` → `Build, Execution, Deployment` → `Build Tools` → `Gradle`
2. 取消勾选 `Download sources and documentation for Gradle`
3. 或者设置 `Gradle JVM` 为已安装的 JDK

**或者在 `gradle.properties` 中添加:**
```properties
# 不下载 Gradle 源码
systemProp.org.gradle.internal.http.connectionTimeout=10000
systemProp.org.gradle.internal.http.socketTimeout=10000
```

### 方案 3: 手动下载源码包 (最快)

```bash
# 手动下载源码包到 Gradle 缓存
cd ~/.gradle/wrapper/dists
mkdir -p gradle-8.2-src
cd gradle-8.2-src
curl -O https://mirrors.cloud.tencent.com/gradle/gradle-8.2-src.zip
unzip gradle-8.2-src.zip
```

## 快速修复步骤

### 方法 A: 清理缓存重新 Sync (推荐)
```bash
# 1. 关闭 Android Studio
# 2. 清理 Gradle 缓存
rm -rf ~/.gradle/wrapper/dists/gradle-8.2*

# 3. 重新打开 Android Studio 并 Sync
# File → Invalidate Caches → Invalidate and Restart
```

### 方法 B: 使用命令行先下载
```bash
cd /Users/mohanlv/Desktop/AndroidComponentProject/App
gradle wrapper --gradle-version 8.2
```

## 验证配置

```bash
# 检查 Wrapper 配置
cat gradle/wrapper/gradle-wrapper.properties

# 测试下载
gradle --version
```

## 完整配置示例

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip
distributionSrcUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-src.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

---
🌙 莫寒慕 · 2026-04-01
