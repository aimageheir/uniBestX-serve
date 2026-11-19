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
}
