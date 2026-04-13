# Gradle 源码下载 - 快速解决方案

## 🎯 最简单的解决方法 (3 步)

### 步骤 1: 关闭 Android Studio

完全退出 Android Studio (⌘Q)

### 步骤 2: 清理缓存

```bash
# 清理 Gradle 源码缓存
rm -rf ~/.gradle/wrapper/dists/gradle-8.2-src

# 清理 Android Studio 缓存
rm -rf ~/Library/Caches/Google/AndroidStudio*/caches/gradle_*
```

### 步骤 3: 在 Android Studio 中设置

1. **打开 Android Studio**
2. **打开任意项目** (不一定是这个)
3. **Preferences** (⌘,)
4. **Build, Execution, Deployment** → **Build Tools** → **Gradle**
5. 找到 **"Gradle User Home"**
6. 确保设置为：`/Users/mohanlv/.gradle`
7. **取消勾选** 任何包含 "sources" 或 "documentation" 的选项
8. **Apply** → **OK**
9. **File** → **Invalidate Caches** → **Invalidate and Restart**
10. 重新打开你的项目

---

## 🚀 如果找不到设置 (Android Studio 版本不同)

### 方法 A: 使用搜索
1. 在 Preferences 窗口按 **⌘F**
2. 搜索 `gradle`
3. 找到 Gradle 相关设置
4. 取消勾选任何 "Download sources" 选项

### 方法 B: 直接修改配置文件

```bash
# 找到 Android Studio 配置目录
CONFIG_DIR=$(find ~/Library -maxdepth 3 -name "AndroidStudio*" -type d 2>/dev/null | head -1)

# 查找 gradle 配置文件
find "$CONFIG_DIR" -name "*gradle*.xml" 2>/dev/null

# 编辑找到的文件，添加或修改:
# <option name="downloadSources" value="false" />
```

---

## 💡 终极方案：修改 hosts (100% 有效)

如果以上都不行，直接阻止官方源：

```bash
# 添加 hosts 规则
echo "127.0.0.1 services.gradle.org" | sudo tee -a /etc/hosts

# 验证
grep services.gradle.org /etc/hosts
```

然后重新 Sync，Android Studio 会因为无法连接官方源而使用本地缓存。

**要恢复时删除该行：**
```bash
sudo sed -i '' '/services.gradle.org/d' /etc/hosts
```

---

## 📋 检查清单

- [ ] Gradle 源码缓存已清理
- [ ] Android Studio 缓存已清理
- [ ] Gradle User Home 设置为 `~/.gradle`
- [ ] 已取消勾选 "Download sources" 选项
- [ ] 已执行 Invalidate Caches

---

## ❓ 还是不行？

请告诉我：
1. 你的 **Android Studio 版本** (About Android Studio)
2. Sync 时的 **完整错误日志**

我会给你更精确的解决方案！

---
🌙 莫寒慕 · 2026-04-01
