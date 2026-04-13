# 🇨🇳 Gradle 国内镜像完整配置

## ✅ 已配置

### Gradle 发行版镜像
**文件：** `App/gradle/wrapper/gradle-wrapper.properties`

```properties
# 腾讯云镜像（推荐）
distributionUrl=https\://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip

# 备选阿里云镜像
# distributionUrl=https\://mirrors.aliyun.com/macports/distfiles/gradle/gradle-8.2-bin.zip
```

### Maven 仓库镜像
**文件：** `App/settings.gradle.kts` + `dependencies-config.gradle.kts`

```kotlin
maven { url = uri("https://maven.aliyun.com/repository/google") }
maven { url = uri("https://maven.aliyun.com/repository/public") }
maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
```

---

## 🚀 如果还是下载失败

### 方式 1：手动下载 Gradle（最可靠）

**1. 用浏览器或 wget 下载：**
```bash
# 腾讯云
wget https://mirrors.cloud.tencent.com/gradle/gradle-8.2-bin.zip

# 或阿里云
wget https://mirrors.aliyun.com/macports/distfiles/gradle/gradle-8.2-bin.zip
```

**2. 解压到 Gradle 缓存目录：**
```bash
# 创建目录
mkdir -p ~/.gradle/wrapper/dists/gradle-8.2-bin

# 解压（根据你的实际路径调整）
unzip gradle-8.2-bin.zip -d ~/.gradle/wrapper/dists/gradle-8.2-bin/

# 或者用 SDKMAN 管理
# sdk install gradle 8.2
```

**3. 重新 Sync Gradle**

---

### 方式 2：配置全局 Gradle 镜像

**创建 `~/.gradle/init.gradle.kts`：**
```kotlin
allprojects {
    repositories {
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    }
}
```

**创建 `~/.gradle/gradle.properties`：**
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
```

---

### 方式 3：使用 SDKMAN 管理 Gradle（推荐 Mac 用户）

**1. 安装 SDKMAN：**
```bash
curl -s "https://get.sdkman.io" | zsh
source "$HOME/.sdkman/bin/sdkman-init.sh"
```

**2. 安装 Gradle 8.2：**
```bash
sdk install gradle 8.2
```

**3. 修改 wrapper 使用本地 Gradle：**
```bash
cd ~/Desktop/AndroidComponentProject/App
gradle wrapper --gradle-version 8.2
```

---

### 方式 4：Android Studio 配置

**Settings → Build, Execution, Deployment → Gradle：**

1. **Gradle user home:** `~/.gradle`
2. **Gradle distribution path:** 选择本地已安装的 Gradle
3. **勾选:**
   - ✅ Offline work（离线模式，如果已下载完依赖）
   - ✅ Gradle daemon

---

## 📋 国内镜像地址大全

### Gradle 发行版
| 镜像源 | 地址 |
|--------|------|
| **腾讯云** | `https://mirrors.cloud.tencent.com/gradle/` |
| **阿里云** | `https://mirrors.aliyun.com/macports/distfiles/gradle/` |
| **华为云** | `https://repo.huaweicloud.com/gradle/` |

### Maven 仓库
| 镜像源 | 地址 |
|--------|------|
| **阿里云 Google** | `https://maven.aliyun.com/repository/google` |
| **阿里云公共库** | `https://maven.aliyun.com/repository/public` |
| **阿里云 Gradle 插件** | `https://maven.aliyun.com/repository/gradle-plugin` |
| **阿里云 Central** | `https://maven.aliyun.com/repository/central` |

---

## ✅ 验证

**命令行测试：**
```bash
cd ~/Desktop/AndroidComponentProject/App

# 清理缓存
./gradlew clean

# 刷新依赖
./gradlew build --refresh-dependencies --info

# 查看下载源
./gradlew dependencies --configuration debugCompileClasspath | head -20
```

**应该能看到：**
```
Downloading from: https://maven.aliyun.com/repository/google/...
Downloading from: https://mirrors.cloud.tencent.com/gradle/...
```

---

## 🔧 故障排除

### Q: 权限错误？
**A:** 清理 Gradle 缓存：
```bash
rm -rf ~/.gradle/caches/
rm -rf ~/.gradle/wrapper/
```

### Q: Android Studio 不生效？
**A:** 
1. `File → Invalidate Caches → Invalidate and Restart`
2. 关闭 Android Studio
3. 删除 `.idea/` 文件夹
4. 重新打开项目

### Q: 某些库还是下载失败？
**A:** 添加更多镜像源作为 fallback：
```kotlin
repositories {
    maven { url = uri("https://maven.aliyun.com/repository/google") }
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
    google()  // 官方（备用）
    mavenCentral()  // 官方（备用）
}
```

---

**推荐先用方式 1 手动下载 Gradle，最稳定！** 🎯
