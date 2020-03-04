package ru.varimax.data.store.server.core.web

import javax.servlet.http.HttpServletRequest

import static ru.varimax.data.store.server.core.web.Const.JAXRS_APPLICATION_PATH

interface Const {
    public static final String JAXRS_APPLICATION_PATH = "/agrest"
}

class HttpServletRequestExt implements HttpServletRequest {

    @Delegate
    HttpServletRequest httpServletRequest

    HttpServletRequestExt(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest
    }

    @Override
    String getRequestURI() {
        return "${httpServletRequest.contextPath}${JAXRS_APPLICATION_PATH}${httpServletRequest.servletPath}"
    }

    @Override
    StringBuffer getRequestURL() {
        return new StringBuffer(requestURI)
    }

    @Override
    String getServletPath() {
        return JAXRS_APPLICATION_PATH
    }
}
