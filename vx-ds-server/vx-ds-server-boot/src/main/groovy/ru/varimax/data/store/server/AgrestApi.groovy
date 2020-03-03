package ru.varimax.data.store.server

import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

@Component
@Path("/api/v1")
class AgrestApi {

    @GET
    @Path("/version")
    Response onVersionRequest(@Context UriInfo uriInfo) {
        return Response.ok().entity("1.0.0").build()
    }

}
