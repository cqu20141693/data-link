package com.witeam.device.authenticate.algorithm.signature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author gow 2024/01/23
 */

public class SignatureFactory {
    private static final Map<String, HmacAlgorithm> INNERHmac;

    public static Signature createHmacSignature(SignatureAlgorithm signatureAlgorithm, byte[] key) {
        HmacAlgorithm hmacAlgorithm =INNERHmac.get(signatureAlgorithm.getName());
        return (Signature) Optional.ofNullable(hmacAlgorithm).map((algorithm) -> {
            return new HmacSignature(key, hmacAlgorithm);
        }).orElse(null);
    }

    public static HmacSignature createHmacSignatureWithoutKey(SignatureAlgorithm signatureAlgorithm) {
        HmacAlgorithm hmacAlgorithm =INNERHmac.get(signatureAlgorithm.getName());
        return Optional.ofNullable(hmacAlgorithm)
                .map((algorithm) -> new HmacSignature(hmacAlgorithm)).orElse(null);
    }

    static {
        Security.addProvider(new BouncyCastleProvider());
        INNERHmac = new HashMap();
        HmacAlgorithm[] var0 = HmacAlgorithm.values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            HmacAlgorithm hmacAlgorithm = var0[var2];
            INNERHmac.put(hmacAlgorithm.getName(), hmacAlgorithm);
        }

    }
}
