##Log4j2 – Useful Conversion Pattern Examples

Below log4j2 conversion patterns are for reference only so that you and me don’t waste time to build these patterns 
 we are creating/editing log4j configuration files.

I am using below log statements for generating the logs.

```java
LOGGER.debug("Debug Message Logged !!");
LOGGER.info("Info Message Logged !!");
```
Now, I will list down different log patterns and their respective generated outputs.

####%d [%p] %c{1} – %m%n
Use it for simple logging i.e. date, level, logger, message. It will generate below output:

```bash
2016-06-20 19:18:02,958 [DEBUG] Log4j2HelloWorldExample - Debug Message Logged !!
2016-06-20 19:18:02,959 [INFO] Log4j2HelloWorldExample - Info Message Logged !!
```
####%d [%-6p] %c{1} – %m%n
Use it for simple logging with pretty print logger level. It will generate below output:

```bash
2016-06-20 19:21:05,271 [DEBUG ] Log4j2HelloWorldExample - Debug Message Logged !!
2016-06-20 19:21:05,272 [INFO  ] Log4j2HelloWorldExample - Info Message Logged !!
```
####%d [%-6p] %c{1} – %m%n
Use it for printing the complete package level. It will generate below output:

```bash
2016-06-20 19:22:05,379 [DEBUG ] com.howtodoinjava.log4j2.examples.Log4j2HelloWorldExample - Debug Message Logged !!
2016-06-20 19:22:05,380 [INFO  ] com.howtodoinjava.log4j2.examples.Log4j2HelloWorldExample - Info Message Logged !!
```
####%d [%-6p] %c{3} – %m%n
Use it for printing the package level upto two levels. It will generate below output:

```bash
2016-06-20 19:23:48,202 [DEBUG ] log4j2.examples.Log4j2HelloWorldExample - Debug Message Logged !!
2016-06-20 19:23:48,204 [INFO  ] log4j2.examples.Log4j2HelloWorldExample - Info Message Logged !!
```
####%d{yyyy/MM/dd HH:mm:ss,SSS} [%-6p] %c{1} – %m%n
Use it for custom date format. It will generate below output:

```bash
2016/06/20 19:24:45,076 [DEBUG ] Log4j2HelloWorldExample - Debug Message Logged !!
2016/06/20 19:24:45,078 [INFO  ] Log4j2HelloWorldExample - Info Message Logged !!
```
####%d [%-6p] %C{1}.%M(%F:%L) – %m%n
Use it for the caller class, method, source file and line number. It will generate below output:

```bash
2016-06-20 19:25:42,249 [DEBUG ] Log4j2HelloWorldExample.methodOne(Log4j2HelloWorldExample.java:14) - Debug Message Logged !!
2016-06-20 19:25:42,251 [INFO  ] Log4j2HelloWorldExample.methodOne(Log4j2HelloWorldExample.java:15) - Info Message Logged !!
```
####%sn %d{yyyy/MM/dd HH:mm:ss,SSS} %r [%-6p] [%t] %c{3} %C{3}.%M(%F:%L) – %m%n
Use it to capture everything discussed above. It will generate below output:

```bash
1 2016/06/20 19:27:03,595 620 [DEBUG ] [main] log4j2.examples.Log4j2HelloWorldExample log4j2.examples.Log4j2HelloWorldExample.main(Log4j2HelloWorldExample.java:14) - Debug Message Logged !!
2 2016/06/20 19:27:03,597 622 [INFO  ] [main] log4j2.examples.Log4j2HelloWorldExample log4j2.examples.Log4j2HelloWorldExample.main(Log4j2HelloWorldExample.java:15) - Info Message Logged !!
```
Feel free to change and use any pattern as per your need.