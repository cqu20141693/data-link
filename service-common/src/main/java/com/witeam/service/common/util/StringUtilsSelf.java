package com.witeam.service.common.util;

/**
 * 弥补外面某些方法不足
 */
public class StringUtilsSelf {

    /**
     * 判断是否所有字符均为ascii字符
     *
     * @param data
     * @return
     */
    public static boolean isAsciiDigits(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        for (int l = data.length() - 1; l > 0; l--) {
            char c = data.charAt(l);
            if (c < 48 || c > 57) {
                return false;
            }
        }

        return true;
    }
}
