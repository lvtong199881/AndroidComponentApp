# AndroidComponentProject

Android 组件化开发组件库，包含 10 个可复用组件，已发布至 GitHub Packages。

---

## 🎯 核心能力

| 能力 | 说明 |
|------|------|
| **组件化架构** | 10 个独立组件（startup、router、network、base、login、home、user、reactnative、logger、websdk） |
| **路由管理** | `@Route` 注解 + KAPT 生成路由表，组件间完全解耦 |
| **网络封装** | OkHttp + Retrofit + Gson，接入 WanAndroid 真实 API，支持 Cookie 自动管理 |
| **登录状态** | LoginState 单例，全局共享登录态，支持页面监听登录状态变化 |
| **React Native** | RNFragment 嵌入原生页面，支持从 GitHub Release 下载 Bundle 热更新 |
| **启动框架** | InitTask 注解 + SPI 机制，组件启动任务自动发现 |
| **日志模块** | LoggerWriter 后台异步写文件 + Logcat 同步打印 |

---

## 📁 项目结构

```
AndroidComponentProject/              # 组件库（直接是各模块）
├── startup/                         # 启动框架
├── router/                         # 路由组件
├── network/                        # 网络组件
├── base/                           # 基础组件
├── login/                          # 登录组件
├── home/                           # 首页组件
├── user/                           # 用户中心
├── reactnative/                    # React Native 组件
├── logger/                         # 日志组件
└── websdk/                         # WebView 组件
```

**配套宿主壳工程：** [ComponentHostApp](https://github.com/lvtong199881/ComponentHostApp)

---

## 🚀 快速开始

**环境要求：** Java 17 / Android Gradle Plugin 8.2.0 / Kotlin 2.2.10 / Android SDK 34

```bash
./gradlew :startup:assembleRelease
```

---

## 📦 路由使用

```kotlin
// 1. 页面添加 @Route 注解
@Route(path = RoutePath.LOGIN, description = "登录页面")
class LoginFragment : BaseFragment()

// 2. 页面跳转（支持 Bundle 参数）
val args = Bundle().apply { putString("url", article.link) }
RouterManager.navigate(RoutePath.WEB_VIEW, args)

// 3. 返回
RouterManager.popBackStack()
```

---

## 🌐 网络请求

```kotlin
val response = apiService.getArticleList(0)
if (response.isSuccessful) {
    val body = response.body()
    if (body?.isSuccess() == true) {
        val articles = body.data?.datas ?: emptyList()
    }
}
```

---

## 📦 已发布组件

组件已发布到 GitHub Packages（groupId: `com.mohanlv.component`）：

| 组件 | 版本 |
|------|------|
| startup、router、network、base | 1.0.4 |
| login、home、reactnative、user | 1.0.4 |
| logger、websdk | 1.0.4 |

引入方式（在宿主项目中）：

```kotlin
// settings.gradle.kts
val modulesFile = file("modules.json")
val modulePaths = mutableMapOf<String, String>()
// ...

// build.gradle.kts
dependencies {
    implementation("com.mohanlv.component:router:1.0.4")
    implementation("com.mohanlv.component:network:1.0.4")
    // ...
}
```

---

## 🔧 模块配置

在 `modules.json` 中配置各模块的源码路径（用于开发调试）：

```json
{
  "base": "../AndroidComponentProject/base",
  "router": "../AndroidComponentProject/router",
  "network": "../AndroidComponentProject/network"
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
