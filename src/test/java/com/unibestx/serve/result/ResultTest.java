package com.unibestx.serve.result;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultTest {

    @Test
    void okWrapsDataWithSuccessCode() {
        Result<String> result = Result.ok("data");

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("成功");
        assertThat(result.getData()).isEqualTo("data");
    }

    @Test
    void okWithoutDataHasNullData() {
        Result<Void> result = Result.ok();

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).isNull();
    }

    @Test
    void failFromResultCode() {
        Result<Void> result = Result.fail(ResultCode.NOT_FOUND);

        assertThat(result.getCode()).isEqualTo(404);
        assertThat(result.getMessage()).isEqualTo("资源不存在");
        assertThat(result.getData()).isNull();
    }

    @Test
    void failWithCustomCode() {
        Result<Void> result = Result.fail(1001, "自定义错误");

        assertThat(result.getCode()).isEqualTo(1001);
        assertThat(result.getMessage()).isEqualTo("自定义错误");
    }
}
