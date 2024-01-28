package com.witeam.device.common.util.crypto;


import javax.validation.constraints.NotNull;

/**
 * @author gow 2024/01/23
 */
public class CryptoWrapper implements Crypto {
    public static final String CRYPTO_DELIMITER = "-";
    private CryptoService cryptoService;

    public CryptoWrapper(@NotNull String signatureTag) {
        String[] split = signatureTag.split("-");

        assert split.length == 2 : "signatureTag format error";

        String signatureKey = split[1];
        CryptoType crypto = CryptoType.valueOf(split[0]);
        this.cryptoService = new CryptoService(crypto.getName(), crypto.getType(), signatureKey);
    }

    public byte[] encrypt(byte[] data) {
        return this.cryptoService.encrypt(data);
    }

    public byte[] decrypt(byte[] data) {
        return this.cryptoService.decrypt(data);
    }
}
