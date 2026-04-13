# Gradle 下载加速 - 已完成配置 🚀

## 测试结果 (2026-04-01 16:30)

| 镜像 | 响应时间 | 状态 |
|------|----------|------|
| 阿里云 Google | 0.085s | ✅ 最快 |
| 官方 Google | 1.26s | ✅ |
| 腾讯云 Gradle | 3.38s | ✅ |
| 官方 Gradle | 1.58s | ✅ (307 重定向) |
| 阿里云 Public | 0.22s | ⚠️ 404 |

## 已完成的优化

### 1. ✅ 全局 Gradle 配置 (`~/.gradle/gradle.properties`)
```properties
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.configureondemand=true
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

### 2. ✅ 项目级镜像配置 (`Components/build.gradle.kts`)
```kotlin
subprojects {
    afterEvaluate {
        repositories {
            maven { url = uri("https://maven.aliyun.com/repository/google") }
            maven { url = uri("https://maven.aliyun.com/repository/public") }
            google()
            mavenCentral()
        }
    }
}
```

### 3. ✅ 插件仓库镜像 (`App/settings.gradle.kts`)
已配置阿里云镜像优先。

### 4. ✅ Gradle Wrapper 镜像 (`App/gradle/wrapper/gradle-wrapper.properties`)
已配置腾讯云镜像。

## 预期效果

- **首次构建**: 依赖下载速度提升 3-5 倍
- **后续构建**: 启用缓存后几乎瞬间完成
- **并行构建**: 多模块编译速度提升 30-50%

## 使用建议

```bash
# 首次构建 (下载依赖)
cd /Users/mohanlv/Desktop/AndroidComponentProject/App
gradle clean build

# 后续构建 (使用缓存)
gradle build

# 强制刷新依赖 (网络好的时候)
gradle build --refresh-dependencies

# 离线模式 (无网络时)
gradle build --offline
```

## 故障排查

如果还是很慢：

1. **测试镜像速度**
   ```bash
   cd Components
   ./speedup-test.sh
   ```

2. **查看 Gradle 缓存**
   ```bash
   du -sh ~/.gradle/caches
   ```

3. **清理缓存重新下载**
   ```bash
   gradle clean
   rm -rf ~/.gradle/caches/modules-2
   ```

---
🌙 莫寒慕 · 2026-04-01
