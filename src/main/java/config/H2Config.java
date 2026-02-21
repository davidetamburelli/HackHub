package config;

import org.h2.server.web.JakartaWebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2Config {
    @Bean
    public ServletRegistrationBean<JakartaWebServlet> h2Console() {
        // Mappa manualmente la console di H2 su /h2-console/*
        ServletRegistrationBean<JakartaWebServlet> bean = new ServletRegistrationBean<>(new JakartaWebServlet(), "/h2-console/*");
        bean.setLoadOnStartup(1);
        return bean;
    }
}