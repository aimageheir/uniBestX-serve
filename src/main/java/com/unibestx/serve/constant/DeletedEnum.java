package com.unibestx.serve.constant;

public enum DeletedEnum {

    NOT_DELETED(0, "未删除"),
    DELETED(1, "已删除");

    private final int code;
    private final String desc;

    DeletedEnum(int code, String desc) {
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
