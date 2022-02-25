### 3 spring loaded 热部署

1. 添加pom.xml依赖, 下载jar到本地

```xml

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>springloaded</artifactId>
    <version>1.2.8.RELEASE</version>
</dependency>

```

2. Idea run configuration

VM
options: `-javaagent:/Users/tinytinycn/.m2/repository/org/springframework/springloaded/1.2.8.RELEASE/springloaded-1.2.8.RELEASE.jar -noverify`

3. 注意: idea 中, 非RUN或Debug 模式下才可以automake;
4. 当类路径上的文件发生更改时，使用spring-boot-devtools的应用程序会自动重新启。
5. intellij idea 开启 auto-make; file>settings>compiler>build project automatically;
6. 搜索maintenance, registry>勾选compiler automaker allow when app running;