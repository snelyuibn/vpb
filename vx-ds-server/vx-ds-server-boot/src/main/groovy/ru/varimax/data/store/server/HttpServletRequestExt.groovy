package ru.varimax.data.store.server

import javax.servlet.http.HttpServletRequest

import static ru.varimax.data.store.server.Const.JAXRS_APPLICATION_PATH

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
