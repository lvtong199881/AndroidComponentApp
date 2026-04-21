# AndroidComponentApp

Android 组件化开发模板工程，支持**源码模式**和 **Maven 模式**一键切换，适合团队协作和模块复用。

---

## 📚 项目结构

```
AndroidComponentApp/
│
├── App/                          # 宿主应用（壳工程）
│   ├── app/                      # 主 Application 模块
│   │   ├── build.gradle.kts
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/mohanlv/app/
│   │       │   ├── AppApplication.kt
│   │       │   └── MainActivity.kt      # Double Click Exit
│   │       └── res/
│   │
│   ├── dependencies-config.gradle.kts  # 🔑 组件依赖配置（核心）
│   ├── settings.gradle.kts
│   └── docs/                     # 踩坑修复文档
│
└── Components/                   # 🔧 可复用组件模块
    │
    ├── base/                     # 基础组件
    │   └── src/main/
    │       ├── java/com/mohanlv/base/
    │       │   ├── base/BaseFragment.kt     # Fragment 基类
    │       │   └── utils/
    │       │       ├── AppUtils.kt          # App 工具类
    │       │       ├── ImageLoader.kt       # Coil 图片加载封装
    │       │       ├── LogUtils.kt          # 日志工具
    │       │       └── SPUtils.kt          # SharedPreferences 封装
    │       └── res/
    │           ├── anim/                   # 页面过渡动画
    │           │   ├── slide_in_right.xml
    │           │   ├── slide_out_left.xml
    │           │   ├── slide_in_left.xml
    │           │   └── slide_out_right.xml
    │           ├── layout/
    │           │   └── item_article.xml     # 共享文章列表项
    │           └── values/colors.xml        # 统一颜色资源
    │
    ├── router/                   # 路由组件（核心）
    │   └── src/main/java/com/mohanlv/router/
    │       ├── RouterManager.kt           # 路由管理器（支持 Bundle 传参）
    │       ├── RoutePath.kt              # 路由路径常量
    │       ├── RouteTable.kt             # ⭐ 编译时自动生成的路由表
    │       └── annotation/Route.kt       # @Route 注解
    │
    ├── network/                   # 网络组件
    │   └── src/main/java/com/mohanlv/network/
    │       ├── NetworkManager.kt          # 网络管理器（单例）
    │       ├── api/ApiService.kt         # Retrofit API 接口（WanAndroid）
    │       ├── model/                   # 数据模型
    │       │   ├── BaseResponse.kt       # 统一响应体
    │       │   ├── Models.kt             # Article、Banner、UserInfo 等
    │       │   └── PageData.kt           # 分页数据
    │       └── interceptor/
    │           ├── HeaderInterceptor.kt  # 请求头拦截器
    │           └── LoggingInterceptor.kt # ⭐ 日志拦截器（一行格式）
    │
    ├── login/                     # 登录组件
    │   └── src/main/java/com/mohanlv/login/
    │       ├── model/LoginResult.kt     # 登录结果
    │       ├── ui/LoginFragment.kt      # 登录页面
    │       └── vm/
    │           ├── LoginViewModel.kt    # 登录 ViewModel
    │           └── LoginState.kt        # 登录状态（单例）
    │
    ├── home/                      # 首页组件
    │   └── src/main/java/com/mohanlv/home/
    │       ├── ui/
    │       │   ├── HomeFragment.kt           # ⭐ Banner 轮播 + 文章列表
    │       │   ├── container/HomeContainerFragment.kt  # Tab 容器 + RN 页面入口
    │       │   └── web/WebFragment.kt        # ⭐ 网页（支持 URL 参数）
    │       └── res/layout/
    │           ├── fragment_home.xml
    │           └── item_banner.xml
    │
    └── reactnative/             # React Native 组件
        └── src/main/java/com/mohanlv/reactnative/
            ├── ui/
            │   ├── RNFragment.kt              # ⭐ React Native 页面容器
            │   └── BundleManagerBottomSheet.kt  # ⭐ Bundle 管理弹窗
            ├── BundleUrl.kt                   # Bundle URL 解析
            ├── BundleConfig.kt                # Bundle 配置
            ├── BundleDownloader.kt             # Bundle 下载器
            ├── ReactNativeHelper.kt           # RN 辅助类（初始化 + Bundle 加载）
            └── ReactNativeStartupTask.kt      # Startup 初始化任务
    │
    └── user/                      # 用户中心组件
        └── src/main/java/com/mohanlv/user/
            ├── ui/
            │   ├── UserFragment.kt       # 个人中心页面
            │   └── CollectFragment.kt    # 我的收藏页面
            └── res/layout/
                ├── fragment_user_center.xml
                └── fragment_collect.xml
```

---

## 🔥 核心能力

### 1. 组件化架构

将业务拆分为独立的组件模块，每个组件可以：
- **独立开发**：单独编译、单独调试
- **独立发布**：发布到 Maven 仓库供其他项目使用
- **按需集成**：只需要哪个组件就引入哪个

### 2. 路由管理（Router）

实现组件间页面的解耦跳转，基于 `@Route` 注解和编译时生成。

**使用方式：**

```kotlin
// 1. 在 Fragment 上添加 @Route 注解
@Route(path = RoutePath.LOGIN, description = "登录页面")
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    // ...
}

// 2. 初始化路由
RouterManager.init(context, containerId = R.id.fragment_container)

// 3. 页面跳转（带 Bundle 参数，过渡动画，自动加入返回栈）
val args = Bundle().apply { putString("url", article.link) }
RouterManager.navigate(RoutePath.WEB_VIEW, args)

// 4. 关闭当前页面
RouterManager.popBackStack()
```

**实现原理：**
- `generate_routes.py` 在编译时扫描所有 `@Route` 注解
- 自动生成 `RouteTable.kt`，包含 path → Fragment 类的映射
- 使用 `Class.forName().newInstance()` 反射创建实例
- 跳转时用 `add()` + `addToBackStack()` 实现叠加效果

**页面过渡动画：**
- 进入：`slide_in_right`（从右滑入，200ms，decelerate）
- 退出：`slide_out_left`（向左滑出，accelerate）
- 返回进入：`slide_in_left`（从左滑入）
- 返回退出：`slide_out_right`（向右滑出）

### 3. 网络封装（Network）

基于 OkHttp + Retrofit + Gson 封装，接入 [WanAndroid API](https://www.wanandroid.com/)。

**功能特性：**
- OkHttp 连接/读取/写入超时 30s 配置
- 自动注入常用请求头（User-Agent、Content-Type、Referer）
- **Cookie 管理**：使用 OkHttp 内置 CookieJar，登录态自动持久化
- **日志打印**：一行格式输出，`[REQ]`/`[RES]`/`[ERR]` 前缀区分
- Gson 自动序列化/反序列化

**日志格式示例：**
```
I/Network: [REQ] GET https://wanandroid.com/lg/collect/list/0/json
I/Network: [RES] 200 89ms https://wanandroid.com/lg/collect/list/0/json

E/Network: [ERR] GET 404 102ms https://wanandroid.com/lg/collect/page/1/json
E/Network:      错误: HttpException: Not Found
E/Network:      堆栈: java.net.HttpRetryException: ...
```

### 4. 首页功能（Home）

- **Banner 轮播**：ViewPager2 自动轮播（3秒切换），深度切换动画，底部指示器
- **文章列表**：RecyclerView + 下拉刷新，点击跳转网页
- **网页查看**：WebFragment 支持 URL 参数传递

### 5. 登录功能

- MVVM 架构（LoginViewModel + LoginState）
- 登录状态全局共享（LoginState 单例）
- 登录成功后 Cookie 自动保存
- 支持从 SPUtils 恢复登录态

### 6. 收藏功能

- 我的收藏页面（CollectFragment）
- 下拉刷新 + 上拉加载更多
- 未登录时提示先登录
- 空状态显示

### 7. Double Click Exit

- MainActivity 双击返回键退出功能
- 2秒内双击确认退出
- 否则提示"再按一次退出"

### 8. React Native 热更新

通过 `RNFragment` 嵌入 React Native 页面，支持从 GitHub Release 下载 Bundle 实现热更新。

**特性：**
- 自动检测最新版本
- Bundle 异步下载，不阻塞主线程
- 本地缓存管理（按仓库/版本存储）
- Bundle 管理弹窗（悬浮按钮打开）

**使用方式：**

```kotlin
// HomeContainerFragment 中点击 RN 页面时
val bundle = Bundle().apply {
    putString("repo", "MyRNApp")
    putString("componentName", "MyRNApp")
}
RNFragment().apply { arguments = bundle }
```

**Bundle URL 格式：**
```
https://github.com/{owner}/{repo}/releases/download/{version}/index.android.bundle
```

**Bundle 存储结构：**
```
{app}/files/react-native/bundles/{repo}/{version}/index.android.bundle
```

### 9. 源码/Maven 双模式切换

修改 `App/gradle.properties`：

```properties
# 源码依赖模式（开发调试用）
component.sourceDependency=true

# Maven 依赖模式（发布/正式环境用）
component.sourceDependency=false
```

---

## 🚀 快速开始

### 环境要求

- **Java**: 17
- **Android Gradle Plugin**: 8.2.0
- **Kotlin**: 2.2.10
- **Gradle**: 8.2
- **Android SDK**: 34

### 运行项目

```bash
cd App
./gradlew assembleDebug
```

### 切换到 Maven 模式

```bash
vim App/gradle.properties
# 设置 component.sourceDependency=false
./gradlew assembleDebug
```

---

## 📦 已集成的组件

| 组件 | 功能 | 状态 |
|------|------|------|
| `base` | BaseFragment、ImageLoader、动画、颜色、共享布局 | ✅ |
| `router` | 路由管理、@Route 注解、编译时生成路由表 | ✅ |
| `network` | OkHttp + Retrofit + Cookie + 日志拦截器 | ✅ |
| `login` | 登录界面、ViewModel、登录状态 | ✅ |
| `home` | Banner 轮播、文章列表、网页查看 | ✅ |
| `user` | 个人中心、我的收藏 | ✅ |
| `reactnative` | React Native 热更新、Bundle 管理弹窗 | ✅ |

---

## 🛠 扩展开发

### 添加新页面

1. 在对应组件的 `ui` 包下创建 Fragment
2. 在 `RoutePath` 中定义路由常量
3. 使用 `@Route(path = "xxx")` 注解标记

```kotlin
@Route(path = RoutePath.COLLECT_LIST, description = "我的收藏")
class CollectFragment : BaseFragment<FragmentCollectBinding>() {
    // ...
}
```

4. 编译后 `RouteTable.kt` 会自动更新

### 页面跳转示例

```kotlin
// 跳转（带 Bundle 参数，默认加入返回栈）
val args = Bundle().apply { putString("url", article.link) }
RouterManager.navigate(RoutePath.WEB_VIEW, args)

// 跳转（不带返回，如首页）
RouterManager.navigate(RoutePath.HOME_CONTAINER, addToBackStack = false)

// 返回
RouterManager.popBackStack()
```

### 网络请求示例

```kotlin
val response = apiService.getArticleList(0)
if (response.isSuccessful) {
    val body = response.body()
    if (body?.isSuccess() == true) {
        val articles = body.data?.datas ?: emptyList()
        // 处理数据
    }
}
```

---

## 📝 WanAndroid API

项目使用 [WanAndroid 开放 API](https://www.wanandroid.com/blog/show/2)，接入真实数据。

**主要接口：**

| 功能 | URL | 方法 |
|------|-----|------|
| 文章列表 | `/article/list/{page}/json` | GET |
| Banner | `/banner/json` | GET |
| 登录 | `/user/login` | POST |
| 收藏列表 | `/lg/collect/list/{page}/json` | GET |
| 收藏文章 | `/lg/collect/add/json` | POST |

---

## 📝 踩坑记录

项目开发过程中遇到的问题和解决方案记录在 `App/docs/` 目录下。

---

## 📄 License

MIT License

---

**🌙 莫寒慕 · lvtong199881**
