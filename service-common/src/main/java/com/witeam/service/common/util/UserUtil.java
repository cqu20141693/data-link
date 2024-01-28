package com.witeam.service.common.util;

import com.witeam.service.common.util.codec.CodecUtil;
import org.springframework.util.StringUtils;

public class UserUtil {
    public static String generatePassword(String pwd, String salt) {
        if (StringUtils.isEmpty(pwd) || StringUtils.isEmpty(salt)) {
            return null;
        }

        String calPassWord = CodecUtil.sha256(pwd + salt);

        return calPassWord;
    }
}
