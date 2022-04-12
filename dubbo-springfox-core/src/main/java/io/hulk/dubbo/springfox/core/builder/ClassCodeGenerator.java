package io.hulk.dubbo.springfox.core.builder;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.spring.ServiceBean;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;


import io.hulk.dubbo.springfox.core.helper.PrimitiveTypeHelper;

/**
 * controller类代码构建器
 */
public class ClassCodeGenerator {

    public static String generateClassCode(ServiceBean sb) throws NoSuchMethodException, ClassNotFoundException {
        StringBuilder codeBuilder = new StringBuilder();

        /**
         * 1 获取接口类和实现类
         */
        final Class<?> dubboInterface = sb.getInterfaceClass();
        final Class<?> rawDubboInterfaceImpl = sb.getRef().getClass();
        Class<?> dubboInterfaceImpl = rawDubboInterfaceImpl;

        String dubboInterfaceImplCanonicalName = rawDubboInterfaceImpl.getCanonicalName();
        // 保护性判断：防止dubbo接口实现类被代理，这里转换为真实类
        if (dubboInterfaceImplCanonicalName.contains("$$")) {
            dubboInterfaceImpl = ClassLoader.getSystemClassLoader()
                .loadClass(dubboInterfaceImplCanonicalName.substring(0, dubboInterfaceImplCanonicalName.indexOf("$$")));
        }

        /**
         * 2 获取包名
         * TODO ddd
         */
        final String packageName = dubboInterfaceImpl.getPackage().getName();
        // 保护性措施：包名尾部添加DubboApi，防止被意外AOP
        codeBuilder.append("package ").append(packageName).append("DubboApi;\n");

        /**
         * 4 获取重组@Tag注解
         */
        final Tag api = dubboInterface.getAnnotation(Tag.class);
        if (api != null) {
            String apiTag = api.name() != "" ? api.name() : "";
            String apiDesc = api.description() != "" ? api.description(): "";
            codeBuilder.append("@io.swagger.v3.oas.annotations.tags.Tag(name = \"" + apiTag +",description = "+ apiDesc+ "\")").append("\n");

            //TODO
        }

        /**
         * 5 创建@RestController @RequestMapping注解
         */
        codeBuilder.append("@org.springframework.web.bind.annotation.RestController\n")
            .append("@org.springframework.web.bind.annotation.RequestMapping(\"/dubbo-api\")\n");

        /**
         * 6 创建类定义
         * public class BookServiceImplDubboApi
         */
        String classDefinition = dubboInterfaceImpl.toGenericString();
        codeBuilder.append(classDefinition.substring(0, classDefinition.lastIndexOf(" ") + 1))
            .append(dubboInterfaceImpl.getSimpleName()).append("DubboApi {\n");

        /**
         * 7 注入实现类
         */
        String dubboInterfaceImplName = dubboInterfaceImpl.getSimpleName().substring(0, 1).toLowerCase()
                                        + dubboInterfaceImpl.getSimpleName().substring(1);
        codeBuilder.append("@org.springframework.beans.factory.annotation.Autowired\n").append("private ")
            .append(dubboInterfaceImpl.getCanonicalName()).append(" ").append(dubboInterfaceImplName).append(";\n");

        /**
         * 8 获取全部方法
         */
        for (Method method : dubboInterface.getMethods()) {

            /**
             * 8.1 获取重组ApiOperation注解
             */
            final Operation apiOperation = method.getAnnotation(Operation.class);
            //final io.swagger.v3.oas.annotations.Parameter annoParameter = method.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
            if (apiOperation != null) {
                codeBuilder.append("@io.swagger.v3.oas.annotations.Operation(summary = \"" + apiOperation.summary()!=""?apiOperation.summary():""
                    +",description = " + apiOperation.description() != "" ? apiOperation.description() : ""
                    + "\")\n");
                //TODO
            }

            /**
             * 8.2 创建@RequestMapping注解
             * 支持多版本
             * 支持方法重写
             */
            String serviceVersion = sb.getVersion() != null && sb.getVersion().length() > 0 ? sb.getVersion() : "0.0.0";
            codeBuilder.append("@org.springframework.web.bind.annotation.GetMapping(value = \"/" + serviceVersion
                               + "/" + dubboInterfaceImpl.getCanonicalName() + "/" + method.getName()+")\n");

            /*if (apiOperation != null && apiOperation.method().trim().length() > 0) {
                codeBuilder.append("/").append(apiOperation.method().trim());

                //TODO
            }*/
           // codeBuilder.append("\", method = org.springframework.web.bind.annotation.RequestMethod.POST)\n");

            /**
             * 8.3 创建方法定义
             */
            codeBuilder.append("public ").append(method.getGenericReturnType().getTypeName()).append(" ")
                .append(method.getName()).append("(");

            /**
             * 8.4 组建方法参数
             */
            final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
            final String[] parameterNames = parameterNameDiscoverer
                .getParameterNames(dubboInterfaceImpl.getMethod(method.getName(), method.getParameterTypes()));
            final Parameter[] parameters = method.getParameters();
            StringBuilder parameterBuilder = new StringBuilder();

            for (int i = 0; i < parameters.length; i++) {
                java.lang.reflect.Parameter parameter = parameters[i];
                String parameterName = parameterNames[i];
                final io.swagger.v3.oas.annotations.Parameter apiParam = parameter.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class);
                if (apiParam != null) {
                    parameterBuilder.append("@io.swagger.v3.oas.annotations.Parameter(name = \"").append(apiParam.name())
                        .append("\"").append(", required = ").append(apiParam.required())
                        .append("\") ");
                }

                if (PrimitiveTypeHelper.primitive(parameter.getType())) {
                    parameterBuilder.append("@org.springframework.web.bind.annotation.RequestParam(value = \"")
                        .append(parameterName).append("\"");
                    if (apiParam == null || apiParam.required() == false) {
                        parameterBuilder.append(", required = false");
                    } else {
                        parameterBuilder.append(", required = true");
                    }
                    parameterBuilder.append(")");
                } else {
                    parameterBuilder.append("@io.hulk.dubbo.springfox.core.annotations.RequestModel(value = \"")
                        .append(parameterName).append("\"");
                    if (apiParam == null || apiParam.required() == false) {
                        parameterBuilder.append(", required = false");
                    } else {
                        parameterBuilder.append(", required = true");
                    }
                    parameterBuilder.append(")");
                }

                parameterBuilder.append(parameter.getParameterizedType().getTypeName()).append(" ");
                parameterBuilder.append(parameterName).append(",");
            }

            if (parameterBuilder != null && parameterBuilder.length() > 0) {
                codeBuilder.append(parameterBuilder.substring(0, parameterBuilder.length() - 1));
            }

            String parameterNamesStr = "";
            if (parameterNames != null && parameterNames.length > 0) {
                parameterNamesStr = String.join(", ", parameterNames);
            }

            /**
             * 8.5 组建方法异常标识
             */
            codeBuilder.append(") ");
            final Class<?>[] exceptionTypes = method.getExceptionTypes();
            if (exceptionTypes != null && exceptionTypes.length > 0) {
                codeBuilder.append("throws ");
                for (int i = 0; i < exceptionTypes.length - 1; i++) {
                    codeBuilder.append(exceptionTypes[i].getCanonicalName()).append(",");
                }
                codeBuilder.append(exceptionTypes[exceptionTypes.length - 1].getCanonicalName());
            }

            /**
             * 8.6 组建方法内容
             */
            codeBuilder.append(" {");
            if (!method.getGenericReturnType().getTypeName().equalsIgnoreCase("void")) {
                codeBuilder.append("return ");
            }
            codeBuilder.append(dubboInterfaceImplName).append(".").append(method.getName()).append("(")
                .append(parameterNamesStr != "" ? parameterNamesStr : "").append(");");

            codeBuilder.append("}");
        }

        /**
         * 9 定义类结尾
         */
        codeBuilder.append("}");
        return codeBuilder.toString();
    }
}
