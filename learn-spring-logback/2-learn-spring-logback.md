# spring logback 日志使用

1. 确保存在相关依赖 `spring-boot-starter-logging`
2. Springboot 默认使用logback 日志
3. Springboot 默认输出日志级别 `WARN`
 
> 级别从低到高: TRACE < DEBUG < INFO < WARN < ERROR < FATAL

4. 配置文件

logback日志框架会自动加载classpath目录下的配置文件: logback-spring.xml(推荐)或logback.xml.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 引入springboot默认配置 -->
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <property resource="application.properties"/>

    <!-- 定义 文件输出格式 -->
    <property name="PATTERN" value="%d{HH:mm:ss.SSS} [%thread] %-5level %logger{80} - %msg%n"/>
    <!-- 定义 文件绝对路径 -->
    <property name="FILE_PATH" value="/data/logs"/>

    <!-- 定义 控制台输出:ConsoleAppender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <!-- 对日志进行编码:
 						%d表示日期时间，
            %thread表示线程名，
            %-5level：级别从左显示5个字符宽度
            %logger{50} 表示logger名字最长50个字符，否则按照句点分割。
            %L 表示行号
            %msg：日志消息，
            %n是换行符
				-->
        <encoder>
            <pattern>${PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 定义 文件输出:RollingFileAppender -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 输出路径 -->
        <file>${FILE_PATH}/${spring.application.name}/out.log</file>
        <!--
            滚动时产生的文件的存放位置及文件名称 %d{yyyy-MM-dd}：按天进行日志滚动
            %i：当文件大小超过maxFileSize时，按照i进行文件滚动
        -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 文件名称 -->
            <fileNamePattern>${FILE_PATH}/${spring.application.name}/out.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 文件最大保存天数 10天 -->
            <maxHistory>10</maxHistory>
            <maxFileSize>100MB</maxFileSize>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <!-- 输出格式 -->
        <encoder>
            <pattern>${PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
	
    <!-- 定义 文件输出:RollingFileAppender 过滤 -->
    <appender name="FILE_ERROR_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!--下面通过 Filter，记录 Error 级别的日志-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <!--滚动策略-->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!--输出路径-->
            <fileNamePattern>${FILE_PATH}/${spring.application.name}/err.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 文件最大保存天数 -->
            <maxHistory>10</maxHistory>
            <maxFileSize>100MB</maxFileSize>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 订单相关的日志 -->
    <appender name="PAY_LOG" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <!-- 每天产生一个文件 -->
        <!-- 输出路径 -->
        <file>${FILE_PATH}/${spring.application.name}/pay.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 文件名称 -->
            <fileNamePattern>${FILE_PATH}/${spring.application.name}/pay.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <!-- 文件最大保存天数 -->
            <maxHistory>10</maxHistory>
            <maxFileSize>100MB</maxFileSize>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <!-- 输出格式 -->
        <encoder>
            <pattern>${PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 测试|开发环境 -->
    <springProfile name="test,dev">
        <logger level="debug" additivity="false" name="com.smcloud.service.feign.PayService">
            <appender-ref ref="PAY_LOG"/>
        </logger>
        <logger level="debug" additivity="false" name="com.smcloud.mapper.PayCenterMapper">
            <appender-ref ref="PAY_LOG"/>
        </logger>
        <logger level="debug" additivity="false" name="com.smcloud">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="FILE"/>
            <appender-ref ref="FILE_ERROR_LOG"/>
        </logger>
        <root level="info">
            <appender-ref ref="FILE"/>
            <appender-ref ref="FILE_ERROR_LOG"/>
        </root>
    </springProfile>

    <!-- 生产环境 -->
    <springProfile name="prod">
        <!--        <logger name="com.smcloud.mapper" level="debug">-->
        <!--            <appender-ref ref="FILE"/>-->
        <!--            <appender-ref ref="FILE_ERROR_LOG"/>-->
        <!--        </logger>-->
        <logger level="debug" additivity="false" name="com.smcloud.service.feign.PayService">
            <appender-ref ref="PAY_LOG"/>
        </logger>
        <logger level="debug" additivity="false" name="com.smcloud.mapper.PayCenterMapper">
            <appender-ref ref="PAY_LOG"/>
        </logger>
        <logger name="com.smcloud" level="debug" additivity="false">
            <appender-ref ref="CONSOLE"/>
            <appender-ref ref="FILE"/>
            <appender-ref ref="FILE_ERROR_LOG"/>
        </logger>
        <root level="info">
            <appender-ref ref="FILE"/>
            <appender-ref ref="FILE_ERROR_LOG"/>
        </root>
    </springProfile>
</configuration>
```

补充说明:

- `` 可以包含零个或多个 `` 元素，标识这个 appender 将会添加到这个 logger;
- logger 属性:
    - name: 用来指定受此 logger 约束的某一个包或者具体的某一个类，没写的时候会报错
    - level：日志打印级别，如果未设置此属性，那么当前 logger 会继承上级的级别，也就是 root 的级别；
    - addtivity：是否向上级 logger 传递打印信息。默认是 true。设置为 false 表示该日志打印设置（控制台打印还是文件打印等具体设置）不会向根 root 标签传递，也就是说该 logger 里怎么设置的那就会怎么打印，跟 root 无关
- root 是根 logger；只不过 root 中不能有 name 和 additivity 属性，是有一个 level属性。
- appender 是一个日志打印的组件，这里组件里面定义了打印过滤的条件、打印输出方式、滚动策略、编码方式、打印格式等等。但是它仅仅是一个打印组件，如果我们不使用一个 logger 或者 root 的 appender-ref 指定某个具体的 appender 时，它就没有什么意义

5. 异步输出

之前的日志配置方式是基于同步的，每次日志输出到文件都会进行一次磁盘IO。采用异步写日志的方式而不让此次写日志发生磁盘IO，阻塞线程从而造成不必要的性能损耗。异步输出日志的方式很简单，添加一个基于异步写日志的`appender`，并指向原先配置的`appender`即可.

```xml
<!-- 异步输出 -->
<appender name="ASYNC-File" class="ch.qos.logback.classic.AsyncAppender">
  <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
  <discardingThreshold>0</discardingThreshold>
  <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
  <queueSize>256</queueSize>
  <!-- 添加附加的appender,最多只能添加一个 -->
  <appender-ref ref="FILE"/>
</appender>

<appender name="ASYNC-FILE-ERROR" class="ch.qos.logback.classic.AsyncAppender">
  <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
  <discardingThreshold>0</discardingThreshold>
  <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
  <queueSize>256</queueSize>
  <!-- 添加附加的appender,最多只能添加一个 -->
  <appender-ref ref="FILE_ERROR_LOG"/>
</appender>

<appender name="ASYNC-PAY-FILE" class="ch.qos.logback.classic.AsyncAppender">
  <!-- 不丢失日志.默认的,如果队列的80%已满,则会丢弃TRACT、DEBUG、INFO级别的日志 -->
  <discardingThreshold>0</discardingThreshold>
  <!-- 更改默认的队列的深度,该值会影响性能.默认值为256 -->
  <queueSize>256</queueSize>
  <!-- 添加附加的appender,最多只能添加一个 -->
  <appender-ref ref="PAY_LOG"/>
</appender>
```

6. 使用Logger日志输出

```text
private static final Logger log = LoggerFactory.getLogger(XXX.class);
log.info("info");
```

或者使用 lombok 的 `@Slf4j` 注解在类上，使用 `log.debug()` 之类方法进行日志打印。



----

# 参考

- [springboot logging](https://docs.spring.io/spring-boot/docs/2.6.4/reference/htmlsingle/#features.logging)
- [springboot logback](https://docs.spring.io/spring-boot/docs/2.6.4/reference/htmlsingle/#howto.logging.logback)
- [logback](https://logback.qos.ch/)
- [springboot日志配置](https://www.jianshu.com/p/f67c721eea1b)