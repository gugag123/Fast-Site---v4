package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Liga o AdminInterceptor nas rotas de cadastros, serviços e agendamentos.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/api/cadastros", "/api/cadastros/**",
                        "/api/servicos", "/api/servicos/**",
                        "/api/agendamentos", "/api/agendamentos/**");
    }
}
