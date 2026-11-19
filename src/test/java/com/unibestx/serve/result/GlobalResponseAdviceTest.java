package com.unibestx.serve.result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GlobalResponseAdviceTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private final GlobalResponseAdvice advice = new GlobalResponseAdvice(objectMapper);
    private final HttpHeaders headers = new HttpHeaders();
    private final ServerHttpResponse response = mock(ServerHttpResponse.class);

    @BeforeEach
    void setUp() {
        given(response.getHeaders()).willReturn(headers);
    }

    @Test
    void wrapsPlainObject() {
        List<String> payload = List.of("payload");
        Object body = advice.beforeBodyWrite(payload, returnType("plain"),
                MediaType.APPLICATION_JSON, null, null, response);

        assertThat(body).isInstanceOf(Result.class);
        Result<?> result = (Result<?>) body;
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).isEqualTo(payload);
        verify(response).setStatusCode(HttpStatus.OK);
    }

    @Test
    void wrapsNullBody() {
        Object body = advice.beforeBodyWrite(null, returnType("plain"),
                MediaType.APPLICATION_JSON, null, null, response);

        assertThat(body).isInstanceOf(Result.class);
        assertThat(((Result<?>) body).getCode()).isEqualTo(200);
        assertThat(((Result<?>) body).getData()).isNull();
    }

    @Test
    void leavesResultUnwrappedAndAppliesStatus() {
        Result<List<String>> original = Result.ok(List.of("a", "b"));

        Object body = advice.beforeBodyWrite(original, returnType("result"),
                MediaType.APPLICATION_JSON, null, null, response);

        assertThat(body).isSameAs(original);
        verify(response).setStatusCode(HttpStatus.OK);
    }

    @Test
    void appliesFailStatusFromResultCode() {
        Result<Void> original = Result.fail(ResultCode.NOT_FOUND);

        Object body = advice.beforeBodyWrite(original, returnType("result"),
                MediaType.APPLICATION_JSON, null, null, response);

        assertThat(body).isSameAs(original);
        verify(response).setStatusCode(HttpStatus.NOT_FOUND);
    }

    @Test
    void keepsResponseEntityStatus() {
        Result<Void> original = Result.fail(403, "禁止访问");

        advice.beforeBodyWrite(original, returnType("responseEntity"),
                MediaType.APPLICATION_JSON, null, null, response);

        verify(response, org.mockito.Mockito.never()).setStatusCode(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void serializesStringAsJson() {
        Object body = advice.beforeBodyWrite("hello", returnType("textual"),
                MediaType.TEXT_PLAIN, null, null, response);

        assertThat(body).isInstanceOf(String.class);
        assertThat((String) body).contains("\"code\":200").contains("\"data\":\"hello\"");
        assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        verify(response).setStatusCode(HttpStatus.OK);
    }

    @Test
    void skipsResourceReturnType() {
        assertThat(advice.supports(new MethodParameter(method("resource"), -1), null)).isFalse();
        assertThat(advice.supports(new MethodParameter(method("bytes"), -1), null)).isFalse();
        assertThat(advice.supports(new MethodParameter(method("plain"), -1), null)).isTrue();
    }

    private MethodParameter returnType(String name) {
        return new MethodParameter(method(name), -1);
    }

    private static java.lang.reflect.Method method(String name) {
        try {
            return Sample.class.getDeclaredMethod(name);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unused")
    static class Sample {
        public Object plain() {
            return null;
        }

        public Result<String> result() {
            return null;
        }

        public org.springframework.http.ResponseEntity<Result<String>> responseEntity() {
            return null;
        }

        public String textual() {
            return null;
        }

        public org.springframework.core.io.Resource resource() {
            return null;
        }

        public byte[] bytes() {
            return new byte[0];
        }
    }
}
