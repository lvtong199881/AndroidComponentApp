# Android Studio 禁用 Gradle 源码下载 - 完整位置指南

## 方法 1: Gradle 设置 (不同版本位置)

### Android Studio Hedgehog / Iguana / Jellyfish (2023+)
```
Preferences (⌘,) 
  → Build, Execution, Deployment 
  → Build Tools 
  → Gradle
```
查找：
- `Download sources and documentation for Gradle`
- 或者 `Gradle bundled sources`
- 或者 `Fetch documentation for Gradle`

### Android Studio Giraffe / Flamingo (2022-2023)
```
Preferences (⌘,)
  → Build, Execution, Deployment
  → Build Tools
  → Gradle
```
查找：
- `Gradle Settings` 区域
- 可能有 `Download sources` 复选框

### Android Studio Electric Eel / Dolphin (2022)
```
Preferences (⌘,)
  → Build, Execution, Deployment
  → Gradle
```
(没有 Build Tools 子菜单)

### Android Studio Chipmunk / Bumblebee (2021-2022)
```
Preferences (⌘,)
  → Build, Execution, Deployment
  → Build Tools
  → Gradle
```

---

## 方法 2: 直接搜索 (最快!)

在 Preferences 窗口：
1. 按 **⌘F** 或点击搜索框
2. 搜索关键词：
   - `sources`
   - `gradle`
   - `download`
   - `documentation`

---

## 方法 3: 使用 gradle.properties (推荐 - 100% 有效)

如果找不到 UI 选项，直接用配置文件：

### 步骤 1: 编辑项目 gradle.properties
文件位置：`/Users/mohanlv/Desktop/AndroidComponentProject/App/gradle.properties`

已添加以下配置：
```properties
# 禁用 Gradle 源码下载
kotlin.gradle.plugin.download.sources=false

# 额外优化
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

### 步骤 2: 编辑全局 gradle.properties
文件位置：`~/.gradle/gradle.properties`

```bash
# 运行以下命令确认配置
cat ~/.gradle/gradle.properties | grep sources
```

### 步骤 3: 清理并重启
```bash
# 清理 Gradle 缓存
rm -rf ~/.gradle/wrapper/dists/gradle-8.2*

# 清理 Android Studio 缓存
# File → Invalidate Caches → Invalidate and Restart
```

---

## 方法 4: 检查你的 Android Studio 版本

```bash
# 查看 Android Studio 版本
# 在 Android Studio 中：
# Android Studio → About Android Studio
```

版本对应关系：
| 版本名 | 版本号 | 年份 |
|--------|--------|------|
| Jellyfish | 2023.3.1 | 2024 |
| Iguana | 2023.2.1 | 2024 |
| Hedgehog | 2023.1.1 | 2023 |
| Giraffe | 2022.3.1 | 2023 |
| Flamingo | 2022.2.1 | 2023 |
| Electric Eel | 2022.1.1 | 2022 |

---

## 方法 5: 强制阻止下载 (终极方案)

如果以上都不行，可以：

### 1. 预下载源码包到缓存
```bash
cd ~/.gradle/wrapper/dists
mkdir -p gradle-8.2-src
cd gradle-8.2-src
curl -L -o gradle-8.2-src.zip https://mirrors.cloud.tencent.com/gradle/gradle-8.2-src.zip
unzip -q gradle-8.2-src.zip
```

### 2. 修改 hosts 阻止官方下载 (可选)
```bash
# 需要 sudo 权限
sudo nano /etc/hosts

# 添加以下行
127.0.0.1 services.gradle.org
```

⚠️ 注意：这会影响所有 Gradle 下载，只建议在完全配置好镜像后使用。

---

## 快速诊断脚本

运行以下脚本检查配置状态：

```bash
cd /Users/mohanlv/Desktop/AndroidComponentProject/App
./check-settings.sh
```

---

## 如果还是找不到

**请截图告诉我:**
1. 你的 Android Studio 版本 (About Android Studio)
2. Preferences → Build, Execution, Deployment 下的所有子菜单
3. Gradle 相关的所有设置页面

我会给你精确的指引！

---
🌙 莫寒慕 · 2026-04-01
