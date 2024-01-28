package com.witeam.service.common.util.codec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CodecUtil {
    private final static Logger logger = LoggerFactory.getLogger(CodecUtil.class);

    public static String sha256(String str) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(str.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(hash);
        } catch (NoSuchAlgorithmException e) {
            logger.error("", e);
            return null;
        }
    }

}
