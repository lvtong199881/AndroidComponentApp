# Gradle 源码下载 - 手动操作指引

## 问题
Android Studio 坚持从 `https://services.gradle.org/distributions/gradle-8.2-src.zip` 下载源码包。

## 最简单的解决方案 (3 选 1)

---

### 方案 A: 修改 hosts 文件 ⭐ 最推荐，100% 有效

**在终端执行以下命令:**

```bash
# 1. 添加 hosts 规则 (需要输入密码)
echo "127.0.0.1 services.gradle.org" | sudo tee -a /etc/hosts

# 2. 验证
grep services.gradle.org /etc/hosts

# 3. 清理 Gradle 缓存
rm -rf ~/.gradle/wrapper/dists/gradle-8.2-src

# 4. 重新打开 Android Studio Sync
```

**效果:** Android Studio 无法连接官方源，会被迫使用本地缓存或跳过下载。

**要恢复时:**
```bash
sudo sed -i '' '/services.gradle.org/d' /etc/hosts
```

---

### 方案 B: 在 Android Studio 中禁用源码下载

**步骤:**

1. **打开 Android Studio**
2. **Preferences** (按 ⌘,)
3. 在左侧导航栏找到并展开:**Build, Execution, Deployment**
4. 展开:**Build Tools**
5. 点击:**Gradle**
6. 在右侧找到并**取消勾选**以下选项 (如果存在):
   - ☐ `Download sources and documentation for Gradle`
   - ☐ `Fetch documentation for Gradle`
   - ☐ `Gradle bundled sources`
7. **Apply** → **OK**
8. **File** → **Invalidate Caches** → **Invalidate and Restart**
9. 重新 Sync 项目

**如果找不到这些选项:**
- 在 Preferences 窗口按 **⌘F** 搜索 `sources`
- 或者搜索 `gradle` 查看所有 Gradle 相关设置

---

### 方案 C: 预下载源码包到正确位置

**在终端执行:**

```bash
# 1. 清理旧缓存
rm -rf ~/.gradle/wrapper/dists/gradle-8.2-src

# 2. 创建正确的目录结构
mkdir -p ~/.gradle/wrapper/dists/gradle-8.2-src/hash_src
cd ~/.gradle/wrapper/dists/gradle-8.2-src/hash_src

# 3. 下载源码包 (腾讯云镜像)
curl -L -o gradle-8.2-src.zip https://mirrors.cloud.tencent.com/gradle/gradle-8.2-src.zip

# 4. 解压
unzip -q gradle-8.2-src.zip

# 5. 创建完成标记
touch gradle-8.2-src.zip.ok

# 6. 验证
ls -la
```

**然后:**
- 关闭 Android Studio
- File → Invalidate Caches → Invalidate and Restart
- 重新 Sync

---

## 推荐执行顺序

1. **先试方案 B** (在 Android Studio 中设置) - 最干净
2. **如果找不到设置，用方案 A** (修改 hosts) - 最可靠
3. **方案 C 作为备选** (预下载缓存)

---

## 验证是否成功

重新 Sync 后，在 **Build** 窗口查看日志：
- ✅ 成功：不再出现 `https://services.gradle.org/distributions/gradle-8.2-src.zip`
- ❌ 失败：仍然尝试从官方源下载

---

## 需要帮助？

如果以上方案都不行，请告诉我：
1. **Android Studio 版本** (About Android Studio)
2. **macOS 版本** (`sw_vers`)
3. **Sync 时的完整日志**

---
🌙 莫寒慕 · 2026-04-01
