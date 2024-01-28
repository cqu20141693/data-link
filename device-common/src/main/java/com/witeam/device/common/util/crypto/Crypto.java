package com.witeam.device.common.util.crypto;

/**
 * @author gow 2024/01/23
 */
public interface Crypto {
    byte[] encrypt(byte[] var1);

    byte[] decrypt(byte[] var1);
}
