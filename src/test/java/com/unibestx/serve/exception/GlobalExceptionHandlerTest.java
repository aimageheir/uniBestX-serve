package com.unibestx.serve.exception;

import com.unibestx.serve.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void businessExceptionKeepsStandardStatus() {
        ResponseEntity<Result<Void>> response =
                handler.handleBusinessException(new BusinessException(401, "登录已过期"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("登录已过期");
    }

    @Test
    void businessExceptionFallsBackToBadRequestForNonHttpCode() {
        ResponseEntity<Result<Void>> response =
                handler.handleBusinessException(new BusinessException(1001, "自定义业务错误"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(1001);
    }

    @Test
    void unexpectedExceptionReturnsInternalServerError() {
        ResponseEntity<Result<Void>> response =
                handler.handleException(new IllegalStateException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).contains("boom");
    }
}
