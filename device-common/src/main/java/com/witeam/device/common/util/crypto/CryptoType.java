package com.witeam.device.common.util.crypto;

/**
 * @author gow 2024/01/23
 */

public enum CryptoType {
    AES("AES", "AES/CBC/PKCS5Padding"),
    SM4("SM4", "SM4/CBC/PKCS5Padding");

    private String name;
    private String type;

    private CryptoType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }
}
