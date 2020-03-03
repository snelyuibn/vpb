package ru.varimax.data.store.server

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.ws.rs.ApplicationPath

import static ru.varimax.data.store.server.Const.JAXRS_APPLICATION_PATH

interface Const {
    public static final String JAXRS_APPLICATION_PATH = "/agrest"
}

@Configuration
@ApplicationPath(JAXRS_APPLICATION_PATH)
class VxDataStoreServerConfig extends ResourceConfig {

    VxDataStoreServerConfig() {
        packages( "ru.varimax.data.store.serverd" )
    }

    @Bean
    ServletContainer servletContainer(List<ServletRegistrationBean> servletRegistrationBeans) {
        ServletContainer servletContainer = servletRegistrationBeans.find {
            it instanceof ServletRegistrationBean && it.servlet instanceof ServletContainer
        }.collect {
            it.servlet
        }.first() as ServletContainer

        return servletContainer
    }
}
