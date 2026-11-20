package com.unibestx.serve.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 文档配置。
 * <p>
 * 在 Swagger UI 里点 Authorize 填入 accessToken，即可带令牌调试受保护接口。
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "accessToken";

    @Bean
    public OpenAPI unibestxOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("UniBestX Serve API")
                        .version("v1")
                        .description("统一响应结构：{code, message, data}"))
                // 声明 accessToken 请求头，Swagger UI 的 Authorize 会用到
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("accessToken")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
