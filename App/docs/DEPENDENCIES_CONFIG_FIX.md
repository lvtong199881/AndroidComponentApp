# Dependencies Config 错误修复

## 错误信息
```
Script 'dependencies-config.gradle.kts' line: 4
Cannot query the value of this provider because it has no value available.
```

## 根本原因

`dependencies-config.gradle.kts` 第 4 行使用 `providers.gradleProperty("xxx").get()` 获取 Gradle 属性：

```kotlin
val componentSourceDependency = providers.gradleProperty("component.sourceDependency").get().toBoolean()
```

**问题：** 当 `gradle.properties` 中没有定义该属性时，provider 没有值，调用 `.get()` 会抛出 `MissingValueException`。

## 解决方案

### 1. 修改 `dependencies-config.gradle.kts` ✅

使用 `.orNull` 和 Elvis 运算符提供默认值：

```kotlin
// 修复前 (会抛出异常)
val componentSourceDependency = providers.gradleProperty("component.sourceDependency").get().toBoolean()

// 修复后 (安全获取，提供默认值)
val componentSourceDependency = providers.gradleProperty("component.sourceDependency").orNull?.toBoolean() ?: true
val componentVersion = providers.gradleProperty("component.version").orNull ?: "1.0.0"
val componentGroupId = providers.gradleProperty("component.groupId").orNull ?: "com.mohanlv.component"
```

### 2. 添加 `gradle.properties` 配置 ✅

在 `App/gradle.properties` 中添加：

```properties
# 组件依赖模式
component.sourceDependency=true
component.groupId=com.mohanlv.component
component.version=1.0.0
```

## 配置说明

| 属性 | 默认值 | 说明 |
|------|--------|------|
| `component.sourceDependency` | `true` | `true`=源码依赖模式（开发调试）<br>`false`=Maven 依赖模式（发布/CI） |
| `component.groupId` | `com.mohanlv.component` | Maven 坐标 groupId |
| `component.version` | `1.0.0` | Maven 坐标 version |

## 依赖模式对比

### 源码依赖模式 (`component.sourceDependency=true`)
- ✅ 使用 `../Components` 目录的源码
- ✅ 实时编译，调试方便
- ✅ 适合开发阶段

### Maven 依赖模式 (`component.sourceDependency=false`)
- ✅ 使用 Maven Local 仓库的二进制包
- ✅ 编译速度快
- ✅ 适合 CI/CD 和发布

## 验证

```bash
cd /Users/mohanlv/Desktop/AndroidComponentProject/App

# 查看当前配置
cat gradle.properties | grep component

# 重新 Sync
gradle build --dry-run
```

## 示例配置

### 开发调试
```properties
component.sourceDependency=true
```

### 发布测试
```properties
component.sourceDependency=false
component.version=1.0.0
```

## 参考文件

- `gradle.properties` - 当前配置
- `gradle.properties.example` - 完整示例配置
- `dependencies-config.gradle.kts` - 依赖配置脚本

---
🌙 莫寒慕 · 2026-04-01
