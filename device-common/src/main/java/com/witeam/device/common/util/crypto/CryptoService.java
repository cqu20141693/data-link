package com.witeam.device.common.util.crypto;


import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.validation.constraints.NotNull;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author gow 2024/01/23
 */
public class CryptoService implements Crypto {
    private static final Logger log = LoggerFactory.getLogger(CryptoService.class);
    private String name;
    private String type;
    private String seed;

    public CryptoService(@NotNull String name, @NotNull String type, @NotNull String seed) {
        this.name = name;
        this.type = type;
        this.seed = seed;
    }

    public byte[] encrypt(byte[] data) {
        try {
            Key key = this.generateKey(this.seed.getBytes(StandardCharsets.UTF_8));
            AlgorithmParameterSpec iv = this.generateIv(this.seed.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(this.type);
            cipher.init(1, key, iv);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException var5) {
            log.error("encrypt occur exception cause={} msg={}", var5.getCause(), var5.getMessage());
            return null;
        }
    }

    public byte[] decrypt(byte[] data) {
        try {
            Key key = this.generateKey(this.seed.getBytes(StandardCharsets.UTF_8));
            AlgorithmParameterSpec iv = this.generateIv(this.seed.getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance(this.type);
            cipher.init(2, key, iv);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException var5) {
            log.error("decrypt occur exception cause={} msg={}", var5.getCause(), var5.getMessage());
            return null;
        }
    }

    public Key generateKey(byte[] seed) {
        return new SecretKeySpec(seed, this.name);
    }

    public AlgorithmParameterSpec generateIv(byte[] seed) {
        return new IvParameterSpec(seed);
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
}
