# 项目规范

## 项目概述
- 组件化 Android 项目，模块包括 login、user、home、shortvideo
- 各模块独立版本管理，统一发布到 GitHub Packages

## 技术栈
- **Gradle**: AGP 8.5.0，Kotlin 2.2.10
- **Java/Android**: Java 17，compileSdk 35，minSdk 24
- **网络库**: Retrofit 2.9.0 + OkHttp 4.12.0
- **图片库**: Coil 2.5.0
- **基础组件**: base、network、router、startup、logger、reactnative、websdk

## 常见任务
- 集成：
  - 先判断当前项目是否有develop分支
    - 如果有，则**从 develop 分支发起 PR 来合并到 main 分支**，不能直接 push 到 main。创建 PR 使用 token（通过环境变量或配置获取 GitHub token）来发起。
    - 如果没有，则把local/develop push到remote/develop，切换到local/main分支，执行git pull，merge origin/develop 到 local/main，最后git push

## 工作模式
- 我说"快速改" → 直接出代码
- 我说"帮我看看" → 只分析问题不改代码
- 涉及新增模块 -> 二次确认
- 不确定业务逻辑 → 问我，不要猜