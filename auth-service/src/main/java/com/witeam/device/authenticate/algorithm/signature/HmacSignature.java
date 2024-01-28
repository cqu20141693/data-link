package com.witeam.device.authenticate.algorithm.signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author gow 2024/01/23
 */

public class HmacSignature implements Signature {
    private static final Logger log = LoggerFactory.getLogger(HmacSignature.class);
    private byte[] key;
    private SignatureAlgorithm algorithm;

    public HmacSignature(SignatureAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public HmacSignature(byte[] key, SignatureAlgorithm algorithm) {
        this.key = key;
        this.algorithm = algorithm;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] doSignature(byte[] data) {
        try {
            if (this.algorithm != null && this.key != null) {
                if (!(this.algorithm instanceof HmacAlgorithm)) {
                    log.error("algorithm must be instanceof HmacSignature ,algorithm={}", this.algorithm.getName());
                    return null;
                } else {
                    SecretKeySpec keySpec = new SecretKeySpec(this.key, this.algorithm.getName());
                    Mac mac = Mac.getInstance(this.algorithm.getName());
                    mac.init(keySpec);
                    return mac.doFinal(data);
                }
            } else {
                log.error("doSignature algorithm={} or key={} is null", this.algorithm, this.key);
                return null;
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException var4) {
            log.error("signature occur exception cause={} msg={}", var4.getCause(), var4.getMessage());
            return null;
        }
    }
}
