package org.example.simplememo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 프론트나 다른 도메인에서 오는 요청을 허용할 지 확인
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") // 모든 요청 허용
                .allowedOrigins("http://localhost:3000") // 프론트엔드 주소 (배포시 도메인주소)
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS") // 허용 메소드 CRUD
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true); // 세션, 쿠키 등 요청정보 제공
    }
}
