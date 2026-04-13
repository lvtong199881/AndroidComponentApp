package com.mohanlv.router.compiler

import com.google.auto.service.AutoService
import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class RouterProcessor : AbstractProcessor() {

    private val routes = mutableListOf<RouteInfo>()

    data class RouteInfo(
        val path: String,
        val className: String,
        val description: String
    )

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf("com.mohanlv.router.annotation.Route")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_17
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (annotations.isEmpty()) return false

        val routeAnnotation = processingEnv.elementUtils.getTypeElement("com.mohanlv.router.annotation.Route")
        if (routeAnnotation == null) return false

        for (element in roundEnv.getElementsAnnotatedWith(routeAnnotation)) {
            if (element.kind != ElementKind.CLASS) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Route annotation can only be applied to classes", element)
                continue
            }

            val typeElement = element as TypeElement
            val path = typeElement.getAnnotationMirrors
                .find { it.annotationType.toString() == "com.mohanlv.router.annotation.Route" }
                ?.getElementValues()?.entries?.find { it.key.simpleName.toString() == "path" }
                ?.value?.value as? String

            val description = typeElement.getAnnotationMirrors
                .find { it.annotationType.toString() == "com.mohanlv.router.annotation.Route" }
                ?.getElementValues()?.entries?.find { it.key.simpleName.toString() == "description" }
                ?.value?.value as? String ?: ""

            if (path.isNullOrEmpty()) {
                processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Route path cannot be empty", element)
                continue
            }

            routes.add(RouteInfo(
                path = path,
                className = typeElement.qualifiedName.toString(),
                description = description
            ))
        }

        if (routes.isNotEmpty() && !roundEnv.processingOver()) {
            generateRouteTable()
        }

        return true
    }

    private fun generateRouteTable() {
        val routeMapType = ParameterizedTypeName.get(
            ClassName.get(Map::class.java),
            ClassName.get(String::class.java),
            ClassName.get(Function::class.java)
        )

        val methodBuilder = MethodSpec.methodBuilder("registerAll")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(ClassName.get("com.mohanlv.router", "RouterManager"), "manager")

        for (route in routes) {
            methodBuilder.addStatement(
                "manager.registerInternal(\$S, () -> new \$L())",
                route.path,
                route.className
            )
        }

        val routeTableClass = TypeSpec.objectBuilder("RouteTable")
            .addModifiers(Modifier.PUBLIC)
            .addMethod(methodBuilder.build())
            .build()

        JavaFile.builder("com.mohanlv.router", routeTableClass)
            .build()
            .writeTo(processingEnv.filer)
    }
}
