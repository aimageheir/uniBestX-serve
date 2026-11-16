package com.unibestx.serve.util;

import cn.hutool.core.util.RandomUtil;

public final class SaltUtil {

    private SaltUtil() {
    }

    public static String randomSalt() {
        return RandomUtil.randomString(16);
    }
}
