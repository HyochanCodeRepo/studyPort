package com.example.studyport.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat 서버 설정
 * 파일 업로드 관련 설정은 application.properties에서 처리
 */
@Configuration
public class TomcatConfig {
    
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        
        // 커넥터 커스터마이징
        factory.addConnectorCustomizers(connector -> {
            try {
                // maxFileCount 설정 시도 (작동하지 않으면 무시)
                connector.setProperty("maxFileCount", "-1");
                System.out.println("✓ Tomcat maxFileCount 설정: -1 (무제한)");
            } catch (Exception e) {
                System.out.println("⚠ Tomcat maxFileCount 설정 불가 (속성 미지원)");
            }
        });
        
        return factory;
    }
}
