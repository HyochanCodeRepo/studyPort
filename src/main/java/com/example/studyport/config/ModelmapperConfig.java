package com.example.studyport.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelmapperConfig {

    //modelmapperr bean 등록
    @Bean
    public ModelMapper modelMapper() { return new ModelMapper(); }

}
