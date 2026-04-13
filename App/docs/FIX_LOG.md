# Gradle 构建错误修复日志

## 问题
```
Caused by: org.gradle.api.InvalidUserDataException: Cannot add a Publication with name 'release' as a Publication with that name already exists.
```

## 根本原因
- **根项目** (`Components/build.gradle.kts`) 已经统一配置了 `maven-publish` 插件和 `release` publication
- **子模块** (home, network, login, base, router) 又重复应用了 `maven-publish` 插件并尝试创建同名的 `release` publication
- Gradle 不允许同名的 Publication 存在多次

## 解决方案
移除所有子模块中的重复配置：

### 修改的文件
- `home/build.gradle.kts`
- `network/build.gradle.kts`
- `login/build.gradle.kts`
- `base/build.gradle.kts`
- `router/build.gradle.kts`

### 修改内容
1. **移除插件引用**: `id("maven-publish")` → `// maven-publish 已在根项目配置，无需重复应用`
2. **移除 publishing 块**: 删除整个 `publishing { ... }` 配置
3. **添加注释**: `// publishing 配置已在根项目统一管理`

## 架构说明
```
Components/ (根项目)
├── build.gradle.kts          ← 统一配置 maven-publish
├── home/build.gradle.kts     ← 仅应用 android/kotlin 插件
├── network/build.gradle.kts  ← 仅应用 android/kotlin 插件
├── login/build.gradle.kts    ← 仅应用 android/kotlin 插件
├── base/build.gradle.kts     ← 仅应用 android/kotlin 插件
└── router/build.gradle.kts   ← 仅应用 android/kotlin 插件
```

## 验证
运行 `verify-fix.sh` 脚本确认修复：
```bash
./verify-fix.sh
```

## 第二次修复 (2026-04-01 16:14)

### 新问题
```
A problem occurred configuring project ':Components:base'.
Extension of type 'PublishingExtension' does not exist.
```

### 原因
根项目尝试配置子模块的 `PublishingExtension`，但子模块没有应用 `maven-publish` 插件。

### 解决方案
修改根项目 `build.gradle.kts`，在配置 publishing 之前先给子模块应用插件：

```kotlin
subprojects {
    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            // 应用 maven-publish 插件
            plugins.apply("maven-publish")
            
            extensions.configure<PublishingExtension> { ... }
        }
    }
}
```

## 重新构建
```bash
cd /Users/mohanlv/Desktop/AndroidComponentProject/App
gradle clean build
```

---
修复时间：2026-04-01 16:14
修复人：莫寒慕 🌙
