package com.mohanlv.init;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.lang.annotation.Annotation;
import com.google.auto.service.AutoService;

/**
 * KAPT Processor：扫描 @InitTask 注解，生成 StartupCollector 实现类
 * 生成 META-INF/services 文件供 ServiceLoader 运行时发现
 */
@SupportedOptions({
    InitTaskProcessor.OPTION_PACKAGE,
    InitTaskProcessor.OPTION_MODULE_NAME
})
@AutoService(Processor.class)
public class InitTaskProcessor extends AbstractProcessor {
    
    static final String OPTION_PACKAGE = "initCollectorPackage";
    static final String OPTION_MODULE_NAME = "initCollectorModuleName";
    
    private String packageName = "com.mohanlv";
    private String moduleName = "";
    
    private static final String ANNOTATION_CLASS = "com.mohanlv.startup.annotation.InitTask";
    
    @Override
    public Set<String> getSupportedOptions() {
        return Set.of(OPTION_PACKAGE, OPTION_MODULE_NAME);
    }
    
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(ANNOTATION_CLASS);
    }
    
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_17;
    }
    
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        packageName = processingEnv.getOptions().getOrDefault(OPTION_PACKAGE, "com.mohanlv");
        moduleName = processingEnv.getOptions().getOrDefault(OPTION_MODULE_NAME, "");
    }
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> initTasks;
        try {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation> annotationClass =
                (Class<? extends Annotation>) Class.forName(ANNOTATION_CLASS);
            initTasks = roundEnv.getElementsAnnotatedWith(annotationClass);
        } catch (ClassNotFoundException e) {
            return false;
        }
        
        if (initTasks.isEmpty()) return false;
        
        generateCollector(initTasks);
        
        return true;
    }
    
    private void generateCollector(Set<? extends Element> tasks) {
        String collectorClassName = moduleName.substring(0, 1).toUpperCase() + moduleName.substring(1) + "StartupCollector";
        String qualifiedCollectorName = packageName + "." + collectorClassName;
        
        try {
            JavaFileObject collectorFile = processingEnv.getFiler().createSourceFile(qualifiedCollectorName);
            try (Writer writer = collectorFile.openWriter()) {
                writer.write(buildCollectorCode(collectorClassName, tasks));
            }
            
            FileObject serviceFile = processingEnv.getFiler().createResource(
                StandardLocation.CLASS_OUTPUT,
                "",
                "META-INF/services/com.mohanlv.startup.StartupCollector"
            );
            try (Writer writer = serviceFile.openWriter()) {
                writer.write(qualifiedCollectorName);
            }
            
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generated " + qualifiedCollectorName);
        } catch (Exception e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate: " + e.getMessage());
        }
    }
    
    private String buildCollectorCode(String className, Set<? extends Element> tasks) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import android.app.Application;\n");
        sb.append("import com.mohanlv.startup.StartupCollector;\n");
        sb.append("import com.mohanlv.startup.StartupTask;\n");
        sb.append("import java.util.List;\n\n");
        sb.append("/**\n");
        sb.append(" * 自动生成的启动任务收集器\n");
        sb.append(" * 由 InitTaskProcessor KAPT 生成\n");
        sb.append(" */\n");
        sb.append("public class ").append(className).append(" implements StartupCollector {\n");
        sb.append("    @Override\n");
        sb.append("    public List<StartupTask> collect(Application application) {\n");
        sb.append("        return List.of(\n");
        
        List<? extends Element> taskList = new ArrayList<>(tasks);
        for (int i = 0; i < taskList.size(); i++) {
            Element element = taskList.get(i);
            TypeElement typeElement = (TypeElement) element;
            
            AnnotationMirror foundMirror = null;
            for (AnnotationMirror mirror : typeElement.getAnnotationMirrors()) {
                if (mirror.getAnnotationType().toString().equals(ANNOTATION_CLASS)) {
                    foundMirror = mirror;
                    break;
                }
            }
            if (foundMirror == null) continue;
            
            String key = getAnnotationValue(foundMirror, "key");
            String priorityStr = getAnnotationValue(foundMirror, "priority");
            int priority = 0;
            try {
                priority = Integer.parseInt(priorityStr);
            } catch (NumberFormatException ignored) {}
            String qualifiedName = typeElement.getQualifiedName().toString();
            boolean isLast = (i == taskList.size() - 1);
            
            sb.append("                new ").append(qualifiedName).append("(application)").append(isLast ? "" : ",").append("\n");
        }
        
        sb.append("        );\n");
        sb.append("    }\n");
        sb.append("}\n");
        
        return sb.toString();
    }
    
    private String getAnnotationValue(AnnotationMirror annotation, String name) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = annotation.getElementValues();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(name)) {
                String value = entry.getValue().toString();
                // 去掉引号
                if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                return value;
            }
        }
        return "";
    }
}
