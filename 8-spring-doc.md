# spring 微服务继承 springdoc

1. spring cloud gateway 模块添加依赖

```xml
<!-- springdoc openapi webflux ui -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-webflux-ui</artifactId>
    <version>1.6.6</version>
</dependency>
```

2. spring cloud gateway 模块配置路由、springdoc swagger-ui配置

```properties
# geteway route
spring.cloud.gateway.routes[0].id=user-center
spring.cloud.gateway.routes[0].uri=http://127.0.0.1:8001
spring.cloud.gateway.routes[0].predicates[0]=Path=/userCenter/**
spring.cloud.gateway.routes[0].filters[0]=StripPrefix=1
spring.cloud.gateway.routes[1].id=openapi
spring.cloud.gateway.routes[1].uri=http://127.0.0.1:${server.port}
spring.cloud.gateway.routes[1].predicates[0]=Path=/v3/api-docs/**
spring.cloud.gateway.routes[1].filters[0]=RewritePath=/v3/api-docs/(?<segment>.*), /$\{segment}/v3/api-docs
# springdoc
springdoc.swagger-ui.use-root-path=true
springdoc.swagger-ui.urls[0].name=userCenter
springdoc.swagger-ui.urls[0].url=/v3/api-docs/userCenter
```

3. user-center API 模块添加依赖

```xml
<!-- springdoc openapi webmvc core -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-webmvc-core</artifactId>
    <version>1.6.6</version>
</dependency>
```

4. user-center API模块 控制类上使用 openapi 3.0 注解

```java
@Tag(name = "测试", description = "这是一个测试控制类")
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/user/{id}")
    @Parameter(name = "id", description = "用户id", in = ParameterIn.PATH, required = true)
    public Object getUser(@PathVariable Integer id) {
        return "user";
    }
}
```

5. 访问文档 `localhost:8000/swagger-ui.html` 或 `localhost:8000` (后者需要配置 springdoc.swagger-ui.use-root-path=true)


# 参考

- [springdoc.org](https://springdoc.org/#Introduction)
- [OpenAPI3.0规范简介及注解使用](https://www.zllr.top/archives/openapi3%E8%A7%84%E8%8C%83%E7%AE%80%E4%BB%8B%E5%8F%8A%E6%B3%A8%E8%A7%A3%E4%BD%BF%E7%94%A8)
