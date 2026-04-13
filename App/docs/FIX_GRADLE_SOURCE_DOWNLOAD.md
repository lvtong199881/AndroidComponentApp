# 彻底解决 Gradle 源码下载问题

## 问题现状

即使预下载了源码包，Android Studio 仍然从 `https://services.gradle.org/distributions/gradle-8.2-src.zip` 下载。

## 根本原因

Android Studio 的 Gradle 插件有**独立的源码下载逻辑**，它：
1. 不尊重 `gradle-wrapper.properties` 中的 `distributionSrcUrl`
2. 有自己的缓存目录和验证机制
3. 强制从官方源下载用于代码导航

## 解决方案 (按有效性排序)

### 方案 1: 在 Android Studio 中切换 Gradle 模式 ⭐⭐⭐⭐⭐

**这是最有效的方案！**

1. 打开 Android Studio
2. `Preferences` (⌘,) → `Build, Execution, Deployment` → `Build Tools` → `Gradle`
3. 找到 **Gradle User Home** 或 **Gradle JDK**
4. 关键：找到 **Android Studio Gradle** 选项，改为 **Default Gradle Wrapper**
5. 或者设置 **Gradle User Home** 为 `~/.gradle`

### 方案 2: 修改 Android Studio 配置文件

找到 Android Studio 的 Gradle 配置文件：

```bash
# 查找配置文件
find ~/Library -name "*gradle*.xml" 2>/dev/null | grep -i android
```

常见位置：
- `~/Library/Application Support/Google/AndroidStudio2023.x/options/gradle.settings.xml`
- `~/Library/Preferences/AndroidStudio2023.x/options/gradle.settings.xml`

编辑文件，添加或修改：
```xml
<application>
  <component name="GradleSystem">
    <option name="linkedExternalProjectsSettings">
      <GradleProjectSettings>
        <option name="delegatedBuild" value="true" />
        <option name="testRunner" value="PLATFORM" />
      </GradleProjectSettings>
    </option>
  </component>
</application>
```

### 方案 3: 使用 Gradle 8.2 的发行版目录结构

Gradle 缓存需要特定的目录结构：

```bash
# 正确的结构应该是：
~/.gradle/wrapper/dists/gradle-8.2-src/<HASH>/
├── gradle-8.2/           # 解压后的源码
├── gradle-8.2-src.zip    # 源码包
├── gradle-8.2-src.zip.lck
└── gradle-8.2-src.zip.ok
```

运行以下命令修复：
```bash
cd ~/.gradle/wrapper/dists/gradle-8.2-src
# 检查是否有哈希子目录
ls -la
# 如果没有，创建一个
mkdir -p src_hash
mv gradle-8.2 src_hash/
mv gradle-8.2-src.zip src_hash/
touch src_hash/gradle-8.2-src.zip.ok
```

### 方案 4: 修改 hosts 文件 (终极方案)

如果以上都不行，直接阻止官方源访问：

```bash
# 编辑 hosts 文件
sudo nano /etc/hosts

# 添加以下行
127.0.0.1 services.gradle.org
```

⚠️ **注意**: 这会阻止所有从 services.gradle.org 的下载，只应在配置好镜像后使用。

### 方案 5: 使用命令行 Gradle 代替

在 Android Studio 中：
1. `Preferences` → `Build, Execution, Deployment` → `Build Tools` → `Gradle`
2. **Gradle User Home**: 设置为 `/Users/mohanlv/.gradle`
3. **Gradle JDK**: 选择已安装的 JDK 17
4. 勾选 **Offline work** (离线模式)

## 快速诊断

运行以下命令检查：

```bash
# 1. 检查 Gradle 缓存
ls -la ~/.gradle/wrapper/dists/

# 2. 检查 Android Studio 配置
find ~/Library -name "*gradle*.xml" 2>/dev/null | head -5

# 3. 测试官方源访问
curl -I https://services.gradle.org/distributions/gradle-8.2-src.zip
```

## 推荐的完整流程

```bash
# 1. 清理 Gradle 源码缓存
rm -rf ~/.gradle/wrapper/dists/gradle-8.2-src

# 2. 预下载源码包到正确位置
cd ~/.gradle/wrapper/dists
mkdir -p gradle-8.2-src/hash
cd gradle-8.2-src/hash
curl -L -o gradle-8.2-src.zip https://mirrors.cloud.tencent.com/gradle/gradle-8.2-src.zip
unzip -q gradle-8.2-src.zip
touch gradle-8.2-src.zip.ok

# 3. 在 Android Studio 中
# File → Invalidate Caches → Invalidate and Restart

# 4. 重新 Sync 项目
```

## 如果还是不行

请告诉我：
1. **Android Studio 版本** (About Android Studio)
2. **Gradle 插件版本** (在 `Plugins` 中查看)
3. **Sync 时的完整日志** (Build 窗口)

我会给你更精确的解决方案！

---
🌙 莫寒慕 · 2026-04-01
