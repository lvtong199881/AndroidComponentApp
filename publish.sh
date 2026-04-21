#!/bin/bash
set -e

# publish.sh - 组件发布脚本
# 将组件发布到本地 Maven 仓库
# 用法: ./publish.sh [version]
# 示例: ./publish.sh 1.0.0

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
APP_DIR="$PROJECT_DIR/App"
COMPONENTS_DIR="$PROJECT_DIR/Components"
GRADLE="$APP_DIR/gradlew"

VERSION=$1

# 读取 version.gradle.kts 中的版本号（如果未指定版本）
if [ -z "$VERSION" ]; then
    VERSION=$(grep 'set("componentVersion"' "$PROJECT_DIR/version.gradle.kts" | sed 's/.*"\(.*\)".*/\1/')
    echo "从 version.gradle.kts 读取版本: $VERSION"
fi

if [ -z "$VERSION" ]; then
    echo "错误: 未指定版本号"
    echo "用法: ./publish.sh [version]"
    exit 1
fi

echo "=========================================="
echo "发布组件 v$VERSION"
echo "=========================================="

# 组件列表（按依赖顺序）
COMPONENTS=("startup" "router" "network" "base" "login" "home" "reactnative" "user")

for component in "${COMPONENTS[@]}"; do
    echo ""
    echo ">>> 构建 $component ..."

    # 修改 version
    sed -i.bak "s/set(\"componentVersion\", \"[^\"]*\")/set(\"componentVersion\", \"$VERSION\")/" "$PROJECT_DIR/version.gradle.kts"

    # 构建 release（在 Components 目录下执行）
    cd "$COMPONENTS_DIR"
    "$GRADLE" :${component}:assembleRelease --no-daemon -q

    if [ $? -ne 0 ]; then
        echo "✗ $component 构建失败"
        cd "$PROJECT_DIR"
        mv "$PROJECT_DIR/version.gradle.kts.bak" "$PROJECT_DIR/version.gradle.kts"
        exit 1
    fi

    echo ">>> 发布 $component ..."
    "$GRADLE" :${component}:publishMavenPublicationToMavenRepository --no-daemon -q

    if [ $? -eq 0 ]; then
        echo "✓ $component 发布成功"
    else
        echo "✗ $component 发布失败"
        cd "$PROJECT_DIR"
        mv "$PROJECT_DIR/version.gradle.kts.bak" "$PROJECT_DIR/version.gradle.kts"
        exit 1
    fi

    # 恢复 version.gradle.kts
    cd "$PROJECT_DIR"
    mv "$PROJECT_DIR/version.gradle.kts.bak" "$PROJECT_DIR/version.gradle.kts"
done

echo ""
echo "=========================================="
echo "发布完成！"
echo "版本: $VERSION"
echo "本地仓库: ~/.m2/repository/releases"
echo ""
echo "在 App/build.gradle.kts 中使用:"
echo '  implementation("com.mohanlv:base:'"$VERSION"'")'
echo '  implementation("com.mohanlv:reactnative:'"$VERSION"'")'
echo "=========================================="
