# 🚀 快速切换指南

## 当前配置

```bash
cat App/gradle.properties | grep component.sourceDependency
```

## 切换模式

### 🔧 开发模式（源码依赖）

**编辑 `App/gradle.properties`：**
```properties
component.sourceDependency=true
```

**特点：**
- ✅ 可直接调试 Components 源码
- ✅ 修改立即生效
- ✅ 无需发布到 Maven

**验证：**
```bash
cd App
./gradlew dependencies --configuration debugCompileClasspath | grep "com.mohanlv.component"
```
输出应显示 `project :base`, `project :router` 等

---

### 📦 发布模式（Maven 依赖）

**步骤 1：发布组件**
```bash
cd ../Components
./gradlew clean publishToMavenLocal
```

**步骤 2：切换配置**
编辑 `App/gradle.properties`：
```properties
component.sourceDependency=false
```

**步骤 3：Sync Gradle**
- Android Studio 会自动下载依赖
- 或命令行：`./gradlew build`

**特点：**
- ✅ 使用 Maven 仓库版本
- ✅ 版本管理清晰
- ✅ 适合 CI/CD

**验证：**
```bash
./gradlew dependencies --configuration debugCompileClasspath | grep "com.mohanlv.component"
```
输出应显示 `com.mohanlv.component:base:1.0.0` 等

---

## 命令行临时切换

```bash
# 临时使用源码模式
./gradlew assembleDebug -Pcomponent.sourceDependency=true

# 临时使用 Maven 模式
./gradlew assembleDebug -Pcomponent.sourceDependency=false
```

---

## 常见问题

### Q: 切换后 Gradle Sync 失败？
**A:** 清理缓存后重试：
```bash
./gradlew clean
rm -rf .gradle/
# 重新 Sync
```

### Q: 断点不生效？
**A:** 确保是源码模式 (`component.sourceDependency=true`)

### Q: 如何确认当前模式？
**A:** 查看 Gradle Sync 输出：
```
📦 Component Dependency Mode: SOURCE (Debug)
或
📦 Component Dependency Mode: MAVEN (Release)
```

---

**一键切换，开发发布两不误！** 🎯
