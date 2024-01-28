package com.witeam.device.authenticate.algorithm.signature;

/**
 * @author gow 2024/01/23
 */
public enum HmacAlgorithm implements SignatureAlgorithm {
    HmacSHA256("HmacSHA256"),
    HmacSM3("HmacSM3"),
    UNKNOWN("unknown");

    private final String name;

    private HmacAlgorithm(String algorithm) {
        this.name = algorithm;
    }

    public String getName() {
        return this.name;
    }
}
