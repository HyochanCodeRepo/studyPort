/***********************************************
 * 클래스명 : WebConfig
 * 기능 : 웹 설정 및 정적 리소스 매핑
 * 작성자 :
 * 작성일 : 2025-07-16
 * 수정 : 2025-09-02
 * ***********************************************/
package com.example.studyport.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Value("${uploadPath}")
    private String uploadPath;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 이미지에 대한 웹 접근 경로 설정
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath)
                .setCachePeriod(3600); // 1시간 캐시
    }
}
