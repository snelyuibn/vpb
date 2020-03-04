package ru.varimax.data.store.server.core

import groovy.util.logging.Slf4j
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.event.EventListener

import javax.sql.DataSource
import java.text.SimpleDateFormat

import static java.lang.Thread.currentThread
import static ru.varimax.data.store.server.core.ServerState.DESTROYED
import static ru.varimax.data.store.server.core.ServerState.STARTED
import static ru.varimax.data.store.server.core.ServerState.STOPPED
import static ru.varimax.data.store.server.core.ServerType.EMBEDDED
import static ru.varimax.data.store.server.core.ServerType.STANDALONE

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

    public static long startTimestamp = 0
    public static long stopTimestamp = 0

    static VxDataStoreServer embedded() {
        return new VxDataStoreServer(EMBEDDED)
    }

    static VxDataStoreServer standalone() {
        return new VxDataStoreServer(STANDALONE)
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
        String message = "\n\n   ***   Varimax data store ${serverType} server STARTING...   ***  \n"
        println "${new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(startTimestamp))} ${message}"

        if (serverType == EMBEDDED) {
            dataSource = DataSourceBuilder.create().url(dataSourceUrl).username(dataSourceUsername).password(dataSourcePassword).build()
            Runnable runnable = new Runnable() {
                @Override
                void run() {
                    try {
                        long startTimestamp = System.currentTimeMillis()
                        while (currentThread().alive && currentThread().interrupted == false) {
                            println "${currentThread().name} has ${currentThread().state} state - ${System.currentTimeMillis() - startTimestamp} msec"
                            Thread.sleep(200)
                        }
                        println "${currentThread().name} has ${currentThread().state} state - ${System.currentTimeMillis() - startTimestamp} msec"
                    } catch (InterruptedException e) {
                        println "${currentThread().name} : ${e.message} - ${System.currentTimeMillis() - startTimestamp} msec"
                    }
                }
            }
            serverThread = new Thread(runnable)
            serverThread.name = "VxDataStoreEmbeddedServerThread"
            serverThread.daemon = false
            serverThread.start()
        }
        if (serverType == STANDALONE) {
            SpringApplication.run(VxDataStoreServer)
        }
        serverState = STARTED
        return this
    }

    VxDataStoreServer stop() {
        if (serverState == STARTED) {
            dataSource.connection.close()
        }
        if (serverThread) {
            while (serverThread.alive && serverThread.interrupted == false) {
                println "Send interrupt signal to ${serverThread.name}"
                serverThread.interrupt()
                Thread.sleep(200)
            }
        }
        serverState = STOPPED
        return this
    }

    VxDataStoreServer destroy() {
        if (serverState != STOPPED) {
            stop()
        }
        serverState = DESTROYED
        return this
    }

    @EventListener(ApplicationReadyEvent)
    void onSpringBootAppReady(ApplicationReadyEvent event) {
        applicationContext = event.applicationContext
        stopTimestamp = System.currentTimeMillis()
        long duration = stopTimestamp - startTimestamp
        String durationText = duration < 1000 ? "${duration} msec." : "${(duration / 1000) as int} sec.}"
        String message = "\n\n   ***   Varimax data store server READY (startup duration ${durationText})   ***  \n"
        if (log.infoEnabled) {
            log.info("${message}")
        } else {
            println "${new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date(stopTimestamp))} ${message}"
        }
    }

}

enum ServerType {
    EMBEDDED,
    STANDALONE
}

enum ServerState {
    CREATED,
    STARTED,
    STOPPED,
    DESTROYED
}