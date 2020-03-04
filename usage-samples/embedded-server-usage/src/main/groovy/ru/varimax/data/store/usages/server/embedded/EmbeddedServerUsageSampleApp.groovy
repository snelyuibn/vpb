package ru.varimax.data.store.usages.server.embedded

import ru.varimax.data.store.server.core.VxDataStoreServer

class EmbeddedServerUsageSampleApp {

    static void main(String[] args) {

        VxDataStoreServer embeddedServer = VxDataStoreServer.embedded()
            .instance("instance1")
            .meta(MetaGraph)
            .dataSourceUrl("jdbc:h2:mem:test_mem;DB_CLOSE_DELAY=-1")
            .dataSourceUsername("sa")
            .dataSourcePassword("")
            .start()

//        VxDataStoreClient client = VxDataStoreClient.connect( "embedded://localhost/instance1" )
//
//        def result = client.execRequest()

        sleep 5000

        embeddedServer.stop()
    }
}
