#!/usr/bin/env python3
import os
import re

routes = []

# 首先解析 RoutePath.kt 获取常量映射
route_constants = {}
routepath_file = './router/src/main/java/com/mohanlv/router/RoutePath.kt'
if os.path.exists(routepath_file):
    with open(routepath_file, 'r', encoding='utf-8') as f:
        content = f.read()
    # 匹配 const val XXX = "yyy"
    for m in re.finditer(r'const\s+val\s+(\w+)\s*=\s*"([^"]+)"', content):
        route_constants[m.group(1)] = m.group(2)

def resolve_path(value):
    """解析路径值，可能是直接字符串或常量引用"""
    if value.startswith('/'):
        return value
    if '.' in value:
        const_name = value.split('.')[-1]
        return route_constants.get(const_name, value)
    return value

def build_full_class_name(filepath, class_name):
    """从文件路径和类名构建完整的全限定类名"""
    # 查找 src/main/java 或 src/main/kotlin 之后的路径
    marker = None
    for m in re.finditer(r'src[/\\](?:main|test)[/\\](java|kotlin)', filepath):
        marker = m.end()
        break
    
    if marker:
        # 提取 src/main/java 之后的路径
        pkg_path = filepath[marker:].lstrip('./').lstrip('.')
        # 转换路径分隔符为点
        pkg_path = pkg_path.replace('/', '.').replace('\\', '.')
        # 移除 .kt 文件扩展名
        if pkg_path.endswith('.kt'):
            pkg_path = pkg_path[:-3]
        # pkg_path 现在是完整的 包.类名，class_name 应该是末尾的类名
        # 如果 pkg_path 以 class_name 结尾，说明已经是完整路径了
        if pkg_path.endswith('.' + class_name):
            return pkg_path
        # 否则拼接
        return f"{pkg_path}.{class_name}"
    
    # 回退方案
    parts = filepath.split(os.sep)
    if parts and parts[-1].endswith('.kt'):
        parts = parts[:-1]
    return '.'.join(parts) + '.' + class_name

# 扫描所有 Kotlin 源文件
for root, dirs, files in os.walk('.'):
    # 跳过不必要的目录
    dirs[:] = [d for d in dirs if d not in ['build', '.gradle', '.git', 'router-processor', '.idea']]
    
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # 查找 @Route 注解
            pattern = r'@Route\s*\(\s*path\s*=\s*"?([^"\s,\)]+)"?(?:\s*,\s*description\s*=\s*"([^"]*)")?\s*\)'
            
            for match in re.finditer(pattern, content):
                path = match.group(1)
                desc = match.group(2) or ""
                
                # 查找类名 - 注解下面的类
                end = min(len(content), match.end() + 200)
                context = content[match.end():end]
                
                m = re.search(r'(?:class|object)\s+(\w+)', context)
                if not m:
                    continue
                class_name = m.group(1)
                
                resolved_path = resolve_path(path)
                if not resolved_path.startswith('/'):
                    continue
                
                full_class = build_full_class_name(filepath, class_name)
                routes.append((resolved_path, full_class, desc))

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
os.makedirs('router/src/main/java/com/mohanlv/router', exist_ok=True)
with open('router/src/main/java/com/mohanlv/router/RouteTable.kt', 'w') as f:
    f.write(output)

print(f"Generated {len(routes)} routes:")
for path, cls, _ in routes:
    print(f"  {path} -> {cls}")
