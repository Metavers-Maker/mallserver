//package com.muling.gateway.config;
//
//import org.springdoc.core.AbstractSwaggerUiConfigProperties;
//import org.springdoc.core.GroupedOpenApi;
//import org.springdoc.core.SwaggerUiConfigParameters;
//import org.springdoc.core.SwaggerUiConfigProperties;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.cloud.gateway.route.RouteDefinition;
//import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//
//import javax.annotation.PostConstruct;
//import java.util.ArrayList;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.Set;
//
///**
// * 一：通过动态配置
// * <p>
// * 二：通过配置文件指定(代码两种方式待测试)
// * springdoc:
// *   swagger-ui:
// *     urls:
// *       - name: user-service
// *         url: /user-service/v3/api-docs
// *       - name: role-service
// *         url: /role-service/v3/api-docs
// */
//@Configuration
//public class SpringDocConfig {
//
//    protected final SwaggerUiConfigProperties swaggerUiConfigProperties;
//    protected final RouteDefinitionLocator routeDefinitionLocator;
//
//    public SpringDocConfig(SwaggerUiConfigProperties swaggerUiConfigProperties, RouteDefinitionLocator routeDefinitionLocator) {
//        this.swaggerUiConfigProperties = swaggerUiConfigProperties;
//        this.routeDefinitionLocator = routeDefinitionLocator;
//    }
//
//    @Bean
//    @Lazy(false)
//    @ConditionalOnProperty(name = "springdoc.api-docs.enabled", matchIfMissing = true)
//    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters,
//                                     RouteDefinitionLocator routeDefinitionLocator) {
//        List<GroupedOpenApi> groups = new ArrayList<>();
//        List<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions().collectList().block();
//
//        definitions.stream().forEach(routeDefinition -> {
//            String group = routeDefinition.getId().replace("ReactiveCompositeDiscoveryClient_", "").toLowerCase();
//            swaggerUiConfigParameters.addGroup(group);
//        });
//        return groups;
//    }
//
//
////    @PostConstruct
////    public void autoInitSwaggerUrls() {
////        List<RouteDefinition> definitions = routeDefinitionLocator.getRouteDefinitions().collectList().block();
////
////        definitions.stream().forEach(routeDefinition -> {
////            AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl = new AbstractSwaggerUiConfigProperties.SwaggerUrl(
////                    routeDefinition.getId().replace("ReactiveCompositeDiscoveryClient_", "").toLowerCase(),
////                    routeDefinition.getUri().toString().replace("lb://", "").toLowerCase() + "/v3/api-docs"
////            );
////            Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = swaggerUiConfigProperties.getUrls();
////            if (urls == null) {
////                urls = new LinkedHashSet<>();
////                swaggerUiConfigProperties.setUrls(urls);
////            }
////            urls.add(swaggerUrl);
////        });
////    }
//}
//
