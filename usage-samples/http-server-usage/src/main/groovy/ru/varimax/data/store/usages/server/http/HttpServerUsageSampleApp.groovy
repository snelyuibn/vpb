package ru.varimax.data.store.usages.server.http

import org.springframework.boot.autoconfigure.SpringBootApplication
import ru.varimax.data.store.server.core.VxDataStoreServer

@SpringBootApplication
class HttpServerUsageSampleApp {

    static void main(String[] args) {

        VxDataStoreServer httpServer = VxDataStoreServer.http()
            .instance("instance1")
            .meta(MetaGraph)
            .start()

        sleep(5000)

        httpServer.stop()

    }
}
