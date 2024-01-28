package com.witeam.device.common.model.device.base;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gow 2024/01/23
 */
public enum CmdStatus {
    SEND_INIT("sendInit", 1),
    SEND_FAIL("sendFail", 2),
    SEND("send", 2),
    ACK("ack", 3),
    ACK_EXPIRE("ackExpire", 3),
    COMPLETE("complete", 4),
    EXEC_FAIL("execFail", 4),
    EXEC_EXPIRE("execExpire", 4),
    UNKNOWN("unknown", -1);

    private static final Map<String, CmdStatus> INNER = new HashMap();
    private final String code;
    private final int level;

    private CmdStatus(String code, int level) {
        this.code = code;
        this.level = level;
    }

    public static CmdStatus parseFromCode(String code) {
        return (CmdStatus)INNER.getOrDefault(code, UNKNOWN);
    }

    public String getCode() {
        return this.code;
    }

    public int getLevel() {
        return this.level;
    }

    static {
        CmdStatus[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            CmdStatus cmdStatus = var0[var2];
            INNER.put(cmdStatus.code, cmdStatus);
        }

    }
}

