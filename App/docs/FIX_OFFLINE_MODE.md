# 禁用 Gradle 离线模式

## 错误信息
```
No cached version of xxx available for offline mode.
```

## 原因
Gradle 被配置为离线模式，无法下载新的依赖包。

## 解决方案

### 方法 1: Android Studio 设置 (推荐)

1. **Preferences** (⌘,) → **Build, Execution, Deployment** → **Build Tools** → **Gradle**
2. **取消勾选** `Offline work` 选项
3. **Apply** → **OK**
4. 重新 Sync 项目

### 方法 2: 命令行构建

```bash
cd /Users/mohanlv/Desktop/AndroidComponentProject/App
./gradlew build --no-offline
```

### 方法 3: 检查 gradle.properties

确保 `gradle.properties` 中没有：
```properties
# 不要添加这行
org.gradle.offline=true
```

## 验证

Sync 时查看 Build 窗口，应该能看到：
- ✅ 正在从 Maven 仓库下载依赖
- ✅ 没有 "offline mode" 相关错误

---
🌙 莫寒慕 · 2026-04-01
