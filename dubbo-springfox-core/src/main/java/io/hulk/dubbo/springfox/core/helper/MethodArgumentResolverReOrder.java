package io.hulk.dubbo.springfox.core.helper;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import javax.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * 调整springmvc参数解析器的作用顺序
 *
 * 详细信息见：https://www.cnblogs.com/java-zhao/p/9119258.html
 *
 */
@Configuration
public class MethodArgumentResolverReOrder {
    @Resource
    private RequestMappingHandlerAdapter adapter;

    @PostConstruct
    public void reorder() {
        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
        argumentResolvers.add(new RequestModelArgumentResolver());
        argumentResolvers.addAll(adapter.getArgumentResolvers());
        adapter.setArgumentResolvers(argumentResolvers);
    }
}