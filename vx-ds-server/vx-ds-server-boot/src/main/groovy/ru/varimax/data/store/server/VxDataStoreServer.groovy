package ru.varimax.data.store.server

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener

import java.text.SimpleDateFormat

@SpringBootApplication
class VxDataStoreServer {

    public static long startTimestamp = System.currentTimeMillis()
    private Logger _logger

    static void main(String[] args) {
        String message = "\n\n   ***   Varimax data store server STARTING...   ***  \n"
        println "${new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())} ${message}"

        SpringApplication.run([VxDataStoreServer] as Class[])
    }

    @EventListener(ApplicationReadyEvent)
    void onSpringBootAppReady(ApplicationReadyEvent event) {
        long duration = System.currentTimeMillis() - startTimestamp
        String durationText = duration < 1000 ? "${duration} msec." : "${(duration / 1000) as int} sec.}"
        String message = "\n\n   ***   Varimax data store server READY (startup duration ${durationText})   ***  \n"
        if (getLog().infoEnabled) {
            getLog().info("${message}")
        } else {
            println "${new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())} ${message}"
        }
    }

    Logger getLog() {
        if (_logger == null) {
            _logger = LoggerFactory.getLogger(VxDataStoreServer)
        }
        return _logger
    }
}
