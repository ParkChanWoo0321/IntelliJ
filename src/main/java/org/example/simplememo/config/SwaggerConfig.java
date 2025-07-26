package org.example.simplememo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Simple Memo API")
                        .version("v1.0")
                        .description("계정 + 메모 + 댓글 시스템 API 명세서"))
                .addTagsItem(new Tag().name("Memo").description("메모 관련 API"))
                .addTagsItem(new Tag().name("Comment").description("댓글 관련 API"))
                .addTagsItem(new Tag().name("Auth").description("로그인 / 회원가입 API"));
    }
}
