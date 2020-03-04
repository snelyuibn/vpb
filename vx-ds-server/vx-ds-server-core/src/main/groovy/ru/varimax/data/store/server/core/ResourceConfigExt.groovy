package ru.varimax.data.store.server.core

import org.glassfish.jersey.server.ResourceConfig
import org.glassfish.jersey.servlet.ServletContainer
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.ws.rs.ApplicationPath

import static ru.varimax.data.store.server.core.web.Const.JAXRS_APPLICATION_PATH

@Configuration
@ApplicationPath(JAXRS_APPLICATION_PATH)
class ResourceConfigExt extends ResourceConfig {

    ResourceConfigExt() {
        packages("ru.varimax.data.store.server")
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
