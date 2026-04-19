# React Native 组件

通用 React Native 页面加载组件，可在 Android 原生应用中加载任意 React Native 组件。

## 使用方式

### 1. JS 侧注册组件

在你的 React Native 项目 `index.js` 中：

```javascript
import { AppRegistry } from 'react-native';
import App from './App';

// 注册应用，第二个参数必须与 Android 侧传递的 componentName 一致
AppRegistry.registerComponent('MyRNComponent', () => App);
```

### 2. 从 Android 跳转

```kotlin
val args = Bundle().apply {
    putString("componentName", "MyRNComponent")  // 必填，JS 侧注册的组件名
    putString("appKey", "MyApp")  // 可选，默认 "MyApp"
    // 可以添加任意自定义属性，会传递给 RN 组件
    putString("routeParam", "value")
}
RouterManager.navigate(RoutePath.RN, args)
```

### 3. 路由路径

```kotlin
RoutePath.RN  // "/common/rn"
```

## JS Bundle 配置

### Debug 模式（从 Metro 加载）

需要额外配置以支持从 Metro 开发服务器加载。这需要在 `Application` 中初始化 React Native 开发服务器。

### Release 模式（从本地 Bundle 加载）

1. 在 React Native 项目根目录执行：

```bash
npx react-native bundle \
  --platform android \
  --dev false \
  --entry-file index.js \
  --bundle-output android/app/src/main/assets/index.android.bundle \
  --assets-dest android/app/src/main/res/
```

2. 将生成的 `index.android.bundle` 复制到：

```
Components/reactnative/src/main/assets/index.android.bundle
```

## 项目结构

```
reactnative/
├── src/main/
│   ├── java/com/mohanlv/reactnative/
│   │   └── ui/
│   │       └── RNFragment.kt          # React Native 页面容器
│   ├── res/layout/
│   │   └── fragment_rn.xml            # Fragment 布局
│   └── assets/                         # JS Bundle 放这里
│       └── index.android.bundle        # RN 入口 Bundle
├── build.gradle.kts
└── README.md
```

## RNFragment 参数说明

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `componentName` | String | ✅ | JS 侧注册的组件名 |
| `appKey` | String | ❌ | 应用名，默认 "MyApp" |
| 其他参数 | 任意 | ❌ | 会作为 props 传递给 RN 组件 |

## 示例

### 跳转到 RN 页面

```kotlin
// 简单跳转
val args = Bundle().apply {
    putString("componentName", "MyRNComponent")
    putString("title", "Hello RN")
}
RouterManager.navigate(RoutePath.RN, args)

// 关闭 RN 页面
RouterManager.popBackStack()
```

## 注意事项

1. **组件名必须匹配**：Android 侧传递的 `componentName` 必须与 JS 侧 `AppRegistry.registerComponent` 的第一个参数一致
2. **Lifecycle 管理**：RNFragment 自动处理 React Native 的生命周期
3. **Bundle 加载**：Release 模式从本地 assets 加载
4. **Props 传递**：除了 `componentName` 和 `appKey`，其他参数都会传递给 RN 组件

## 依赖版本

- React Native: 0.76.9
