package ru.varimax.data.store.server.core

import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener

import javax.sql.DataSource
import java.text.SimpleDateFormat

import static java.lang.Thread.currentThread
import static ru.varimax.data.store.server.core.ServerState.STARTED
import static ru.varimax.data.store.server.core.ServerState.STOPPED
import static ru.varimax.data.store.server.core.ServerType.EMBEDDED
import static ru.varimax.data.store.server.core.ServerType.HTTP

@SpringBootApplication
@Slf4j
class VxDataStoreServer {

    ServerType serverType
    ServerState serverState
    String instanceName
    Class meta
    DataSource dataSource
    String dataSourceUrl
    String dataSourceUsername
    String dataSourcePassword
    ApplicationContext applicationContext
    Thread serverThread

    public long startTimestamp = 0
    public long stopTimestamp = 0

    static VxDataStoreServer embedded() {
        return new VxDataStoreServer(EMBEDDED)
    }

    static VxDataStoreServer http() {
        return new VxDataStoreServer(HTTP)
    }

    VxDataStoreServer() {
    }

    private VxDataStoreServer(ServerType serverType) {
        this.serverType = serverType
        serverState = ServerState.CREATED
    }

    VxDataStoreServer instance(String instanceName) {
        this.instanceName = instanceName
        return this
    }

    VxDataStoreServer meta(Class meta) {
        this.meta = meta
        return this
    }

    VxDataStoreServer dataSourceUrl(String url) {
        this.dataSourceUrl = url
        return this
    }

    VxDataStoreServer dataSourceUsername(String username) {
        this.dataSourceUsername = username
        return this
    }

    VxDataStoreServer dataSourcePassword(String password) {
        this.dataSourcePassword = password
        return this
    }

    VxDataStoreServer start() {
        startTimestamp = System.currentTimeMillis()
        printLog "\n\n   ***   Varimax data store ${serverType} server '${instanceName}' STARTING...   ***  \n"

        if (serverType == EMBEDDED) {
            dataSource = DataSourceBuilder.create().url(dataSourceUrl).username(dataSourceUsername).password(dataSourcePassword).build()
            serverThread = new Thread({
                try {
                    long startTimestamp = System.currentTimeMillis()
                    while (currentThread().alive && currentThread().interrupted == false) {
                        printLog "${currentThread().name} has ${currentThread().state} state - ${System.currentTimeMillis() - startTimestamp} msec"
                        Thread.sleep(1000)
                    }
                    printLog "${currentThread().name} has ${currentThread().state} state - ${System.currentTimeMillis() - startTimestamp} msec"
                } catch (InterruptedException e) {
                    printLog "${currentThread().name} : ${e.message} - ${System.currentTimeMillis() - startTimestamp} msec"
                }
            })
            serverThread.name = "VxDataStoreEmbeddedServerThread"
            serverThread.daemon = false
            serverThread.start()
            long duration = System.currentTimeMillis() - startTimestamp
            String durationText = duration < 1000 ? "${duration} msec." : "${(duration / 1000) as int} sec."
            String readyMessage = "\n\n   ***   Varimax data store ${EMBEDDED} server '${instanceName}' READY (startup duration ${durationText})   ***  \n"
            printLog(readyMessage)
        }
        if (serverType == HTTP) {
            SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder(VxDataStoreServer)
            SpringApplication springApplication = springApplicationBuilder.build()
            applicationContext = springApplication.run(
                "--vx.http.server.instance.name=${instanceName}",
                "--vx.http.server.instance.start.timestamp=${startTimestamp}"
            )
        }
        serverState = STARTED
        return this
    }

    VxDataStoreServer stop() {
        if (serverType == EMBEDDED) {
            if (serverState == STARTED && dataSource) {
                dataSource.connection.close()
            }
            if (serverThread) {
                while (serverThread.alive && serverThread.interrupted == false) {
                    printLog "Send interrupt signal to ${serverThread.name}"
                    serverThread.interrupt()
                    Thread.sleep(200)
                }
            }
        }
        if (serverType == HTTP) {
            SpringApplication.exit(applicationContext)
        }
        serverState = STOPPED
        stopTimestamp = System.currentTimeMillis()
        return this
    }

    @EventListener(ApplicationReadyEvent)
    void onSpringBootAppReady(ApplicationReadyEvent event) {
        applicationContext = event.applicationContext
        String instanceName = applicationContext.environment.getProperty("vx.http.server.instance.name")
        long startTimestamp = applicationContext.environment.getProperty("vx.http.server.instance.start.timestamp") as long
        long duration = System.currentTimeMillis() - startTimestamp
        String durationText = duration < 1000 ? "${duration} msec." : "${(duration / 1000) as int} sec."
        String message = "\n\n   ***   Varimax data store ${HTTP} server '${instanceName}' READY (startup duration ${durationText})   ***  \n"
        printLog(message)
    }

    private void printLog(String message) {
        if (log.infoEnabled) {
            log.info("${message}")
        } else {
            println "${new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())} ${message}"
        }
    }

}

enum ServerType {
    EMBEDDED,
    HTTP
}

enum ServerState {
    CREATED,
    STARTED,
    STOPPED
}