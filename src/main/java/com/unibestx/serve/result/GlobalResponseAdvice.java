package com.unibestx.serve.result;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * 统一响应包装：把 Controller 返回的业务对象包装为 {@link Result}，并同步 HTTP 状态码。
 * <p>
 * 已包装的 {@link Result} 原样透传，避免二次包装；文件下载类响应不处理。
 */
@ControllerAdvice
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public GlobalResponseAdvice(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 只对当前 Advice 生效的返回类型做过滤：跳过文件流和字节数组，避免破坏二进制内容。
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> parameterType = returnType.getParameterType();
        return !Resource.class.isAssignableFrom(parameterType) && !byte[].class.isAssignableFrom(parameterType);
    }

    /**
     * 在响应写出前统一包装响应体。
     *
     * @param body 原始响应体（{@link ResponseEntity} 已被拆解，这里拿到的是其内容）
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // Swagger 的 /v3/api-docs 返回的是 OpenAPI 规范本身，包装后会破坏文档，直接放行
        if (isApiDocsRequest(request)) {
            return body;
        }
        // 容器错误页（异常发生在过滤器等 MVC 之外时转发到 /error），转成失败响应，
        // 不能按普通业务对象包装，否则会出现 code=200、message=成功 的假成功
        if (isErrorRequest(request)) {
            int status = errorStatus(body);
            response.setStatusCode(ResultCode.httpStatusOf(status));
            return Result.fail(status, errorMessage(body));
        }
        // ResponseEntity 的返回类型由使用者自行控制状态码和响应头，这里不再覆盖
        boolean responseEntity = isResponseEntity(returnType);
        // 已包装的结果直接透传，同时把 body 的 code 同步到 HTTP 状态码
        if (body instanceof Result<?> result) {
            if (!responseEntity) {
                response.setStatusCode(ResultCode.httpStatusOf(result.getCode()));
            }
            return body;
        }
        // String 默认会被 StringHttpMessageConverter 当作纯文本写出，需手动序列化成 JSON
        if (body instanceof String) {
            if (!responseEntity) {
                response.setStatusCode(HttpStatus.OK);
            }
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            try {
                return objectMapper.writeValueAsString(Result.ok(body));
            } catch (Exception e) {
                throw new IllegalStateException("统一响应序列化失败", e);
            }
        }
        // 普通对象、集合、以及 void 方法的 null 返回值统一包装为成功响应
        if (!responseEntity) {
            response.setStatusCode(HttpStatus.OK);
        }
        return Result.ok(body);
    }

    /**
     * 判断返回类型是否为 {@link ResponseEntity}，此类响应由使用者自行决定状态码。
     */
    private boolean isResponseEntity(MethodParameter returnType) {
        return ResponseEntity.class.isAssignableFrom(returnType.getParameterType());
    }

    /**
     * 判断是否为 Swagger/OpenAPI 文档请求。
     */
    private boolean isApiDocsRequest(ServerHttpRequest request) {
        if (request == null || request.getURI() == null) {
            return false;
        }
        String path = request.getURI().getPath();
        return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui");
    }

    /**
     * 判断是否为容器错误转发请求。
     */
    private boolean isErrorRequest(ServerHttpRequest request) {
        if (request == null || request.getURI() == null) {
            return false;
        }
        String path = request.getURI().getPath();
        return "/error".equals(path) || path.startsWith("/error/");
    }

    /**
     * 从 Spring Boot 错误页响应体里取 HTTP 状态码。
     */
    private int errorStatus(Object body) {
        if (body instanceof Map<?, ?> map && map.get("status") instanceof Integer status
                && status >= 400 && status <= 599) {
            return status;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    /**
     * 从 Spring Boot 错误页响应体里取提示信息。
     */
    private String errorMessage(Object body) {
        if (body instanceof Map<?, ?> map) {
            Object detail = map.get("message");
            if (detail != null) {
                String text = String.valueOf(detail);
                // 直接访问 /error 时 Spring 会给占位信息，跳过
                if (!text.isBlank() && !"None".equalsIgnoreCase(text)
                        && !"No message available".equalsIgnoreCase(text)) {
                    return text;
                }
            }
            Object error = map.get("error");
            if (error != null) {
                String text = String.valueOf(error);
                if (!text.isBlank() && !"None".equalsIgnoreCase(text)
                        && !"No message available".equalsIgnoreCase(text)) {
                    return text;
                }
            }
        }
        return ResultCode.INTERNAL_ERROR.getMessage();
    }
}
