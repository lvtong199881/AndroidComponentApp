# Android Studio Gradle 源码下载问题 - 终极解决方案

## 问题根源
Android Studio 在 Sync 时会**忽略** `gradle-wrapper.properties` 中的 `distributionSrcUrl`，强制从官方源下载源码包。

## 解决方案 (按推荐顺序)

### 方案 1: 在 Android Studio 禁用源码下载 ⭐ 最推荐

**步骤:**
1. 打开 Android Studio
2. `Preferences` (⌘,) → `Build, Execution, Deployment` → `Build Tools` → `Gradle`
3. **取消勾选** `Download sources and documentation for Gradle`
4. 点击 `Apply` → `OK`
5. `File` → `Invalidate Caches` → `Invalidate and Restart`

**效果:** 不再下载源码包，Sync 速度飞快

---

### 方案 2: 手动下载源码包到缓存 (如果确实需要源码)

```bash
# 1. 找到 Gradle 缓存目录
GRADLE_CACHE=~/.gradle/wrapper/dists

# 2. 创建源码目录
mkdir -p $GRADLE_CACHE/gradle-8.2-src

# 3. 下载源码包 (腾讯云镜像)
cd $GRADLE_CACHE/gradle-8.2-src
curl -L -o gradle-8.2-src.zip https://mirrors.cloud.tencent.com/gradle/gradle-8.2-src.zip

# 4. 解压
unzip -q gradle-8.2-src.zip

# 5. 确认目录结构
# 应该看到：gradle-8.2-src/gradle-8.2/ 包含 src 目录
```

---

### 方案 3: 修改 Android Studio Gradle 设置

**编辑 `~/.gradle/gradle.properties`:**
```bash
cat >> ~/.gradle/gradle.properties << 'EOF'

# 禁用 Gradle 源码下载
kotlin.gradle.plugin.download.sources=false
org.gradle.gradle.distribution.sources=false
EOF
```

**或者在项目 `gradle.properties` 中添加:**
```properties
# 不下载 Gradle 源码
systemProp.org.gradle.internal.download.sources=false
```

---

### 方案 4: 使用代理/hosts 重定向 (最后手段)

如果 Android Studio 坚持要从官方源下载，可以：

1. **先手动下载到缓存** (方案 2)
2. **修改 hosts 阻止官方下载** (不推荐，可能影响其他功能)

---

## 验证步骤

### 1. 检查 Android Studio 设置
```
Preferences → Build, Execution, Deployment → Gradle
```
确保 `Download sources and documentation for Gradle` **未勾选**

### 2. 清理 Android Studio 缓存
```
File → Invalidate Caches → 勾选所有选项 → Invalidate and Restart
```

### 3. 检查 Gradle 缓存
```bash
ls -la ~/.gradle/wrapper/dists/gradle-8.2*
```

### 4. 查看 Gradle 日志
Sync 时在 `Build` 窗口查看实际下载 URL

---

## 为什么 distributionSrcUrl 不起作用？

Android Studio 的 Gradle 插件有自己的逻辑：
- 使用 `GradleInstallationLocator` 定位 Gradle
- 独立管理源码下载
- 不尊重 Wrapper 的 `distributionSrcUrl` 配置

这是 Android Studio 的已知行为，不是 Bug。

---

## 最佳实践

1. **开发环境**: 禁用源码下载 (方案 1)
2. **需要调试 Gradle**: 手动下载源码 (方案 2)
3. **团队项目**: 在 `.gitignore` 中添加 Gradle 缓存，让每个人自己配置

---

## 快速命令

```bash
# 检查当前配置
cat ~/.gradle/gradle.properties

# 清理 Gradle 缓存
rm -rf ~/.gradle/wrapper/dists/gradle-8.2*

# 预下载二进制包
cd /Users/mohanlv/Desktop/AndroidComponentProject/App
gradle wrapper --gradle-version 8.2

# 查看 Gradle 版本
gradle --version
```

---
🌙 莫寒慕 · 2026-04-01
