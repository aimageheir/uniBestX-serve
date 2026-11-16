package com.unibestx.serve.util;

import cn.hutool.crypto.SecureUtil;

public final class DigestUtil {

    private DigestUtil() {
    }

    public static String md5(String data) {
        return SecureUtil.md5(data);
    }

    public static String sha256(String data) {
        return SecureUtil.sha256(data);
    }

    public static String sha256WithSalt(String data, String salt) {
        return SecureUtil.sha256(salt + data);
    }
}
