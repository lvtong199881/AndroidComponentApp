# AndroidComponentProject

Android 组件化开发组件库，提供 10 个可复用组件模块，已发布至 GitHub Packages。

---

## 📦 组件列表

| 模块 | 说明 |
|------|------|
| **startup** | 启动框架，InitTask 注解 + SPI 机制，组件启动任务自动发现 |
| **base** | 基础组件，BaseFragment、BaseDialogFragment、SPUtils 等基础类 |
| **logger** | 日志模块，后台异步写文件 + Logcat 同步打印 |
| **network** | 网络组件，OkHttp + Retrofit + Gson，接入 WanAndroid API |
| **router** | 路由组件，@Route 注解 + KAPT 生成路由表，组件间完全解耦 |
| **login** | 登录组件，LoginState 单例，全局共享登录态，支持页面监听 |
| **home** | 首页组件，Banner 轮播 + 文章列表 |
| **user** | 用户中心，积分信息 + 收藏文章 |
| **websdk** | WebView 组件，支持加载网页 |
| **reactnative** | React Native 组件，支持嵌入 RN 页面和 Bundle 热更新 |

---

## 📁 项目结构

```
AndroidComponentProject/
├── startup/                     # 启动框架
├── base/                        # 基础组件
├── logger/                      # 日志模块
├── network/                     # 网络组件
├── router/                      # 路由组件
├── login/                       # 登录组件
├── home/                        # 首页组件
├── user/                        # 用户中心
├── websdk/                      # WebView 组件
├── reactnative/                 # React Native 组件
│
├── publish.sh                   # 发布脚本
├── version.gradle.kts           # 版本配置
└── settings.gradle.kts          # 模块配置
```

---

## 🚀 构建

```bash
# 构建单个组件
./gradlew :startup:assembleRelease

# 构建所有组件
./gradlew assembleRelease
```

---

## 📦 发布到 Maven

```bash
# 发布到本地
./publish.sh 1.0.0 --local

# 发布到 GitHub Packages
./publish.sh 1.0.0 --github
```

---

## 🔧 使用组件

在宿主项目中添加依赖：

```kotlin
dependencies {
    implementation("com.mohanlv.component:router:1.0.4")
    implementation("com.mohanlv.component:network:1.0.4")
    implementation("com.mohanlv.component:login:1.0.4")
    // ...
}
```

---

## 📝 API

项目接入 [WanAndroid 开放 API](https://www.wanandroid.com/blog/show/2) 提供真实数据。

---

## 📄 License

MIT License

---

**🌙 莫寒慕 · lvtong199881**
