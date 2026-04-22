#!/bin/bash
set -e

# publish.sh - 组件发布脚本
# 将组件发布到本地 Maven 或 GitHub Packages
# 用法: ./publish.sh [version] [--local] [--github]
# 示例: 
#   ./publish.sh 1.0.0 --local       # 发布到本地 Maven
#   ./publish.sh 1.0.0 --github     # 发布到 GitHub Packages
#   ./publish.sh 1.0.0               # 默认发布到 GitHub

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
GRADLE="$PROJECT_DIR/gradlew"
TOKEN_FILE="$HOME/.github_token"

VERSION=$1
TARGET="github"

# 解析参数
for arg in "$@"; do
    case $arg in
        --local) TARGET="local" ;;
        --github) TARGET="github" ;;
    esac
done

# 读取 version.gradle.kts 中的版本号（如果未指定版本）
if [ -z "$VERSION" ]; then
    VERSION=$(grep 'set("componentVersion"' "$PROJECT_DIR/version.gradle.kts" | sed 's/.*"\(.*\)".*/\1/')
    echo "从 version.gradle.kts 读取版本: $VERSION"
fi

if [ -z "$VERSION" ]; then
    echo "错误: 未指定版本号"
    echo "用法: ./publish.sh [version] [--local|--github]"
    exit 1
fi

# 设置 GitHub Token 环境变量
if [ "$TARGET" = "github" ]; then
    if [ -f "$TOKEN_FILE" ]; then
        export GITHUB_TOKEN=$(cat "$TOKEN_FILE")
    else
        echo "错误: GitHub token 文件不存在: $TOKEN_FILE"
        exit 1
    fi
fi

echo "=========================================="
echo "发布组件 v$VERSION"
echo "目标: $TARGET"
echo "=========================================="

# 组件列表（按依赖顺序）
COMPONENTS=("startup" "router" "network" "base" "login" "home" "reactnative" "user" "logger" "websdk")

for component in "${COMPONENTS[@]}"; do
    echo ""
    echo ">>> 构建 $component ..."

    # 修改 version
    sed -i.bak "s/set(\"componentVersion\", \"[^\"]*\")/set(\"componentVersion\", \"$VERSION\")/" "$PROJECT_DIR/version.gradle.kts"

    # 构建 release
    cd "$PROJECT_DIR"
    "$GRADLE" :${component}:assembleRelease -DcomponentVersion="$VERSION" --no-daemon -q 2>&1 | grep -v "^$"

    if [ $? -ne 0 ]; then
        echo "✗ $component 构建失败"
        cd "$PROJECT_DIR"
        mv "$PROJECT_DIR/version.gradle.kts.bak" "$PROJECT_DIR/version.gradle.kts" 2>/dev/null || true
        exit 1
    fi

    echo ">>> 发布 $component ..."
    
    if [ "$TARGET" = "github" ]; then
        "$GRADLE" :${component}:publishMavenPublicationToGitHubPackagesRepository -DcomponentVersion="$VERSION" --no-daemon -q 2>&1 | grep -v "^$"
    else
        "$GRADLE" :${component}:publishMavenPublicationToLocalMavenRepository -DcomponentVersion="$VERSION" --no-daemon -q 2>&1 | grep -v "^$"
    fi

    if [ $? -eq 0 ]; then
        echo "✓ $component 发布成功"
    else
        echo "✗ $component 发布失败"
        cd "$PROJECT_DIR"
        mv "$PROJECT_DIR/version.gradle.kts.bak" "$PROJECT_DIR/version.gradle.kts" 2>/dev/null || true
        exit 1
    fi

    # 恢复 version.gradle.kts
    cd "$PROJECT_DIR"
    mv "$PROJECT_DIR/version.gradle.kts.bak" "$PROJECT_DIR/version.gradle.kts" 2>/dev/null || true
done

echo ""
echo "=========================================="
echo "发布完成！"
echo "版本: $VERSION"
echo "=========================================="
