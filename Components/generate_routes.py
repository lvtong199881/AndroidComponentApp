#!/usr/bin/env python3
"""
路由表自动生成脚本
在编译前运行，扫描所有 @Route 注解并生成 RouteTable.kt
运行方式：python generate_routes.py
"""
import os
import re
import sys

def generate_routes():
    base_dir = os.path.dirname(os.path.abspath(__file__))
    os.chdir(base_dir)
    
    routes = []
    
    # 首先解析 RoutePath.kt 获取常量映射
    route_constants = {}
    routepath_file = './router/src/main/java/com/mohanlv/router/RoutePath.kt'
    if os.path.exists(routepath_file):
        with open(routepath_file, 'r', encoding='utf-8') as f:
            content = f.read()
        # 匹配 const val XXX = "yyy" 或 const val XXX = "scheme://path"
        for m in re.finditer(r'const\s+val\s+(\w+)\s*=\s*"([^"]+)"', content):
            const_name = m.group(1)
            const_value = m.group(2)
            route_constants[const_name] = const_value
    
    print(f"Found {len(route_constants)} route constants")
    
    def resolve_path(value):
        """解析路径值，可能是直接字符串或常量引用"""
        if value.startswith('/') or '://' in value:
            return value
        if '.' in value:
            const_name = value.split('.')[-1]
            return route_constants.get(const_name, value)
        return value
    
    def build_full_class_name(filepath, class_name):
        """从文件路径和类名构建完整的全限定类名"""
        # 查找 src/main/java 或 src/main/kotlin 之后的路径
        marker = None
        for m in re.finditer(r'src[/\\](?:main|test)[/\\](?:java|kotlin)', filepath):
            marker = m.end()
            break
        
        if marker:
            pkg_path = filepath[marker:].lstrip('./').lstrip('.')
            pkg_path = pkg_path.replace('/', '.').replace('\\', '.')
            if pkg_path.endswith('.kt'):
                pkg_path = pkg_path[:-3]
            if pkg_path.endswith('.' + class_name):
                return pkg_path
            return f"{pkg_path}.{class_name}"
        
        parts = filepath.split(os.sep)
        if parts and parts[-1].endswith('.kt'):
            parts = parts[:-1]
        return '.'.join(parts) + '.' + class_name
    
    def extract_internal_path(full_path):
        """从完整路径提取内部路径（去掉 scheme）"""
        if '://' in full_path:
            return full_path.split('://', 1)[1]
        return full_path
    
    # 扫描所有 Kotlin 源文件
    for root, dirs, files in os.walk('.'):
        # 跳过不必要的目录
        dirs[:] = [d for d in dirs if d not in ['build', '.gradle', '.git', '.idea', 'build.gradle.kts']]
        
        for file in files:
            if file.endswith('.kt'):
                filepath = os.path.join(root, file)
                try:
                    with open(filepath, 'r', encoding='utf-8') as f:
                        content = f.read()
                except:
                    continue
                
                # 查找 @Route 注解 - 支持多种格式
                # @Route(path = RoutePath.HOME)
                # @Route(path = "/home/main")
                # @Route(path = "oneandroid://home/main")
                pattern = r'@Route\s*\(\s*path\s*=\s*([^"\s,\)]+)(?:\s*,\s*description\s*=\s*"([^"]*)")?\s*\)'
                
                for match in re.finditer(pattern, content):
                    raw_path = match.group(1).strip()
                    desc = match.group(2) or ""
                    
                    # 解析路径（可能是常量引用）
                    full_path = resolve_path(raw_path)
                    
                    # 提取内部路径
                    internal_path = extract_internal_path(full_path)
                    
                    # 跳过无效路径（需要是有效的主机/路径格式）
                    if not internal_path or '/' not in internal_path:
                        continue
                    
                    # 查找类名 - 注解下面的类
                    end = min(len(content), match.end() + 300)
                    context = content[match.end():end]
                    
                    m = re.search(r'(?:class|object)\s+(\w+)', context)
                    if not m:
                        continue
                    class_name = m.group(1)
                    
                    full_class = build_full_class_name(filepath, class_name)
                    routes.append((internal_path, full_class, desc))
    
    # 去重（按路径）
    seen = set()
    unique_routes = []
    for r in routes:
        if r[0] not in seen:
            seen.add(r[0])
            unique_routes.append(r)
    
    routes = unique_routes
    
    # 生成 RouteTable.kt
    lines = []
    lines.append('package com.mohanlv.router')
    lines.append('')
    lines.append('import androidx.fragment.app.Fragment')
    lines.append('')
    lines.append('/**')
    lines.append(' * Auto-generated route table by compile-time scanning')
    lines.append(' * DO NOT EDIT MANUALLY')
    lines.append(' * Generated at: ' + os.popen('date').read().strip())
    lines.append(' */')
    lines.append('object RouteTable {')
    lines.append('')
    lines.append('    fun registerAll(manager: RouterManager) {')
    
    for path, cls, _ in routes:
        lines.append(f'        manager.registerInternal("{path}") {{ Class.forName("{cls}").newInstance() as Fragment }}')
    
    lines.append('    }')
    lines.append('}')
    lines.append('')
    
    output = '\n'.join(lines)
    
    # 写入文件
    output_dir = 'router/src/main/java/com/mohanlv/router'
    os.makedirs(output_dir, exist_ok=True)
    output_file = os.path.join(output_dir, 'RouteTable.kt')
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(output)
    
    print(f"Generated {len(routes)} routes to {output_file}:")
    for path, cls, _ in routes:
        print(f"  {path} -> {cls}")
    
    return len(routes)

if __name__ == '__main__':
    try:
        count = generate_routes()
        sys.exit(0)
    except Exception as e:
        print(f"Error: {e}")
        sys.exit(1)
