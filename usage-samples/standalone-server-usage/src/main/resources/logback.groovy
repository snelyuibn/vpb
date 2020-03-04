import java.text.SimpleDateFormat

import static java.io.File.pathSeparator
import static java.lang.System.getProperties

printText "[BEGIN] Configure logback for Varimax data store server"

scan("10 seconds")

boolean isRunningAtIde = properties.'java.class.path'.tokenize(pathSeparator).find { it.contains('idea_rt.jar') }
boolean isRunningAsBoot = properties.getProperty("java.class.path").contains("service-registry-boot")

String consolePattern
if (isRunningAtIde) {
    consolePattern = "%date{dd.MM.yy HH:mm:ss.SSS} %-192msg [%-5level] [thread:%thread] [location: %class.%method\\(%file:%line\\)] %n"
} else {
    consolePattern = "%date{dd.MM.yy HH:mm:ss.SSS} %msg [%-5level] [thread:%thread] [location: %class.%method\\(%file:%line\\)] %n"
}
printText "isRunningAtIde = ${isRunningAtIde}"
printText "isRunningAsBoot = ${isRunningAsBoot}"

appender("Console-Appender", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = consolePattern
    }
}

def appenders = ["Console-Appender"]

//noinspection GroovyPointlessBoolean
if (isRunningAtIde == false & isRunningAsBoot == false) {
    appender("File-Appender", FileAppender) {
        file = "@maven.property.logging.log.file.location@"
        encoder(PatternLayoutEncoder) {
            pattern = consolePattern
        }
    }

    appenders += "File-Appender"
}

printText "appenders = ${appenders}"

// логгеры приложения
logger("ru.varimax.data.store", WARN)

// спринг
logger("org.springframework", WARN)
logger("org.springframework.web", WARN)

// БД
logger("org.springframework.transaction", WARN)
logger("org.springframework.jdbc.datasource.DataSourceTransactionManager", ERROR)
logger("org.apache.cayenne.log.JdbcEventLogger", WARN)

// http client
logger("org.apache.http", WARN)

// http server
logger("org.eclipse.jetty", WARN)
logger("org.eclipse.jetty.server.Server", WARN)
logger("JETTY_HTTP_CONTENT_LOGGER", WARN)
logger("org.glassfish.jersey", WARN)

root(WARN, appenders)

printText "[SUCCESS] Configure logback for Varimax data store server"

System.setProperty("logback.ready", "true")

void printText(String text) {
    if (System.getProperty("logback.ready") != "true") {
        println "${new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())} ${text}"
    }
}