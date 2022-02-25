# Logging

Spring Boot使用Commons Logging进行所有内部日志记录，但是使底层日志实现保持打开状态。提供了Java Util Logging，Log4J2和Logback的默认配置。在每种情况下，记录器都已预先配置为使用控制台输出console output，同时还提供可选文件输出file output。

默认情况下，如果你使用“Starters”，则使用Logback进行日志记录。还包括适当的Logback routing，以确保使用Java Util Logging，Commons Logging，Log4J或SLF4J的依赖库均能正常工作.

> Java有许多可用的日志记录框架。如果上面的列表看起来令人困惑，请不要担心。通常，您不需要更改日志记录依赖项，并且Spring Boot默认值可以正常工作。

> 将应用程序部署到Servlet容器或应用程序服务器时，通过Java Util Logging API执行的日志记录不会路由到应用程序的日志中。这样可以防止容器或其他已部署到容器中的应用程序执行的日志记录出现在应用程序的日志中。

## 4.1 Log Format

Spring Boot的默认日志输出类似于以下示例：

```text
2019-03-05 10:57:51.112  INFO 45469 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet Engine: Apache Tomcat/7.0.52
2019-03-05 10:57:51.253  INFO 45469 --- [ost-startStop-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2019-03-05 10:57:51.253  INFO 45469 --- [ost-startStop-1] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 1358 ms
2019-03-05 10:57:51.698  INFO 45469 --- [ost-startStop-1] o.s.b.c.e.ServletRegistrationBean        : Mapping servlet: 'dispatcherServlet' to [/]
2019-03-05 10:57:51.702  INFO 45469 --- [ost-startStop-1] o.s.b.c.embedded.FilterRegistrationBean  : Mapping filter: 'hiddenHttpMethodFilter' to: [/*]
```

输出以下项目：

- Date and Time: 毫秒精度，易于分类。
- Log Level: 日志等级`ERROR`, `WARN`, `INFO`, `DEBUG`, or `TRACE`.
- Process ID: 进程ID
- `---`: 分隔符以区分实际日志消息的开始。
- Thread name: 用方括号括起来（对于控制台输出，可能会被截断）。
- Logger name: 通常是源类名称（通常是缩写）。
- Message: 日志信息

> 登录没有FATAL致命级别。它映射到ERROR错误级别。

## 4.2 Console Output

默认日志配置在消息写入时将消息回显到控制台。默认情况下，将记录ERROR级别，WARN级别和INFO级别的消息。您还可以通过使用--debug标志启动应用程序来启用“调试”模式。

`$ java -jar myapp.jar --debug`

> 您还可以在application.properties中指定debug = true。

当启用debug模式后，将配置一些core loggers（嵌入式容器，Hibernate和Spring Boot）以输出更多信息。启用debug模式, 不会将您的应用程序配置为 记录所有具有DEBUG级别的消息。

或者，您可以通过使用--trace标志（或application.properties中的trace = true）启动应用程序来启用“跟踪”模式。这样做可以为某些核心记录器（嵌入式容器，Hibernate模式生成以及整个Spring产品组合）启用跟踪记录。

### 4.2.1 Color-coded Output

如果您的终端支持ANSI，则使用彩色输出来提高可读性。您可以将`spring.output.ansi.enabled`设置为支持的值，以覆盖自动检测。

使用％clr转换字配置颜色编码。转换器以最简单的形式根据对数级别为输出着色，如以下示例所示：

```text
%clr(%5p)
```

下表描述了日志级别到颜色的映射：

| Level   | Color  |
| :------ | :----- |
| `FATAL` | Red    |
| `ERROR` | Red    |
| `WARN`  | Yellow |
| `INFO`  | Green  |
| `DEBUG` | Green  |
| `TRACE` | Green  |

另外，您可以通过将其提供为转换的选项来指定应使用的颜色或样式。例如，要使文本变黄，请使用以下设置：

```text
%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){yellow}
```

支持以下颜色和样式：blue/cyan/faint/green/magenta/red/yellow

## 4.3 File Output

默认情况下，Spring Boot仅记录到控制台，不写日志文件。如果除了控制台输出外还想写日志文件，则需要设置`logging.file.name`或`logging.file.path`属性（例如，在`application.properties`中）。

下表显示了如何结合使用logging.*属性：

| `logging.file.name` | `logging.file.path` | Example    | Description                                                  |
| :------------------ | :------------------ | :--------- | :----------------------------------------------------------- |
| *(none)*            | *(none)*            |            | 仅控制台记录                                                 |
| Specific file       | *(none)*            | `my.log`   | 写入指定的日志文件。名称可以是确切位置，也可以是相对于当前目录的位置。 |
| *(none)*            | Specific directory  | `/var/log` | 将`spring.log`写入指定目录。名称可以是确切位置，也可以是相对于当前目录的位置。 |

日志文件达到10 MB时会rotate旋转，并且与控制台输出一样，默认情况下会记录ERROR级别，WARN级别和INFO级别的消息。可以使用`logging.file.max-size`属性更改大小限制。除非已设置`logging.file.max-history`属性，否则默认情况下将保留最近7天的Rotated log files。可以使用`logging.file.total-size-cap`限制日志归档文件的总大小。当日志归档的总大小超过该阈值时，将删除备份。要在应用程序启动时强制清除日志归档文件，请使用`logging.file.clean-history-on-start`属性。

> 日志记录属性配置 Logging properties独立于实际的日志记录基础结构。因此，特定的配置keys（例如Logback的logback.configurationFile）不是由Spring Boot管理的。

## 4.4 Log Levels

所有受支持的日志记录器logging systems 通过使用`logging.level.<logger-name> = <level>`，可以在Spring环境中（例如，在`application.properties`中）设置日志级别。其中level是TRACE, DEBUG, INFO, WARN, ERROR, FATAL, or OFF。`root` logger 可以使用`logging.level.root`的方式配置。

以下示例显示了`application.properties`中的潜在logging settings日志记录设置：

```properties
logging.level.root=warn
logging.level.org.springframework.web=debug
logging.level.org.hibernate=error
```

也可以使用环境变量设置日志记录级别。例如: `LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_WEB=DEBUG` 将会把`org.springframework.web`日志记录设置成 `DEBUG`.

> 以上方法仅适用于程序包级别的日志记录。由于宽松的绑定总是将环境变量转换为小写，因此无法以这种方式为单个类配置日志记录。如果需要为类配置日志记录，则可以使用`SPRING_APPLICATION_JSON`变量。

## 4.5 Log Groups

能够将相关记录器 logger 分组在一起通常是很有用的，以便可以同时配置它们。例如，您可能通常会更改所有与Tomcat相关的记录器的记录级别，但是您不容易记住顶层软件包。

为了解决这个问题，Spring Boot允许您在Spring Environment中定义日志记录组。例如，以下是通过将“ tomcat”组添加到`application.properties`来定义它的方法：

```properties
logging.group.tomcat=org.apache.catalina, org.apache.coyote, org.apache.tomcat
```

定义后，您可以使用一行更改该组中所有记录器的级别：

```properties
logging.level.tomcat=TRACE
```

Spring Boot包含以下预定义的日志记录组，它们可以直接开箱使用：

| Name | Loggers                                                      |
| :--- | :----------------------------------------------------------- |
| web  | `org.springframework.core.codec`, `org.springframework.http`, `org.springframework.web`, `org.springframework.boot.actuate.endpoint.web`, `org.springframework.boot.web.servlet.ServletContextInitializerBeans` |
| sql  | `org.springframework.jdbc.core`, `org.hibernate.SQL`, `org.jooq.tools.LoggerListener` |

## 4.6 Custom Log Configuration

可以通过在类路径中包括适当的库来激活各种日志记录系统，并可以通过在类路径的根目录中或在以下Spring Environment属性指定的位置中提供适当的配置文件来进一步自定义日志文件：`logging.config`。

您可以通过使用`org.springframework.boot.logging.LoggingSystem`系统属性来强制Spring Boot使用特定的日志记录系统。该值应该是`LoggingSystem`实现的完全限定的类名。您也可以使用`none`完全禁用Spring Boot的日志记录配置。

> 由于日志记录是在创建ApplicationContext之前初始化的，因此无法从Spring @Configuration文件中的@PropertySources控制日志记录。更改日志记录系统或完全禁用它的唯一方法是通过系统属性。

根据您的日志记录系统，将加载以下文件：

| Logging System          | Customization                                                |
| :---------------------- | :----------------------------------------------------------- |
| Logback                 | `logback-spring.xml`, `logback-spring.groovy`, `logback.xml`, or `logback.groovy` |
| Log4j2                  | `log4j2-spring.xml` or `log4j2.xml`                          |
| JDK (Java Util Logging) | `logging.properties`                                         |

> 如果可能，我们建议您在日志配置中使用`-spring`变体配置名（例如，`logback-spring.xml`而不是`logback.xml`）。如果使用标准配置位置，Spring将无法完全控制日志初始化。

> 当从一个“executable jar”运行时，Java Util Logging存在一些已知的类加载问题，这个问题会引起麻烦。我们建议您尽可能从“executable jar”运行时, 避免使用它。

为了帮助自定义，将一些其他属性从Spring Environment转移到System属性，如下表所述：

| Spring Environment                    | System Property                   | Comments                                                     |
| :------------------------------------ | :-------------------------------- | :----------------------------------------------------------- |
| `logging.exception-conversion-word`   | `LOG_EXCEPTION_CONVERSION_WORD`   | The conversion word used when logging exceptions.            |
| `logging.file.clean-history-on-start` | `LOG_FILE_CLEAN_HISTORY_ON_START` | Whether to clean the archive log files on startup (if LOG_FILE enabled). (Only supported with the default Logback setup.) |
| `logging.file.name`                   | `LOG_FILE`                        | If defined, it is used in the default log configuration.     |
| `logging.file.max-size`               | `LOG_FILE_MAX_SIZE`               | Maximum log file size (if LOG_FILE enabled). (Only supported with the default Logback setup.) |
| `logging.file.max-history`            | `LOG_FILE_MAX_HISTORY`            | Maximum number of archive log files to keep (if LOG_FILE enabled). (Only supported with the default Logback setup.) |
| `logging.file.path`                   | `LOG_PATH`                        | If defined, it is used in the default log configuration.     |
| `logging.file.total-size-cap`         | `LOG_FILE_TOTAL_SIZE_CAP`         | Total size of log backups to be kept (if LOG_FILE enabled). (Only supported with the default Logback setup.) |
| `logging.pattern.console`             | `CONSOLE_LOG_PATTERN`             | The log pattern to use on the console (stdout). (Only supported with the default Logback setup.) |
| `logging.pattern.dateformat`          | `LOG_DATEFORMAT_PATTERN`          | Appender pattern for log date format. (Only supported with the default Logback setup.) |
| `logging.pattern.file`                | `FILE_LOG_PATTERN`                | The log pattern to use in a file (if `LOG_FILE` is enabled). (Only supported with the default Logback setup.) |
| `logging.pattern.level`               | `LOG_LEVEL_PATTERN`               | The format to use when rendering the log level (default `%5p`). (Only supported with the default Logback setup.) |
| `logging.pattern.rolling-file-name`   | `ROLLING_FILE_NAME_PATTERN`       | Pattern for rolled-over log file names (default `${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz`). (Only supported with the default Logback setup.) |
| `PID`                                 | `PID`                             | The current process ID (discovered if possible and when not already defined as an OS environment variable). |

所有受支持的日志记录系统在解析其配置文件时都可以查阅系统属性。有关示例，请参见·spring-boot.jar·中的默认配置：

- [Logback](https://github.com/spring-projects/spring-boot/tree/v2.2.7.RELEASE/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/logback/defaults.xml)
- [Log4j 2](https://github.com/spring-projects/spring-boot/tree/v2.2.7.RELEASE/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/log4j2/log4j2.xml)
- [Java Util logging](https://github.com/spring-projects/spring-boot/tree/v2.2.7.RELEASE/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/java/logging-file.properties)

> 如果要在日志记录属性中使用占位符，则应使用[Spring Boot's syntax](https://docs.spring.io/spring-boot/docs/2.2.7.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config-placeholders-in-properties)而不是基础框架的语法。值得注意的是，如果使用Logback，则应使用`:`作为属性名称与其默认值之间的分隔符，而不应使用`:-.`

> 您可以通过仅覆盖`LOG_LEVEL_PATTERN`（或带Logback的`logging.pattern.level`）来将MDC和其他临时内容添加到日志行。例如，如果您使用`logging.pattern.level=user:%X{user} %5p`，则默认日志格式包含“ user”的MDC条目, 如果存在，如以下示例所示。
>
> ```
> 2019-08-30 12:30:04.031 user:someone INFO 22174 --- [  nio-8080-exec-0] demo.Controller
> Handling authenticated request
> ```

## 4.7 Logback Extensions

Spring Boot包含许多Logback扩展，可以帮助进行高级配置。您可以在`logback-spring.xml`配置文件中使用这些扩展名。

> 由于标准`logback.xml`配置文件加载得太早，因此无法在其中使用扩展名。您需要使用`logback-spring.xml`或定义`logging.config`属性。

> 这些扩展不能与Logback的[配置扫描](https://logback.qos.ch/manual/configuration.html#autoScan)一起使用。如果尝试这样做，则对配置文件进行更改将导致类似于以下记录之一的错误：
>
> ```
> ERROR in ch.qos.logback.core.joran.spi.Interpreter@4:71 - no applicable action for [springProperty], current ElementPath is [[configuration][springProperty]]
> ERROR in ch.qos.logback.core.joran.spi.Interpreter@4:71 - no applicable action for [springProfile], current ElementPath is [[configuration][springProfile]]
> ```

### 4.7.1 Profile-specific Configuration

通过`<springProfile>`标记，您可以根据 active spring profiles 有选择地包括或排除配置部分。在`<configuration>`元素内的任何位置都支持 Profile sections。使用name属性指定profile接受哪些配置。 `<springProfile>`标记可以包含简单的profile 名称（例如，`staging`）或 profile表达式。profile表达式允许表达更复杂的配置文件逻辑，例如: `production & (eu-central | eu-west)`。有关[更多详细信息](https://docs.spring.io/spring/docs/5.2.6.RELEASE/spring-framework-reference/core.html#beans-definition-profiles-java)，请参阅参考指南。以下清单显示了三个样本概要文件：

```xml
<springProfile name="staging">
    <!-- configuration to be enabled when the "staging" profile is active -->
</springProfile>

<springProfile name="dev | staging">
    <!-- configuration to be enabled when the "dev" or "staging" profiles are active -->
</springProfile>

<springProfile name="!production">
    <!-- configuration to be enabled when the "production" profile is not active -->
</springProfile>
```

### 4.7.2 Enviroment Properties

`<springProperty>`标记使您可以从`Spring Environment`中公开属性，以在Logback中使用。如果要访问Logback配置中的`application.properties`文件中的值，则这样做很有用。该标签的工作方式类似于Logback的标准`<property>`标签。但是，不是指定`value`，而是指定属性的`source`（来自`Enviroment`）。如果需要将属性存储在`local`范围以外的其他位置，则可以使用`scope`属性。如果需要fallback value后备值（如果未在`Enviroment`中设置该属性），则可以使用`defaultValue`属性。以下示例显示如何公开在Logback中使用的属性：

```xml
<springProperty scope="context" name="fluentHost" source="myapp.fluentd.host"
        defaultValue="localhost"/>
<appender name="FLUENT" class="ch.qos.logback.more.appenders.DataFluentAppender">
    <remoteHost>${fluentHost}</remoteHost>
    ...
</appender>
```

> `source`必须在kebab情况下指定（例如`my.property-name`）。但是，可以使用宽松的规则将属性添加到`Enviroment`中。