package com.unibestx.serve.constant;

public enum BaseStatusEnum {

    DISABLED(0, "禁用"),
    ENABLED(1, "正常");

    private final int code;
    private final String desc;

    BaseStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
