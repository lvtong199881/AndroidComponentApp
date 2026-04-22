#!/usr/bin/env python3
"""
启动任务表自动生成脚本
在编译前运行，扫描所有 @InitTask 注解并生成 StartupTable.kt
使用反射实例化，避免 startup 模块依赖其他业务模块
运行方式：python generate_startup.py
"""
import os
import re
import sys

def generate_startup_table():
    base_dir = os.path.dirname(os.path.abspath(__file__))
    os.chdir(base_dir)
    
    tasks = []  # (key, priority, full_class, params)
    
    # 扫描所有 Kotlin 源文件
    for root, dirs, files in os.walk('.'):
        dirs[:] = [d for d in dirs if d not in ['build', '.gradle', '.git', '.idea', 'node_modules']]
        
        for file in files:
            if not file.endswith('.kt'):
                continue
            
            filepath = os.path.join(root, file)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    content = f.read()
            except:
                continue
            
            # 查找 @InitTask 注解
            pattern = r'@InitTask\s*\(\s*(?:key\s*=\s*)?["\']([^"\',\s]+)["\'](?:[^)]*\bpriority\s*=\s*(\d+))?'
            
            for match in re.finditer(pattern, content):
                task_key = match.group(1)
                priority_str = match.group(2) or "0"
                priority = int(priority_str)
                
                # 查找注解下面的类名
                end_pos = min(len(content), match.end() + 500)
                context = content[match.end():end_pos]
                
                class_match = re.search(r'(?:class|object)\s+(\w+)', context)
                if not class_match:
                    continue
                class_name = class_match.group(1)
                
                # 在类定义中查找构造函数参数列表
                search_start = max(0, match.start() - 50)
                search_end = min(len(content), match.start() + 1500)
                class_area = content[search_start:search_end]
                
                # 找构造函数参数
                constr_match = re.search(
                    r'constructor\s*\(([\s\S]*?)\)\s*(?::|{)',
                    class_area
                )
                
                if not constr_match:
                    constr_match = re.search(
                        r'class\s+\w+\s*<[^>]*>\s*\(([\s\S]*?)\)|'
                        r'class\s+\w+\s*\(([\s\S]*?)\)',
                        class_area
                    )
                    param_str = constr_match.group(1) if constr_match.group(1) is not None else ""
                else:
                    param_str = constr_match.group(1)
                
                params = []
                if param_str:
                    param_pattern = re.compile(
                        r'(?:(?:private|public|protected|internal|val|var)\s+)*(\w+)\s*:\s*([^\s,]+(?:\s*\*\s*[^\s,]+)*)',
                        re.MULTILINE
                    )
                    for pm in param_pattern.finditer(param_str):
                        param_name = pm.group(1)
                        param_type = pm.group(2).strip().rstrip('?').strip()
                        params.append((param_name, param_type))
                
                # 构建完整类名
                marker = None
                for m in re.finditer(r'src[/\\](?:main|test)[/\\](?:java|kotlin)', filepath):
                    marker = m.end()
                    break
                
                if marker:
                    pkg_path = filepath[marker:].lstrip('./').lstrip('.')
                    pkg_path = pkg_path.replace('/', '.').replace('\\', '.')
                    if pkg_path.endswith('.kt'):
                        pkg_path = pkg_path[:-3]
                    full_class = f"{pkg_path}.{class_name}" if not pkg_path.endswith(class_name) else pkg_path
                else:
                    full_class = class_name
                
                tasks.append((task_key, priority, full_class, params))
    
    # 去重（按 key）
    seen = set()
    unique_tasks = []
    for t in tasks:
        if t[0] not in seen:
            seen.add(t[0])
            unique_tasks.append(t)
    
    tasks = unique_tasks
    tasks.sort(key=lambda x: x[1])
    
    # 生成 StartupTable.kt（反射版本，避免 startup 依赖其他模块）
    lines = []
    lines.append('package com.mohanlv.startup')
    lines.append('')
    lines.append('import android.app.Application')
    lines.append('')
    lines.append('/**')
    lines.append(' * 自动生成的启动任务表')
    lines.append(' * 由 generate_startup.py 在编译前自动生成')
    lines.append(' * 使用反射实例化，避免 startup 模块依赖其他业务模块')
    lines.append(' */')
    lines.append('object StartupTable {')
    lines.append('')
    lines.append('    /**')
    lines.append('     * 收集所有任务实例，按 priority 排序')
    lines.append('     */')
    lines.append('    fun collectTasks(application: Application, extraParams: Map<String, Any> = emptyMap()): List<StartupTask> {')
    lines.append('        val tasks = mutableListOf<StartupTask>()')
    lines.append('')
    
    for task_key, priority, full_class, params in tasks:
        lines.append(f'        // {full_class} (key={task_key}, priority={priority})')
        
        # 根据参数类型决定调用哪个 helper
        has_int_param = any(p[1] in ('Int', 'Integer') for p in params)
        has_other_param = any(p[1] not in ('Int', 'Integer', 'Application') for p in params)
        
        if has_other_param:
            # 有其他类型参数，生成带完整参数映射的调用
            arg_lines = []
            for param_name, param_type in params:
                if param_type == 'Application':
                    arg_lines.append(f'            application')
                elif param_type in ('Int', 'Integer'):
                    arg_lines.append(f'            (extraParams["{param_name}"] as? Int) ?: 0')
                elif param_type == 'String':
                    arg_lines.append(f'            (extraParams["{param_name}"] as? String) ?: ""')
                elif param_type == 'Boolean':
                    arg_lines.append(f'            (extraParams["{param_name}"] as? Boolean) ?: false')
                else:
                    short_type = param_type.split('.')[-1]
                    arg_lines.append(f'            (extraParams["{param_name}"] as? {short_type}) ?: throw IllegalArgumentException("Missing param: {param_name}")')
            
            lines.append(f'        tasks.add(instantiate("{full_class}", application, mapOf(')
            for param_name, param_type in params:
                if param_type != 'Application':
                    if param_type in ('Int', 'Integer'):
                        lines.append(f'            "{param_name}" to ((extraParams["{param_name}"] as? Int) ?: 0),')
                    elif param_type == 'String':
                        lines.append(f'            "{param_name}" to ((extraParams["{param_name}"] as? String) ?: ""),')
                    elif param_type == 'Boolean':
                        lines.append(f'            "{param_name}" to ((extraParams["{param_name}"] as? Boolean) ?: false),')
                    else:
                        short_type = param_type.split('.')[-1]
                        lines.append(f'            "{param_name}" to (extraParams["{param_name}"] as? {short_type}) ?: throw IllegalArgumentException("Missing param: {param_name}"),')
            lines.append('        )))')
        elif has_int_param:
            # 有 Int 参数
            int_param_name = next(p[0] for p in params if p[1] in ('Int', 'Integer'))
            lines.append(f'        tasks.add(instantiateWithInt(')
            lines.append(f'            "{full_class}",')
            lines.append(f'            application,')
            lines.append(f'            extraParams["{int_param_name}"] as? Int ?: 0')
            lines.append(f'        ))')
        else:
            # 只有 Application 参数
            lines.append(f'        tasks.add(instantiate("{full_class}", application))')
        
        lines.append('')
    
    lines.append('        return tasks')
    lines.append('    }')
    lines.append('')
    lines.append('    private fun instantiate(className: String, application: Application): StartupTask {')
    lines.append('        return Class.forName(className)')
    lines.append('            .getDeclaredConstructor(Application::class.java)')
    lines.append('            .newInstance(application) as StartupTask')
    lines.append('    }')
    lines.append('')
    lines.append('    private fun instantiateWithInt(className: String, application: Application, intParam: Int): StartupTask {')
    lines.append('        return Class.forName(className)')
    lines.append('            .getDeclaredConstructor(Application::class.java, Int::class.javaPrimitiveType)')
    lines.append('            .newInstance(application, intParam) as StartupTask')
    lines.append('    }')
    lines.append('}')
    lines.append('')
    
    output = '\n'.join(lines)
    
    output_dir = 'startup/src/main/java/com/mohanlv/startup'
    os.makedirs(output_dir, exist_ok=True)
    output_file = os.path.join(output_dir, 'StartupTable.kt')
    
    need_update = True
    if os.path.exists(output_file):
        with open(output_file, 'r', encoding='utf-8') as f:
            existing = f.read()
        if existing == output:
            need_update = False
            print(f"Startup table unchanged, skipping update")
    
    if need_update:
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(output)
        print(f"Generated {len(tasks)} startup tasks to {output_file}:")
        for task_key, priority, full_class, params in tasks:
            param_str = ', '.join([f"{n}:{t}" for n, t in params])
            print(f"  [{priority:3d}] {task_key} -> {full_class}({param_str})")
    else:
        print(f"Startup table is up to date, no changes needed")
    
    return len(tasks)

if __name__ == '__main__':
    try:
        count = generate_startup_table()
        sys.exit(0)
    except Exception as e:
        import traceback
        traceback.print_exc()
        print(f"Error: {e}")
        sys.exit(1)
