package com.bsgmakeup

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebMvc
class WebConfig: WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:4201",
                "http://localhost:4210",
                "https://bsg-makeup.com",
                "https://urielguillen.github.io",
                "https://bsg-makeup-notifications.web.app",
                "https://bsg-makeup-notifications.firebaseapp.com"
            )
            .allowedMethods("*")
            .allowCredentials(true)
    }
}
