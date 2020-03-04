package ru.varimax.data.store.server.core.web

import org.glassfish.jersey.servlet.ServletContainer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/api/v1")
class VxDataStoreServerApi {

    final ServletContainer servletContainer

    VxDataStoreServerApi(ServletContainer servletContainer) {
        this.servletContainer = servletContainer
    }

    @GetMapping("/ping")
    String onPingRequest() {
        return "pong"
    }

    @GetMapping("/version")
    void onVersionRequest(HttpServletRequest httpServletRequest, ServletResponse httpServletResponse) {
        servletContainer.service(new HttpServletRequestExt(httpServletRequest), httpServletResponse)
    }
}

