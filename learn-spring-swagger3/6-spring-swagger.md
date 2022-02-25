# springboot 集成 swagger 3.0

1. 依赖

```xml

<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-boot-starter</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```

2. application.properties 配置文件中，添加如下配置 `spring.mvc.pathmatch.matching-strategy=ant_path_matcher`
3. 添加swagger Java配置类

```java

@EnableOpenApi
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30).pathMapping("/")
                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(true)
                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())
                // 选择哪些接口作为swagger的doc发布
                .select()
                // 使用了注解的类
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"))
                // 授权信息设置，必要的header token等认证信息
                .securitySchemes(securitySchemes())
                // 授权信息全局应用
                .securityContexts(securityContexts());
    }

    /**
     * API 页面上半部分展示信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("学习使用swagger3.0")
                .description("learn spring swagger description content")
                .contact(new Contact("admin", "https://example.com", "admin@example.com"))
                .version("Application Version: 6.6.6")
                .build();
    }

    private Set<String> newHashSet(String... ts) {
        if (ts.length > 0) {
            return new LinkedHashSet<>(Arrays.asList(ts));
        }
        return null;
    }

    /**
     * 设置授权信息
     */
    private List securitySchemes() {
        ApiKey apiKey = new ApiKey("Authorization", "authorization", In.HEADER.toValue());
        return Collections.singletonList(apiKey);
    }

    /**
     * 授权信息全局应用
     */
    private List securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(
                                Collections.singletonList(
                                        new SecurityReference("Authorization", new AuthorizationScope[]{new AuthorizationScope("global", "")}
                                        )
                                )
                        )
                        .build()
        );
    }
}
```

4. 访问 [http://localhost:8080/swagger-ui/](http://localhost:8080/swagger-ui/)