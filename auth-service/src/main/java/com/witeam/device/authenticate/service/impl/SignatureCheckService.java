package com.witeam.device.authenticate.service.impl;

import com.witeam.device.authenticate.algorithm.signature.Signature;
import com.witeam.device.authenticate.service.CheckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author gow 2024/01/24
 */

@Service
public class SignatureCheckService implements CheckService {
    private static final Logger log = LoggerFactory.getLogger(SignatureCheckService.class);
    private static final int SIGNATURE_TOKEN_LENGTH = 3;
    private final Long NONCE_EXPIRE_TIME = 3600000L;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public SignatureCheckService() {
    }

    public Boolean check(Signature signature, String token, String validToken) {
        String[] split = validToken.split(":");
        if (split.length != 3) {
            log.info("validToken format error ,validToken={}", validToken);
            return false;
        } else {
            String signatureToken = split[0];
            String nonce = split[1];
            String time = split[2];

            try {
                long timestamp = Long.parseLong(time);
                long intervalTime = System.currentTimeMillis() - timestamp;
                if (intervalTime < 0L || intervalTime > this.NONCE_EXPIRE_TIME) {
                    log.info("signature is expired or a future time current={},validToken={}", System.currentTimeMillis(), validToken);
                    return false;
                }
            } catch (NumberFormatException var12) {
                log.info("validToken format error ,validToken={}", validToken);
                return false;
            }

            if (this.checkNonceUsed(token, nonce)) {
                log.info("nonce is used in recently 1 hour loginkey={},nonce={}", token, nonce);
                return false;
            } else {
                String origin = String.join(":", token, nonce, time);
                byte[] bytes = signature.doSignature(origin.getBytes(StandardCharsets.UTF_8));
                if (bytes == null) {
                    return false;
                } else {
                    String base64String = Base64.getEncoder().encodeToString(bytes);
                    return base64String.equals(signatureToken);
                }
            }
        }
    }

    private Boolean checkNonceUsed(String loginKey, String nonce) {
        String key = String.join(":", "S:N", loginKey, nonce);
        Boolean hasKey = this.stringRedisTemplate.hasKey(key);
        if (hasKey != null && hasKey) {
            return true;
        } else {
            this.stringRedisTemplate.opsForValue().set(key, "", this.NONCE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
            return false;
        }
    }
}
