# 项目规范

## 项目概述
- 组件化 Android 项目，模块包括 login、user、home、shortvideo
- 各模块独立版本管理，统一发布到 GitHub Packages

## 技术栈
- **Gradle**: AGP 8.5.0，Kotlin 2.2.10
- **Java/Android**: Java 17，compileSdk 35，minSdk 24
- **网络库**: Retrofit 2.9.0 + OkHttp 4.12.0
- **图片库**: Coil 2.5.0
- **路由库**：Router
- **基础组件**: base、network、router、startup、logger、reactnative、websdk

## 常见任务
- 集成：
  - 先判断当前项目是否有develop分支
    - 如果有，则**从 develop 分支发起 PR 来合并到 main 分支**，不能直接 push 到 main。调用github api创建PR, 通过环境变量或配置获取 GitHub token。
    - 如果没有，则把local/develop push到remote/develop，切换到local/main分支，执行git pull，merge origin/develop 到 local/main，最后git push

## 工作模式
- 我说"快速改" → 直接出代码
- 我说"帮我看看" → 只分析问题不改代码
- 涉及新增模块 -> 二次确认
- 不确定业务逻辑 → 问我，不要猜

## 代码规范

### RecyclerView 列表规范
- **Adapter 继承**：`RecyclerView.Adapter<XXXViewHolder>`，禁止使用 `ListAdapter`
- **ViewHolder 写法**：
  - 必须独立成文件，不允许 `inner class`
  - 点击回调通过构造方法传递：`ViewHolder(binding) { }`
  - bind 方法接收完整数据模型，不在 ViewHolder 内持有数据列表
- 禁止新增xml文件写shape，必须使用[ShapeBuilder.kt](common/src/main/java/com/mohanlv/common/ShapeBuilder.kt)
- 禁止新增或修改模块依赖
- layout文件根布局必须使用`ConstraintLayout`