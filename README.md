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
│   │       │   └── MainActivity.kt
│   │       └── res/
│   │
│   ├── dependencies-config.gradle.kts  # 🔑 组件依赖配置（核心）
│   ├── settings.gradle.kts
│   └── docs/                     # 踩坑修复文档
│
└── Components/                   # 🔧 可复用组件模块
    ├── base/                     # 基础组件（Base类、工具类）
    │   └── src/main/java/com/mohanlv/base/
    │       ├── base/BaseFragment.kt     # 通用 Fragment 基类（ViewBinding）
    │       └── utils/
    │           ├── AppUtils.kt          # App 工具类
    │           ├── LogUtils.kt          # 日志工具
    │           └── SPUtils.kt           # SharedPreferences 封装
    │
    ├── router/                   # 路由组件
    │   └── src/main/java/com/mohanlv/router/
    │       ├── RouterManager.kt           # 路由管理器（核心）
    │       ├── RoutePath.kt               # 路由路径常量
    │       ├── RouteParams.kt            # 路由参数
    │       └── annotation/Route.kt       # @Route 注解
    │
    ├── network/                   # 网络组件
    │   └── src/main/java/com/mohanlv/network/
    │       ├── NetworkManager.kt          # 网络管理器（单例）
    │       ├── api/ApiService.kt          # Retrofit API 接口
    │       ├── model/BaseResponse.kt      # 统一响应体
    │       ├── interceptor/
    │       │   ├── HeaderInterceptor.kt   # 请求头拦截器
    │       │   └── LoggingInterceptor.kt  # 日志拦截器
    │       └── utils/RequestExtensions.kt # 请求扩展函数
    │
    ├── login/                     # 登录组件
    │   └── src/main/java/com/mohanlv/login/
    │       ├── model/LoginResult.kt
    │       ├── ui/LoginFragment.kt
    │       └── vm/LoginViewModel.kt
    │
    ├── home/                      # 首页组件
    │   └── src/main/java/com/mohanlv/home/
    │       ├── ui/
    │       │   ├── HomeFragment.kt
    │       │   ├── container/HomeContainerFragment.kt
    │       │   └── web/WebFragment.kt
    │       └── res/layout/
    │
    └── user/                      # 用户中心组件
        └── src/main/java/com/mohanlv/user/
            └── ui/UserFragment.kt
```

---

## 🔥 核心能力

### 1. 组件化架构

将业务拆分为独立的组件模块，每个组件可以：
- **独立开发**：单独编译、单独调试
- **独立发布**：发布到 Maven 仓库供其他项目使用
- **按需集成**：只需要哪个组件就引入哪个

```
App (壳工程)
    │
    ├── login  ──→  登录模块
    ├── home   ──→  首页模块
    ├── user   ──→  用户中心模块
    └── router ──→  路由模块（所有组件共用）
```

### 2. 路由管理（Router）

实现组件间页面的解耦跳转，不依赖组件的具体实现。

**使用方式：**

```kotlin
// 1. 在 Fragment 上添加 @Route 注解
@Route(path = "/login/main")
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    // ...
}

// 2. 初始化路由
RouterManager.init(context, containerId = R.id.fragment_container)

// 3. 页面跳转
RouterManager.navigate(RoutePath.LOGIN)

// 4. 获取 Fragment 实例（用于嵌套）
val fragment = RouterManager.getFragment(RoutePath.HOME)
```

**实现原理：**
- 自定义 `@Route` 注解标记需要注册的 Fragment
- `RouterManager.init()` 时通过 **Dex 扫描** 自动发现所有带注解的 Fragment
- 将 path → Fragment 构造器的映射存入 `ConcurrentHashMap`
- 跳转时通过 path 找到对应的 Fragment 实例化并显示

### 3. 网络封装（Network）

基于 OkHttp + Retrofit 封装， 提供统一的网络请求能力。

**使用方式：**

```kotlin
// 1. 初始化（通常在 Application 中）
NetworkManager.init(context, baseUrl = "https://api.example.com/")

// 2. 定义 API 接口
interface ApiService {
    @GET("/user/info")
    suspend fun getUserInfo(): BaseResponse<UserInfo>
}

// 3. 获取 API 实例
val api = NetworkManager.createApi(ApiService::class.java)

// 4. 发起请求
val result = api.getUserInfo()
```

**功能特性：**
- OkHttp 连接/读取超时 30s 配置
- 自动注入常用请求头（User-Agent、Content-Type 等）
- 可配置 Header 拦截器
- 请求/响应日志打印
- Gson 自动序列化/反序列化
- 支持 RetryOnConnectionFailure

### 4. BaseFragment 基类

封装 Fragment 通用逻辑，减少样板代码。

```kotlin
@Route(path = "/home/main")
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        // 初始化视图
    }

    override fun initData() {
        // 加载数据
    }

    override fun initEvent() {
        // 设置点击事件等
    }
}
```

**提供的能力：**
- `inflateBinding()` — 子类实现，返回 ViewBinding 实例
- `initView()` — 视图初始化
- `initData()` — 数据加载
- `initEvent()` — 事件监听设置
- `showLoading() / hideLoading()` — 加载状态 UI
- `showError()` — 错误提示

### 5. 源码/Maven 双模式切换

这是本项目最大的亮点 —— **一行配置切换依赖方式**。

#### 模式说明

| 模式 | 适用场景 | 优点 | 缺点 |
|------|----------|------|------|
| **源码模式** | 开发调试 | 可直接断点调试组件代码 | 编译稍慢 |
| **Maven 模式** | 发布/CI | 编译快，组件独立 | 调试需 publish |

#### 切换方式

修改 `App/gradle.properties`：

```properties
# 源码依赖模式（开发调试用）
component.sourceDependency=true

# Maven 依赖模式（发布/正式环境用）
component.sourceDependency=false
```

#### 实现原理

在 `dependencies-config.gradle.kts` 中通过 `includeBuild` 和 `dependencySubstitution` 实现：

```kotlin
if (componentSourceDependency) {
    // 源码模式：直接依赖 Components 目录下的模块
    includeBuild("../Components") {
        dependencySubstitution {
            substitute(module("com.mohanlv.component:base")).using(project(":base"))
            substitute(module("com.mohanlv.component:router")).using(project(":router"))
            // ...
        }
    }
} else {
    // Maven 模式：从本地 Maven 仓库读取组件
    dependencyResolutionManagement {
        repositories {
            mavenLocal()
            // 阿里云镜像加速
            maven { url = uri("https://maven.aliyun.com/repository/google") }
            maven { url = uri("https://maven.aliyun.com/repository/public") }
        }
    }
}
```

#### 发布组件到 Maven

```bash
cd Components
./gradlew publishToMavenLocal
```

发布后，其他项目只需修改 groupId、artifactId、version 即可引用。

---

## 🚀 快速开始

### 环境要求

- **Java**: 17
- **Android Gradle Plugin**: 8.2.0
- **Kotlin**: 2.2.10
- **Gradle**: 8.2
- **Android SDK**: Build Tools 30.0.3 +

### 运行项目

```bash
cd App
./gradlew assembleDebug
```

或直接用 Android Studio 打开 `App` 目录，同步后运行。

### 切换到 Maven 模式

```bash
# 编辑配置文件
vim App/gradle.properties

# 确保有这一行
component.sourceDependency=false

# 重新 Sync 项目
# Android Studio: File → Sync Project with Gradle Files
```

---

## 📦 已集成的组件

| 组件 | 功能 | 依赖情况 |
|------|------|----------|
| `base` | BaseFragment、工具类 | 基础组件，所有组件共享 |
| `router` | 路由管理、@Route 注解 | 基础组件，App 和所有业务组件共享 |
| `network` | OkHttp + Retrofit 封装 | 基础组件，需要网络的功能组件共享 |
| `login` | 登录界面 + ViewModel | 业务组件 |
| `home` | 首页 Tab 容器 | 业务组件 |
| `user` | 个人中心 | 业务组件 |

---

## 🛠 扩展开发

### 添加新组件

1. 在 `Components/` 下创建新模块目录
2. 创建 `build.gradle.kts` 引入基础组件
3. 在 `App/dependencies-config.gradle.kts` 中注册
4. 使用 `@Route` 注解标记 Fragment

**示例：新建 `profile` 组件**

```kotlin
// Components/profile/build.gradle.kts
dependencies {
    implementation(project(":base"))
    implementation(project(":router"))
    implementation(project(":network"))
}
```

```kotlin
// 在 dependencies-config.gradle.kts 中添加
substitute(module("$componentGroupId:profile")).using(project(":profile"))
```

### 添加新页面

1. 在对应组件的 `ui` 包下创建 Fragment
2. 在 `RoutePath` 中定义路由常量
3. 使用 `@Route(path = "xxx")` 注解标记

```kotlin
@Route(path = "/profile/main")
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    // ...
}
```

---

## 📝 踩坑记录

项目开发过程中遇到的问题和解决方案记录在 `App/docs/` 目录下：

| 文档 | 内容 |
|------|------|
| `GRADLE_SETUP.md` | Java 17 配置 |
| `GRADLE_SPEEDUP_SUMMARY.md` | Maven 仓库加速（阿里云镜像） |
| `DEPENDENCIES_CONFIG_FIX.md` | 组件依赖配置脚本修复 |
| `FIX_OFFLINE_MODE.md` | Gradle 离线模式问题 |
| `QUICK_SWITCH.md` | 源码/Maven 模式切换 |
| `MIRRORS.md` | 国内镜像配置 |

---

## 📄 License

MIT License

---

**🌙 莫寒慕 · lvtong199881**
