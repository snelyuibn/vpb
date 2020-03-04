package ru.varimax.data.store.usages.server.standalone


import org.springframework.boot.autoconfigure.SpringBootApplication
import ru.varimax.data.store.server.core.VxDataStoreServer

@SpringBootApplication
class StandaloneServerUsageSampleApp {

    static void main(String[] args) {
        VxDataStoreServer standaloneServer = VxDataStoreServer.standalone()
            .instance("instance1")
            .meta(MetaGraph)
            .start()
    }

}
