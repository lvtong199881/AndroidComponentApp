# AndroidComponentApp

Android 组件化开发模板工程，支持**源码模式**和 **Maven 模式**一键切换，适合团队协作和模块复用。

---

## 🎯 核心能力

| 能力 | 说明 |
|------|------|
| **组件化架构** | 7 个独立组件（base、router、network、login、home、user、reactnative），支持源码/Maven 双模式切换 |
| **路由管理** | `@Route` 注解 + 编译时生成路由表，组件间完全解耦 |
| **网络封装** | OkHttp + Retrofit + Gson，接入 WanAndroid 真实 API，支持 Cookie 自动管理 |
| **登录状态** | LoginState 单例，全局共享登录态，支持页面监听登录状态变化 |
| **React Native** | RNFragment 嵌入原生页面，支持从 GitHub Release 下载 Bundle 热更新 |
| **首页** | Banner 轮播 + 文章列表 + 网页查看 |

---

## 📁 项目结构

```
AndroidComponentApp/
├── App/                          # 宿主应用（壳工程）
└── Components/                   # 7 个可复用组件模块
    ├── base/                     # 基础组件
    ├── router/                   # 路由组件
    ├── network/                  # 网络组件
    ├── login/                    # 登录组件
    ├── home/                     # 首页组件
    ├── user/                     # 用户中心
    └── reactnative/              # React Native 组件
```

---

## 🚀 快速开始

**环境要求：** Java 17 / Android Gradle Plugin 8.2.0 / Kotlin 2.2.10 / Android SDK 34

```bash
cd App
./gradlew assembleDebug
```

**切换 Maven 模式：** 修改 `App/gradle.properties`：

```properties
component.sourceDependency=false
```

---

## 📦 路由使用

```kotlin
// 1. 页面添加 @Route 注解
@Route(path = RoutePath.LOGIN, description = "登录页面")
class LoginFragment : BaseFragment<FragmentLoginBinding>()

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

组件已发布到 GitHub Packages（groupId: `com.mohanlv`）：

| 组件 | 版本 |
|------|------|
| startup、router、network、base | 0.0.5 |
| login、home、reactnative、user | 0.0.5 |

引入方式：

```kotlin
dependencies {
    implementation("com.mohanlv:router:0.0.5")
    implementation("com.mohanlv:network:0.0.5")
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
