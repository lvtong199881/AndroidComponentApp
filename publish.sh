#!/bin/bash
# 发布组件到 GitHub Packages
# 用法: ./publish.sh <module> <version>
# 示例: ./publish.sh startup 1.0.0

set -e

# 设置 Java 17
export JAVA_HOME=/Users/mohanlv/Library/Java/JavaVirtualMachines/corretto-17.0.18-1/Contents/Home

if [ -z "$1" ] || [ -z "$2" ]; then
    echo "用法: ./publish.sh <module> <version>"
    echo "示例: ./publish.sh startup 1.0.0"
    exit 1
fi

MODULE=$1
VERSION=$2

echo ">>> Publishing ${MODULE} v${VERSION}"

./gradlew :${MODULE}:assembleRelease :${MODULE}:publishMavenPublicationToGitHubPackagesRepository -P${MODULE}.version=${VERSION} --no-daemon
