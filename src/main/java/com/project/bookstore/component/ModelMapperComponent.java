package com.project.bookstore.component;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperComponent {
    @Bean
    public ModelMapper modelmapper() {
        return new ModelMapper();
    }
}