package com.unibestx.serve.exception;

import com.unibestx.serve.result.Result;
import com.unibestx.serve.result.ResultCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(ResultCode.INTERNAL_ERROR.getCode(), message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public Result<Void> toResult() {
        return Result.fail(code, getMessage());
    }
}
