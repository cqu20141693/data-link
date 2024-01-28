package com.witeam.device.authenticate.service;

import com.witeam.device.authenticate.algorithm.signature.Signature;

/**
 * @author gow 2024/01/24
 */
public interface CheckService {
    Boolean check(Signature signature, String token, String validToken);
}
