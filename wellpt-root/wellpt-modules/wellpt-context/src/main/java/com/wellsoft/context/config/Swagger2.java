package com.wellsoft.context.config;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections.CollectionUtils;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableSwagger2
public class Swagger2 {


    @Resource
    DefaultListableBeanFactory defaultListableBeanFactory;

    @Autowired
    TypeResolver typeResolver;

    @Bean
    public Docket createRestApi() {
        Map<String, Map<String, String>> groupMap = getGroupMap();
        for (String groupName : groupMap.keySet()) {
            ApiSelectorBuilder apiSelectorBuilder = new Docket(DocumentationType.SWAGGER_2)
                    .groupName(groupName)// 分组名称
                    .enable(!Config.getAppEnv().equalsIgnoreCase(Config.ENV_PRD)) // 生产环境不开启
                    .apiInfo(apiInfo())
                    .select();
            ResolvedType[] resolvedTypes = null;
            if (groupMap.get(groupName).containsKey("package")) {
                apiSelectorBuilder.apis(RequestHandlerSelectors.basePackage(groupMap.get(groupName).get("package")));
                Reflections reflections = new Reflections(groupMap.get(groupName).get("package"));
                Set<Class<?>> set = reflections.getTypesAnnotatedWith(ApiModel.class);
                if (CollectionUtils.isNotEmpty(set)) {
                    resolvedTypes = new ResolvedType[set.size()];
                    List<Class<?>> list = set.stream().collect(Collectors.toList());
                    for (int i = 0; i < list.size(); i++) {
                        Class<?> clazz = list.get(i);
                        resolvedTypes[i] = typeResolver.resolve(clazz);
                    }
                }
            } else {
                apiSelectorBuilder.apis(RequestHandlerSelectors.any());
            }
            if (groupMap.get(groupName).containsKey("path")) {
                apiSelectorBuilder.paths(PathSelectors.ant(groupMap.get(groupName).get("path")));
            } else {
                apiSelectorBuilder.paths(PathSelectors.any());
            }
            apiSelectorBuilder.apis(RequestHandlerSelectors.withClassAnnotation(Api.class)); //指定扫描类上的注解
            Docket docket = null;
            if (resolvedTypes != null) {
                docket = apiSelectorBuilder.build().additionalModels(resolvedTypes[0], resolvedTypes);
            } else {
                docket = apiSelectorBuilder.build();
            }
            defaultListableBeanFactory.registerSingleton("swagger_api" + groupName, docket);
        }

        return new Docket(DocumentationType.SWAGGER_2)
                .enable(!Config.getAppEnv().equalsIgnoreCase(Config.ENV_PRD)) // 生产环境不开启
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class)) //指定扫描类上的注解
                .paths(PathSelectors.any())
                .build();
    }

    private Map<String, Map<String, String>> getGroupMap() {
        Map<String, Map<String, String>> resultMap = new HashMap<>();
        Set<String> keySet = Config.keySet();
        for (String key : keySet) {
            if (key.startsWith("swagger.api.group")) {
                String[] keySplit = key.split("\\.");
                if (keySplit.length == 5) {
                    Map<String, String> map = null;
                    if (resultMap.containsKey(keySplit[3])) {
                        map = resultMap.get(keySplit[3]);
                    } else {
                        map = new HashMap<>();
                        resultMap.put(keySplit[3], map);
                    }
                    map.put(keySplit[4], Config.getValue(key));
                }
            }
        }
        return resultMap;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("wellsoft restful apis")
                .description("wellsoft restful apis")
                .termsOfServiceUrl("")
                .contact(new Contact("wellsoft", "", ""))
                .version("1.0")
                .build();
    }

}