# springboot 防止重复提交

## 1 添加依赖

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
</dependencies>
```

## 2 添加application.properties配置

- application.properties配置文件中添加 redis相关配置;

```properties
spring.redis.host=127.0.0.1
spring.redis.port=6379
spring.redis.password=
```

## 3 CacheLock注解

```java

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheLock {
    String prefix() default ""; // 缓存中的key前缀

    int expire() default 5; // 缓存过期时间

    TimeUnit timeUnit() default TimeUnit.SECONDS; // 缓存过期时间单位

    String delimiter() default ":"; // 缓存中的key, 分隔符
}
```

## 4 CacheParam注解

```java

@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheParam {
    String name() default "";
}
```

## 5 key生成策略接口类

- 需要实现者自己手动注入@Bean

```java
public interface CacheKeyGenerator {
    String getLockKey(ProceedingJoinPoint joinPoint);
}
```

## 6 key生成策略实现类

```java
public class LockKeyGenerator implements CacheKeyGenerator {
    @Override
    public String getLockKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheLock cacheLock = method.getAnnotation(CacheLock.class);

        final Object[] args = joinPoint.getArgs();
        final Parameter[] parameters = method.getParameters();
        StringBuilder builder = new StringBuilder();
        // TODO 默认解析方法里面带 CacheParam 注解的属性,如果没有尝试着解析实体对象中的
        for (int i = 0; i < parameters.length; i++) {
            final CacheParam annotation = parameters[i].getAnnotation(CacheParam.class);
            if (annotation == null) {
                continue;
            }
            builder.append(cacheLock.delimiter()).append(args[i]);
        }
        if (StringUtils.isEmpty(builder.toString())) {
            final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                final Object object = args[i];
                final Field[] fields = object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    final CacheParam annotation = field.getAnnotation(CacheParam.class);
                    if (annotation == null) {
                        continue;
                    }
                    field.setAccessible(true);
                    builder.append(cacheLock.delimiter()).append(ReflectionUtils.getField(field, object));
                }
            }
        }
        return cacheLock.prefix() + builder.toString();
    }
}
```

## 7 AOP拦截器

- redis线程安全的, `opsForValue().setIfAbsent(key,value)`作用是如果缓存中没有当前 Key 则进行缓存同时返回 `true` 反之亦然；**当缓存后给 `key`
  在设置个过期时间，防止因为系统崩溃而导致锁迟迟不释放形成死锁；**

```java

@Component
@Aspect
public class LockMethodInterceptor {
    private final StringRedisTemplate lockRedisTemplate;
    private final CacheKeyGenerator cacheKeyGenerator;

    @Autowired
    public LockMethodInterceptor(StringRedisTemplate lockRedisTemplate, CacheKeyGenerator cacheKeyGenerator) {
        this.lockRedisTemplate = lockRedisTemplate;
        this.cacheKeyGenerator = cacheKeyGenerator;
    }

    @Around("execution(public * *(..)) && @annotation(com.example.demo.annotation.CacheLock)")
    public Object interceptor(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        CacheLock cacheLock = method.getAnnotation(CacheLock.class);

        if (StringUtils.isEmpty(cacheLock.prefix())) {
            throw new RuntimeException("lock key is null");
        }

        String lockKey = cacheKeyGenerator.getLockKey(joinPoint);

        try {
            Boolean res = lockRedisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.set(lockKey.getBytes(), new byte[0], Expiration.from(cacheLock.expire(), cacheLock.timeUnit()), RedisStringCommands.SetOption.SET_IF_ABSENT));
            if (res != null && !res) {
                throw new RuntimeException("请勿重复提交");
            }
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new RuntimeException("系统异常");
            }
        } finally {
            // 实际业务中取消这里的注释
            lockRedisTemplate.delete(lockKey);
        }
    }
}
```

## 8 Controller使用

- @CacheLock(prefix = "books")
- 动态的值可以加上`@CacheParam`

```java
@CacheLock(prefix = "say")
@GetMapping("/say")
public String say(){
        System.out.println("say hello!");
        return"hello world";
        }

@CacheLock(prefix = "go")
@GetMapping("/go")
public String go(@CacheParam(name = "token") @RequestParam String token){
        return"go with token: "+token;
        }
```

## 9 注入key生成策略实例

- main主函数实现注入

```java

@SpringBootApplication
@EnableAspectJAutoProxy
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CacheKeyGenerator cacheKeyGenerator() {
        return new LockKeyGenerator();
    }
}
```


